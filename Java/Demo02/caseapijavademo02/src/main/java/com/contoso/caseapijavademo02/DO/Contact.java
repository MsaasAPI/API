package com.contoso.caseapijavademo02.DO;

import com.contoso.caseapijavademo02.DO.Interfaces.IContact;
import com.google.gson.*;
import com.google.gson.annotations.*;

public class Contact implements IContact
{

    @Override
    public void importFromJson(String input) {

    }

    @Override
    public String outputToJson() {
        return null;
    }

    @Override
    public boolean isUnpopulated() {
        return false;
    }

    @Override
    public void setLastName(String newLastName) {

    }

    @Override
    public String getLastName() {
        return null;
    }

    @Override
    public void setFirstName(String newFirstName) {

    }

    @Override
    public String getFirstName() {
        return null;
    }

    @Override
    public void setEmail(String newEmail) {

    }

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public void setPhone(String newPhone) {

    }

    @Override
    public String getPhone() {
        return null;
    }

    @Override
    public void setPreferredContactChannel(String newPreference) {

    }

    @Override
    public String getPreferredContactChannel() {
        return null;
    }

    @Override
    public void setAsPrimaryContact() {

    }

    @Override
    public boolean isPrimaryContact() {
        return false;
    }

}