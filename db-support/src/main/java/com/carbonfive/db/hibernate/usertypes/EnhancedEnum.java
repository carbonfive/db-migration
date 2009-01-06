package com.carbonfive.db.hibernate.usertypes;

/**
 * An enhanced enum has the notion of a code which differs from the name of the enum which can be used as alternative identifier (e.g. for persisting in a
 * database) and a display-formatted name for end-users.
 * <p/>
 * The display name can be a message code to be used in a message bundle when internationalization is a requirement, or it could simply be a string which is
 * displayed in a single language application.
 *
 * @see com.carbonfive.db.hibernate.usertypes.EnhancedEnumUserType
 */
public interface EnhancedEnum
{
    String getCode();

    String getDisplayName();
}