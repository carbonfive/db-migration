package com.carbonfive.db.jdbc.datasource;

import javax.sql.*;

public interface DataSourceManager
{
    DataSource getDataSource(Object key) throws DataSourceCreationException;
}
