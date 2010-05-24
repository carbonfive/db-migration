package com.carbonfive.db.migration.maven;

import com.carbonfive.db.migration.Migration;
import com.carbonfive.db.migration.MigrationManager;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.Collection;
import java.util.Set;

import static java.lang.String.format;

/**
 * Check current schema against available migrations to see if database is up to date,
 * causing the build to fail if the database is not up to date.
 * <p/>
 *
 * @goal check
 * @phase process-test-resources
 */
public class CheckMojo extends AbstractMigrationMojo
{
    public void executeMojo() throws MojoExecutionException
    {
        getLog().info("Checking " + getUrl() + " using migrations at " + getMigrationsPath() + ".");

        Set<Migration> pendingMigrations;
        try
        {
            MigrationManager manager = createMigrationManager();
            pendingMigrations = manager.pendingMigrations();
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Failed to check " + getUrl(), e);
        }

        if (pendingMigrations.isEmpty())
        {
            //getLog().info("Database is up-to-date.");
            return;
        }

        Collection<String> pendingMigrationsNames = Collections2.transform(pendingMigrations, new Function<Migration, String>()
        {
            public String apply(Migration migration)
            {
                return migration.getFilename();
            }
        });

        String msg = format("There %s %d pending migrations: \n\n    %s\n\n    Run db-migration:migrate to apply pending migrations.",
                pendingMigrations.size() == 1 ? "is" : "are",
                pendingMigrations.size(),
                Joiner.on("\n    ").join(pendingMigrationsNames));
        getLog().warn(msg);

        throw new MojoExecutionException(format("There %s %d pending migrations, migrate your db and try again.", pendingMigrations.size() == 1 ? "is" : "are", pendingMigrations.size()));
    }
}