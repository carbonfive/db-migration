package com.carbonfive.db.jdbc.datasource;

import com.mockrunner.mock.jdbc.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.junit.*;
import org.springframework.jdbc.datasource.*;

import javax.sql.*;
import java.sql.*;

public class RoutingDataSourceTest
{
    private Connection cxn1;
    private Connection cxn2;
    private DataSource ds1;
    private DataSource ds2;

    @Before
    public void setUp()
    {
        cxn1 = new MockConnection();
        ds1 = new TestDataSource(cxn1);

        cxn2 = new MockConnection();
        ds2 = new TestDataSource(cxn2);
    }

    @Test
    public void testBasicRouting() throws Exception
    {
        DataSourceFactory dataSourceFactory = createMock(DataSourceFactory.class);
        expect(dataSourceFactory.createDataSource("xian")).andReturn(ds1);
        expect(dataSourceFactory.createDataSource("pants")).andReturn(ds2);
        replay(dataSourceFactory);

        ContextServiceImpl contextService = new ContextServiceImpl();

        DataSource dataSource = new ContextRoutingDataSource(dataSourceFactory, contextService);

        contextService.setCurrentClient("xian");
        assertSame(cxn1, dataSource.getConnection());

        contextService.setCurrentClient("pants");
        assertSame(cxn2, dataSource.getConnection("a", "b"));

        verify(dataSourceFactory);
    }

    @Test
    public void testInvalidName() throws Exception
    {
        DataSourceFactory dataSourceFactory = createMock(DataSourceFactory.class);
        expect(dataSourceFactory.createDataSource(anyObject())).andThrow(new DataSourceCreationException("boom!"));
        replay(dataSourceFactory);

        ContextServiceImpl contextService = new ContextServiceImpl();

        DataSource dataSource = new ContextRoutingDataSource(dataSourceFactory, contextService);

        try
        {
            dataSource.getConnection();
            fail();
        }
        catch (DataSourceCreationException e)
        {
            // Ignored
        }

        verify(dataSourceFactory);
    }

    private class TestDataSource extends AbstractDataSource
    {
        private final Connection connection;

        private TestDataSource(Connection connection)
        {
            this.connection = connection;
        }

        public Connection getConnection() throws SQLException
        {
            return connection;
        }

        public Connection getConnection(String username, String password) throws SQLException
        {
            return connection;
        }
    }
}
