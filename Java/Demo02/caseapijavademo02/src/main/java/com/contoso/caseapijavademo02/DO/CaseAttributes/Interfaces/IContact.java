package com.contoso.caseapijavademo02.DO.CaseAttributes.Interfaces;

/**
 * Contract for Case Customer Contact.
 *
 * Used within Customer entity to hold contact info.
 * The major difference from IPartnerAgentInformation is that it is entity with id, besides extra fields.
 */
public interface IContact extends IPublicProfile, IEntity
{
    IContactChannel getPreferredContactChannel();
    boolean isPrimaryContact();
}