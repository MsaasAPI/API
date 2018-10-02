package com.contoso.caseapijavademo02.DO.Interfaces;

import com.contoso.caseapijavademo02.DO.ContactChannel;

public interface ICustomerBuilder extends IFluentBuilderBase
{
    ICustomerBuilder CreateCustomer();
    ICustomerBuilder AddContactOfLastName(String newLastName);
    ICustomerBuilder OfFirstName(String newFirstName);
    ICustomerBuilder OfEmail(String newEmail);
    ICustomerBuilder OfPhone(String newPhone);
    ICustomerBuilder OfPreferredContactChannel(IContactChannel newPreferredContactChannel);
    ICustomerBuilder AsPrimaryContact();
    ICustomerBuilder BuildThisContact();
}