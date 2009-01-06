package com.carbonfive.db.jdbc.datasource;

public class ContextRoutingDataSource extends RoutingDataSource
{
    private ContextServiceImpl contextService;

    public ContextRoutingDataSource(ContextServiceImpl contextService)
    {
        this.contextService = contextService;
    }

    public ContextRoutingDataSource(DataSourceFactory dataSourceFactory, ContextServiceImpl contextService)
    {
        super(dataSourceFactory);
        this.contextService = contextService;
    }

    protected String determineCurrentDataSourceKey()
    {
        return contextService.currentClient();
    }
}
