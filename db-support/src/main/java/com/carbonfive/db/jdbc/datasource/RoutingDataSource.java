package com.carbonfive.db.jdbc.datasource;

import org.springframework.jdbc.datasource.*;

import javax.sql.*;
import java.sql.*;
import java.util.*;

public abstract class RoutingDataSource extends AbstractDataSource
{
    private boolean createNewDataSources = true;
    private Map<Object, DataSource> dataSources = new HashMap<Object, DataSource>();
    private DataSourceFactory dataSourceFactory;

    public RoutingDataSource()
    {
    }

    public RoutingDataSource(DataSourceFactory dataSourceFactory)
    {
        this.dataSourceFactory = dataSourceFactory;
    }

    public void setCreateNewDataSources(boolean createNewDataSources)
    {
        this.createNewDataSources = createNewDataSources;
    }

    public Connection getConnection() throws SQLException
    {
        DataSource dataSource = findDataSource();
        return dataSource.getConnection();
    }

    public Connection getConnection(String username, String password) throws SQLException
    {
        DataSource dataSource = findDataSource();
        return dataSource.getConnection(username, password);
    }

    protected DataSource findDataSource()
    {
        String lookupKey = determineCurrentDataSourceKey();

        synchronized (this)
        {
            if (!dataSources.containsKey(lookupKey))
            {
                if (!createNewDataSources)
                {
                    throw new RuntimeException();
                }
                createDataSource(lookupKey);
            }
        }

        return this.dataSources.get(lookupKey);
    }

    protected DataSource createDataSource(Object lookupKey) throws DataSourceCreationException
    {
        DataSource dataSource = dataSourceFactory.createDataSource(lookupKey);
        dataSources.put(lookupKey, dataSource);
        return dataSource;
    }

    protected abstract String determineCurrentDataSourceKey();
}
