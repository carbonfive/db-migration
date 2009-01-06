package com.carbonfive.db.hibernate.usertypes;

import com.mockrunner.mock.jdbc.*;
import static org.junit.Assert.*;
import org.junit.*;

import java.sql.*;
import java.util.*;

public class EnhancedEnumUserTypeTest
{
    @Test
    public void testNullSafeGet() throws SQLException
    {
        EnhancedEnumUserType type = new EnhancedEnumUserType();
        Properties props = new Properties();
        props.setProperty("enumClass", TestEnhancedEnum.class.getName());
        type.setParameterValues(props);

        MockResultSet rs = new MockResultSet("1");
        rs.addColumn("type");
        rs.addRow(new Object[] { "CARN" });
        rs.addRow(new Object[] { "HERB" });
        rs.addRow(new Object[] { null });
        rs.next();

        Object o = type.nullSafeGet(rs, new String[] { "type" }, null);
        assertTrue(o instanceof TestEnhancedEnum);
        assertEquals(TestEnhancedEnum.CARNIVORE, o);

        rs.next();
        o = type.nullSafeGet(rs, new String[] { "type" }, null);
        assertTrue(o instanceof TestEnhancedEnum);
        assertEquals(TestEnhancedEnum.HERBAVORE, o);

        rs.next();
        o = type.nullSafeGet(rs, new String[] { "type" }, null);
        assertNull(o);
    }

    @Test
    public void testNullSafeSet() throws SQLException
    {
        EnhancedEnumUserType type = new EnhancedEnumUserType();
        Properties props = new Properties();
        props.setProperty("enumClass", TestEnhancedEnum.class.getName());
        type.setParameterValues(props);

        MockPreparedStatement statement = new MockPreparedStatement(new MockConnection(), "");

        type.nullSafeSet(statement, TestEnhancedEnum.OMNIVORE, 0);
        type.nullSafeSet(statement, TestEnhancedEnum.HERBAVORE, 1);
        type.nullSafeSet(statement, null, 2);

        assertEquals("OMNI", statement.getParameter(0));
        assertEquals("HERB", statement.getParameter(1));
        assertNull(statement.getParameter(2));
    }

    @Test
    public void testFindByCodeRealCodes()
    {
        assertEquals(TestEnhancedEnum.CARNIVORE, EnhancedEnumUserType.findByCode(TestEnhancedEnum.class, "CARN"));
        assertEquals(TestEnhancedEnum.OMNIVORE, EnhancedEnumUserType.findByCode(TestEnhancedEnum.class, "OMNI"));
        assertEquals(TestEnhancedEnum.HERBAVORE, EnhancedEnumUserType.findByCode(TestEnhancedEnum.class, "HERB"));
    }

    public void testFindByCodeInvalid()
    {
        try
        {
            EnhancedEnumUserType.findByCode(TestEnhancedEnum.class, "FOO");
            fail();
        }
        catch (Exception e)
        {
            // Ignored
        }
    }

}
