package com.carbonfive.db.jdbc.datasource;

public class ContextServiceImpl
{
    private String currentClient = "client0";

    public void setCurrentClient(String currentClient)
    {
        this.currentClient = currentClient;
    }

    public String currentClient()
    {
        return currentClient;
    }
}
