package com.ringcentral.rcandroidsdk.rcsdk.platform;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ringcentral.rcandroidsdk.rcsdk.http.RCHeaders;
import com.ringcentral.rcandroidsdk.rcsdk.http.RCRequest;
import com.ringcentral.rcandroidsdk.rcsdk.http.RCResponse;
import com.ringcentral.rcandroidsdk.rcsdk.subscription.Subscription;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by andrew.pang on 6/25/15.
 */
public class Platform implements Serializable{
    String appKey;
    String appSecret;
    String server;
    String account = "~";
    Auth auth;
    Subscription subscription;

    static String ACCESS_TOKEN_TTL = "3600"; //60 minutes
    //static String REFRESH_TOKEN_TTL = "36000";  // 10 hours
    static String REFRESH_TOKEN_TTL = "604800";  // 1 week

    /**
     *
     * @param appKey
     * @param appSecret
     * @param server Pass in either "SANDBOX" or "PRODUCTION"
     */
    public Platform(String appKey, String appSecret, String server){
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.server = server;
        this.auth = new Auth();
    }

    /**
     * Sets authentication data for platform's auth
     *
     * @param authData A map of the parsed authentication response
     */
    public void setAuthData(Map<String, String> authData){
        this.auth.setData(authData);
    }

    public Auth getAuthData(){
        return auth.getData();
    }

    /**
     *
     * @return AccessToken from auth
     */
    public String getAccessToken(){
        return this.auth.getAccessToken();
    }

    /**
     * Checks if the access token is valid, and if not refreshes the token
     *
     * @throws Exception
     */
    public void isAuthorized() throws Exception{
        if(!this.auth.isAccessTokenValid()){
            this.refresh();
        }
        if(!this.auth.isAccessTokenValid()){
            throw new Exception("Access token is expired");
        }
    }

