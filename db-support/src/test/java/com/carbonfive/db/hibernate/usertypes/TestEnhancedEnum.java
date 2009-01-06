package com.carbonfive.db.hibernate.usertypes;

public enum TestEnhancedEnum implements EnhancedEnum
{
    CARNIVORE("CARN", "Carnivore"), OMNIVORE("OMNI", "Omnivore"), HERBAVORE("HERB", "Herbavore");

    private final String code;
    private final String displayName;

    private TestEnhancedEnum(String code, String displayName)
    {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode()
    {
        return code;
    }

    public String getDisplayName()
    {
        return displayName;
    }
}
