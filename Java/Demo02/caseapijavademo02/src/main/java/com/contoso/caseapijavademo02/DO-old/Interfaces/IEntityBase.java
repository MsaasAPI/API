package com.contoso.caseapijavademo02.DO.Interfaces;

public interface IEntityBase
{
    void importFromJson(String input);
    String outputToJson();
    boolean isUnpopulated();
}