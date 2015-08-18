package com.ringcentral.rcandroidsdk.oldsdk;

import android.util.Base64;

import com.pubnub.api.PubnubError;
import com.ringcentral.rcandroidsdk.oldsdk.RCRequest;
import com.ringcentral.rcandroidsdk.oldsdk.RCResponse;
import com.ringcentral.rcandroidsdk.rcsdk.platform.Auth;
import com.ringcentral.rcandroidsdk.rcsdk.subscription.Subscription;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by andrew.pang on 6/25/15.
 */
public class OldPlatform implements Serializable{
    String appKey;
    String appSecret;
    String server;
    String account = "~";
    public Auth auth;
    Subscription subscription;

    /**
     *
     * @param server Pass in either "SANDBOX" or "PRODUCTION"
     */
    public OldPlatform(String appKey, String appSecret, String server){
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.auth = new Auth();
        if(server.toUpperCase().equals("SANDBOX")){
            this.server = "https://platform.devtest.ringcentral.com";
        }
        else if(server.toUpperCase().equals("PRODUCTION")){
            this.server = "https://platform.ringcentral.com";
        }
    }

    /**
     * Sets authentication data for platform's auth
     *
     * @param authData A hashmap of the parsed authentication response
     */
    public void setAuthData(HashMap<String, String> authData){
        this.auth.setData(authData);
    }

    public Auth getAuthData(){
        return auth.getData();
    }

