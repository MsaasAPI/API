package com.contoso.caseapijavademo01;

import java.io.*;
import java.net.URI;
import java.security.*;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

import com.microsoft.aad.adal4j.*;
import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.log4j.BasicConfigurator;
import org.json.*;

/**
 * Hello world!
 *
 */
public class Program {
    /***** USER CONFIGURABLE FIELDS *****/
    private static final String PARTNER_NAME = "";
    private static final String LOG_FOLDER = "Logs/";
    private static final Integer MAX_RETRIES = 3;
    
    /***** CREDENTIALS *****/
    private static final String CLIENT_ID_CERT = "";
    private static final String AUTHORITY = "";
    private static final String RESOURCE = "";
    private static final String KEY_STORE_NAME = "";
    private static final String ALIAS_IN_KEY_STORE = "";
    private static final String KEY_STORE_PASSWORD = "";
    
    /***** OTHER CONSTANTS & STATIC FIELDS *****/
    private static final String BASE_URI = "";
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy_MMdd_HHmm");
    private static final String LOG_NAME = "log_" + DATE_FORMATTER.format(new Date()) + ".txt";
    private static final Integer TIME_DELAY = 1000;
    private static final Map<Attr, String> _payloads = new HashMap<Attr, String>(); // Contains data (resources and payloads) required per https://msegksdev.trafficmanager.net/swagger/ui/index?sapId=2e12ea69-0884-9b66-8431-13094bcbe81e#/Cases.
    private static final Map<Attr, String> _caches = new HashMap<Attr, String>(); // Contains HTTP request and repsonse data associated with the most recent transaction. Subject to overwrite by subsequent transactions.
    private static String _token = "";

    public static void main(String[] args) {
        BasicConfigurator.configure();
        SetPayloads();
        GetRequestToken();

        // //--Scenario 100 Partner creates a new case. Empty string parameter is placeholder for caseNumber
        // Run(Api.SCENARIO100_CREATE_CASE, "", _payloads.get(Attr.CASE_PAYLOAD));        

        // //--Scenario 110 Partner assigns or reassigns an agent
        // Run(Api.SCENARIO110_ASSIGN_REASSIGN_PARTNER_CASE_REFERENCES_AGENT, _payloads.get(Attr.CASE_NUMBER), _payloads.get(Attr.PARTNER_CASE_REFERENCES_ID_GUID), _payloads.get(Attr.PARTNER_CASE_REFERENCES_AGENT_PAYLOAD));

        // //--Scenario 115 Partner changes partner's case state
        // Run(Api.SCENARIO115_CHANGE_PARTNER_CASE_REFERENCES_PARTNER_CASE_STATE, _payloads.get(Attr.CASE_NUMBER), _payloads.get(Attr.PARTNER_CASE_REFERENCES_ID_GUID), _payloads.get(Attr.PARTNER_CASE_REFERENCES_CASE_STATE_PAYLOAD)); 

        // //--Scenario 120 Partner creates a new note
        // Run(Api.SCENARIO120_CREATE_NOTE, _payloads.get(Attr.CASE_NUMBER), _payloads.get(Attr.NOTE_PAYLOAD)); 

        // //--Scenario 140 Partner creates a new contact 
        // Run(Api.SCENARIO140_CREATE_CONTACT, _payloads.get(Attr.CASE_NUMBER), _payloads.get(Attr.CUSTOMER_ID_GUID), _payloads.get(Attr.CONTACT_PAYLOAD)); 

        // //--Scenario 145 Partner updates a contact 
        // Run(Api.SCENARIO145_UPDATE_CONTACT, _payloads.get(Attr.CASE_NUMBER), _payloads.get(Attr.CUSTOMER_ID_GUID), _payloads.get(Attr.CONTACT_ID_GUID), _payloads.get(Attr.CONTACT_PAYLOAD)); 

        // //--Scenario 190 Partner closes a case 
        // Run(Api.SCENARIO190_CLOSE_CASE, _payloads.get(Attr.CASE_NUMBER), _payloads.get(Attr.PARTNER_CASE_REFERENCES_ID_GUID), _payloads.get(Attr.PARTNER_CASE_REFERENCES_CASE_STATE_CLOSURE_PAYLOAD)); 

        // //--Scenario 200 Partner gets a case
        Run(Api.SCENARIO200_GET_CASE, _payloads.get(Attr.CASE_NUMBER)); 

        //--Demo
        // Run(Api.DEMO, "");
        System.exit(0);
    }

