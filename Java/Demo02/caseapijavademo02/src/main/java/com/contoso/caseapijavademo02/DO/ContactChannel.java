package com.contoso.caseapijavademo02.DO;

import com.contoso.caseapijavademo02.DO.Interfaces.IContactChannel;

public enum ContactChannel implements IContactChannel
{
    NONE,
    EMAIL,
    PHONE
}