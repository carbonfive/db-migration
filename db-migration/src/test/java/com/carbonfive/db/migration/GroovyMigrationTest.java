package com.carbonfive.db.migration;

import com.carbonfive.db.jdbc.DatabaseType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;

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
