package com.contoso.caseapijavademo02.DO;

import com.contoso.caseapijavademo02.DO.Interfaces.INote;
import com.google.gson.*;
import com.google.gson.annotations.*;

public class Note implements INote
{
    Gson gson;

    @Expose @SerializedName("Content") private String content = ""; // Only field to be serialized into JSON

    public Note() {
        super();

        gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
    }

    public Note(String newContent) {
        super();
        content = newContent;
    }

    @Override
    public void importFromJson(String input) {
        content = gson.fromJson(input, String.class);
    }

    @Override
    public String outputToJson() {
        return gson.toJson(content);
    }

    @Override
    public boolean isUnpopulated() {
        return content.trim().isEmpty();
    }

    @Override
    public void setNote(String newContent) {
        content = newContent;
    }

    @Override
    public String getNote() {
        return content;
    }
} 