package com.contoso.caseapijavademo02.DO;

import com.contoso.caseapijavademo02.DO.Interfaces.*;
import com.google.gson.*;
import com.google.gson.annotations.*;

public class Contact implements IContact
{
    Gson gson;

    @Expose @SerializedName("FirstName") private String firstName = "";
    @Expose @SerializedName("LastName") private String lastName = "";
    @Expose @SerializedName("Email") private String email = "";
    @Expose @SerializedName("Phone") private String phone = "";
    @Expose @SerializedName("PreferredContactChannel") private IContactChannel preferredContactChannel;
    @Expose @SerializedName("IsPrimaryContact") private boolean isPrimaryContact = false;
 
    public Contact() {
        super();
    }

    // Purposely provision a package-private constructor (and this is the only constructor) so that only FluentBuilder under the same package can invoke this constructor.
    Contact(
            String newFirstName, 
            String newLastName,
            String newEmail,
            String newPhone,
            IContactChannel newPreferredContactChannel,
            boolean newIsPrimaryContact) {
        super();

        firstName = newFirstName;
        lastName = newLastName;
        email = newEmail;
        phone = newPhone;
        preferredContactChannel = newPreferredContactChannel;
        isPrimaryContact = newIsPrimaryContact;
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
        return lastName.trim().isEmpty() 
            && firstName.trim().isEmpty() 
            && email.trim().isEmpty() 
            && phone.trim().isEmpty();
    }

    @Override
    public void setLastName(String newLastName) {
        lastName = newLastName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setFirstName(String newFirstName) {
        firstName = newFirstName;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setEmail(String newEmail) {
        email = newEmail;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setPhone(String newPhone) {
        phone = newPhone;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public void setPreferredContactChannel(IContactChannel newPreference) {
        preferredContactChannel = newPreference;
    }

    @Override
    public IContactChannel getPreferredContactChannel() {
        return preferredContactChannel;
    }

    @Override
    public void setAsPrimaryContact() {
        isPrimaryContact = true;
    }

    @Override
    public boolean isPrimaryContact() {
        return isPrimaryContact;
    }

}