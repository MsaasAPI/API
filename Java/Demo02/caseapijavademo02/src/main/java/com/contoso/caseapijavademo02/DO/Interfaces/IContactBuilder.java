package com.contoso.caseapijavademo02.DO.Interfaces;

public interface IContactBuilder extends IFluentBuilderBase
{
    IContactBuilder CreateContactOfLastName(String newLastName);
    IContactBuilder WithFirstName(String newFirstName);
    IContactBuilder WithEmail(String newEmail);
    IContactBuilder WithPhone(String newPhone);
    IContactBuilder WithPreferredContactChannelToBe(IContactChannel newPreferredContactChannel);
    IContactBuilder AsPrimaryContact();
}