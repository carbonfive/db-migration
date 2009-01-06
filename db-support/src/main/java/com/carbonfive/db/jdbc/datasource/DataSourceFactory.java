package com.carbonfive.db.jdbc.datasource;

import javax.sql.*;

public interface DataSourceFactory
{
    DataSource createDataSource(Object databaseKey) throws DataSourceCreationException;
}
