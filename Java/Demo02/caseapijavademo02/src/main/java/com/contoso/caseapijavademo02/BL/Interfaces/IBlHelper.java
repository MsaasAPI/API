package com.contoso.caseapijavademo02.BL.Interfaces;

import org.apache.http.HttpResponse;

/**
 * Contract for general-purpose Business Layer helper object.
 */
public interface IBlHelper
{
    String getAccessToken();
    String getLogName();
    String parseResponseBody(HttpResponse response);
}