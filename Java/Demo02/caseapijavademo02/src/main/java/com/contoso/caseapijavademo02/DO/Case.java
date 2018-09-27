package com.contoso.caseapijavademo02.DO;

import java.util.*;

import com.contoso.caseapijavademo02.DO.Interfaces.*;
import com.google.gson.annotations.*;

public class Case implements ICase
{
    @Expose @SerializedName("Customer") private ICustomer customer;
    @Expose @SerializedName("Notes") private List<INote> notes;
    @Expose @SerializedName("PartnerCaseReferences") private List<IPartnerCaseReference> partnerCaseReferences;

    public Case() {
        super();
    }

    public Case(
            ICustomer newCustomer, 
            List<INote> newNotes, 
            List<IPartnerCaseReference> newPartnerCaseReferences) {
        super();
        customer = newCustomer;
        notes = newNotes;
        partnerCaseReferences = newPartnerCaseReferences;
    }

    public Case(
        ICustomer newCustomer, 
        INote newNote, 
        IPartnerCaseReference newPartnerCaseReference) {
        super();

        notes = new ArrayList<INote>();
        notes.add(newNote);

        partnerCaseReferences = new ArrayList<IPartnerCaseReference>();
        partnerCaseReferences.add(newPartnerCaseReference);
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
        if (!customer.isUnpopulated())
            return false;

        for(INote n : notes){
            if(!n.isUnpopulated())
                return false;
        }

        for(IPartnerCaseReference p : partnerCaseReferences){
            if(!p.isUnpopulated())
                return false;
        }

        return true;
    }

    @Override
    public void setCustomer(ICustomer newCustomer) {
        customer = newCustomer;
    }

    @Override
    public ICustomer getCustomer() {
        return customer;
    }

    @Override
    public void setNotes(List<INote> newNotes) {
        notes = newNotes;
    }

    @Override
    public List<INote> getNotes() {
        return notes;
    }
    
    @Override
    public List<INote> findNotesByKeyword(String keyword) {
        List<INote> matchingNotes = new ArrayList<INote>();
        String cappedKeyword = keyword.toUpperCase();

        // Return all notes containing keyword in major attributes. Case insensitive.
        for(INote n : notes){
            if (n.getContent().toUpperCase().contains(cappedKeyword))
                matchingNotes.add(n);
        }

        return matchingNotes;
    }

    @Override
    public void setPartnerCaseReferences(List<IPartnerCaseReference> newPartnerCaseReferences) {
        partnerCaseReferences = newPartnerCaseReferences;
    }

    @Override
    public List<IPartnerCaseReference> getPartnerCaseReferences() {
        return partnerCaseReferences;
    }

    @Override
    public List<IPartnerCaseReference> findPartnerCaseReferencesByKeyword(String keyword) {
        return null;
    }
}