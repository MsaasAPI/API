package com.contoso.caseapijavademo02.DO;

import com.contoso.caseapijavademo02.DO.Interfaces.*;

public class ContactBuilder implements IContactBuilder 
{
    private String lastName;
    private String firstName;
    private String email;
    private String phone;
    private IContactChannel preferredContactChannel;
    private boolean isPrimaryContact;

    public ContactBuilder() {
        super();
        resetFields();
    }

    @Override
    public IContactBuilder CreateContactOfLastName(String newLastName) {
        lastName = newLastName;
        return this;
    }

    @Override
    public IContactBuilder OfFirstName(String newFirstName) {
        firstName = newFirstName;
        return this;
    }

    @Override
    public IContactBuilder OfEmail(String newEmail) {
        email = newEmail;
        return this;
    }

    @Override
    public IContactBuilder OfPhone(String newPhone) {
        phone = newPhone;
        return this;
    }

    @Override
    public IContactBuilder OfPreferredContactChannel(IContactChannel newPreferredContactChannel) {
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
        IContact contact = new Contact(firstName, lastName, email, phone, preferredContactChannel, isPrimaryContact);
        resetFields();
        return contact;
    }

    private void resetFields(){
        lastName = "";
        firstName = "";
        email = "";
        phone = "";
        preferredContactChannel = ContactChannel.NONE;
        isPrimaryContact = false;
    }
}