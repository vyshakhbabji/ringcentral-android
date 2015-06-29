package com.ringcentral.rcandroidsdk.rcsdk.platform;

import android.util.Base64;

import com.ringcentral.rcandroidsdk.rcsdk.http.Headers;
import com.ringcentral.rcandroidsdk.rcsdk.http.Request;
import com.ringcentral.rcandroidsdk.rcsdk.http.RequestAsyncTask;
import com.ringcentral.rcandroidsdk.rcsdk.http.Response;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by andrew.pang on 6/25/15.
 */
public class Platform implements RequestAsyncTask.RequestResponse{
    String appKey;
    String appSecret;
    String server;
    Auth auth;

    static final String AUTHORIZATION = "authorization";
//    ACCOUNT_ID = '~'
//    ACCOUNT_PREFIX = '/account/'
//    URL_PREFIX = '/restapi'
//    TOKEN_ENDPOINT = '/restapi/oauth/token'
//    REVOKE_ENDPOINT = '/restapi/oauth/revoke'
//    API_VERSION = 'v1.0'
    static String ACCESS_TOKEN_TTL = "3600"; //60 minutes
    //static String REFRESH_TOKEN_TTL = "36000"  // 10 hours
    static String REFRESH_TOKEN_TTL = "604800";  // 1 week

    public Platform(String appKey, String appSecret, String server){
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.server = server;
        this.auth = new Auth();
    }

    public void setAuthData(HashMap<String, String> parameters){
        this.auth.setData(parameters);
    }

    public HashMap<String, String> getAuthData(){
        return auth.getData();
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
        //Hard coded
        headerMap.put("url", "https://platform.devtest.ringcentral.com/restapi/oauth/token");
        this.authCall(body, headerMap);
    }

    public void authCall(HashMap<String, String> body, HashMap<String, String> headerMap){
        Request request = new Request(body, headerMap);
        request.headers.setHeader(AUTHORIZATION, "Basic " + this.getApiKey());
        request.headers.setHeader(Headers.CONTENT_TYPE, Headers.URL_ENCODED_CONTENT_TYPE);
        request.setURL(request.getUrl());

        //Calls async send
        RequestAsyncTask requestTask = new RequestAsyncTask(request);
        requestTask.delegate = this;
        requestTask.execute();
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

    public void RequestResponseProcessFinish(Map result){
        Map<String, String> c = result;
        //this.auth.setData(result);
    }

}
