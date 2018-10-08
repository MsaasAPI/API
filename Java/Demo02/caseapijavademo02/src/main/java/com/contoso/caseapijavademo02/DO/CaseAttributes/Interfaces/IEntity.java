package com.contoso.caseapijavademo02.DO.CaseAttributes.Interfaces;

/**
 * Serves as base for all class objects with identity field (CaseNumber or id).
 */
interface IEntity
{
    Object getId(); // Object instead of UUID because Case only takes String CaseNumber
}