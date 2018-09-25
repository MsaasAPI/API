package com.contoso.caseapijavademo02.DO.Interfaces;

public interface INote extends IEntityBase
{
    void setNote(String noteInput);
    String getNote();
}