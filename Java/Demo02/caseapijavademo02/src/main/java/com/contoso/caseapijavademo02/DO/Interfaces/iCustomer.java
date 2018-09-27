package com.contoso.caseapijavademo02.DO.Interfaces;

import java.util.List;

import com.contoso.caseapijavademo02.DO.Contact;

public interface ICustomer extends IEntityBase
{
    void setContacts(List<IContact> contacts);
    List<IContact> getContacts();
    List<IContact> findContactsByKeyword(String keyword);
}