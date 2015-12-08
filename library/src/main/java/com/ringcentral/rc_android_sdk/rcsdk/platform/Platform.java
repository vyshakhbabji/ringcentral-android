/*
 * Copyright (c) 2015 RingCentral, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package com.ringcentral.rc_android_sdk.rcsdk.platform;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ringcentral.rc_android_sdk.rcsdk.http.Client;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Request.Builder;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * Created by vyshakh.babji on 11/5/15.
 */

public class Platform {
    /*
    Revoke Session Endpoint
     */
    final String REVOKE_ENDPOINT_URL = "/restapi/oauth/revoke";

   /*
   *
   Authentication  and Refresh Token Endpoint
    */

    final String TOKEN_ENDPOINT_URL = "/restapi/oauth/token";


    protected String appKey;
    protected String appSecret;
    protected Server server;


    protected Auth auth;

    protected Request request;

    protected Client client;

    Object lock = new Object();


    /**
     * Creates Platform object
     *
     * @param client
     * @param appKey
     * @param appSecret
     * @param server
     */

    public Platform(Client client, String appKey, String appSecret, Server server) {
        super();
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.server = server;
        this.auth = new Auth();
        this.client = client;
    }

    /**
     * @return Base 64 encoded app credentials
     */

    protected String apiKey() {
        return Credentials.basic(appKey, appSecret);
    }

    /**
     * @return Authorization Header
     */

    protected String authHeader() {
        return this.auth.tokenType() + " " + this.auth.access_token;
    }

    /**
     * Checks if the current access token is valid. If the access token is expired, it does token refresh.
     * FIXME This is asynchronous method, so it must accept a callback
     */
    public boolean ensureAuthentication() {


        if(auth().accessTokenValid()){
            return true;
        }
        else{
            synchronized (lock){

                refresh(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        throw new AuthException("Ensure Authentication Failed.");
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (response.isSuccessful()){
                            return;
                        }
                    }
                });

            }
            return true;
        }







