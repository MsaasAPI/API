package com.contoso.caseapijavademo02.DL.Interfaces;

/**
 * Read JSON string from storage. JSON deserialization defers to BO.
 */
public interface IJsonReader extends IRepositoryAccessor
{
    IJsonReader get(String fileName);
    IJsonReader at(String path);
    String execute();
}