package com.contoso.caseapijavademo02.DO;

import com.contoso.caseapijavademo02.DO.Interfaces.*;

public class ContactBuilder implements IContactBuilder 
{
    private String lastName;
    private String firstName;
    private String email;
    private String phone;
    private IContactChannel preferredContactChannel;
    private boolean isPrimaryContact = false;

    @Override
    public IContactBuilder CreateContactOfLastName(String newLastName) {
        lastName = newLastName;
        return this;
    }

    @Override
    public IContactBuilder WithFirstName(String newFirstName) {
        firstName = newFirstName;
        return this;
    }

    @Override
    public IContactBuilder WithEmail(String newEmail) {
        email = newEmail;
        return this;
    }

    @Override
    public IContactBuilder WithPhone(String newPhone) {
        phone = newPhone;
        return this;
    }

    @Override
    public IContactBuilder WithPreferredContactChannelToBe(IContactChannel newPreferredContactChannel) {
        preferredContactChannel = newPreferredContactChannel;
        return this;
    }

    @Override
    public IContactBuilder AsPrimaryContact() {
        isPrimaryContact = true;
        return this;
	}

    @Override
    public IEntityBase Build() {
        return new Contact(firstName, lastName, email, phone, preferredContactChannel, isPrimaryContact);
    }
}