package com.carbonfive.db.migration;

import com.carbonfive.db.jdbc.*;
import org.apache.commons.collections.*;
import org.apache.commons.lang.time.*;
import org.slf4j.*;
import org.springframework.dao.*;
import org.springframework.jdbc.core.*;

import javax.sql.*;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class DataSourceMigrationManager implements MigrationManager
{
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private JdbcTemplate jdbcTemplate;

    private DatabaseType dbType;
    private VersionStrategy versionStratgey = new SimpleVersionStrategy();
    private MigrationResolver migrationResolver = new ResourceMigrationResolver();

    public DataSourceMigrationManager(DataSource dataSource)
    {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dbType = determineDatabaseType();
    }

    public DataSourceMigrationManager(DataSource dataSource, DatabaseType dbType)
    {
        this(dataSource);
        this.dbType = dbType;
    }

    protected void enableMigrations()
    {
        try
        {
            jdbcTemplate.execute(new ConnectionCallback()
            {
                public Object doInConnection(Connection connection) throws SQLException, DataAccessException
                {
                    versionStratgey.enableVersioning(dbType, connection);
                    return null;
                }
            });

            log.info("Successfully enabled migrations.");
        }
        catch (DataAccessException e)
        {
            log.error("Could not enable migrations.", e);
            throw new MigrationException(e);
        }
    }

    public boolean validate()
    {
        return pendingMigrations().isEmpty();
    }

    public SortedSet<String> pendingMigrations()
    {
        Set<String> appliedMigrations = (Set<String>) jdbcTemplate.execute(new ConnectionCallback()
        {
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException
            {
                return versionStratgey.appliedMigrations(dbType, connection);
            }
        });

        Set<Migration> availableMigrations = migrationResolver.resolve();
        CollectionUtils.transform(availableMigrations, new Transformer()
        {
            public Object transform(Object o)
            {
                return ((Migration) o).getVersion();
            }
        });

        SortedSet<String> pendingMigrations = new TreeSet<String>();
        CollectionUtils.select(availableMigrations, new PendingMigrationPredicate(appliedMigrations), pendingMigrations);

        return pendingMigrations;
    }

    public void migrate()
    {
        Set<String> appliedMigrations = (Set<String>) jdbcTemplate.execute(new ConnectionCallback()
        {
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException
            {
                return versionStratgey.appliedMigrations(dbType, connection);
            }
        });

        if (appliedMigrations == null)
        {
            enableMigrations();
            appliedMigrations = Collections.EMPTY_SET;
        }

        Set<Migration> availableMigrations = migrationResolver.resolve();

        // Which migrations need to be applied (ie: are pending)?
        final List<Migration> pendingMigrations = new ArrayList<Migration>(availableMigrations.size());
        CollectionUtils.select(availableMigrations, new PendingMigrationPredicate(appliedMigrations), pendingMigrations);
        Collections.sort(pendingMigrations);

        if (pendingMigrations.isEmpty())
        {
            log.info("Database is up to date; no migration necessary.");
            return;
        }

        // Check that no two pending migrations are the same version.
        // ...
        StopWatch watch = new StopWatch();
        watch.start();

        log.info("Migrating database... applying " + pendingMigrations.size() + " migration" + (pendingMigrations.size() > 1 ? "s" : "") + ".");

        try
        {
            jdbcTemplate.execute(new ConnectionCallback()
            {
                public Object doInConnection(Connection connection) throws SQLException, DataAccessException
                {
                    int successfulCount = 0;
                    Migration currentMigration = null;

                    final boolean autoCommit = connection.getAutoCommit();
                    connection.setAutoCommit(false);

                    try
                    {
                        for (Migration migration : pendingMigrations)
                        {
                            currentMigration = migration;
                            log.info("Running migration version " + currentMigration.getVersion() + ".");

                            final Date startTime = new Date();
                            StopWatch migrationWatch = new StopWatch();
                            migrationWatch.start();

                            currentMigration.migrate(dbType, connection);
                            versionStratgey.recordMigration(dbType, connection, currentMigration.getVersion(), startTime, migrationWatch.getTime());

                            connection.commit();

                            ++successfulCount;
                        }
                    }
                    catch (Throwable e)
                    {
                        assert currentMigration != null;
                        String message = "Migration for version " + currentMigration.getVersion() + " failed, rolling back and terminating migration.";
                        log.error(message, e);
                        connection.rollback();
                        throw new MigrationException(message, e);
                    }
                    finally
                    {
                        connection.setAutoCommit(autoCommit);
                    }

                    return successfulCount;
                }
            });
        }
        catch (DataAccessException e)
        {
            log.error("Failed to migrate database.", e);
            throw new MigrationException(e);
        }

        watch.stop();

        log.info("Migrated database in " + DurationFormatUtils.formatDurationHMS(watch.getTime()) + ".");
    }

    public void setDatabaseType(DatabaseType dbType)
    {
        this.dbType = dbType;
    }

    public void setMigrationResolver(MigrationResolver migrationResolver)
    {
        this.migrationResolver = migrationResolver;
    }

    public void setVersionStratgey(VersionStrategy versionStratgey)
    {
        this.versionStratgey = versionStratgey;
    }

    private DatabaseType determineDatabaseType()
    {
        return (DatabaseType) jdbcTemplate.execute(new ConnectionCallback()
        {
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException
            {
                return DatabaseUtils.databaseType(connection.getMetaData().getURL());
            }
        });
    }

    private static class PendingMigrationPredicate implements Predicate
    {
        private final Set<String> appliedMigrations;

        public PendingMigrationPredicate(Set<String> appliedMigrations)
        {
            this.appliedMigrations = appliedMigrations == null ? Collections.EMPTY_SET : appliedMigrations;
        }

        public boolean evaluate(Object input)
        {
            if (input instanceof Migration)
            {
                return !appliedMigrations.contains(((Migration) input).getVersion());
            }
            else
            {
                return !appliedMigrations.contains(input.toString());
            }
        }

    }
}
