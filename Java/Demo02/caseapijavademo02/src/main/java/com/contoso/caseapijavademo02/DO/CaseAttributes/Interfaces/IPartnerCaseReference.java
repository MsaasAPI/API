package com.contoso.caseapijavademo02.DO.CaseAttributes.Interfaces;

/**
 * Contract for Case PartnerCaseReference.
 */
public interface IPartnerCaseReference extends IEntity
{
    IPartnerAgentInformation getPartnerAgentInformation();
    String getPartnerCaseState();
    String getPartnerCaseId();
    String getPartnerId();
    String getPartnerName();
    String getPartnerCaseUri();
}