package com.carbonfive.db.migration;

import com.carbonfive.db.jdbc.*;
import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;
import org.junit.*;
import org.springframework.core.io.*;
import org.springframework.jdbc.core.simple.*;

import javax.sql.*;
import java.sql.*;

public class GroovyMigrationTest
{
    private DataSource dataSource;

    @Before
    public void setup()
    {
        dataSource = DatabaseTestUtils.createUniqueDataSource();
    }

    @Test
    public void testMigrate() throws Exception
    {
        Resource script = new ClassPathResource("/test_migrations/groovy_1/001_create_users.groovy");
        Connection connection = dataSource.getConnection();
        new GroovyMigration("1", script).migrate(DatabaseType.H2, connection);
        connection.close();

        SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(dataSource);
        assertThat(jdbcTemplate.queryForInt("select count(*) from users"), is(1));
    }
}
