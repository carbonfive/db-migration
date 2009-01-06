package com.carbonfive.db;

import com.carbonfive.db.jdbc.*;
import org.springframework.beans.*;
import org.springframework.jdbc.datasource.*;

import javax.sql.*;
import java.sql.*;

public class DatabaseTestUtils
{
    public static int dbNameIndex = 0;

    public static DataSource createDataSource(String url, String username, String password)
    {
        Driver driver;
        try
        {
            driver = (Driver) BeanUtils.instantiateClass(Class.forName(DatabaseUtils.driverClass(url)));
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }

        return new SimpleDriverDataSource(driver, url, username, password);
    }
}