//        if (!this.auth.accessTokenValid()) {
//            this.refresh(new Callback() {
//                @Override
//                public void onFailure(Request request, IOException e) {
//
//                }
//
//                @Override
//                public void onResponse(Response response) {
//                    if (response.isSuccessful())
//                        return;
//                    else
//                        throw new AuthException("Ensure Authentication Failed.");
//
//                }
//            });
//            return false;
//        } else
//            return true;
    }


    /**
     * Sets Request body for content type FORM_TYPE_MARKDOWN("application/x-www-form-urlencoded")
     *
     * @param body
     * @return
     */
    protected RequestBody formBody(HashMap<String, String> body) {
        FormEncodingBuilder formBody = new FormEncodingBuilder();
        for (HashMap.Entry<String, String> entry : body.entrySet())
            formBody.add(entry.getKey(), entry.getValue());
        return formBody.build();
    }

    /**
     * Get Auth object
     *
     * @return
     */
    public Auth auth() {
        return auth;
    }

    /**
     * Checks if the login is valid
     */
    public boolean loggedIn() {
        return this.auth.accessTokenValid();
    }

    /**
     * Sets Login Credentials for authentication
     *
     * @param userName
     * @param extension
     * @param password
     * @param callback
     */
    public void login(String userName, String extension, String password, Callback callback) throws AuthException {


        HashMap<String, String> body = new HashMap<String, String>();
        body.put("username", userName);
        body.put("password", password);
        body.put("extension", extension);
        body.put("grant_type", "password");
        requestToken(TOKEN_ENDPOINT_URL, body, callback);
    }

    /**
     * Sets Request Header
     *
     * @param hm
     * @return
     * @throws IOException
     * FIXME Async
     */
    public Builder inflateRequest(HashMap<String, String> hm) {
        //add user-agent
        if (hm == null) {
            hm = new HashMap<String, String>();
            if (ensureAuthentication())
                hm.put("Authorization", authHeader());
        }
        Builder requestBuilder = new Request.Builder();
        for (Entry<String, String> entry : hm.entrySet())
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        return requestBuilder;
    }

    /**
     * Sets authentication values after successful authentication
     *
     * @param response
     */
    protected void setAuth(Response response) {
        try {
            this.auth.setData(jsonToHashMap(response));
        } catch (Exception e) {
            e.printStackTrace(); //FIXME
        }

    }

    /**
     * Creates request object
     *
     * @param endpoint
     * @param body
     * @param callback
     */
    protected void requestToken(String endpoint, HashMap<String, String> body, final Callback callback) throws AuthException {

        final String URL = server.value + endpoint;
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", apiKey());
        headers.put("Content-Type", ContentTypeSelection.FORM_TYPE_MARKDOWN.value.toString());
        request = inflateRequest(headers).url(URL).post(formBody(body)).build();
        final Callback c = new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                throw new AuthException("Unable to request token.", e); //FIXME Call the callback instead of throwing
            }

            @Override
            public void onResponse(Response response) throws IOException {
                setAuth(response);
                callback.onResponse(response);
            }
        };
        client.loadResponse(request, c);
    }


    /**
     * Sets new access and refresh tokens
     *
     * @param callback
     * @throws Exception
     */
    public void refresh(Callback callback) throws AuthException {

        try {

            if (!this.auth.refreshTokenValid()) {
                throw new IOException("Refresh Token has Expired");
            } else {
                HashMap<String, String> body = new HashMap<String, String>();
                body.put("grant_type", "refresh_token");
                body.put("refresh_token", this.auth.refreshToken());
                requestToken(TOKEN_ENDPOINT_URL, body, callback);
            }
        } catch (IOException e) {
            throw new AuthException("Unable to refresh.", e);
        }
    }

    /**
     * Revoke current session
     *
     * @param callback
     */
    public void logout(Callback callback) throws AuthException {
        HashMap<String, String> body = new HashMap<String, String>();
        body.put("access_token", this.auth.access_token);
        requestToken(REVOKE_ENDPOINT_URL, body, callback);
        this.auth.reset(); //FIXME This should go inside the callback
    }


    /**
     * Send API Request
     *
     * @param method
     * @param apiURL
     * @param body
     * @param headerMap
     * @param callback
     */
    public void sendRequest(String method, String apiURL, RequestBody body, HashMap<String, String> headerMap, final Callback callback) {

        final String URL = server.value + apiURL;
        try {
            ensureAuthentication(); //FIXME Async
            request = client.createRequest(method, URL, body, inflateRequest(headerMap));
            client.loadResponse(request, callback);
        } catch (AuthException e) {
            throw new AuthException("Unable to make API Call.", e);
        }
    }

    /**
     * Sets auth data
     *
     * @param response
     * @return
     * @throws IOException
     */
    protected HashMap<String, String> jsonToHashMap(Response response) throws AuthException {
        try {
            if (response.isSuccessful()) {
                Gson gson = new Gson();
                Type HashMapType = new TypeToken<HashMap<String, String>>() {
                }.getType();
                String responseString = response.body().string();
                Log.v("OAuth Response :", responseString);
                return gson.fromJson(responseString, HashMapType);
            } else {
                Log.v("Error Message: ", "HTTP Status Code " + response.code() + " " + response.message());
                return new HashMap<>();
            }
        } catch (IOException e) {
            throw new AuthException("Unable to request token.", e);
        }
    }

    /**
     * Sets content-type
     * FIXME Change naming
     */
    public enum ContentTypeSelection {
        FORM_TYPE_MARKDOWN("application/x-www-form-urlencoded"), JSON_TYPE_MARKDOWN(
                "application/json"), MULTIPART_TYPE_MARKDOWN("multipart/mixed;");
        protected MediaType value;

        private ContentTypeSelection(String contentType) {
            this.value = MediaType.parse(contentType);
        }
    }

    /**
     * RingCentral API Endpoint Server. See
     * <a href ="https://developer.ringcentral.com/api-docs/latest/index.html#!#Resources.html">Server Endpoint</a> for more information.
     */
    public enum Server {
        PRODUCTION("https://platform.ringcentral.com"), SANDBOX(
                "https://platform.devtest.ringcentral.com");
        private String value;

        Server(String url) {
            this.value = url;
        }
    }

    //FIXME get, post, put, delete methods are missing

}