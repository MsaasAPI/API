package com.contoso.caseapijavademo02.DO.Builders.Interfaces;

import java.util.UUID;
import com.contoso.caseapijavademo02.DO.CaseAttributes.Interfaces.*;

public interface ICustomerBuilder extends IEntityBuilder
{
    ICustomerBuilder addContact(IContact contact);
    ICustomer buildWithId(UUID id);

    @Override
    ICustomer build();
}