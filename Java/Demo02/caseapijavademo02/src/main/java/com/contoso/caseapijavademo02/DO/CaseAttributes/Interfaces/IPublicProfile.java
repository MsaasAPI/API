package com.contoso.caseapijavademo02.DO.CaseAttributes.Interfaces;

/**
 * Serves as base for contacts, like Contact and PartnerAgentInformation.
 */
interface IPublicProfile
{
    String getLastName();
    String getFirstName();
    String getEmail();
    String getPhone();
}