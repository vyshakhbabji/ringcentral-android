package com.ringcentral.rcandroidsdk.rcsdk.platform;

import android.os.Parcelable;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ringcentral.rcandroidsdk.rcsdk.http.RCHeaders;
import com.ringcentral.rcandroidsdk.rcsdk.http.RCRequest;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by andrew.pang on 6/25/15.
 */
public class Platform{
    String appKey;
    String appSecret;
    String server;
    String account = ACCOUNT_ID;
    Auth auth;
    Map<String, String> responseMap;

    static final String AUTHORIZATION = "authorization";
    static final String ACCOUNT_ID = "~";
    static final String ACCOUNT_PREFIX = "/account/";
    static final String URL_PREFIX = "/restapi";
    static final String TOKEN_ENDPOINT = "/restapi/oauth/token";
    static final String REVOKE_ENDPOINT = "/restapi/oauth/revoke";
    static final String API_VERSION = "v1.0";
    static String ACCESS_TOKEN_TTL = "3600"; //60 minutes
    //static String REFRESH_TOKEN_TTL = "36000";  // 10 hours
    static String REFRESH_TOKEN_TTL = "604800";  // 1 week

    public Platform(String appKey, String appSecret, String server){
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.server = server;
        this.auth = new Auth();
    }

    public void setAuthData(Map<String, String> parameters){
        this.auth.setData(parameters);
    }

    public Auth getAuthData(){
        return auth.getData();
    }

    public String getAccessToken(){
        return this.auth.getAccessToken();
    }

    public void isAuthorized() throws Exception{
        if(!this.auth.isAccessTokenValid()){
            this.refresh();
        }
        if(!this.auth.isAccessTokenValid()){
            throw new Exception("Access token is expired");
        }
    }

    public void refresh() throws Exception{
        if(!this.auth.isRefreshTokenValid()){
            throw new Exception("Refresh token is expired");
        } else{
            HashMap<String, String> body = new HashMap<>();
            //Body
            body.put("grant_type", "password");
            body.put("refresh_token", this.auth.getRefreshToken());
            body.put("access_token_ttl", ACCESS_TOKEN_TTL);
            body.put("refresh_token_ttl", REFRESH_TOKEN_TTL);
            //Header
            HashMap<String, String> headerMap = new HashMap<>();
            headerMap.put("method", "POST");
            headerMap.put("url", TOKEN_ENDPOINT);
            this.authCall(body, headerMap);
        }
    }

    public void logout(){
        HashMap<String, String> body = new HashMap<>();
        body.put("token", this.getAccessToken());
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("method", "POST");
        headerMap.put("url", REVOKE_ENDPOINT);
        headerMap.put(RCHeaders.CONTENT_TYPE, RCHeaders.URL_ENCODED_CONTENT_TYPE);
        this.logoutPost(body, headerMap);
        this.auth.reset();
    }

    public void authorize(String username, String extension, String password){
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
        headerMap.put("url", TOKEN_ENDPOINT);
        this.authCall(body, headerMap);
    }

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

    public String apiURL(String url, HashMap<String, String> options){
        String builtUrl = "";
        boolean has_http = url.contains("http://") || url.contains("https://");
        if(options.containsKey("addServer") && !has_http){
            builtUrl += this.server;
        }
        if(url.contains(URL_PREFIX) == false && !has_http){
            builtUrl += URL_PREFIX + "/" + API_VERSION;
        }

        if(url.contains(ACCOUNT_PREFIX) == true){
            builtUrl = builtUrl.replace(ACCOUNT_PREFIX + ACCOUNT_ID, ACCOUNT_PREFIX + this.account);
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

    public void authCall(HashMap<String, String> body, HashMap<String, String> headerMap){
        RCRequest RCRequest = new RCRequest(body, headerMap);
        RCRequest.RCHeaders.setHeader(AUTHORIZATION, "Basic " + this.getApiKey());
        RCRequest.RCHeaders.setHeader(RCHeaders.CONTENT_TYPE, RCHeaders.URL_ENCODED_CONTENT_TYPE);
        HashMap<String, String> options = new HashMap<>();
        options.put("addServer", "true");
        RCRequest.setURL(this.apiURL(RCRequest.getUrl(), options));
        try {
            RCRequest.post(new Callback() {
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
                    responseMap = gson.fromJson(responseString, mapType);
                    setAuthData(responseMap);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void get(HashMap<String, String> body, HashMap<String, String> headerMap) {
        RCRequest RCRequest = new RCRequest(body, headerMap);
        RCRequest.setMethod("GET");
        RCRequest.RCHeaders.setHeader(AUTHORIZATION, this.auth.getTokenType() + " " + this.getAccessToken());
        HashMap<String, String> options = new HashMap<>();
        options.put("addServer", "true");
        RCRequest.setURL(this.apiURL(RCRequest.getUrl(), options));
        try {
            RCRequest.get(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    e.printStackTrace();
                }
                @Override
                public void onResponse(Response response) throws IOException {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    String responseString = response.body().string();
                    System.out.print(responseString);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void post(HashMap<String, String> body, HashMap<String, String> headerMap) {
        RCRequest RCRequest = new RCRequest(body, headerMap);
        HashMap<String, String> options = new HashMap<>();
        options.put("addServer", "true");
        RCRequest.setURL(this.apiURL(RCRequest.getUrl(), options));
        RCRequest.setMethod("POST");
        RCRequest.RCHeaders.setHeader(AUTHORIZATION, "Bearer " + this.getAccessToken());
        try {
            RCRequest.post(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    String responseString = response.body().string();
                    System.out.print(responseString);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logoutPost(HashMap<String, String> body, HashMap<String, String> headerMap) {
        RCRequest RCRequest = new RCRequest(body, headerMap);
        HashMap<String, String> options = new HashMap<>();
        options.put("addServer", "true");
        RCRequest.setURL(this.apiURL(RCRequest.getUrl(), options));
        RCRequest.setMethod("POST");
        RCRequest.RCHeaders.setHeader(AUTHORIZATION, "Basic " + this.getApiKey());
        try {
            RCRequest.post(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    e.printStackTrace();
                }
                @Override
                public void onResponse(Response response) throws IOException {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    String responseString = response.body().string();
                    System.out.print(responseString);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void put(HashMap<String, String> body, HashMap<String, String> headerMap) {
        RCRequest RCRequest = new RCRequest(body, headerMap);
        RCRequest.setMethod("PUT");
        //this.apiCall(RCRequest);
    }

    public void delete(HashMap<String, String> body, HashMap<String, String> headerMap) {
        RCRequest RCRequest = new RCRequest(body, headerMap);
        HashMap<String, String> options = new HashMap<>();
        options.put("addServer", "true");
        RCRequest.setURL(this.apiURL(RCRequest.getUrl(), options));
        RCRequest.setMethod("DELETE");
        RCRequest.RCHeaders.setHeader(AUTHORIZATION, "Bearer " + this.getAccessToken());
        try {
            RCRequest.delete(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    String responseString = response.body().string();
                    System.out.print(responseString);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
