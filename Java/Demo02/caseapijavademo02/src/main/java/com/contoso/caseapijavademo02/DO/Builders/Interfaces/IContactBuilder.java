package com.contoso.caseapijavademo02.DO.Builders.Interfaces;

import java.util.UUID;
import com.contoso.caseapijavademo02.DO.CaseAttributes.Interfaces.*;

public interface IContactBuilder extends IEntityBuilder
{
    IContactBuilder addLastName(String partnerAgentLastName);
    IContactBuilder addFirstName(String partnerAgentFirstName);
    IContactBuilder addEmail(String partnerAgentEmail);
    IContactBuilder addPhone(String partnerAgentPhone);
    IContactBuilder setPreferredContactChannel(IContactChannel contactChannel);
    IContactBuilder setAsPrimaryContact();
    IContact buildWithId(UUID id);

    @Override
    IContact build();
}