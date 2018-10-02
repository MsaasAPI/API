package com.contoso.caseapijavademo02.DO;

import java.util.*;
import com.contoso.caseapijavademo02.DO.Interfaces.*;

public class CustomerBuilder implements ICustomerBuilder
{
    private List<IContact> contacts;
    private String contactLastName;
    private String contactFirstName;
    private String contactEmail;
    private String contactPhone;
    private IContactChannel contactPreferredContactChannel;
    private boolean contactIsPrimaryContact;

    public CustomerBuilder() {
        super();
        resetAllFields();
    }

    @Override
    public ICustomerBuilder CreateCustomer() {
        return this;
    }

    @Override
    public ICustomerBuilder AddContactOfLastName(String newLastName) {
        contactLastName = newLastName;
        return this;
    }

    @Override
    public ICustomerBuilder OfFirstName(String newFirstName) {
        contactFirstName = newFirstName;
        return this;
    }

    @Override
    public ICustomerBuilder OfEmail(String newEmail) {
        contactEmail = newEmail;
        return this;
    }

    @Override
    public ICustomerBuilder OfPhone(String newPhone) {
        contactPhone = newPhone;
        return this;
    }

    @Override
    public ICustomerBuilder OfPreferredContactChannel(IContactChannel newPreferredContactChannel) {
        contactPreferredContactChannel = newPreferredContactChannel;
        return this;
    }

    @Override
    public ICustomerBuilder AsPrimaryContact() {
        contactIsPrimaryContact = true;
        return this;
    }

    @Override
    public ICustomerBuilder BuildThisContact() {
        IContact contact = new Contact(contactFirstName, contactLastName, contactEmail, contactPhone, contactPreferredContactChannel, contactIsPrimaryContact);
        contacts.add(contact);
        resetContactFields();
        return this;
    }

    @Override
    public IEntityBase Build() {
        ICustomer customer = new Customer(contacts);
        resetAllFields();
        return customer;
    }

    private void resetContactFields(){
        contactLastName = "";
        contactFirstName = "";
        contactEmail = "";
        contactPhone = "";
        contactPreferredContactChannel = ContactChannel.NONE;
        contactIsPrimaryContact = false;
    }

    private void resetAllFields(){
        contacts = new ArrayList<IContact>();
        resetContactFields();
    }
}