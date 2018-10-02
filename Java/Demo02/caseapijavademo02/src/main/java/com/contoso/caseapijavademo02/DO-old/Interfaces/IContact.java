package com.contoso.caseapijavademo02.DO.Interfaces;

import com.contoso.caseapijavademo02.DO.Interfaces.*;

public interface IContact extends IPersonProfile
{
    void setPreferredContactChannel(IContactChannel newPreference);
    IContactChannel getPreferredContactChannel();

    void setAsPrimaryContact();
    boolean isPrimaryContact();
}