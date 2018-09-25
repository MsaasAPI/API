package com.contoso.caseapijavademo02.DO;

import com.contoso.caseapijavademo02.DO.Interfaces.INote;
import com.google.gson.*;

class Note implements INote
{
    Gson gson;
    String noteContent = "";

    public Note() {
        super();
        GsonBuilder builder = new GsonBuilder();
        gson = builder.create();
    }

    @Override
    public void importFromJson(String input) {
        this.noteContent = gson.fromJson(input, String.class);
    }

    @Override
    public String outputToJson() {
        return gson.toJson(this.noteContent);
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
        return this.noteContent;
    }
} 