package com.carbonfive.db.jdbc.datasource;

import com.mchange.v2.c3p0.*;
import org.slf4j.*;

import javax.sql.*;
import java.beans.*;

public class C3P0DataSourceFactory implements DataSourceFactory
{
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private String driver;
    private String url;
    private String username;
    private String password;

    public C3P0DataSourceFactory()
    {
    }

    public C3P0DataSourceFactory(String driver, String url, String username, String password)
    {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public void setDriver(String driver)
    {
        this.driver = driver;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public DataSource createDataSource(Object databaseKey)
    {
        String databaseName = databaseKey.toString();
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        try
        {
            dataSource.setDriverClass(driver);
        }
        catch (PropertyVetoException e)
        {
            String msg = "Could not create data source with driver class '" + driver + "' for '" + databaseName + "'.";
            log.error(msg, e);
            throw new DataSourceCreationException(msg, e);
        }
        dataSource.setJdbcUrl(url + databaseName);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}
