package com.carbonfive.db.migration;

import org.h2.*;
import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;
import org.junit.*;
import org.springframework.jdbc.core.simple.*;
import org.springframework.jdbc.datasource.*;

import javax.sql.*;

public class H2MigrationTest
{
    private DataSourceMigrationManager migrationManager;
    private SimpleJdbcTemplate jdbcTemplate;

    private static final String URL = "jdbc:h2:mem:h2-migration-test;DB_CLOSE_DELAY=-1";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";

    @Before
    public void setup() throws Exception
    {
        DataSource dataSource = new SimpleDriverDataSource(new Driver(), URL, USERNAME, PASSWORD);
        migrationManager = new DataSourceMigrationManager(dataSource);
        migrationManager.setMigrationResolver(new ResourceMigrationResolver("classpath:/test_migrations/h2/"));

        jdbcTemplate = new SimpleJdbcTemplate(dataSource);
    }

    @Test
    public void migrateShouldApplyPendingMigrations()
    {
        migrationManager.migrate();

        assertThat(jdbcTemplate.queryForInt("select count(version) from schema_version"), is(2));
    }
}