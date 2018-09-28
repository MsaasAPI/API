package com.contoso.caseapijavademo02.DO;

import java.util.*;
import com.contoso.caseapijavademo02.DO.Interfaces.*;

public class CustomerBuilder implements ICustomerBuilder
{
    private List<IContact> contacts;

    public CustomerBuilder() {
        super();
        contacts = new ArrayList<IContact>();
    }

    @Override
    public ICustomerBuilder CreateCustomer() {
        return this;
    }

    @Override
    public ICustomerBuilder WithAdditionalContact(IContact newContact) {
        contacts.add(newContact);
        return this;
    }

	@Override
	public ICustomerBuilder WithAdditionalContacts(List<IContact> newContacts) {
        contacts.addAll(newContacts);
		return this;
	}

    @Override
    public IEntityBase Build() {
        ICustomer customer = new Customer(contacts);
        contacts = new ArrayList<IContact>();
        return customer;
    }
}