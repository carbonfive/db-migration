package com.carbonfive.db.hibernate.usertypes;

import org.hibernate.*;

import java.sql.*;

/**
 * A specialized currentVersion of EnumUserType for storing Java 1.5 enum values via hibernate when a value other than the enum name should be used. For
 * example, there may be an enumer with the value "REPRESENTMENT" which should be persisted as "REP".
 *
 * @see com.carbonfive.db.hibernate.usertypes.EnhancedEnum
 * @see com.carbonfive.db.hibernate.usertypes.EnumUserType
 * @see org.hibernate.usertype.EnhancedUserType
 * @see org.hibernate.usertype.ParameterizedType
 */
public class EnhancedEnumUserType extends EnumUserType
{
    @Override
    @SuppressWarnings("unchecked")
    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException
    {
        String code = rs.getString(names[0]);
        return rs.wasNull() ? null : findByCode((Class<? extends EnhancedEnum>) enumClass, code);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException
    {
        if (value == null)
        {
            st.setNull(index, Types.VARCHAR);
        }
        else
        {
            st.setString(index, ((EnhancedEnum) value).getCode());
        }
    }

    public static EnhancedEnum findByCode(Class<? extends EnhancedEnum> enumClazz, String code)
    {
        for (EnhancedEnum e : enumClazz.getEnumConstants())
        {
            if (e.getCode().equals(code))
            {
                return e;
            }
        }
        throw new IllegalArgumentException("EnhancedEnum with code '" + code + "' does not exist.");
    }
}
