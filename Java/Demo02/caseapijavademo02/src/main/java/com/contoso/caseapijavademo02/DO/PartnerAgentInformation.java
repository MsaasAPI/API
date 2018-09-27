package com.contoso.caseapijavademo02.DO;

import com.contoso.caseapijavademo02.DO.Interfaces.IPartnerAgentInformation;
import com.google.gson.*;
import com.google.gson.annotations.*;

public class PartnerAgentInformation implements IPartnerAgentInformation
{
    Gson gson;

    @Expose @SerializedName("FirstName") private String firstName = "";
    @Expose @SerializedName("LastName") private String lastName = "";
    @Expose @SerializedName("Email") private String email = "";
    @Expose @SerializedName("Phone") private String phone = "";
    
    public PartnerAgentInformation() {
        super();
    }

    public PartnerAgentInformation(
            String newFirstName, 
            String newLastName,
            String newEmail,
            String newPhone){
        super();

        firstName = newFirstName;
        lastName = newLastName;
        email = newEmail;
        phone = newPhone;
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
}