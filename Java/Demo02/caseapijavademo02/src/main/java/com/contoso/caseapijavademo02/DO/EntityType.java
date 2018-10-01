package com.contoso.caseapijavademo02.DO;

import com.contoso.caseapijavademo02.DO.Interfaces.IEntityType;

public enum EntityType implements IEntityType {
    CASE,
    CUSTOMER,
    CONTACT,
    NOTE,
    PARTNER_CASE_REFERENCE,
    PARTNER_AGENT_INFORMATION
}