    /**
     * Returns AccessToken from auth onject
     *
     * @return
     */
    public String getAccessToken(){
        return this.auth.getAccessToken();
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
     * Gives the header value needed to pass in as "authorization" for API calls
     * @return
     */
    public String getAuthHeader(){
        return this.auth.getTokenType() + " " + this.getAccessToken();
    }

    /**
     * Takes in part of a URL along with options to return the endpoint for the API call
     *
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
            LinkedHashMap<String, String> body = new LinkedHashMap<>();
            //Body
            body.put("grant_type", "refresh_token");
            body.put("refresh_token", this.auth.getRefreshToken());
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
                            RCResponse refreshResponse = new RCResponse(response);
                            HashMap<String, String> responseMap = refreshResponse.getJson();
                            setAuthData(responseMap);
                            System.out.println("refresh");
                        }
                    });
        }
    }

    /**
     * Revokes access for current access token
     *
     */
    public void logout(Callback callback){
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        body.put("token", this.getAccessToken());
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("method", "POST");
        headerMap.put("url", "/restapi/oauth/revoke");
        headerMap.put("Content-Type", "application/x-www-form-urlencoded");
        this.authCall(body, headerMap, callback);
        this.auth.reset();
    }

    /**
     * Method used for API calls, with the request type, body, headers, and callback as parameters.
     *
     */
    public void apiCall(String method, LinkedHashMap<String, String> body, HashMap<String, String> headerMap, Callback callback){
        try{
            this.isAuthorized();
            RCRequest RCRequest = new RCRequest(body, headerMap);
            RCRequest.RCHeaders.setHeader("authorization", this.getAuthHeader());
            HashMap<String, String> options = new HashMap<>();
            options.put("addServer", "true");
            RCRequest.setURL(this.apiURL(RCRequest.getUrl(), options));
            RCRequest.setMethod(method);
            if(method.toUpperCase().equals("DELETE")){
                RCRequest.delete(callback);
            } else if(method.toUpperCase().equals("POST")){
                RCRequest.post(callback);
            } else if(method.toUpperCase().equals("PUT")){
                RCRequest.put(callback);
            } else {
                RCRequest.get(callback);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Takes in parameters used for authorization and makes an auth call
     *
     */
    public void authorize(String username, String extension, String password, Callback callback){
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        //Body
        body.put("grant_type", "password");
        body.put("username", username);
        body.put("extension", extension);
        body.put("password", password);
        //Header
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("method", "POST");
        headerMap.put("url", "/restapi/oauth/token");
        this.authCall(body, headerMap, callback);
    }

    /**
     * POST request set up for making authorization calls
     *callback
     */
    public void authCall(LinkedHashMap<String, String> body, HashMap<String, String> headerMap, Callback callback){
        RCRequest RCRequest = new RCRequest(body, headerMap);
        RCRequest.RCHeaders.setHeader("authorization", "Basic " + this.getApiKey());
        RCRequest.RCHeaders.setHeader("Content-Type", "application/x-www-form-urlencoded");
        HashMap<String, String> options = new HashMap<>();
        options.put("addServer", "true");
        RCRequest.setURL(this.apiURL(RCRequest.getUrl(), options));
        try {
            RCRequest.post(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Makes a call to the POST Subscription api, and with the response, creates a Pubnub subscription
     */
    public void subscribe(){
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        body.put("\"eventFilters\"", "[ \n" +
                "    \"/restapi/v1.0/account/~/extension/~/presence\", \n" +
                "    \"/restapi/v1.0/account/~/extension/~/message-store\" \n" +
                "  ]");
        body.put("\"deliveryMode\"", "{\"transportType\": \"PubNub\",\"encryption\": \"false\"}");
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
                            subscription = new Subscription();
                            subscription.subscribe(responseJson,
                                    new com.pubnub.api.Callback() {

                                        @Override
                                        public void connectCallback(String channel, Object message) {
                                            System.out.println("SUBSCRIBE : CONNECT on channel:" + channel
                                                    + " : " + message.getClass() + " : "
                                                    + message.toString());
                                        }

                                        @Override
                                        public void disconnectCallback(String channel, Object message) {
                                            String decryptedString = subscription.notify(message.toString(), subscription.deliveryMode.encryptionKey);
                                            System.out.print(decryptedString);
                                        }

                                        @Override
                                        public void reconnectCallback(String channel, Object message) {
                                            System.out.println("SUBSCRIBE : RECONNECT on channel:" + channel
                                                    + " : " + message.getClass() + " : " + message.toString());
                                        }

                                        @Override
                                        public void successCallback(String channel, Object message) {
                                            String decryptedString = subscription.notify(message.toString(), subscription.deliveryMode.encryptionKey);
                                            System.out.println(decryptedString);
                                        }

                                        @Override
                                        public void errorCallback(String channel, PubnubError error) {
                                            System.out.println("SUBSCRIBE : ERROR on channel " + channel
                                                    + " : " + error.toString());
                                        }

                                    });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * Makes a DELETE API call for the current subscription, and unsubscribes with Pubnub
     */
    public void removeSubscription() {
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        HashMap<String, String> headers = new HashMap<>();
        headers.put("url", "/restapi/v1.0/subscription" + subscription.id);
        this.delete(headers, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);
                RCResponse rcResponse = new RCResponse(response);
                subscription.unsubscribe();
            }
        });
    }
    /**
     * Sets the header and body to make a GET request
     *
     */
    public void get(HashMap<String, String> headerMap, Callback callback) {
        LinkedHashMap<String, String> body = null;
        this.apiCall("GET", body, headerMap, callback);
    }

    /**
     * Sets the header and body to make a POST request
     *
     */
    public void post(LinkedHashMap<String, String> body, HashMap<String, String> headerMap, Callback callback) {
        this.apiCall("POST", body, headerMap, callback);
    }

    /**
     * Sets up body and header for a PUT request
     *
     */
    public void put(LinkedHashMap<String, String> body, HashMap<String, String> headerMap, Callback callback) {
        this.apiCall("PUT", body, headerMap, callback);
    }

    /**
     * Sets up body and headers for a DELETE request
     *
     */
    public void delete(HashMap<String, String> headerMap, Callback callback) {
        LinkedHashMap<String, String> body = null;
        this.apiCall("DELETE", body, headerMap, callback);
    }
}
