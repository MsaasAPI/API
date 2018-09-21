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
    // private static final String PARTNER_NAME = "FJ";
    private static final String LOG_FOLDER = "Logs/";
    private static final Integer MAX_RETRIES = 3;
    
    /***** CREDENTIALS *****/
    private static final String CLIENT_ID_CERT = "c6bee0f5-1a60-4d25-8629-b0bdd8e4ce2e";
    private static final String AUTHORITY = "https://login.microsoftonline.com/keystonemseg.onmicrosoft.com";
    private static final String RESOURCE = "https://api-ppe.support.microsoft.com";
    private static final String KEY_STORE_NAME = "C:\\Program Files\\Java\\jre-10.0.2\\bin\\AadKeyStore01.jks";
    private static final String ALIAS_IN_KEY_STORE = "te-55a00fe8-7011-411a-b2cc-c935edcf59c1";
    private static final String KEY_STORE_PASSWORD = "CaseExchange!+";
    
    /***** OTHER CONSTANTS & STATIC FIELDS *****/
    private static final String BASE_URI = "https://api-ppe.support.microsoft.com/v1/cases";
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy_MMdd_HHmm");
    private static final String LOG_NAME = "log_" + DATE_FORMATTER.format(new Date()) + ".txt";
    private static final Integer TIME_DELAY = 1000;
    private static final Random _random = new Random(new Date().getTime());
    private static final Map<Attr, String> _payloads = new HashMap<Attr, String>(); // Contains data (resources and payloads) required per https://msegksdev.trafficmanager.net/swagger/ui/index?sapId=2e12ea69-0884-9b66-8431-13094bcbe81e#/Cases.
    private static final Map<Attr, String> _caches = new HashMap<Attr, String>(); // Contains HTTP request and repsonse data associated with the most recent transaction. Subject to overwrite by subsequent transactions.
    private static String _token = "";

    public static void main(String[] args) {
        BasicConfigurator.configure();
        SetPayloads();
        GetRequestToken();

        //--Scenario 100 Partner creates a new case. Empty string parameter is placeholder for caseNumber
        Run(Api.SCENARIO100_CREATE_CASE, "", _payloads.get(Attr.CASE_PAYLOAD));        

        //--Scenario 110 Partner assigns or reassigns an agent
        Run(Api.SCENARIO110_ASSIGN_REASSIGN_PARTNER_CASE_REFERENCES_AGENT, _payloads.get(Attr.CASE_NUMBER), _payloads.get(Attr.PARTNER_CASE_REFERENCES_ID_GUID), _payloads.get(Attr.PARTNER_CASE_REFERENCES_AGENT_PAYLOAD));

        //--Scenario 115 Partner changes partner's case state
        Run(Api.SCENARIO115_CHANGE_PARTNER_CASE_REFERENCES_PARTNER_CASE_STATE, _payloads.get(Attr.CASE_NUMBER), _payloads.get(Attr.PARTNER_CASE_REFERENCES_ID_GUID), _payloads.get(Attr.PARTNER_CASE_REFERENCES_CASE_STATE_PAYLOAD)); 

        //--Scenario 120 Partner creates a new note
        Run(Api.SCENARIO120_CREATE_NOTE, _payloads.get(Attr.CASE_NUMBER), _payloads.get(Attr.NOTE_PAYLOAD)); 

        //--Scenario 200 Partner gets a case
        Run(Api.SCENARIO200_GET_CASE, _payloads.get(Attr.CASE_NUMBER)); 

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
                // Demo();
                break;
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
        _payloads.put(Attr.CASE_NUMBER, "118092114525128");
        _payloads.put(Attr.CUSTOMER_ID_GUID, "4fc1f65e-d740-4385-a8e0-5c0d58b8374e");
        _payloads.put(Attr.CONTACT_ID_GUID, "aa743cf9-134c-4707-8df1-49d3c52a8c84");
        _payloads.put(Attr.PARTNER_CASE_REFERENCES_ID_GUID, "9afe430e-bb99-4c71-aae3-91c137f542fa");
        _payloads.put(Attr.CASE_PAYLOAD, "{\"SupportAreaPath\": \"32d322a8-acae-202d-e9a9-7371dccf381b\","
                                        + "\"Severity\": \"2\"," 
                                        + "\"CreationChannel\": \"Web\"," 
                                        + "\"Title\": \"Case 20180921005\","
                                        + "\"IssueDescription\": \"20180921001 Testing\"," 
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
                                                                            + "\"PartnerCaseId\": \"Partner 005\"}],"
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
