package com.carbonfive.db.migration;

import com.carbonfive.db.jdbc.schema.*;
import com.mysql.jdbc.*;
import static org.hamcrest.core.Is.*;
import org.junit.*;
import static org.junit.Assert.*;
import org.springframework.jdbc.core.simple.*;
import org.springframework.jdbc.datasource.*;

import javax.sql.*;
import static java.lang.String.format;
import static java.lang.System.*;

public class MySQLMigrationTest
{
    private DataSourceMigrationManager migrationManager;
    private SimpleJdbcTemplate jdbcTemplate;

    private static final String URL = format("jdbc:mysql://%s/mysql_migration_test", getProperty("jdbc.host", "localhost"));
    private static final String USERNAME = "dev";
    private static final String PASSWORD = "dev";

    @Before
    public void setup() throws Exception
    {
        new CreateDatabase(URL, USERNAME, PASSWORD).execute();

        DataSource dataSource = new SimpleDriverDataSource(new Driver(), URL, USERNAME, PASSWORD);
        migrationManager = new DataSourceMigrationManager(dataSource);
        migrationManager.setMigrationResolver(new ResourceMigrationResolver("classpath:/test_migrations/mysql_50/"));

        jdbcTemplate = new SimpleJdbcTemplate(dataSource);
    }

    @After
    public void teardown() throws Exception
    {
        new DropDatabase(URL, USERNAME, PASSWORD).execute();
    }

    @Test
    public void singleLineFunction()
    {
        jdbcTemplate.update("CREATE FUNCTION hello (s CHAR(20)) RETURNS CHAR(50) DETERMINISTIC RETURN CONCAT('Hello, ',s,'!');");
    }

    @Test
    public void multiLineFunction()
    {
        jdbcTemplate.update("CREATE FUNCTION weighted_average (n1 INT, n2 INT, n3 INT, n4 INT) RETURNS INT DETERMINISTIC BEGIN DECLARE avg INT; SET avg = (n1+n2+n3*2+n4*4)/8; RETURN avg; END;");
    }

    @Test
    public void storedProcedure()
    {
        jdbcTemplate.update("CREATE PROCEDURE payment(payment_amount DECIMAL(6,2), payment_seller_id INT) BEGIN DECLARE n DECIMAL(6,2); SET n = payment_amount - 1.00; INSERT INTO Moneys VALUES (n, CURRENT_DATE); IF payment_amount > 1.00 THEN UPDATE Sellers SET commission = commission + 1.00 WHERE seller_id = payment_seller_id; END IF; END;");
    }

    @Test
    public void migrateShouldApplyPendingMigrations()
    {
        migrationManager.migrate();

        assertThat(jdbcTemplate.queryForInt("select count(version) from schema_version"), is(5));

        assertThat(jdbcTemplate.queryForInt("select count(*) from books"), is(9));
        assertThat(jdbcTemplate.queryForInt("select count(*) from authors"), is(2));
    }
}
