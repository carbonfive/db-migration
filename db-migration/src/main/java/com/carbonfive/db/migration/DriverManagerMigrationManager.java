package com.carbonfive.db.migration;

import com.carbonfive.db.jdbc.*;
import org.springframework.jdbc.datasource.*;

public class DriverManagerMigrationManager extends DataSourceMigrationManager
{
    public DriverManagerMigrationManager(String driver, String url, String username, String password)
    {
        super(new DriverManagerDataSource(driver, url, username, password));
    }

    public DriverManagerMigrationManager(String driver, String url, String username, String password, DatabaseType dbType)
    {
        super(new DriverManagerDataSource(driver, url, username, password), dbType);
    }
}