    /**
     * Uses the refresh token to refresh authentication
     *
     * @throws Exception
     */
    public void refresh() throws Exception{
        if(!this.auth.isRefreshTokenValid()){
            throw new Exception("Refresh token is expired");
        } else {
            HashMap<String, String> body = new HashMap<>();
            //Body
            body.put("grant_type", "password");
            body.put("refresh_token", this.auth.getRefreshToken());
            body.put("access_token_ttl", ACCESS_TOKEN_TTL);
            body.put("refresh_token_ttl", REFRESH_TOKEN_TTL);
            //Header
            HashMap<String, String> headerMap = new HashMap<>();
            headerMap.put("method", "POST");
            headerMap.put("url", "/restapi/oauth/token");
            this.authCall(body, headerMap,
                    new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            e.printStackTrace();
                        }
                        @Override
                        public void onResponse(Response response) throws IOException {
                            if (!response.isSuccessful())
                                throw new IOException("Unexpected code " + response);
                            //callResponse = response;
                            String responseString = response.body().string();
                            System.out.print(responseString);

                            Gson gson = new Gson();
                            Type mapType = new TypeToken<Map<String, String>>() {
                            }.getType();
                            Map<String, String> responseMap = gson.fromJson(responseString, mapType);
                            setAuthData(responseMap);
                        }
                    });
        }
    }

    /**
     * Revokes access for current access token
     *
     * @param c
     */
    public void logout(Callback c){
        HashMap<String, String> body = new HashMap<>();
        body.put("token", this.getAccessToken());
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("method", "POST");
        headerMap.put("url", "/restapi/oauth/revoke");
        headerMap.put("Content-Type", "application/x-www-form-urlencoded");
        this.logoutPost(body, headerMap, c);
        this.auth.reset();
    }

    /**
     * Takes in parameters used for authorization and makes an auth call
     *
     * @param username
     * @param extension
     * @param password
     * @param c
     */
    public void authorize(String username, String extension, String password, Callback c){
        HashMap<String, String> body = new HashMap<>();
        //Body
        body.put("grant_type", "password");
        body.put("username", username);
        body.put("extension", extension);
        body.put("password", password);
        body.put("access_token_ttl", ACCESS_TOKEN_TTL);
        body.put("refresh_token_ttl", REFRESH_TOKEN_TTL);
        //Header
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("method", "POST");
        headerMap.put("url", "/restapi/oauth/token");
        this.authCall(body, headerMap, c);
    }

    /**
     * Encodes the app key and app secret in base64 to be used in authentication
     *
     * @return
     */
    public String getApiKey(){
        String keySec = appKey + ":" + appSecret;
        byte[] message = new byte[0];
        try {
            message = keySec.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String encoded = Base64.encodeToString(message, Base64.DEFAULT);
        String apiKey = (encoded).replace("\n", "");
        return apiKey;
    }

    /**
     * Takes in part of a URL along with options to return the endpoint for the API call
     *
     * @param url
     * @param options
     * @return
     */
    public String apiURL(String url, HashMap<String, String> options){
        String builtUrl = "";
        boolean has_http = url.contains("http://") || url.contains("https://");
        if(options.containsKey("addServer") && !has_http){
            builtUrl += this.server;
        }
        if(url.contains("/restapi") == false && !has_http){
            builtUrl += "/restapi" + "/" + "v1.0";
        }

        if(url.contains("/account/") == true){
            builtUrl = builtUrl.replace("/account/" + "~", "/account/" + this.account);
        }

        builtUrl += url;

        if(options.containsKey("addMethod")){
            if(builtUrl.contains("?")){
                builtUrl += "&";
            } else {
                builtUrl += "?";
            }
            builtUrl += "_method=" + options.get("addMethod");
        }

        if(options.containsKey("addToken")){
            if(builtUrl.contains("?")){
                builtUrl += "&";
            } else {
                builtUrl += "?";
            }
            builtUrl += "access_token=" + this.auth.getAccessToken();
        }

        return builtUrl;
    }

    /**
     * POST request set up for making authorization calls
     *
     * @param body
     * @param headerMap
     * @param c
     */
    public void authCall(HashMap<String, String> body, HashMap<String, String> headerMap, Callback c){
        RCRequest RCRequest = new RCRequest(body, headerMap);
        RCRequest.RCHeaders.setHeader("authorization", "Basic " + this.getApiKey());
        RCRequest.RCHeaders.setHeader("Content-Type", "application/x-www-form-urlencoded");
        HashMap<String, String> options = new HashMap<>();
        options.put("addServer", "true");
        RCRequest.setURL(this.apiURL(RCRequest.getUrl(), options));
        try {
            RCRequest.post(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the header and body to make a GET request
     *
     * @param body
     * @param headerMap
     * @param c
     */
    public void get(HashMap<String, String> body, HashMap<String, String> headerMap, Callback c) {
        RCRequest RCRequest = new RCRequest(body, headerMap);
        RCRequest.setMethod("GET");
        RCRequest.RCHeaders.setHeader("authorization", this.auth.getTokenType() + " " + this.getAccessToken());
        HashMap<String, String> options = new HashMap<>();
        options.put("addServer", "true");
        RCRequest.setURL(this.apiURL(RCRequest.getUrl(), options));
        try {
            RCRequest.get(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the header and body to make a POST request
     *
     * @param body
     * @param headerMap
     * @param c
     */
    public void post(HashMap<String, String> body, HashMap<String, String> headerMap, Callback c) {
        RCRequest RCRequest = new RCRequest(body, headerMap);
        HashMap<String, String> options = new HashMap<>();
        options.put("addServer", "true");
        RCRequest.setURL(this.apiURL(RCRequest.getUrl(), options));
        RCRequest.setMethod("POST");
        RCRequest.RCHeaders.setHeader("authorization", "Bearer " + this.getAccessToken());
        try {
            RCRequest.post(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * POST request set up for logging out
     *
     * @param body
     * @param headerMap
     * @param c
     */
    public void logoutPost(HashMap<String, String> body, HashMap<String, String> headerMap, Callback c) {
        RCRequest RCRequest = new RCRequest(body, headerMap);
        HashMap<String, String> options = new HashMap<>();
        options.put("addServer", "true");
        RCRequest.setURL(this.apiURL(RCRequest.getUrl(), options));
        RCRequest.setMethod("POST");
        RCRequest.RCHeaders.setHeader("authorization", "Basic " + this.getApiKey());
        try {
            RCRequest.post(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets up body and header for a PUT request
     *
     * @param body
     * @param headerMap
     * @param c
     */
    public void put(HashMap<String, String> body, HashMap<String, String> headerMap, Callback c) {
        RCRequest RCRequest = new RCRequest(body, headerMap);
        HashMap<String, String> options = new HashMap<>();
        options.put("addServer", "true");
        RCRequest.setURL(this.apiURL(RCRequest.getUrl(), options));
        RCRequest.setMethod("PUT");
        RCRequest.RCHeaders.setHeader("authorization", "Bearer " + this.getAccessToken());
        try {
            RCRequest.put(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets up body and headers for a DELETE request
     *
     * @param body
     * @param headerMap
     * @param c
     */
    public void delete(HashMap<String, String> body, HashMap<String, String> headerMap, Callback c) {
        RCRequest RCRequest = new RCRequest(body, headerMap);
        HashMap<String, String> options = new HashMap<>();
        options.put("addServer", "true");
        RCRequest.setURL(this.apiURL(RCRequest.getUrl(), options));
        RCRequest.setMethod("DELETE");
        RCRequest.RCHeaders.setHeader("authorization", "Bearer " + this.getAccessToken());
        try {
            RCRequest.delete(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * GET Account Info API call
     *
     * @param c
     */
    public void accountInfo(Callback c){
        HashMap<String, String> body = null;
        HashMap<String, String> headers = new HashMap<>();
        headers.put("url", "/restapi/v1.0/account/~");
        this.get(body, headers, c);
    }

    /**
     * GET Call Log API call
     *
     * @param c
     */
    public void callLog(Callback c){
        HashMap<String, String> callLogBody = null;
        HashMap<String, String> callLogHeaders = new HashMap<>();
        callLogHeaders.put("url", "/restapi/v1.0/account/~/call-log");
        this.get(callLogBody, callLogHeaders, c);
    }

    /**
     * GET Message Store API call
     *
     * @param c
     */
    public void messageStore(Callback c){
        HashMap<String, String> messageStoreBody = null;
        HashMap<String, String> messageStoreHeaders = new HashMap<>();
        messageStoreHeaders.put("url", "/restapi/v1.0/account/~/extension/~/message-store");
        this.get(messageStoreBody, messageStoreHeaders, c);
    }

    /**
     * RingOut API call using POST request
     * @param to Phone number calling to
     * @param from Phone number calling from
     * @param callerId Phone number used for caller ID
     * @param hasPrompt "True" or "False" states whether a prompt plays before call
     * @param c
     */
    public void ringOut(String to, String from, String callerId, String hasPrompt, Callback c){
        HashMap<String, String> body = new HashMap<>();
        body.put("body", "{\n" +
                "  \"to\": {\"phoneNumber\": \"" + to
                + "\"},\n" +
                "  \"from\": {\"phoneNumber\": \"" + from
                + "\"},\n" +
                "  \"callerId\": {\"phoneNumber\": \"" + callerId
                + "\"},\n" +
                "  \"playPrompt\": " + hasPrompt
                + "\n" + "}");
        HashMap<String, String> headers = new HashMap<>();
        headers.put("url", "/restapi/v1.0/account/~/extension/~/ringout");
        headers.put("Content-Type", "application/json");
        this.post(body, headers, c);
    }

    /**
     * SMS API call using POST request
     *
     * @param to Phone number sending SMS to
     * @param from Phone number sending SMS from
     * @param message SMS text message body
     * @param c
     */
    public void sendSMS(String to, String from, String message, Callback c){
        HashMap<String, String> body = new HashMap<>();
        body.put("body", "{\n" +
                "  \"to\": [{\"phoneNumber\": \"" + to + "\"}],\n" +
                "  \"from\": {\"phoneNumber\": \"" + from + "\"},\n" +
                "  \"text\": \"" + message + "\"\n" + "}");
        HashMap<String, String> headers = new HashMap<>();
        headers.put("url", "/restapi/v1.0/account/~/extension/~/sms");
        headers.put("Content-Type", "application/json");
        this.post(body, headers, c);
    }

    public void postSubscription(){
        HashMap<String, String> body = new HashMap<>();
        body.put("body", "{\n" +
                "  \"eventFilters\": [ \n" +
                "    \"/restapi/v1.0/account/~/extension/~/presence\", \n" +
                "    \"/restapi/v1.0/account/~/extension/~/message-store\" \n" +
                "  ], \n" +
                "  \"deliveryMode\": { \n" +
                "    \"transportType\": \"PubNub\", \n" +
                "    \"encryption\": \"false\" \n" +
                "  } \n" +
                "}");
        HashMap<String, String> headers = new HashMap<>();
        headers.put("url", "/restapi/v1.0/subscription");
        headers.put("Content-Type", "application/json");
        this.post(body, headers,
                new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (!response.isSuccessful())
                            throw new IOException("Unexpected code " + response);
                        RCResponse rcResponse = new RCResponse(response);
                        try {
                            JSONObject responseJson = new JSONObject(rcResponse.getBody());
                            JSONObject deliveryMode = responseJson.getJSONObject("deliveryMode");
                            String subscriberKey = deliveryMode.getString("subscriberKey");
                            String secretKey = deliveryMode.getString("secretKey");
                            String encryptionKey = deliveryMode.getString("encryptionKey");
                            String address = deliveryMode.getString("address");
                            //Subscription
                            subscription = new Subscription(subscriberKey, secretKey);
                            subscription.subscribe(address);
                            subscription.presence();
                        } catch(JSONException e){
                            e.printStackTrace();
                        }

                    }
                });
    }
}
