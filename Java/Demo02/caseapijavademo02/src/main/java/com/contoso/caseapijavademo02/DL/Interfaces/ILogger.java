package com.contoso.caseapijavademo02.DL.Interfaces;

/**
 * Write/Update logs to storage.
 */
public interface ILogger extends IRepositoryAccessor
{
    ILogger upsert(String item);
    ILogger as(String entryName);
    ILogger at(String path);
    void execute();
}