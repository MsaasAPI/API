package com.contoso.caseapijavademo02.DO.Interfaces;

public interface IAttributeGroup
{
    void importFromJson(String input);
    String outputToJson();
    boolean isUnpopulated();
}