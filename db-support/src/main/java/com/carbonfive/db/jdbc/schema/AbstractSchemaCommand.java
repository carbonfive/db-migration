package com.carbonfive.db.jdbc.schema;

import com.carbonfive.db.jdbc.*;
import org.springframework.beans.*;

import static java.lang.String.*;
import java.sql.*;
import java.util.*;

abstract class AbstractSchemaCommand
{
    private String driver;
    private String url;
    private String username;
    private String password;

    public AbstractSchemaCommand(String url, String username, String password)
    {
        this(DatabaseUtils.driverClass(url), url, username, password);
    }

    public AbstractSchemaCommand(String driver, String url, String username, String password)
    {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public abstract void execute() throws SQLException, ClassNotFoundException;

    public void execute(String createSql) throws SQLException, ClassNotFoundException
    {
        String serverUrl = DatabaseUtils.extractServerUrl(url);
        String databaseName = DatabaseUtils.extractDatabaseName(url);

        switch (DatabaseUtils.databaseType(url))
        {
            case MYSQL:
                databaseName = "`" + databaseName + "`";
                break;

            case SQL_SERVER:
            case POSTGRESQL:
                databaseName = "\"" + databaseName + "\"";
                break;
        }

        Driver driver = (Driver) BeanUtils.instantiateClass(Class.forName(this.driver));
        Properties properties = new Properties();
        properties.put("user", username);
        properties.put("password", password);
        Connection connection = driver.connect(serverUrl, properties);

        try
        {
            Statement statement = connection.createStatement();
            statement.execute(format(createSql, databaseName));
            statement.close();
        }
        finally
        {
            connection.close();
        }
    }
}
