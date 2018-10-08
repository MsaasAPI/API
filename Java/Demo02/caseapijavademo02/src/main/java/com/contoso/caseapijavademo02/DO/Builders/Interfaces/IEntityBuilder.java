package com.contoso.caseapijavademo02.DO.Builders.Interfaces;

import com.contoso.caseapijavademo02.DO.CaseAttributes.Interfaces.IEntity;

/**
 * Serves as base for all builders used to instantiate case objects with identity fields (id or CaseNumber)
 */
interface IEntityBuilder
{
    IEntity build(); // return IEntity to ensure all builders are used to create entities only.
}