package com.contoso.caseapijavademo02.BL.Interfaces;

import java.util.Map;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

public interface IScenarioConfigurator
{
    Map<String, String> getHeaders();
    HttpEntityEnclosingRequestBase getRequestVerb();
    String getUri();
    String getPayload();
    boolean getPublishToSelfFlag();
}