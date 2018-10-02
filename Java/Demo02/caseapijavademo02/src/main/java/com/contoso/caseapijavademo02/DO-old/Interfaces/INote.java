package com.contoso.caseapijavademo02.DO.Interfaces;

public interface INote extends IEntityBase
{
    void setContent(String newContent);
    String getContent();
}