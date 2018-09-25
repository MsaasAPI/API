package com.contoso.caseapijavademo02.DO.Interfaces;

interface IPartnerCaseReference
{
    void importFromJson(String input);
    String outputToJson();
}