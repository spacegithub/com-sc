package com.sc.utils.web;


public enum BooleanType
{
    False("0",Boolean.FALSE),
    True("1",Boolean.TRUE);
    private final String type;
    private final Boolean bool;

    BooleanType(String type, Boolean bool)
    {
        this.type = type;
        this.bool = bool;
    }

    public static BooleanType getBooleanType(String type)
    {
        for (BooleanType booleanType : BooleanType.values())
        {
            if (booleanType.getType().equals(type)){
                return booleanType;
            }
        }
        return null;
    }

    public Boolean getBool()
    {
        return bool;
    }

    public String getType()
    {
        return type;
    }
}
