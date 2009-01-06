package com.carbonfive.db.jdbc;

import java.io.*;
import java.sql.*;

public class ScriptRunner
{
    private DatabaseType dbType;

    public ScriptRunner(DatabaseType dbType)
    {
        this.dbType = dbType;
    }

    public void execute(Connection connection, Reader reader) throws IOException, SQLException
    {
        ScriptRunnerImpl scriptRunner;

        switch (dbType)
        {
            default:
                scriptRunner = new ScriptRunnerImpl();
        }

        scriptRunner.execute(connection, reader);
    }
}
