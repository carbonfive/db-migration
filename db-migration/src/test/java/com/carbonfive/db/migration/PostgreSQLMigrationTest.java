package com.carbonfive.db.migration;

import com.carbonfive.db.jdbc.schema.*;
import static org.hamcrest.core.Is.*;
import org.junit.*;
import static org.junit.Assert.*;
import org.postgresql.*;
import org.springframework.jdbc.core.simple.*;
import org.springframework.jdbc.datasource.*;

import javax.sql.*;
import static java.lang.String.format;
import static java.lang.System.*;

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