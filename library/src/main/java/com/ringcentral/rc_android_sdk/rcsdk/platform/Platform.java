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

import android.os.Build;
import android.util.Log;

import com.ringcentral.rc_android_sdk.rcsdk.http.APICallback;
import com.ringcentral.rc_android_sdk.rcsdk.http.APIException;
import com.ringcentral.rc_android_sdk.rcsdk.http.APIResponse;
import com.ringcentral.rc_android_sdk.rcsdk.http.Client;
import com.ringcentral.rc_android_sdk.rcsdk.http.RingCentralException;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Request.Builder;
import com.squareup.okhttp.RequestBody;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;


public class Platform {
    /*
    Revoke Session Endpoint
     */
    private static final String REVOKE_ENDPOINT_URL = "/restapi/oauth/revoke";

    /*
    Authentication and Refresh Token Endpoint
     */
    private static final String TOKEN_ENDPOINT_URL = "/restapi/oauth/token";
    private static final String  USER_AGENT  = "Android "+Build.VERSION.SDK_INT+"/Build "+ Build.VERSION.RELEASE + "/RCAndroidSDK";

    protected String appKey;
    protected String appSecret;
    protected Server server;

    protected Auth auth;
    //protected Request request;
    protected Client client;
    protected Queue<APICallback> queue = new LinkedBlockingQueue<>();
    protected Builder requestBuilder;
    Object lock = new Object();
    boolean refreshInProgress;
    boolean state;

