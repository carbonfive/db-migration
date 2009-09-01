package com.carbonfive.db.migration;

import com.carbonfive.db.jdbc.schema.CreateDatabase;
import com.carbonfive.db.jdbc.schema.DropDatabase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.postgresql.Driver;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import static java.lang.String.format;
import static java.lang.System.getProperty;

public class PostgreSQLMigrationTest
{
    private DataSourceMigrationManager migrationManager;
    private SimpleJdbcTemplate jdbcTemplate;

    private static final String URL = format("jdbc:postgresql://%s/postgresql_migration_test", getProperty("jdbc.host", "localhost"));
    private static final String USERNAME = "dev";
    private static final String PASSWORD = "dev";

    @Before
    public void setup() throws Exception
    {
        new CreateDatabase(URL, USERNAME, PASSWORD).execute();

        DataSource dataSource = new SimpleDriverDataSource(new Driver(), URL, USERNAME, PASSWORD);
        migrationManager = new DataSourceMigrationManager(dataSource);
        migrationManager.setMigrationResolver(new ResourceMigrationResolver("classpath:/test_migrations/postgresql_8/"));

        jdbcTemplate = new SimpleJdbcTemplate(dataSource);
    }

    @After
    public void teardown() throws Exception
    {
        new DropDatabase(URL, USERNAME, PASSWORD).execute();
    }

    @Test
    public void migrateShouldApplyPendingMigrations()
    {
        migrationManager.migrate();

        assertThat(jdbcTemplate.queryForInt("select count(version) from schema_version"), is(1));
    }
}