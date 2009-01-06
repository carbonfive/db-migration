package com.carbonfive.db.hibernate.usertypes;

import org.hibernate.*;
import org.hibernate.usertype.*;

import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * A hibernate user type for mapping Java 1.5 enum types to and from database columns.  Can be used as identifiers or discriminators.
 * <p/>
 * The name of the enum is stored in the database.  For more control over what code is persisted, see EnhancedEnumUserType.
 * <p/>
 * Types can be defined in the hibernate-mapping files like this:
 * <pre>
 * <typedef name="PostType" class="common.hibernate.EnumUserType"> <param name="enumClass">tbd.model.content.PostType</param> </typedef>
 * </pre>
 * <p/>
 * And then used like this: <property name="type" type="PostType"> <meta attribute="property-type">PostType</meta> </property>
 *
 * @see com.carbonfive.db.hibernate.usertypes.EnhancedEnumUserType
 * @see org.hibernate.usertype.EnhancedUserType
 * @see org.hibernate.usertype.ParameterizedType
 */
public class EnumUserType implements EnhancedUserType, ParameterizedType
{
    protected Class<? extends Enum> enumClass;

    @SuppressWarnings("unchecked")
    public void setParameterValues(Properties parameters)
    {
        String enumClassName = parameters.getProperty("enumClass");
        try
        {
            enumClass = (Class<? extends Enum>) Class.forName(enumClassName);
        }
        catch (ClassNotFoundException cnfe)
        {
            throw new HibernateException("Enum class not found", cnfe);
        }
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException
    {
        return cached;
    }

    public Serializable disassemble(Object value) throws HibernateException
    {
        return (Enum) value;
    }

    public Object deepCopy(Object value) throws HibernateException
    {
        return value;
    }

    public boolean equals(Object x, Object y) throws HibernateException
    {
        return x == y;
    }

    public int hashCode(Object x) throws HibernateException
    {
        return x.hashCode();
    }

    public boolean isMutable()
    {
        return false;
    }

    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException
    {
        String name = rs.getString(names[0]);
        return rs.wasNull() ? null : Enum.valueOf(enumClass, name);
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException
    {
        if (value == null)
        {
            st.setNull(index, Types.VARCHAR);
        }
        else
        {
            st.setString(index, ((Enum) value).name());
        }
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException
    {
        return original;
    }

    public Class returnedClass()
    {
        return enumClass;
    }

    public int[] sqlTypes()
    {
        return new int[] { Types.VARCHAR };
    }

    public String objectToSQLString(Object value)
    {
        return '\'' + ((Enum) value).name() + '\'';
    }

    public String toXMLString(Object value)
    {
        return ((Enum) value).name();
    }

    public Object fromXMLString(String xmlValue)
    {
        return Enum.valueOf(enumClass, xmlValue);
    }

}
