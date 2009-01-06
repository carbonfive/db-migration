package com.carbonfive.db.migration;

import com.carbonfive.db.jdbc.*;
import groovy.lang.*;
import org.apache.commons.io.*;
import org.apache.commons.lang.*;
import org.springframework.core.io.*;

import java.io.*;
import java.sql.*;

public class GroovyMigration extends AbstractMigration
{
    final private Resource script;

    public GroovyMigration(String version, Resource script)
    {
        super(version);
        this.script = script;
    }

    public void migrate(DatabaseType dbType, Connection connection)
    {
        Binding binding = new Binding();
        binding.setVariable("connection", connection);
        GroovyShell shell = new GroovyShell(binding);

        InputStream inputStream = null;
        try
        {
            inputStream = script.getInputStream();
            shell.evaluate(IOUtils.toString(inputStream));
            Validate.isTrue(!connection.isClosed(), "JDBC Connection should not be closed.");
        }
        catch (IOException e)
        {
            throw new MigrationException(e);
        }
        catch (SQLException e)
        {
            throw new MigrationException(e);
        }
        finally
        {
            IOUtils.closeQuietly(inputStream);
        }
    }
}
