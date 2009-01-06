package com.carbonfive.db.hibernate.usertypes;

import com.mockrunner.mock.jdbc.*;
import org.hamcrest.core.*;
import static org.junit.Assert.*;
import org.junit.*;

import java.sql.*;
import java.util.*;

public class EnumUserTypeTest
{
    @Test
    public void testNullSafeGet() throws SQLException
    {
        EnumUserType type = new EnumUserType();
        Properties props = new Properties();
        props.setProperty("enumClass", TestEnum.class.getName());
        type.setParameterValues(props);

        MockResultSet rs = new MockResultSet("1");
        rs.addColumn("type");
        rs.addRow(new Object[] { "MALE" });
        rs.addRow(new Object[] { "PAT" });
        rs.addRow(new Object[] { null });
        rs.next();

        Object o = type.nullSafeGet(rs, new String[] { "type" }, null);
        assertThat(o, new IsInstanceOf(TestEnum.class));
        assertEquals(TestEnum.MALE, o);

        rs.next();
        o = type.nullSafeGet(rs, new String[] { "type" }, null);
        assertThat(o, new IsInstanceOf(TestEnum.class));
        assertEquals(TestEnum.PAT, o);

        rs.next();
        o = type.nullSafeGet(rs, new String[] { "type" }, null);
        assertNull(o);
    }

    @Test
    public void testNullSafeSet() throws SQLException
    {
        EnumUserType type = new EnumUserType();
        Properties props = new Properties();
        props.setProperty("enumClass", TestEnum.class.getName());
        type.setParameterValues(props);

        MockPreparedStatement statement = new MockPreparedStatement(new MockConnection(), "");

        type.nullSafeSet(statement, TestEnum.MALE, 0);
        type.nullSafeSet(statement, TestEnum.FEMALE, 1);
        type.nullSafeSet(statement, null, 2);

        assertEquals("MALE", statement.getParameter(0));
        assertEquals("FEMALE", statement.getParameter(1));
        assertNull(statement.getParameter(2));
    }

}
