package com.contoso.caseapijavademo01;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.*;
import com.microsoft.aad.adal4j.*;

import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.log4j.BasicConfigurator;

/**
 * Hello world!
 *
 */
public class Program {
    /***** USER CONFIGURABLE FIELDS *****/
    // private static final String PARTNER_NAME = "FJ";
    // private static final String LOG_FOLDER = "Logs/";
    private static final Integer MAX_RETRIES = 3;
    
    /***** CREDENTIALS *****/
    private static final String CLIENT_ID_CERT = "c6bee0f5-1a60-4d25-8629-b0bdd8e4ce2e";
    private static final String AUTHORITY = "https://login.microsoftonline.com/keystonemseg.onmicrosoft.com";
    private static final String RESOURCE = "https://api-ppe.support.microsoft.com";
    private static final String KEY_STORE_NAME = "C:\\Program Files\\Java\\jre-10.0.2\\bin\\AadKeyStore01.jks";
    private static final String ALIAS_IN_KEY_STORE = "te-55a00fe8-7011-411a-b2cc-c935edcf59c1";
    private static final String KEY_STORE_PASSWORD = "CaseExchange!+";
    
    /***** OTHER CONSTANTS & STATIC FIELDS *****/
    // private static final String BASE_URI = "https://api-ppe.support.microsoft.com/v1/cases";
    // private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy_MMdd_HHmm");
    // private static final String LOG_NAME = "log_" + DATE_FORMATTER.format(new Date()) + ".txt";
    private static final Integer TIME_DELAY = 1000;
    // private static final Random _random = new Random(new Date().getTime());
    // private static final Map<Attr, String> _payloads = new HashMap<Attr, String>(); // Contains data (resources and payloads) required per https://msegksdev.trafficmanager.net/swagger/ui/index?sapId=2e12ea69-0884-9b66-8431-13094bcbe81e#/Cases.
    private static final Map<Attr, String> _caches = new HashMap<Attr, String>(); // Contains HTTP request and repsonse data associated with the most recent transaction. Subject to overwrite by subsequent transactions.
    private static String _token = "";

    public static void main(String[] args) {
        BasicConfigurator.configure();
        System.out.println("Hello World!");
        // SetPayloads();
        GetRequestToken();
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
}
