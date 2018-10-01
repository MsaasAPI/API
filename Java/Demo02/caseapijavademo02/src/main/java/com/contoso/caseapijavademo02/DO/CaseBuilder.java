package com.contoso.caseapijavademo02.DO;

import java.util.*;

import com.contoso.caseapijavademo02.DO.Interfaces.*;

public class CaseBuilder implements ICaseBuilder 
{
    private IEntityType currentEntity;
    private List<IContact> contacts;
    private List<INote> notes;
    private IContact contact;
    private ICustomer customer;
    private INote note;
    private IPartnerAgentInformation partnerAgentInformation;
    private IPartnerCaseReference partnerCaseReference;
    private String caseTitle;
    private String caseDescription;
    private String caseSupportCountry;
    private String caseSupportLanguage;
    private String contactFirstName;
    private String contactLastName;
    private String contactEmail;
    private String contactPhone;
    private IContactChannel contactPreferredContactChannel;
    private boolean contactIsPrimaryContact;
    private String partnerAgentInformationFirstName;
    private String partnerAgentInformationLastName;
    private String partnerAgentInformationEmail;
    private String partnerAgentInformationPhone;

    @Override
    public ICaseBuilder CreateCaseOfTitle(String newTitle) {
        currentEntity = EntityType.CASE;
        caseTitle = newTitle;
        return this;
    }

    @Override
    public ICaseBuilder AddCustomer() {
        currentEntity = EntityType.CUSTOMER;
        return this;
    }

    @Override
    public ICaseBuilder BuildThisCustomer() {
        customer = new Customer(contacts);
        return this;
    }

    @Override
    public ICaseBuilder AddContactOfLastName(String newLastName) {
        currentEntity = EntityType.CONTACT;
        contactLastName = newLastName;
        return this;
    }

    @Override
    public ICaseBuilder BuildThisContact() {
        contact = new Contact(contactFirstName, contactLastName, contactEmail, contactPhone, contactPreferredContactChannel, contactIsPrimaryContact);
        return this;
    }

    @Override
    public ICaseBuilder AddPartnerCaseReferenceOfPartnerCaseId(String newPartnerCaseId) {
        currentEntity = EntityType.PARTNER_CASE_REFERENCE;
        return this;
    }

    @Override
    public ICaseBuilder BuildThisPartnerCaseReference() {
        return this;
    }

    @Override
    public ICaseBuilder AddPartnerAgentInformationOfLastName(String newLastName) {
        currentEntity = EntityType.PARTNER_AGENT_INFORMATION;
        partnerAgentInformationLastName = newLastName;
        return this;
    }

    @Override
    public ICaseBuilder BuildThisPartnerAgentInformation() {
        partnerAgentInformation = new PartnerAgentInformation(partnerAgentInformationFirstName, partnerAgentInformationLastName, partnerAgentInformationEmail, partnerAgentInformationPhone);
        return this;
    }

    @Override
    public ICaseBuilder OfFirstName(String newFirstName) {
        return this;
    }

    @Override
    public ICaseBuilder OfEmail(String newEmail) {
        return this;
    }

    @Override
    public ICaseBuilder OfPhone(String newPhone) {
        return this;
    }

    @Override
    public ICaseBuilder OfPreferredContactChannel(IContactChannel newPreferredContactChannel) {
        return this;
    }

    @Override
	public ICaseBuilder AsPrimaryContact() {
		return this;
	}

    @Override
    public IEntityBase Build() {
        ICase theCase = new Case();
        return theCase;
    }
}