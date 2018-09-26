package com.contoso.caseapijavademo02.DO.Interfaces;

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

    void setPreferredContactChannel(String newPreference);
    String getPreferredContactChannel();

    void setAsPrimaryContact();
    boolean isPrimaryContact();
}