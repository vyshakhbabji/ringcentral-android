package com.ringcentral.rc_android_sdk.rcsdk.platform;

import android.util.Base64;

import com.pubnub.api.PubnubError;
import com.ringcentral.rc_android_sdk.rcsdk.http.Transaction;
import com.ringcentral.rc_android_sdk.rcsdk.subscription.Subscription;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by andrew.pang on 8/13/15.
 */
public class Platform implements Serializable {
    String appKey;
    String appSecret;
    String server;
    String account = "~";
    public Auth auth;
    Subscription subscription;

    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    public static final MediaType JSON_TYPE_MARKDOWN
            = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType MULTI_TYPE_MARKDOWN
            = MediaType.parse("multipart/mixed; boundary=Boundary_1_14413901_1361871080888");

    /**
     *
     * @param server Pass in either "SANDBOX" or "PRODUCTION"
     */
    public Platform(String appKey, String appSecret, String server){
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

    public void setServer(String server) {
        this.server = server;
    }

    public String getServer() {
        return server;
    }

    /**
     * Sets the app credentials which are the appKey and appSecret
     */
    public void setAppCredentials(String appKey, String appSecret){
        this.appKey = appKey;
        this.appSecret = appSecret;
    }

    /**
     * Gets a hashmap that contains the keys, "appKey" and "appSecret"
     *
     */
    public HashMap<String, String> getAppCredentials(){
        HashMap<String, String> appCredentials = new HashMap<>();
        appCredentials.put("appKey", this.appKey);
        appCredentials.put("appSecret", this.appSecret);
        return appCredentials;
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
        //When encoding with Android's Base64 API,  '\n' is automatically added, so it needs to be removed
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
        if(!(url.contains("/restapi")) && !has_http){
            builtUrl += "/restapi" + "/" + "v1.0";
        }

        if(url.contains("/account/")){
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
     * Takes the body and prepares it to be passed in the HTTP request as a string, based on the MediaType of the body
     *
     * @return
     */
    public String getBodyString(HashMap<String, String> body, MediaType mediaType){
        String bodyString = "";
        try {
            StringBuilder data = new StringBuilder();
            int count = 0;
            if(!(mediaType == MEDIA_TYPE_MARKDOWN)){
                data.append("{ ");
            }
            //Iterate through the HashMap
            for(Map.Entry<String, String> entry: body.entrySet()){
                //If the MediaType is 'x-www-form-urlencoded', then encode the body
                if(mediaType == MEDIA_TYPE_MARKDOWN) {
                    if (count != 0) {
                        data.append("&");
                    }
                    data.append(entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
                    count++;
                }
                else{
                    if (count != 0) {
                        data.append(", ");
                    }
                    data.append(entry.getKey());
                    data.append(": ");
                    data.append(entry.getValue());
                    count++;
                }
            }
            if(!(mediaType == MEDIA_TYPE_MARKDOWN)){
                data.append(" }");
            }
            bodyString = data.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bodyString;
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
        //If after calling a refresh, the accessToken is still not valid, throw exception
        if(!this.auth.isAccessTokenValid()){
            throw new Exception("Access token is expired");
        }
    }

    /**
     * Method used for API calls, with the request type, body, headers, and callback as parameters.
     *
     */
    public void apiCall(String method, String url, LinkedHashMap<String, String> body, HashMap<String, String> headerMap, Callback callback) {
        try {
            OkHttpClient client = new OkHttpClient();
            //Check if the Platform is authorized, and add the authorization header
            this.isAuthorized();
            headerMap.put("authorization", this.getAuthHeader());
            //Generate the proper url to be passed into the request
            HashMap<String, String> options = new HashMap<>();
            options.put("addServer", "true");
            String apiUrl = apiURL(url, options);

            Request.Builder requestBuilder = new Request.Builder();
            //Add all the headers to the Request.Builder from the headerMap
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
            Request request = null;
            if (method.toUpperCase().equals("GET")) {
                request = requestBuilder
                        .url(apiUrl)
                        .build();
            } else if (method.toUpperCase().equals("DELETE")) {
                request = requestBuilder
                        .url(apiUrl)
                        .delete()
                        .build();
            } else {
                //For POST and PUT requests, find and set what MediaType the body is
                MediaType mediaType;
                if (headerMap.containsValue("application/json")) {
                    mediaType = JSON_TYPE_MARKDOWN;
                } else if (headerMap.containsValue("multipart/mixed")) {
                    mediaType = MULTI_TYPE_MARKDOWN;
                } else {
                    mediaType = MEDIA_TYPE_MARKDOWN;
                }
                String bodyString = getBodyString(body, mediaType);
                if (method.toUpperCase().equals("POST")) {
                    request = requestBuilder
                            .url(apiUrl)
                            .post(RequestBody.create(mediaType, bodyString))
                            .build();
                } else if (method.toUpperCase().equals("PUT")) {
                    request = requestBuilder
                            .url(apiUrl)
                            .put(RequestBody.create(mediaType, bodyString))
                            .build();
                }
            }
            //Make OKHttp request call, that returns response to the callback
            client.newCall(request).enqueue(callback);
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
        String url = "/restapi/oauth/token";
        //Body
        body.put("grant_type", "password");
        body.put("username", username);
        body.put("extension", extension);
        body.put("password", password);
        //Header
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("authorization", "Basic " + this.getApiKey());
        headerMap.put("Content-Type", "application/x-www-form-urlencoded");
        this.authCall(url,body, headerMap, callback);
    }

    /**
     * POST request set up for making authorization calls
     */

    public void authCall(String url, LinkedHashMap<String, String> body, HashMap<String, String> headerMap, Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder();
        for(Map.Entry<String, String> entry: headerMap.entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        Request request = null;
        MediaType mediaType = MEDIA_TYPE_MARKDOWN;
        String bodyString = getBodyString(body, mediaType);
        HashMap<String, String> options = new HashMap<>();
        options.put("addServer", "true");
        String apiUrl = apiURL(url, options);
        request = requestBuilder
                        .url(apiUrl)
                        .post(RequestBody.create(mediaType, bodyString))
                        .build();
        client.newCall(request).enqueue(callback);
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
            String url = "/restapi/oauth/token";
            //Body
            body.put("grant_type", "refresh_token");
            body.put("refresh_token", this.auth.getRefreshToken());
            //Header
            HashMap<String, String> headerMap = new HashMap<>();
            headerMap.put("method", "POST");
            //Makes an auth call with the refresh token and sets the Auth data with the new response
            this.authCall(url, body, headerMap,
                    new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            e.printStackTrace();
                        }
                        @Override
                        public void onResponse(Response response) throws IOException {
                            if (!response.isSuccessful())
                                throw new IOException("Unexpected code " + response);
                            Transaction transaction = new Transaction(response);
                            HashMap<String, String> responseMap = transaction.getAuthJson();
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
        String url = "/restapi/oauth/revoke";
        headerMap.put("method", "POST");
        headerMap.put("Content-Type", "application/x-www-form-urlencoded");
        this.authCall(url, body, headerMap, callback);
        this.auth.reset();
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
        String url = "/restapi/v1.0/subscription";
        headers.put("Content-Type", "application/json");
        //Makes a POST request to the RingCentral API to receive PubNub info
        this.post(url, body, headers,
                new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (!response.isSuccessful())
                            throw new IOException("Unexpected code " + response);
                        Transaction transaction = new Transaction(response);
                        try {
                            JSONObject responseJson = new JSONObject(transaction.getBodyString());
                            subscription = new Subscription();
                            //With the response from the RingCentral API, make a subscription call to PubNub
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
                                            //Decrypt the PubNub message
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
        String url =  "/restapi/v1.0/subscription" + subscription.id;
        this.delete(url, headers, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);
                Transaction transaction = new Transaction(response);
                subscription.unsubscribe();
            }
        });
    }
    /**
     * Sets the header and body to make a GET request
     *
     */
    public void get(String url, HashMap<String, String> headerMap, Callback callback) {
        LinkedHashMap<String, String> body = null;
        this.apiCall("GET", url, body, headerMap, callback);
    }

    /**
     * Sets the header and body to make a POST request
     *
     */
    public void post(String url, LinkedHashMap<String, String> body, HashMap<String, String> headerMap, Callback callback) {
        this.apiCall("POST", url, body, headerMap, callback);
    }

    /**
     * Sets up body and header for a PUT request
     *
     */
    public void put(String url, LinkedHashMap<String, String> body, HashMap<String, String> headerMap, Callback callback) {
        this.apiCall("PUT", url, body, headerMap, callback);
    }

    /**
     * Sets up body and headers for a DELETE request
     *
     */
    public void delete(String url, HashMap<String, String> headerMap, Callback callback) {
        LinkedHashMap<String, String> body = null;
        this.apiCall("DELETE", url, body, headerMap, callback);
    }

}
