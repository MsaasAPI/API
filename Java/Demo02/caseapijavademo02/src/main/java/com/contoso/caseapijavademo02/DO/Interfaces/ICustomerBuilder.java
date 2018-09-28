package com.contoso.caseapijavademo02.DO.Interfaces;

import java.util.List;

public interface ICustomerBuilder extends IFluentBuilderBase
{
    ICustomerBuilder CreateCustomer();
    ICustomerBuilder WithAdditionalContact(IContact newContact);
    ICustomerBuilder WithAdditionalContacts(List<IContact> newContacts);
}