package com.carbonfive.db.jdbc.schema;

import com.carbonfive.db.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assume.*;
import org.junit.*;

import javax.sql.*;
import static java.lang.String.*;
import static java.lang.System.*;
import java.net.*;
import java.sql.*;

public class CreateDatabaseTest
{
    @Test
    public void createMysqlDatabase() throws Exception
    {
        createDatabase(format("jdbc:mysql://%s/create_database_test", getProperty("jdbc.host", "localhost")));
        createDatabase(format("jdbc:mysql://%s/create-database-test", getProperty("jdbc.host", "localhost")));
    }

    @Test
    public void createPostgresqlDatabase() throws Exception
    {
        createDatabase(format("jdbc:postgresql://%s/create_database_test", getProperty("jdbc.host", "localhost")));
        createDatabase(format("jdbc:postgresql://%s/create-database-test", getProperty("jdbc.host", "localhost")));
    }

    @Test
    public void createSqlServerDatabase() throws Exception
    {
        assumeThat(InetAddress.getLocalHost().getHostAddress(), startsWith("10.4.5"));
        
        createDatabase("jdbc:jtds:sqlserver://sqlserver2000/create_database_test");
        createDatabase("jdbc:jtds:sqlserver://sqlserver2000/create-database-test");
    }

    private void createDatabase(String url) throws SQLException, ClassNotFoundException
    {
        final String username = "dev";
        final String password = "dev";

        new CreateDatabase(url, username, password).execute();

        DataSource dataSource = DatabaseTestUtils.createDataSource(url, username, password);
        dataSource.getConnection().close(); // Throws an exception if database is not found.

        new DropDatabase(url, username, password).execute();
    }
}