    /**
     * Acquires Bearer Token (valid for one hour) from the authorization server per https://tools.ietf.org/html/rfc6750.
     * <p>Per RFC 6750, "Any party in possession of a bearer token (a "bearer") can use it to get access to the associated resources (without demonstrating possession of a cryptographic key). To prevent misuse, bearer tokens need to be protected from disclosure in storage and in transport."
     * <p>Therefore, the following security recommendations are essential to safe-guard the bear tokens:
     * <p>- Validate TLS certificate chains to avoid DNS hijacking.
     * <p>- Always use TLS (https) to avoid token expossure to numerous attacks.
     * <p>- Don't store bearer tokens in cookies to avoid Cross-site request forgery.
     * <p>- Don't pass bearer tokens in page URLs (ie, as query string parameters) to avoid stealing from the history data, logs, or other unsecured locations.
     * 
     * @return  None. Acquired bearer token is assigned to global variable _token for all subsequent HTTP requests behind the scene.
     */
    private static void GetRequestToken() {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            char[] passwordArray = KEY_STORE_PASSWORD.toCharArray();
            FileInputStream fis = new java.io.FileInputStream(KEY_STORE_NAME);
            keyStore.load(fis, passwordArray);
            fis.close();

            X509Certificate cert = (X509Certificate) keyStore.getCertificate(ALIAS_IN_KEY_STORE);
            PrivateKey key = (PrivateKey) keyStore.getKey(ALIAS_IN_KEY_STORE, KEY_STORE_PASSWORD.toCharArray());
            AsymmetricKeyCredential asymmetricKeyCredential = AsymmetricKeyCredential.create(CLIENT_ID_CERT, key, cert);
            AuthenticationContext authContext = new AuthenticationContext(AUTHORITY, false, Executors.newSingleThreadExecutor());
            Future<AuthenticationResult> authResultAsync = authContext.acquireToken(RESOURCE, asymmetricKeyCredential, null);
            AuthenticationResult authResult = authResultAsync.get();
            _token = "Bearer " + authResult.getAccessToken();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage() + "\n\n\n\n");
        }
    }

    /**
     * Makes HTTP request to the Automated Case Exchange (ACE) with up to MAX_RETRIES retries.
     * 
     * @param  request            type of HTTP request (GET, POST, PATCH in the context of ACE).
     * @param  url                complete path (including base domain and path) to ACE service.
     * @param  payload            request body sent to ACE.
     * @param  publishEventToSelf flag specifying whether to publish Service Bus event to self (POST and PATCH only).
     * 
     * @return  None. Associated transaction data (request and response) is cached in _caches for immediate subsequent processing.
     */
    private static void MakeRequest(HttpRequestBase request, String url, String payload, boolean publishEventToSelf) {
        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            Integer retryCounter = 0;
            AddMetaDataToHttpRequest(request, url, payload, publishEventToSelf);

            while(true){
                try{
                    HttpResponse response = httpClient.execute(request);
                    CacheTransaction(request, response);
                    ValidateStatusCode();
                    return;
                } catch (Exception e) {
                    PrintRetryError(retryCounter, e);
                    Thread.sleep(TIME_DELAY);
                    retryCounter++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage() + "\n\n\n\n");
        }
    }

    /**
     * Serves as the core method of this application by:
     * <p>1. crafting proper signatures per Swagger specifications (https://msegksdev.trafficmanager.net/swagger/ui/index?sapId=2e12ea69-0884-9b66-8431-13094bcbe81e#/Cases).
     * <p>2. invoking MakeRequest() to carry out the actual HTTP transactions.
     * <p>3. processing and logging transaction data (request and response).
     * 
     * @param  api        type of API calls specified by the enum Api.
     * @param  caseNumber unique identifier for a valid case in the ACE.
     * @param  args       other indefinite number of attribute(s) (contact id guid, payload, etc).
     * 
     * @return  None. Essential transaction data is printed on console or logged in storage.
     */
    private static void Run(Api api, String caseNumber, String... args) {
        try {
            switch (api) {
            case GET_TOKEN:
                System.out.println(_token);
                break;
            case SCENARIO100_CREATE_CASE:
                MakeRequest(new HttpPost(), BASE_URI, args[0], false);
                break;
            case SCENARIO110_ASSIGN_REASSIGN_PARTNER_CASE_REFERENCES_AGENT:
            case SCENARIO115_CHANGE_PARTNER_CASE_REFERENCES_PARTNER_CASE_STATE:
            case SCENARIO190_CLOSE_CASE:
                MakeRequest(new HttpPatch(), BASE_URI + "/" + caseNumber + "/partnerCaseReferences/" + args[0], args[1], false);
                break;
            case SCENARIO110_ASSIGN_REASSIGN_PARTNER_CASE_REFERENCES_AGENT_AND_PUBLISH_EVENT_TO_SELF:
            case SCENARIO115_CHANGE_PARTNER_CASE_REFERENCES_PARTNER_CASE_STATE_AND_PUBLISH_EVENT_TO_SELF:
            case SCENARIO190_CLOSE_CASE_AND_PUBLISH_EVENT_TO_SELF:
                MakeRequest(new HttpPatch(), BASE_URI + "/" + caseNumber + "/partnerCaseReferences/" + args[0], args[1], true);
                break;
            case SCENARIO120_CREATE_NOTE:
                MakeRequest(new HttpPost(), BASE_URI + "/" + caseNumber + "/notes", args[0], false);
                break;
            case SCENARIO120_CREATE_NOTE_AND_PUBLISH_EVENT_TO_SELF:
                MakeRequest(new HttpPost(), BASE_URI + "/" + caseNumber + "/notes", args[0], true);
                break;
            case SCENARIO140_CREATE_CONTACT:
                MakeRequest(new HttpPost(), BASE_URI + "/" + caseNumber + "/customers/" + args[0] + "/contacts", args[1], false);
                break;
            case SCENARIO140_CREATE_CONTACT_AND_PUBLISH_EVENT_TO_SELF:
                MakeRequest(new HttpPost(), BASE_URI + "/" + caseNumber + "/customers/" + args[0] + "/contacts", args[1], true);
                break;
            case SCENARIO145_UPDATE_CONTACT:
                MakeRequest(new HttpPatch(), BASE_URI + "/" + caseNumber + "/customers/" + args[0] + "/contacts/" + args[1], args[2], false);
                break;
            case SCENARIO145_UPDATE_CONTACT_AND_PUBLISH_EVENT_TO_SELF:
                MakeRequest(new HttpPatch(), BASE_URI + "/" + caseNumber + "/customers/" + args[0] + "/contacts/" + args[1], args[2], true);
                break;
            case SCENARIO200_GET_CASE:
                MakeRequest(new HttpGet(), BASE_URI + "/" + caseNumber, "", false);
                break;
            case DEMO:
                Demo();
                return;
            default:
                break;
            }

            if(api != Api.GET_TOKEN){
                LogCaches();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage() + "\n\n\n\n");
        }
    }

    /**
     * Chains up several scenarios to illustrate a common usage.
     */
    private static void Demo(){
        String newCaseNumber =              Scenario100_1a_CreateCase();
        JSONObject newCaseJson =            Scenario100_2a_GetNewCase(newCaseNumber);
        System.out.println(newCaseJson);
        String partnerCaseReferenceIdGuid = Scenario100_2a_GetPartnerCaseReferenceIdGuidFromNewCase(newCaseJson);
        JSONObject defaultCustomer =        Scenario100_2a_GetDefaultCustomerFromNewCase(newCaseJson);
        System.out.println(defaultCustomer);
        String defaultCustomerIdGuid =      Scenario100_2a_GetDefaultCustomerIdGuidFromDefaultCustomer(defaultCustomer);
        String contactIdGuid =              Scenario100_2a_GetContactIdGuidByKeywordFromDefaultCustomer(defaultCustomer, "John");
                                            Scenario110_AssignReassignPartnerCaseReferencesAgent(newCaseNumber, partnerCaseReferenceIdGuid);
                                            Scenario115_ChangePartnerCaseReferencesPartnerCaseState(newCaseNumber, partnerCaseReferenceIdGuid);
                                            Scenario120_CreateNote(newCaseNumber);
                                            Scenario145_UpdateContact(newCaseNumber, defaultCustomerIdGuid, contactIdGuid);
                                            Scenario190_CloseCase(newCaseNumber, partnerCaseReferenceIdGuid);
    }

    /**
     * Makes POST: creates new case and returns case number.
     * 
     * @return  Case number (numerical unique identifier) of created case.
     */
    private static String Scenario100_1a_CreateCase(){
        Run(Api.SCENARIO100_CREATE_CASE, "", _payloads.get(Attr.CASE_PAYLOAD));
        return new JSONObject(_caches.get(Attr.HTTP_RESPONSE_BODY)).getString("CaseNumber");
    }

    /**
     * Makes GET: acquires casejson of the created case.
     * 
     * @param  newCaseNumber numerical unique identifier of the created case.
     * 
     * @return  New case casejson.
     */
    private static JSONObject Scenario100_2a_GetNewCase(String newCaseNumber){
        Run(Api.SCENARIO200_GET_CASE, newCaseNumber);
        return new JSONObject(_caches.get(Attr.HTTP_RESPONSE_BODY));
    }

    /**
     * Parse PartnerCaseReference ID GUID from the casejson.
     * 
     * @param  newCaseJson complete data in JSON format of the created case.
     * 
     * @return  PartnerCaseReference ID GUID.
     */
    private static String Scenario100_2a_GetPartnerCaseReferenceIdGuidFromNewCase(JSONObject newCaseJson){
        JSONArray partnerCaseReferences = ((JSONArray)newCaseJson.getJSONArray("PartnerCaseReferences"));

        for (Iterator<Object> iterator = partnerCaseReferences.iterator(); iterator.hasNext();) {
            JSONObject pcr = (JSONObject)iterator.next();

            if(pcr.getString("PartnerName").toUpperCase().equals(PARTNER_NAME)){
                return pcr.getString("id");
            }
        }

        return "";
    }

    /**
     * Parse the default Customer json from the casejson.
     * 
     * @param  newCaseJson complete data in JSON format of the created case.
     * 
     * @return  Default Customer json.
     */
    private static JSONObject Scenario100_2a_GetDefaultCustomerFromNewCase(JSONObject newCaseJson){
        return (JSONObject)newCaseJson.getJSONArray("Customers").get(0); // Applicable to single-Customer cases only, which is true, for now.
    }

    /**
     * Parse Customer ID GUID from the default Customer json.
     * 
     * @param  defaultCustomer complete data in JSON format of the Customer item of the created case, assuming we will be dealing with single-Customer cases only, which is true for now.
     * 
     * @return  Customer ID GUID.
     */
    private static String Scenario100_2a_GetDefaultCustomerIdGuidFromDefaultCustomer(JSONObject defaultCustomer){
        return defaultCustomer.getString("id");
    }

    /**
     * <p>Parse Contact ID GUID by keyword from the default Customer json.
     * 
     * @param  defaultCustomer Customer JSON of the created case, assuming we will be dealing with single-Customer cases only, which is true for now.
     * @param  keyword         string used to search for match among LastName, FirstName, Email, and Phone attributes of the Customer Contact.
     * 
     * @return  Contact ID GUID.
     */
    private static String Scenario100_2a_GetContactIdGuidByKeywordFromDefaultCustomer(JSONObject defaultCustomer, String keyword){
        String upperCasedKeyword = keyword.toUpperCase();
        JSONArray contacts = (JSONArray)defaultCustomer.getJSONArray("Contacts");

        for (Iterator<Object> iterator = contacts.iterator(); iterator.hasNext();) {
            JSONObject c = (JSONObject)iterator.next();

            if(c.getString("LastName").toUpperCase().contains(upperCasedKeyword) 
                || c.getString("FirstName").toUpperCase().contains(upperCasedKeyword) 
                || c.getString("Email").toUpperCase().contains(upperCasedKeyword)
                || c.getString("Phone").toUpperCase().contains(upperCasedKeyword)){
                
                return c.getString("id");
            }
        }

        return "";
    }

    /**
     * Makes PATCH: updates partner agent info.
     * 
     * @param  newCaseNumber              numerical unique identifier of the created case.
     * @param  partnerCaseReferenceIdGuid ID GUID of the partner's PartnerCaseReference item.
     * 
     * @return  None, since Response type is HTTP 204 No Content.
     */
    private static void Scenario110_AssignReassignPartnerCaseReferencesAgent(String newCaseNumber, String partnerCaseReferenceIdGuid) {
        Run(Api.SCENARIO110_ASSIGN_REASSIGN_PARTNER_CASE_REFERENCES_AGENT, newCaseNumber, partnerCaseReferenceIdGuid, _payloads.get(Attr.PARTNER_CASE_REFERENCES_AGENT_PAYLOAD));
    }

    /**
     * Makes PATCH: updates partner case state.
     * 
     * @param  newCaseNumber              numerical unique identifier of the created case.
     * @param  partnerCaseReferenceIdGuid GUID of the partner's PartnerCaseReference item.
     * 
     * @return  None, since Response type is HTTP 204 No Content.
     */
    private static void Scenario115_ChangePartnerCaseReferencesPartnerCaseState(String newCaseNumber, String partnerCaseReferenceIdGuid) {
        Run(Api.SCENARIO115_CHANGE_PARTNER_CASE_REFERENCES_PARTNER_CASE_STATE, newCaseNumber, partnerCaseReferenceIdGuid, _payloads.get(Attr.PARTNER_CASE_REFERENCES_CASE_STATE_PAYLOAD));
    }

    /**
     * Makes POST: creates new note to the created case.
     * 
     * @param  newCaseNumber numerical unique identifier of the created case.
     * 
     * @return  None, since Response type is HTTP 201, and response body contains only Note ID GUID.
     */
    private static void Scenario120_CreateNote(String newCaseNumber){
        Run(Api.SCENARIO120_CREATE_NOTE, newCaseNumber, _payloads.get(Attr.NOTE_PAYLOAD));
    }

    /**
     * <p>Makes PATCH: updates Contact.
     * 
     * @param  newCaseNumber         numerical unique identifier of the created case.
     * @param  defaultCustomerIdGuid ID GUID of the Customer item of the created case, assuming we will be dealing with single-Customer cases only, which is true for now.
     * @param  contactIdGuid         ID GUID of the target Contact item to be updated.
     * 
     * @return  None, since Response type is HTTP 204 No Content.
     */
    private static void Scenario145_UpdateContact(String newCaseNumber, String defaultCustomerIdGuid, String contactIdGuid){
        Run(Api.SCENARIO145_UPDATE_CONTACT, newCaseNumber, defaultCustomerIdGuid, contactIdGuid, _payloads.get(Attr.CONTACT_PAYLOAD));
    }

    /**
     * Makes PATCH: sets partner case state to "Closed".
     * 
     * @param  newCaseNumber              numerical unique identifier of the created case.
     * @param  partnerCaseReferenceIdGuid GUID of the partner's PartnerCaseReference item.
     * 
     * @return  None, since Response type is HTTP 204 No Content.
     */
    private static void Scenario190_CloseCase(String newCaseNumber, String partnerCaseReferenceIdGuid) {
        Run(Api.SCENARIO190_CLOSE_CASE, newCaseNumber, partnerCaseReferenceIdGuid, _payloads.get(Attr.PARTNER_CASE_REFERENCES_CASE_STATE_CLOSURE_PAYLOAD));
    }

    /**
     * <p>Assigns headers, payload to HTTP request.
     * <p>**CAUTION** DO NOT use SelfNotification header in Production code.
     * 
     * @param  request            type of HTTP request (GET, POST, PATCH in the context of ACE).
     * @param  url                complete path (including base domain and path) to ACE service.
     * @param  payload            request body sent to ACE.
     * @param  publishEventToSelf boolean flag specifying whether to publish said HTTP request back to sender (partner self) via Service Bus events. Applicable to POST and PATCH requests only.
     */
    private static void AddMetaDataToHttpRequest(HttpRequestBase request, String url, String payload, boolean publishEventToSelf){
        try{
            request.setURI(new URI(url));
            request.addHeader("Authorization", _token);

            if (request.getMethod() != "GET") { // GET requests need no payload
                StringEntity entity = new StringEntity(payload, "utf-8");
                entity.setContentType("application/json");
                ((HttpEntityEnclosingRequestBase) request).setEntity(entity);
           }

            if (publishEventToSelf) { // Only applicable to POST Note, POST Contact, PATCH Contact, or PATCH PartnerCaseReference
                request.addHeader("test-mseg", "{\'SelfNotification\':\'true\'}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage() + "\n\n\n\n");
        }
    }

    /**
     * <p>Saves most recent transaction request and response in _caches for immediate processing.
     * <p>Note that Cached data will be overwritten by data from next transaction upon next round of caching.
     * 
     * @param  request  type of HTTP request (GET, POST, PATCH in the context of ACE).
     * @param  response message responded by server after receiving and interpreting request.
     */
    private static void CacheTransaction(HttpRequestBase request, HttpResponse response){
        try {
            _caches.put(Attr.HTTP_REQUEST, request.toString());
            _caches.put(Attr.HTTP_RESPONSE_STATUS_CODE, Integer.toString(response.getStatusLine().getStatusCode()));
            _caches.put(Attr.HTTP_RESPONSE_STATUS_REASON, response.getStatusLine().getReasonPhrase());

            if(response.getEntity() == null) // PATCH requests return no entity
                return;

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String buffer = "";
            String responseBody = "";
            while ((buffer = bufferedReader.readLine()) != null) {
                responseBody += buffer + "\n";
            }
            _caches.put(Attr.HTTP_RESPONSE_BODY, responseBody);
        } catch (Exception e) {
            e.printStackTrace();
           System.out.println(e.getMessage() + "\n\n\n\n");
        }
    }

    /**
     * <p>Determines pass/fail of a given transaction per available Status Codes (https://www.ietf.org/rfc/rfc2616.txt).
     * <p>200 OK:         an entity corresponding to the requested resource is sent in the response.
     * <p>201 Created:    the request has been fulfilled and resulted in a new resource being created.
     * <p>204 No Content: the server has fulfilled the request but does not need to return an entity-body.
     * <p>If Status Code is not listed above, HTTP Exception will be thrown.
     */
    private static void ValidateStatusCode() throws HttpException{
        List<String> okHttpCodes = List.of("200", "201", "204");

        if(!okHttpCodes.contains(_caches.get(Attr.HTTP_RESPONSE_STATUS_CODE))){
            throw new HttpException("HTTP request was not successful: \r"
                                        + _caches.get(Attr.HTTP_REQUEST) + "\r"
                                        + _caches.get(Attr.HTTP_RESPONSE_STATUS_CODE) + "\r"
                                        + _caches.get(Attr.HTTP_RESPONSE_STATUS_REASON) + "\r"
                                        + _caches.get(Attr.HTTP_RESPONSE_BODY) + "\r");
        }
    }

    /**
     * <p>Populates _payloads with case attributes in the form of json strings.
     * <p>_payloads is directly used by Run().
     * 
     * @return  None, since _payloads is a global variable.
     */
    private static void SetPayloads() {
        _payloads.put(Attr.CASE_NUMBER, "118092114525133");
        _payloads.put(Attr.CUSTOMER_ID_GUID, "5a1d6732-ed96-414b-add3-f617275f5df0");
        _payloads.put(Attr.CONTACT_ID_GUID, "ab453400-4596-40cd-a59d-d96156cea939");
        _payloads.put(Attr.PARTNER_CASE_REFERENCES_ID_GUID, "f94754e4-fdfe-4369-9553-f198aa6f20e2");
        _payloads.put(Attr.CASE_PAYLOAD, "{\"SupportAreaPath\": \"32d322a8-acae-202d-e9a9-7371dccf381b\","
                                        + "\"Severity\": \"2\"," 
                                        + "\"CreationChannel\": \"Web\"," 
                                        + "\"Title\": \"Case 20180921028\","
                                        + "\"IssueDescription\": \"20180921028 Testing\"," 
                                        + "\"SupportCountry\": \"US\","
                                        + "\"SupportLanguage\": \"en-US\","
                                        + "\"EntitlementInformation\": { \"EntitlementId\": \"U291cmNlOkZyZWUsRnJlZUlkOjAwMDAwMDAwLTAwMDAtMDAwMC0wMDAwLTAwMDAwMDAwMDAwMCxMb2NhbGU6ZW4tdXMs\"},"
                                        + "\"Customers\": [{\"CustomerName\": \"LiBingBing LTD\"," 
                                                                + "\"CustomerId\": \"Unknown\","
                                                                + "\"CustomerIdSource\": \"Unknown\"," 
                                                                + "\"Contacts\": [{\"LastName\": \"Doe\","
                                                                                + "\"FirstName\": \"John\"," 
                                                                                + "\"Phone\": \"+86 21 2213 0000\","
                                                                                + "\"Email\": \"JD@Wonderland.org\"," 
                                                                                + "\"PreferredContactChannel\": \"Phone\","
                                                                                + "\"IsPrimaryContact\": true}]}],"
                                        + "\"PartnerCaseReferences\": [{\"PartnerName\": \"ThePartners\","
                                                                            + "\"PartnerCaseState\": \"New\","
                                                                            + "\"PartnerAgentInformation\": {\"LastName\": \"Goodiez\","
                                                                                                          + "\"FirstName\": \"Cookiez\"," 
                                                                                                          + "\"Email\":\"GC@Yum.com\","
                                                                                                          + "\"Phone\": \"+1-425-882-8080\"},"
                                                                            + "\"PartnerCaseId\": \"Partner 028\"}],"
                                        + "\"Notes\": [{\"Content\": \"<div style='color: rgb(0, 0, 0); font-family: Calibri,Arial,Helvetica,sans-serif; font-size: 11pt;'>Test Note Template<br></div>\"}]}");
        _payloads.put(Attr.NOTE_PAYLOAD, "{\"Content\": \"Test @ " + DATE_FORMATTER.format(new Date()) + "\"}");
        _payloads.put(Attr.CONTACT_PAYLOAD, "{\"LastName\": \"Diamond\"," 
                                           + "\"FirstName\": \"Apmex\"," 
                                           + "\"Email\": \"DA@Tiffany.co.fr\","
                                           + "\"Phone\": \"bff-4ever\"," 
                                           + "\"PreferredContactChannel\": \"Phone\"}");
        _payloads.put(Attr.PARTNER_CASE_REFERENCES_AGENT_PAYLOAD, "{\"PartnerAgentInformation\": {\"LastName\": \"Wangdu\","
                                                                 + "\"FirstName\": \"Phunsukh\"," 
                                                                 + "\"Email\":\"Phunsukh.Wangdu@Ladakh.org\","
                                                                 + "\"Phone\": \"+1-425-882-8888\"}}");
        _payloads.put(Attr.PARTNER_CASE_REFERENCES_CASE_STATE_PAYLOAD, "{\"PartnerCaseState\": \"Active\"}");
        _payloads.put(Attr.PARTNER_CASE_REFERENCES_CASE_STATE_CLOSURE_PAYLOAD, "{\"PartnerCaseState\": \"Closed\"}");
    }

    /**
     * <p>Enumeration of recommended API types offerred by ACE.
     * <p>The only differences for any API with or without "PUBLISH_EVENT_TO_SELF", is:
     * <p>- The addition of reqeust header "test-mseg", "{\'SelfNotification\':\'true\'}".
     * <p>- Publishing of Service Bus event to the partner's own subscription for testing purpose.
     */
    private enum Api {
        SCENARIO100_CREATE_CASE,
        SCENARIO110_ASSIGN_REASSIGN_PARTNER_CASE_REFERENCES_AGENT,
        SCENARIO110_ASSIGN_REASSIGN_PARTNER_CASE_REFERENCES_AGENT_AND_PUBLISH_EVENT_TO_SELF,
        SCENARIO115_CHANGE_PARTNER_CASE_REFERENCES_PARTNER_CASE_STATE,
        SCENARIO115_CHANGE_PARTNER_CASE_REFERENCES_PARTNER_CASE_STATE_AND_PUBLISH_EVENT_TO_SELF,
        SCENARIO120_CREATE_NOTE,
        SCENARIO120_CREATE_NOTE_AND_PUBLISH_EVENT_TO_SELF,
        SCENARIO140_CREATE_CONTACT,
        SCENARIO140_CREATE_CONTACT_AND_PUBLISH_EVENT_TO_SELF,
        SCENARIO145_UPDATE_CONTACT,
        SCENARIO145_UPDATE_CONTACT_AND_PUBLISH_EVENT_TO_SELF,
        SCENARIO190_CLOSE_CASE,
        SCENARIO190_CLOSE_CASE_AND_PUBLISH_EVENT_TO_SELF,
        SCENARIO200_GET_CASE, 
        GET_TOKEN,
        DEMO
    }

    /**
     * List of keys stored in the global map/dictionary _payloads.
     */
    private enum Attr {
        CASE_NUMBER,
        CASE_PAYLOAD,
        CONTACT_ID_GUID,
        CONTACT_PAYLOAD,
        CUSTOMERS,
        CUSTOMER_ID_GUID,
        ENTIRE_CASE,
        NEWEST_CONTACT,
        NOTE_PAYLOAD,
        PARTNER_CASE_REFERENCES_ID_GUID,
        PARTNER_CASE_REFERENCES_AGENT_PAYLOAD,
        PARTNER_CASE_REFERENCES_CASE_STATE_PAYLOAD,
        PARTNER_CASE_REFERENCES_CASE_STATE_CLOSURE_PAYLOAD,
        PARTNER_CASE_REFERENCESS,
        NOTES,
        HTTP_REQUEST,
        HTTP_RESPONSE_STATUS_CODE,
        HTTP_RESPONSE_STATUS_REASON,
        HTTP_RESPONSE_BODY
    }

    /**
     * Print retry errors on console.
     * 
     * @param  retryCounter number of retries attmpted.
     * @param  e            exception object.
     */
    private static void PrintRetryError(Integer retryCounter, Exception e){
        System.out.println("FAILED - Command failed on retry " + retryCounter + " of " + MAX_RETRIES + " error: " + e);

        if (retryCounter >= MAX_RETRIES) {
            System.out.println("Max retries exceeded.");
            System.exit(1);
        }
    }

    /**
     * Log contains of the global variable map/dictionary _caches to disk storage.
     */
    private static void LogCaches() {
        // Determine whether the directory exists. If not, create one
        File directory = new File(LOG_FOLDER);

        if (!directory.exists())
            directory.mkdir();

        // Log data
        try (FileWriter fw = new FileWriter(LOG_FOLDER + LOG_NAME, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {
            out.println(_caches + "\n\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage() + "\n\n\n\n");
        }
    }
}
