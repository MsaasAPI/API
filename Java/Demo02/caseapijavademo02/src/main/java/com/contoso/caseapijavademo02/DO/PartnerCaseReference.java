package com.contoso.caseapijavademo02.DO;

import com.contoso.caseapijavademo02.DO.Interfaces.*;
import com.google.gson.*;
import com.google.gson.annotations.*;

public class PartnerCaseReference implements IPartnerCaseReference
{
    Gson gson;

    @Expose @SerializedName("PartnerCaseId") private String partnerCaseId = "";
    @Expose @SerializedName("PartnerCaseState") private String partnerCaseState = "";
    @Expose @SerializedName("PartnerAgentInformation") private IPartnerAgentInformation partnerAgentInformation;
    @Expose @SerializedName("PartnerName") private String partnerName = "";
    @Expose @SerializedName("PartnerId") private String partnerId = "";
    
    public PartnerCaseReference() {
        super();
    }

    public PartnerCaseReference(
            String newPartnerCaseId, 
            String newPartnerCaseState,
            IPartnerAgentInformation newPartnerAgentInformation,
            String newPartnerName,
            String newPartnerId) {
        super();

        partnerCaseId = newPartnerCaseId;
        partnerCaseState = newPartnerCaseState;
        partnerAgentInformation = newPartnerAgentInformation;
        partnerName = newPartnerName;
        partnerId = newPartnerId;
    }

    @Override
    public void importFromJson(String input) {

    }

    @Override
    public String outputToJson() {
        return null;
    }

    @Override
    public boolean isUnpopulated() {
        return partnerCaseId.trim().isEmpty() 
            && partnerCaseState.trim().isEmpty() 
            && partnerAgentInformation.isUnpopulated() 
            && partnerName.trim().isEmpty()
            && partnerId.trim().isEmpty();
        }
        
    @Override
    public void setPartnerCaseId(String newPartnerCaseId) {
        partnerCaseId = newPartnerCaseId;
    }
    
    @Override
    public String getPartnerCaseId() {
        return partnerCaseId;
    }

    @Override
    public void setPartnerCaseState(String newPartnerCaseState) {
        partnerCaseState = newPartnerCaseState;
    }

    @Override
    public String getPartnerCaseState() {
        return partnerCaseState;
    }

    @Override
    public void setPartnerAgentInformation(IPartnerAgentInformation newPartnerAgentInformation) {
        partnerAgentInformation = newPartnerAgentInformation;
    }

    @Override
    public IPartnerAgentInformation getPartnerAgentInformation() {
        return partnerAgentInformation;
    }

    @Override
    public void setPartnerName(String newPartnerName) {
        partnerName = newPartnerName;
    }

    @Override
    public String getPartnerName() {
        return partnerName;
    }

    @Override
    public void setPartnerId(String newPartnerId) {
        partnerId = newPartnerId;
    }

    @Override
    public String getPartnerId() {
        return partnerId;
    }
}