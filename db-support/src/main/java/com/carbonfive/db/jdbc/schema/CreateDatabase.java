package com.carbonfive.db.jdbc.schema;

import java.sql.*;

public class CreateDatabase extends AbstractSchemaCommand
{
    public static final String CREATE_DATABASE_SQL = "create database %s";

    private String driver;
    private String url;
    private String username;
    private String password;

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
