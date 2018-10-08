package com.contoso.caseapijavademo02.DO.Builders.Interfaces;

import com.contoso.caseapijavademo02.DO.CaseAttributes.Interfaces.*;

/**
 * Contact for builder to instantiate Case in fluent manner.
 */
public interface ICaseBuilder extends IEntityBuilder
{
    ICaseBuilder addCustomer(ICustomer customer);
    ICaseBuilder addNote(INote note);
    ICaseBuilder addPartnerCaseReference(IPartnerCaseReference partnerCaseReference);
    ICaseBuilder addCaseNumber(String caseNumber);
    ICaseBuilder addTitle(String title);
    ICaseBuilder addIssueDescription(String issueDescription);
    ICaseBuilder addState(String state);
    ICaseBuilder addAgentId(String agentId);
    ICaseBuilder addSupportCountry(String supportCountry);
    ICaseBuilder addSupportLanguage(String supportLanguage);
    ICaseBuilder addSupportTimeZone(String supportTimeZone);

    @Override
    ICase build();
}