package com.contoso.caseapijavademo02.DO.CaseAttributes.Interfaces;

/**
 * Serves as base for all case objects with identity field (CaseNumber or id).
 */
public interface IEntity
{
    Object getId(); // Object instead of UUID because Case only takes String CaseNumber
}