package com.contoso.caseapijavademo02.DO.CaseAttributes.Interfaces;

import java.util.List;

/**
 * Contract for Case.
 */
public interface ICase extends IEntity
{
    List<ICustomer> getCustomers();
    List<INote> getNotes();
    List<IPartnerCaseReference> getPartnerCaseReferences();
    String getTitle();
    String getIssueDescription();
    String getState();
    String getAgentId();
    String getSeverity();
    String getSupportCountry();
    String getSupportLanguage();
    String getSupportTimeZone();
}