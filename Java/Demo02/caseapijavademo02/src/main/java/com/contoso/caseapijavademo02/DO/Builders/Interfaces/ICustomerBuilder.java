package com.contoso.caseapijavademo02.DO.Builders.Interfaces;

import java.util.UUID;
import com.contoso.caseapijavademo02.DO.CaseAttributes.Interfaces.*;

/**
 * Contact for builder to instantiate Customer in fluent manner.
 */
public interface ICustomerBuilder extends IEntityBuilder
{
    ICustomerBuilder addContact(IContact contact);
    ICustomer buildWithId(UUID id);

    @Override
    ICustomer build();
}