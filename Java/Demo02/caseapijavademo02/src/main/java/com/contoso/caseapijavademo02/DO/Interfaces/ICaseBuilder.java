package com.contoso.caseapijavademo02.DO.Interfaces;

import java.util.List;

public interface ICaseBuilder extends IFluentBuilderBase
{
    ICaseBuilder CreateCaseOfTitle(String newTitle);
    ICaseBuilder AddCustomer();
    ICaseBuilder BuildThisCustomer();
    ICaseBuilder AddContactOfLastName(String newLastName);
    ICaseBuilder BuildThisContact();
    ICaseBuilder AddPartnerCaseReferenceOfPartnerCaseId(String newPartnerCaseId);
    ICaseBuilder BuildThisPartnerCaseReference();
    ICaseBuilder AddPartnerAgentInformationOfLastName(String newLastName);
    ICaseBuilder BuildThisPartnerAgentInformation();
    ICaseBuilder OfFirstName(String newFirstName);
    ICaseBuilder OfEmail(String newEmail);
    ICaseBuilder OfPhone(String newPhone);
    ICaseBuilder OfPreferredContactChannel(IContactChannel newPreferredContactChannel);
    ICaseBuilder AsPrimaryContact();
}