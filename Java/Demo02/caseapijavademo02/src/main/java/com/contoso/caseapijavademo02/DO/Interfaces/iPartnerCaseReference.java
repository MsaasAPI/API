package com.contoso.caseapijavademo02.DO.Interfaces;

public interface IPartnerCaseReference extends IEntityBase
{
    public void setPartnerCaseId(String newPartnerCaseId);
    public String getPartnerCaseId();
    public void setPartnerCaseState(String newPartnerCaseState);
    public String getPartnerCaseState();
    public void setPartnerAgentInformation(IPartnerAgentInformation newPartnerAgentInformation);
    public IPartnerAgentInformation getPartnerAgentInformation();
    public void setPartnerName(String newPartnerName);
    public String getPartnerName();
    public void setPartnerId(String newPartnerId);
    public String getPartnerId();
}