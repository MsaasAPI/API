package com.contoso.caseapijavademo02.DO.Interfaces;

public interface IPartnerCaseReference
{
    void importFromJson(String input);
    String outputToJson();
}