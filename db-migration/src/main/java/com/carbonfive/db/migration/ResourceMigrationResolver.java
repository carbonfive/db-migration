package com.carbonfive.db.migration;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import static org.springframework.util.StringUtils.collectionToCommaDelimitedString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A MigrationResolver which leverages Spring's robust Resource loading mechanism, supporting 'file:', 'classpath:', and standard url format resources.
 * <p/>
 * Migration Location Examples: <ul> <li>classpath:/db/migrations/</li> <li>file:src/main/db/migrations/</li> <li>file:src/main/resources/db/migrations/</li>
 * </ul> All of the resources found in the migrations location which do not start with a '.' will be considered migrations.
 * <p/>
 * Configured out of the box with a SimpleVersionExtractor and the default resource pattern CLASSPATH_MIGRATIONS_SQL.
 *
 * @see Resource
 * @see PathMatchingResourcePatternResolver
 * @see VersionExtractor
 * @see MigrationFactory
 */
public class ResourceMigrationResolver implements MigrationResolver
{
    private static final String CLASSPATH_MIGRATIONS_SQL = "classpath:/db/migrations/";

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private String migrationsLocation;
    private VersionExtractor versionExtractor;
    private MigrationFactory migrationFactory = new MigrationFactory();

    public ResourceMigrationResolver()
    {
        this(CLASSPATH_MIGRATIONS_SQL);
    }

    public ResourceMigrationResolver(String migrationsLocation)
    {
        this(migrationsLocation, new SimpleVersionExtractor());
    }

    public ResourceMigrationResolver(String migrationsLocation, VersionExtractor versionExtractor)
    {
        setMigrationsLocation(migrationsLocation);
        setVersionExtractor(versionExtractor);
    }

    public Set<Migration> resolve()
    {
        Set<Migration> migrations = new HashSet<Migration>();

        // Find all resources in the migrations location.
        PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        List<Resource> resources;
        try
        {
            resources = new ArrayList<Resource>(Arrays.asList(patternResolver.getResources(migrationsLocation)));
        }
        catch (IOException e)
        {
            throw new MigrationException(e);
        }

        // Remove resources starting with a '.' (e.g. .svn, .cvs, etc)
        CollectionUtils.filter(resources, new Predicate()
        {
            public boolean evaluate(Object object)
            {
                try
                {
                    return !(((Resource) object).getFilename().startsWith(".") || (((Resource) object).getFile().isDirectory()));
                }
                catch (IOException e)
                {
                    return false;
                }
            }
        });

        if (resources.isEmpty())
        {
            String message = "No migrations were found using resource pattern '" + migrationsLocation + "'. Terminating migration.";
            log.error(message);
            throw new MigrationException(message);
        }

        if (log.isDebugEnabled())
        {
            log.debug("Found " + resources.size() + " resources: " + collectionToCommaDelimitedString(resources));
        }

        // Extract versions and create executable migrations for each resource.
        for (Resource resource : resources)
        {
            String version = versionExtractor.extractVersion(resource.getFilename());
            migrations.add(migrationFactory.create(version, resource));
        }

        return migrations;
    }

    public void setMigrationsLocation(String migrationsLocation)
    {
        if (!migrationsLocation.endsWith("*"))
        {
            migrationsLocation += "*";
        }

        this.migrationsLocation = migrationsLocation;
    }

    public void setVersionExtractor(VersionExtractor versionExtractor)
    {
        this.versionExtractor = versionExtractor;
    }

    public void setMigrationFactory(MigrationFactory migrationFactory)
    {
        this.migrationFactory = migrationFactory;
    }
}
