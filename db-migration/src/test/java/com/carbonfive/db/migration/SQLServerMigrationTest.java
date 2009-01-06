package com.carbonfive.db.migration;

import com.carbonfive.db.jdbc.schema.*;
import net.sourceforge.jtds.jdbc.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.text.StringStartsWith.*;
import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import org.springframework.jdbc.core.simple.*;
import org.springframework.jdbc.datasource.*;

import javax.sql.*;
import java.net.*;

public class SQLServerMigrationTest
{
    private DataSourceMigrationManager migrationManager;
    private SimpleJdbcTemplate jdbcTemplate;

    private static final String URL = "jdbc:jtds:sqlserver://sqlserver2000/sqlserver_migration_test";
    private static final String USERNAME = "dev";
    private static final String PASSWORD = "dev";

    public SQLServerMigrationTest() throws UnknownHostException
    {
        assumeThat(InetAddress.getLocalHost().getHostAddress(), startsWith("10.4.5"));
    }

    @Before
    public void setup() throws Exception
    {
        new CreateDatabase(URL, USERNAME, PASSWORD).execute();

        DataSource dataSource = new SimpleDriverDataSource(new Driver(), URL, USERNAME, PASSWORD);
        migrationManager = new DataSourceMigrationManager(dataSource);
        migrationManager.setMigrationResolver(new ResourceMigrationResolver("classpath:/test_migrations/sqlserver_2000/"));

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

        assertThat(jdbcTemplate.queryForInt("select count(version) from schema_version"), is(2));
    }
}