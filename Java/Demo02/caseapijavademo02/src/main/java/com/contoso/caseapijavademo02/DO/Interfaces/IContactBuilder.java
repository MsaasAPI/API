package com.contoso.caseapijavademo02.DO.Interfaces;

public interface IContactBuilder extends IFluentBuilderBase
{
    IContactBuilder CreateContactOfLastName(String newLastName);
    IContactBuilder OfFirstName(String newFirstName);
    IContactBuilder OfEmail(String newEmail);
    IContactBuilder OfPhone(String newPhone);
    IContactBuilder OfPreferredContactChannel(IContactChannel newPreferredContactChannel);
    IContactBuilder AsPrimaryContact();
}