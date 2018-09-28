package com.contoso.caseapijavademo02.DO.Interfaces;

import com.contoso.caseapijavademo02.DO.ContactChannel;

public interface IContactBuilder extends IFluentBuilderBase
{
    IContactBuilder CreateNewContactOfLastName(String newLastName);
    IContactBuilder WithFirstName(String newFirstName);
    IContactBuilder WithEmail(String newEmail);
    IContactBuilder WithPhone(String newPhone);
    IContactBuilder WithPreferredContactChannelToBe(ContactChannel newPreferredContactChannel);
    IContactBuilder AsPrimaryContact();
}