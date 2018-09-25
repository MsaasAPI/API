package com.contoso.caseapijavademo02.DO.Interfaces;

public interface IContact
{
    void importFromJson(String input);
    String outputToJson();
}