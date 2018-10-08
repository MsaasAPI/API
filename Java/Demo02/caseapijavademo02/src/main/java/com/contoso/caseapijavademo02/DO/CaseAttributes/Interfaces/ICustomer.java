package com.contoso.caseapijavademo02.DO.CaseAttributes.Interfaces;

import java.util.List;

/**
 * Contract for Case Customer.
 */
public interface ICustomer extends IEntity
{
    List<IContact> getContacts();
}