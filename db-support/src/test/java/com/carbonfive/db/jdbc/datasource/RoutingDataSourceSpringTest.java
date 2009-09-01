package com.carbonfive.db.jdbc.datasource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Properties;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class RoutingDataSourceSpringTest
{
    @Autowired private Properties properties;
    @Autowired private RoutingDataSource dataSource;
    @Autowired private ContextServiceImpl contextService;

    private String[] dataSourceNames = { "one", "two", "three" };

    @Before
    public void onSetUp() throws Exception
    {
        for (String dataSourceName : dataSourceNames)
        {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName(properties.getProperty("jdbc.driver"));
            dataSource.setUrl(properties.getProperty("jdbc.url") + dataSourceName + ";DB_CLOSE_DELAY=-1");
            dataSource.setUsername(properties.getProperty("jdbc.username"));
            dataSource.setPassword(properties.getProperty("jdbc.password"));
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.execute("create table db_name (name varchar(128))");
            jdbcTemplate.update("insert into db_name values ('" + dataSourceName + "')");
        }
    }

    @Test
    public void testRoutingDataSource() throws SQLException
    {
        for (String dataSourceName : dataSourceNames)
        {
            contextService.setCurrentClient(dataSourceName);
            SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(dataSource);
            String name = jdbcTemplate.queryForObject("select name from db_name", String.class, Collections.EMPTY_MAP);
            assertThat(name, is(dataSourceName));
        }
    }
}
