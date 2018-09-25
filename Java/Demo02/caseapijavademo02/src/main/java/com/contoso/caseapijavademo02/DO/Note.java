package com.contoso.caseapijavademo02.DO;

import com.contoso.caseapijavademo02.DO.Interfaces.INote;

class Note implements INote
{
    String noteContent = "";

    @Override
    public void importFromJson(String input) {

    }

    @Override
    public String outputToJson() {
        return null;
    }

    @Override
    public boolean isUnpopulated() {
        return this.noteContent.trim().isEmpty();
    }

    @Override
    public void setNote(String noteInput) {
        this.noteContent = noteInput;
    }

    @Override
    public String getNote() {
        return null;
    }
} 