    /**
     * Creates Platform object
     *
     * @param client
     * @param appKey
     * @param appSecret
     * @param server
     */
    public Platform(Client client, String appKey, String appSecret, Server server) {
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
     * @return Authorization Header "bearer @accesstoken"
     */
    protected String authHeader() {
        return this.auth.tokenType() + " " + this.auth.access_token;
    }

    /**
     * Checks if the current access token is valid. If the access token is expired, it does token refresh.
     * If refresh is not needed null will be returned as response
     */
    protected void ensureAuthentication(final APICallback callback) throws APIException {
        if (auth.accessTokenValid()) {
            callback.onResponse(null);
        } else {
            refreshInProgress = true;
            refresh(callback);
        }
    }

    /**
     * Sets Request body for content type FORM_TYPE("application/x-www-form-urlencoded")
     *
     * @param body Input body as key:value pairs
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
     * @return Auth Object
     */
    public Auth auth() {
        return auth;
    }

    /**
     * Checks if the login is valid
     */
    public void loggedIn(final LoggedInCallback callback) {
        ensureAuthentication(new APICallback() {
            @Override
            public void onResponse(APIResponse response) {
                if (response == null)
                    callback.onResponse(true);
            }

            @Override
            public void onFailure(APIException e) {
                callback.onResponse(false);
            }
        });
    }

    /**
     * Sets Login Credentials for authentication
     *
     * @param userName
     * @param extension
     * @param password
     * @param callback
     */
    public void login(String userName, String extension, String password, APICallback callback) throws APIException {
        HashMap<String, String> body = new HashMap<String, String>();
        body.put("username", userName);
        body.put("password", password);
        body.put("extension", extension);
        body.put("grant_type", "password");
        requestToken(TOKEN_ENDPOINT_URL, body, callback);
    }

    /**
     * Actual function that inflates request
     * @param request
     * @return
     */
    protected Request inflateRequestHeaders(boolean skipAuthCheck, final Request request) {
        if (!auth().accessTokenValid()&&!skipAuthCheck) {
            throw new RingCentralException("Internal inflate request has been called without authentication");
        }
        Builder requestBuilder = request.newBuilder();
        if(!skipAuthCheck)
        {
            requestBuilder.addHeader("Authorization", authHeader());
        }
        //      requestBuilder.addHeader("User-Agent", USER_AGENT);

        return requestBuilder.build();

    }


    /**
     * Sets Request Header
     *
     * @param request
     * @param callback
     */
    public void inflateRequest(final Request request, final boolean skipAuthCheck,final InflateCallback callback) throws APIException {
        if (!skipAuthCheck) {
            ensureAuthentication(new APICallback() {
                @Override
                public void onResponse(APIResponse response) {
                    if(response==null){
                        Request inflatedRequest = inflateRequestHeaders(skipAuthCheck,request);
                        callback.onResponse(inflatedRequest);
                    }
                }

                @Override
                public void onFailure(APIException e) {
                    callback.onFailure(e); }
            });
        } else {
            Request inflatedRequest = inflateRequestHeaders(skipAuthCheck,request);
            callback.onResponse(inflatedRequest);
        }
    }

    /**
     * Sets authentication values after successful authentication
     *
     * @param response
     */
    protected void setAuth(APIResponse response) throws APIException {
        this.auth.setData(auth.jsonToHashMap(response));
    }

    /**
     * Creates request object
     *
     * @param endpoint
     * @param body
     * @param callback
     */
    protected void requestToken(String endpoint, final HashMap<String, String> body, final APICallback callback) throws APIException {
        final String URL = server.value+endpoint;
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", apiKey());
        headers.put("Content-Type", ContentTypeSelection.FORM_TYPE.value.toString());
        Request request = client.createRequest("POST", URL, formBody(body), headers);
        sendRequest(request,true, new APICallback() {
            @Override
            public void onFailure(APIException e) {
                callback.onFailure(new APIException("Unable to request token. Request Failed.", e));
            }

            @Override
            public void onResponse(APIResponse response) {
                try {
                    setAuth(response);
                    callback.onResponse(response);
                } catch (APIException e) {
                    callback.onFailure(e);
                }
            }
        });
    }


    public void refresh(final APICallback callback) throws APIException {

        ExecutorService executorService = Executors.newCachedThreadPool();

        synchronized (lock) {
            if (refreshInProgress == false) {
                refreshInProgress = true;
            }
            queue.add(callback);
        }

        synchronized (lock) {
            try {
                Future future = executorService.submit(new Runnable() {
                    public void run() {
                        Log.v("Queue" , String.valueOf(queue.size())); //FIXME
                        try {
                            makeRefresh(new APICallback() {

                                public void onResponse(APIResponse response) {
                                    while (!queue.isEmpty()) {
                                        APICallback c = queue.poll();
                                        c.onResponse(response);
                                        Log.v("dequeue", String.valueOf(queue.size()));
                                    }
                                }

                                @Override
                                public void onFailure(APIException e) {
                                    while (!queue.isEmpty()) {
                                        APICallback c = queue.poll();
                                        c.onFailure(e);
                                        Log.v("dequeue ", String.valueOf(queue.size()));
                                    }
                                }
                            });
                        } catch (APIException e) {
                            callback.onFailure(e);
                        }
                    }
                });
                while (!future.isDone())
                    future.get();
                Log.v("future.get() = ", String.valueOf(future.isDone()));
            } catch (InterruptedException e) {
                //FIXME Callback
                throw new RuntimeException("Interupted exception Occured while refreshing. Refresh Failed.Try logging in Again");
            } catch (ExecutionException e) {
                //FIXME Callback
                throw new RuntimeException("Thread execution exception Occured while refreshing. Refresh Failed. Try logging in Again");
            }
        }


        synchronized (lock) {
            refreshInProgress = false;
        }
    }

    protected void makeRefresh(final APICallback callback) throws APIException {
        if (!this.auth.refreshTokenValid()) {
//                  callback.onFailure(new APIException(null, new IOException("Refresh Token is Invalid"))); //FIXME
        } else {
            HashMap<String, String> body = new HashMap<String, String>();
            body.put("grant_type", "refresh_token");
            body.put("refresh_token", this.auth.refreshToken());

            requestToken(TOKEN_ENDPOINT_URL, body, callback);
        }
    }

    /**
     * Revoke current session
     *
     * @param callback
     */
    public void logout(final APICallback callback) throws APIException {
        HashMap<String, String> body = new HashMap<String, String>();
        body.put("access_token", this.auth.access_token);
        requestToken(REVOKE_ENDPOINT_URL, body, new APICallback() {
            public void onFailure(APIException e) { callback.onFailure(e); }
            public void onResponse(APIResponse response) {
                auth.reset();
                callback.onResponse(response);
            }
        });
    }

    protected void sendRequest(Request request, boolean skipAuthentication, final APICallback callback) throws APIException {
        inflateRequest(request, skipAuthentication, new InflateCallback() {
            public void onResponse(Request inflatedRequest) {
                client.sendRequest(inflatedRequest, callback);
            }

            public void onFailure(APIException e) {
                callback.onFailure(e);
            }
        });
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
    public void send(String method, String apiURL, RequestBody body, HashMap<String, String> headerMap, final APICallback callback) throws APIException {
        final String endpointURL=server.value+apiURL;
        Request request = client.createRequest(method, endpointURL, body == null ? null : body, headerMap);
        sendRequest(request,false, callback);
    }

    public void get(String apiURL, RequestBody body, HashMap<String, String> headerMap, final APICallback callback) throws APIException {
        send("get", apiURL, body == null ? null : body, headerMap, callback);
    }

    public void post(String apiURL, RequestBody body, HashMap<String, String> headerMap, final APICallback callback) throws APIException {
        send("post", apiURL, body, headerMap, callback);
    }

    public void put(String apiURL, RequestBody body, HashMap<String, String> headerMap, final APICallback callback) throws APIException {
        send("put", apiURL, body, headerMap, callback);
    }

    public void delete(String apiURL, RequestBody body, HashMap<String, String> headerMap, final APICallback callback) throws APIException {
        send("delete", apiURL, body == null ? null : body, headerMap, callback);
    }

    public void expire_access() {
        auth().expire_access();
    } //FIXME Remove

    /**
     * Sets content-type
     */
    public enum ContentTypeSelection {
        FORM_TYPE("application/x-www-form-urlencoded"), JSON_TYPE(
                "application/json"), MULTIPART_TYPE("multipart/mixed;");
        protected MediaType value;

        private ContentTypeSelection(String contentType) {
            this.value = MediaType.parse(contentType);
        }
    }

    /**
     * RingCentral API Endpoint Server. See
     * "https://developer.ringcentral.com/api-docs/latest/index.html#!#Resources.html" Server Endpoint for more information.
     */
    public enum Server {
        PRODUCTION("https://platform.ringcentral.com"), SANDBOX(
                "https://platform.devtest.ringcentral.com");
        private String value;

        Server(String url) {
            this.value = url;
        }
    }

}