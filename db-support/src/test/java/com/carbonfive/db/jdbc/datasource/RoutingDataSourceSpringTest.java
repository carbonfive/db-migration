package com.carbonfive.db.jdbc.datasource;

import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runner.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.simple.*;
import org.springframework.jdbc.datasource.*;
import org.springframework.test.context.*;
import org.springframework.test.context.junit4.*;

import java.sql.*;
import java.util.*;

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
            assertEquals(dataSourceName, name);
        }
    }
}
