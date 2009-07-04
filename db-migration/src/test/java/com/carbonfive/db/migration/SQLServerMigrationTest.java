package com.carbonfive.db.migration;

import com.carbonfive.db.jdbc.schema.CreateDatabase;
import com.carbonfive.db.jdbc.schema.DropDatabase;
import net.sourceforge.jtds.jdbc.Driver;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.Is.is;
import org.junit.After;
import static org.junit.Assume.assumeThat;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.net.InetAddress;
import java.net.UnknownHostException;

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