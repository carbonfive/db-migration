package com.carbonfive.db.migration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import org.hsqldb.jdbcDriver;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

public class HSQLMigrationTest
{
    private DataSourceMigrationManager migrationManager;
    private SimpleJdbcTemplate jdbcTemplate;

    private static final String URL = "jdbc:hsqldb:file:./tmp/hsql-migration-test";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";

    @Before
    public void setup() throws Exception
    {
        DataSource dataSource = new SimpleDriverDataSource(new jdbcDriver(), URL, USERNAME, PASSWORD);
        migrationManager = new DataSourceMigrationManager(dataSource);
        migrationManager.setMigrationResolver(new ResourceMigrationResolver("classpath:/test_migrations/hsql/"));

        jdbcTemplate = new SimpleJdbcTemplate(dataSource);
    }

    @Test
    public void migrateShouldApplyPendingMigrations()
    {
        migrationManager.migrate();

        assertThat(jdbcTemplate.queryForInt("select count(version) from schema_version"), is(2));
    }
}