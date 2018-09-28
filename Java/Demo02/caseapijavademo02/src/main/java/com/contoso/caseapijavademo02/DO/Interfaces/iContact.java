package com.contoso.caseapijavademo02.DO.Interfaces;

import com.contoso.caseapijavademo02.DO.ContactChannel;

public interface IContact extends IEntityBase
{
    void setLastName(String newLastName);
    String getLastName();

    void setFirstName(String newFirstName);
    String getFirstName();

    void setEmail(String newEmail);
    String getEmail();

    void setPhone(String newPhone);
    String getPhone();

    void setPreferredContactChannel(IContactChannel newPreference);
    IContactChannel getPreferredContactChannel();

    void setAsPrimaryContact();
    boolean isPrimaryContact();
}