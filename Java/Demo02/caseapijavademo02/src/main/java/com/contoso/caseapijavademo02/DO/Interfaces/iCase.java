package com.contoso.caseapijavademo02.DO.Interfaces;

import java.util.List;

public interface ICase extends IEntityBase
{
    void setCustomer(ICustomer newCustomer);
    ICustomer getCustomer();
    void setNotes(List<INote> newNotes);
    List<INote> getNotes();
    List<INote> findNotesByKeyword(String keyword);
    void setPartnerCaseReferences(List<IPartnerCaseReference> newPartnerCaseReferences);
    List<IPartnerCaseReference> getPartnerCaseReferences();
    List<IPartnerCaseReference> findPartnerCaseReferencesByKeyword(String keyword);
}