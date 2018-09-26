package com.contoso.caseapijavademo02.DO;

import com.contoso.caseapijavademo02.DO.Interfaces.INote;
import com.google.gson.*;
import com.google.gson.annotations.*;

public class Note implements INote
{
    Gson gson;
    @Expose String content = ""; // Only field to be serialized into JSON

    public Note() {
        super();

        gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
    }

    @Override
    public void importFromJson(String input) {
        content = gson.fromJson(input, String.class);
    }

    @Override
    public String outputToJson() {
        return gson.toJson(this.content);
    }

    @Override
    public boolean isUnpopulated() {
        return this.content.trim().isEmpty();
    }

    @Override
    public void setNote(String noteInput) {
        this.content = noteInput;
    }

    @Override
    public String getNote() {
        return this.content;
    }
} 