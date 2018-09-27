package com.contoso.caseapijavademo02.DO;

import java.util.*;

import com.contoso.caseapijavademo02.DO.Interfaces.*;
import com.google.gson.Gson;
import com.google.gson.annotations.*;

public class Customer implements ICustomer 
{
    Gson gson;

    @Expose @SerializedName("Contacts") private List<IContact> contacts;
    
    public Customer() {
        super();
    }

    public Customer(List<IContact> newContacts) {
        super();
        contacts = newContacts;
    }

    public Customer(IContact newContact) {
        super();
        contacts = new ArrayList<IContact>();
        contacts.add(newContact);
    }

    @Override
    public void importFromJson(String input) {

    }

    @Override
    public String outputToJson() {
        return null;
    }

    @Override
    public boolean isUnpopulated() {
        if(contacts == null)
            return true;

        // it is NOT unpopulated if any contact is populated
        for(IContact c : contacts) {
            if (!c.isUnpopulated())
                return false;
        }
        
        return true;
    }

    @Override
    public void setContacts(List<IContact> newContacts) {
        contacts = newContacts;
    }

    @Override
    public List<IContact> getContacts() {
        return contacts;
    }

    @Override
    public List<IContact> findContactsByKeyword(String keyword) {
        List<IContact> matchingContacts = new ArrayList<IContact>();
        String cappedKeyword = keyword.toUpperCase();

        // Return all contacts containing keyword in major attributes. Case insensitive.
        for(IContact c : contacts){
            if (c.getFirstName().toUpperCase().contains(cappedKeyword)
                || c.getLastName().toUpperCase().contains(cappedKeyword)
                || c.getEmail().toUpperCase().contains(cappedKeyword)
                || c.getPhone().toUpperCase().contains(cappedKeyword))
                matchingContacts.add(c);
        }

        return matchingContacts;
	}
}