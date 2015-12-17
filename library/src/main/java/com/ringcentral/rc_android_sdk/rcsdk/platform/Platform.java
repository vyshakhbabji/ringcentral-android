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

import com.ringcentral.rc_android_sdk.rcsdk.http.APICallback;
import com.ringcentral.rc_android_sdk.rcsdk.http.APIResponse;
import com.ringcentral.rc_android_sdk.rcsdk.http.Client;
//import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Request.Builder;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
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
    final String REVOKE_ENDPOINT_URL = "/restapi/oauth/revoke";

   /*
   Authentication and Refresh Token Endpoint
    */
    final String TOKEN_ENDPOINT_URL = "/restapi/oauth/token";


    protected String appKey;
    protected String appSecret;
    protected Server server;

    protected Auth auth;
    protected Request request;
    protected Client client;

    Object lock = new Object();
    boolean refreshInProgress;
    boolean state;

    protected Queue<APICallback> queue = new LinkedBlockingQueue<>();

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
     *
     */
    protected void ensureAuthentication(final APICallback callback) throws AuthException {
        if (!loggedIn()) {
            refreshInProgress = true;
            refresh(callback);
        }
        else{
            client._response(callback);
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
    public void login(String userName, String extension, String password, APICallback callback) throws AuthException {
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
     * @return fombody
     * FIXME Async :  Fixed
     */
    protected Builder inflateRequest(HashMap<String, String> hm) {
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
    protected void setAuth(APIResponse response) throws IOException {
        this.auth.setData(auth.jsonToHashMap(response.response()));
    }

    /**
     * Creates request object
     *
     * @param endpoint
     * @param body
     * @param callback
     */
    protected void requestToken(String endpoint, HashMap<String, String> body, final APICallback callback) throws AuthException {
        final String URL = server.value + endpoint;
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", apiKey());
        headers.put("Content-Type", ContentTypeSelection.FORM_TYPE.value.toString());
        request = inflateRequest(headers).url(URL).post(formBody(body)).build();
        final APICallback c = new APICallback() {
            @Override
            public void onAPIFailure(Request request, IOException e) {
                callback.onAPIFailure(request, e);
            }

            @Override
            public void onAPIResponse(APIResponse response) throws IOException {
                try {
                    setAuth(response);
                    callback.onAPIResponse(response);
                } catch (IOException e) {
                    callback.onAPIFailure(response.request(), new IOException("IOException Occured. Failed Logout with error code " + response.ok()));
                }
            }
        };
        client.sendRequest(request, c);
    }



    public  void refresh(final APICallback callback) throws AuthException {

        ExecutorService executorService = Executors.newCachedThreadPool();

        synchronized(lock){
            if (refreshInProgress == false) {
                refreshInProgress = true;
            }
            queue.add(callback);
        }

        synchronized (lock) {
            Future future = executorService.submit(new Runnable() {
                public void run() {
                        System.out.println("Queue" + String.valueOf(queue.size()));
<<<<<<< HEAD
                        makeRefresh(new APICallback() {

                            public void onAPIResponse(APIResponse response) throws IOException {
                                while (!queue.isEmpty()) {
                                    APICallback c = queue.poll();
                                    c.onAPIResponse(response);
=======
                        makeRefresh(new Callback() {
                            @Override
                            public void onResponse(Response response) throws IOException {
                                while (!queue.isEmpty()) {
                                    Callback c = queue.poll();
                                    c.onResponse(response);
>>>>>>> f35ebd5902482eda5b90874d7e689071c019ffdc
                                    System.out.println("dequeue " + queue.size());
                                }
                            }

                            @Override
<<<<<<< HEAD
                            public void onAPIFailure(Request request, IOException e) {
                                while (!queue.isEmpty()) {
                                    APICallback c = queue.poll();
                                    c.onAPIFailure(request, e);
=======
                            public void onFailure(Request request, IOException e) {
                                while (!queue.isEmpty()) {
                                    Callback c = queue.poll();
                                    c.onFailure(request, e);
>>>>>>> f35ebd5902482eda5b90874d7e689071c019ffdc
                                    System.out.println("dequeue " + queue.size());
                                }
                            }
                        });
                }
            });
            try {
                while(!future.isDone())
                    future.get();
                Log.v("future.get() = " ,String.valueOf(future.isDone()));
                } catch (InterruptedException e) {
                    throw new RuntimeException("Interupted exception Occured while refreshing");
                } catch (ExecutionException e) {
                    throw new RuntimeException("Thread execution exception Occured while refreshing");
                }
        }

        synchronized (lock) {
            refreshInProgress = false;
        }
    }

    protected void makeRefresh(final APICallback callback){
         if (!this.auth.refreshTokenValid()) {
            callback.onAPIFailure(request,new IOException("Refresh Token is Invalid"));
         }
         else{
             HashMap<String, String> body = new HashMap<String, String>();
             body.put("grant_type", "refresh_token");
             body.put("refresh_token", this.auth.refreshToken());
             try {
                 requestToken(TOKEN_ENDPOINT_URL, body,callback);
             } catch (AuthException e) {
                 callback.onAPIFailure(request,new IOException("Refresh Token is Invalid"));
             }
         }
    }

    /**
     * Revoke current session
     *
     * @param callback
     */
    public void logout(final APICallback callback) throws AuthException {
        HashMap<String, String> body = new HashMap<String, String>();
        body.put("access_token", this.auth.access_token);
        requestToken(REVOKE_ENDPOINT_URL, body, new APICallback() {

            public void onAPIFailure(Request request, IOException e) {
                callback.onAPIFailure(request, e);
            }


            public void onAPIResponse(APIResponse response) throws IOException {
                if (response.ok()) {
                    auth.reset();
                    callback.onAPIResponse(response);
                } else {
                    callback.onAPIFailure(response.request(), new IOException("IOException Occured. Failed Logout with error code " + response.statusCode()));
                }
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
<<<<<<< HEAD
    public void sendRequest(final String method, final String apiURL, final RequestBody body, final HashMap<String, String> headerMap, final APICallback callback) throws AuthException {
=======
    public void sendRequest(final String method, final String apiURL, final RequestBody body, final HashMap<String, String> headerMap, final Callback callback) throws AuthException {

        ensureAuthentication(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                callback.onFailure(request,e);
            }
>>>>>>> f35ebd5902482eda5b90874d7e689071c019ffdc

                    HashMap<String, String> header = null;
                    if (headerMap == null)
                        header = new HashMap<String, String>();
                    else
                        header = headerMap;
                    final String URL = server.value + apiURL;
                    if (!header.containsKey("Authorization")) {
                        header.put("Authorization", authHeader());
                    }
                    request = client.createRequest(method, URL, body, inflateRequest(header));
                    client.sendRequest(request, callback);

//                else if(response.code()==400 && showError(response).contains("invalid_"))
//                    callback.onFailure(response.request(),new IOException("API Call failed with error code: "+response.code()));



    }

    /**
     * Sets auth data
     * @param response
     * @return
     * @throws IOException
     */

    /**
     * Sets content-type
     *
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



    public void get(String apiURL, RequestBody body, HashMap<String, String> headerMap, final APICallback callback) throws AuthException {

        sendRequest("get",apiURL,body==null?null:body,headerMap,callback);
    }

    public void post(String apiURL, RequestBody body, HashMap<String, String> headerMap, final APICallback callback) throws AuthException {
        sendRequest("post",apiURL,body,headerMap,callback);
    }

    public void put(String apiURL, RequestBody body, HashMap<String, String> headerMap, final APICallback callback) throws AuthException {

        sendRequest("put",apiURL,body,headerMap,callback);
    }

    public void delete(String apiURL, RequestBody body, HashMap<String, String> headerMap, final APICallback callback) throws AuthException {

        sendRequest("delete",apiURL,body==null?null:body,headerMap,callback);
    }


    public void expire_access(){
        auth().expire_access();
    }

    public String showError(Response response) {
        String message = "";
        if (!response.isSuccessful()) {
            message = "HTTP error code: " + response.code() + "\n";

            try {
                JSONObject data = new JSONObject(response.body().string());

                if (data == null) {
                    message = "Unknown response reason phrase";
                }

                if (data.getString("message") != null)
                    message = message + data.getString("message");

                if (data.getString("error_description") != null)
                    message = message + data.getString("error_description");

                if (data.getString("description") != null)
                    message = message + data.getString("description");


            } catch (JSONException | IOException e) {
                message = message + " and additional error happened during JSON parse " + e.getMessage();
            }
        } else {
            message = "";
        }
        return message;
    }

}