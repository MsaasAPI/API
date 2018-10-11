package com.contoso.caseapijavademo02.BL.Interfaces;

public interface IScenarioRunner
{
    void provisionRequest();
    void sendRequest();
    void cacheTransaction();
    void logTransaction();
}