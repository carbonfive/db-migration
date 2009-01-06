package com.carbonfive.db.jdbc.datasource;

public class DataSourceCreationException extends RuntimeException
{
    public DataSourceCreationException(String message)
    {
        super(message);
    }

    public DataSourceCreationException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
