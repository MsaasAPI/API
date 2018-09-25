package com.contoso.caseapijavademo02.DO.Interfaces;

public interface ICustomer
{
    void importFromJson(String input);
    String outputToJson();
}