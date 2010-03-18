package com.carbonfive.db.jdbc.schema;

import java.sql.SQLException;

public class DropDatabase extends AbstractSchemaCommand
{
    public static final String DROP_DATABASE_SQL = "drop database %s";

    public DropDatabase(String url, String username, String password)
    {
        super(url, username, password);
    }

    public DropDatabase(String driver, String url, String username, String password)
    {
        super(driver, url, username, password);
    }

    public void execute() throws SQLException, ClassNotFoundException
    {
        execute(DROP_DATABASE_SQL);
    }
}