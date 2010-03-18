package com.carbonfive.db.jdbc.schema;

import java.sql.SQLException;

public class CreateDatabase extends AbstractSchemaCommand
{
    public static final String CREATE_DATABASE_SQL = "create database %s";

    public CreateDatabase(String url, String username, String password)
    {
        super(url, username, password);
    }

    public CreateDatabase(String driver, String url, String username, String password)
    {
        super(driver, url, username, password);
    }

    public void execute() throws SQLException, ClassNotFoundException
    {
        execute(CREATE_DATABASE_SQL);
    }
}
