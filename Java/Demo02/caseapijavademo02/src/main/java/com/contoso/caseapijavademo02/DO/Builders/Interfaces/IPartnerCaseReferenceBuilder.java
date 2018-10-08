package com.contoso.caseapijavademo02.DO.Builders.Interfaces;

import java.util.UUID;
import com.contoso.caseapijavademo02.DO.CaseAttributes.Interfaces.IPartnerCaseReference;

/**
 * Contact for builder to instantiate PartnerCaseReference in fluent manner.
 * 
 * Populate fields for PartnerAgentInformation here since it's not entity.
 */
public interface IPartnerCaseReferenceBuilder extends IEntityBuilder
{
    IPartnerCaseReferenceBuilder addPartnerCaseState(String partnerCaseState);
    IPartnerCaseReferenceBuilder addPartnerCaseId(String partnerCaseId);
    IPartnerCaseReferenceBuilder addPartnerId(String partnerId);
    IPartnerCaseReferenceBuilder addPartnerName(String partnerName);
    IPartnerCaseReferenceBuilder addPartnerCaseUri(String partnerCaseUri);
    IPartnerCaseReferenceBuilder addPartnerAgentLastName(String partnerAgentLastName);
    IPartnerCaseReferenceBuilder addPartnerAgentFirstName(String partnerAgentFirstName);
    IPartnerCaseReferenceBuilder addPartnerAgentEmail(String partnerAgentEmail);
    IPartnerCaseReferenceBuilder addPartnerAgentPhone(String partnerAgentPhone);
    IPartnerCaseReference buildWithId(UUID id);

    @Override
    IPartnerCaseReference build();
}