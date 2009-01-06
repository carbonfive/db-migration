package com.carbonfive.db.migration;

import com.carbonfive.db.jdbc.*;
import org.apache.commons.io.*;
import org.apache.commons.lang.*;
import org.springframework.core.io.*;

import java.io.*;
import java.sql.*;

public class SQLScriptMigration extends AbstractMigration
{
    private Resource resource;

    public SQLScriptMigration(String version, Resource resource)
    {
        super(version);
        Validate.isTrue(resource != null);

        this.resource = resource;
    }

    public void migrate(DatabaseType dbType, Connection connection)
    {
        InputStream inputStream = null;
        try
        {
            inputStream = resource.getInputStream();
            ScriptRunner scriptRunner = new ScriptRunner(dbType);
            scriptRunner.execute(connection, new InputStreamReader(inputStream, "UTF-8"));
            Validate.isTrue(!connection.isClosed(), "JDBC Connection should not be closed.");
        }
        catch (IOException e)
        {
            throw new MigrationException("Error while reading script input stream.", e);
        }
        catch (SQLException e)
        {
            throw new MigrationException("Error while executing migration script.", e);
        }
        finally
        {
            IOUtils.closeQuietly(inputStream);
        }
    }
}
