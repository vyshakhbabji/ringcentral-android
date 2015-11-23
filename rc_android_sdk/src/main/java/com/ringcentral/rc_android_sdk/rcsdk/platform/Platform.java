
package com.ringcentral.rc_android_sdk.rcsdk.platform;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

import com.ringcentral.rc_android_sdk.rcsdk.http.Client;

/**
 * Created by vyshakh.babji on 11/5/15.
 */

public class Platform {

    protected final int ACCESS_TOKEN_TTL = 3600;
    protected final int REFRESH_TOKEN_TTL = 604800;
    final String REVOKE_ENDPOINT_URL = "/restapi/oauth/revoke";
    final String TOKEN_ENDPOINT_URL = "/restapi/oauth/token";
    protected String appKey;
    protected String appSecret;
    protected Server server;
    protected Auth auth;
    protected Request request;
    protected Client client;

    public Platform(Client client, String appKey, String appSecret, Server server) {
        super();
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.server = server;
        this.auth = new Auth();
        this.client = client;
    }

    public String apiKey() {
        return Credentials.basic(appKey, appSecret);
    }

    protected String authHeader() {
        return this.auth.tokenType() + " " + this.auth.access_token;
    }

    protected boolean ensureAuthentication() {
        if (!this.auth.accessTokenValid()) {
            try {
                this.refresh(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (response.isSuccessful())
                            return;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        } else
            return true;
    }

    protected RequestBody formBody(HashMap<String, String> body) {
        FormEncodingBuilder formBody = new FormEncodingBuilder();
        for (HashMap.Entry<String, String> entry : body.entrySet())
            formBody.add(entry.getKey(), entry.getValue());
        return formBody.build();
    }

    public Auth auth() {
        return auth;
    }

    public boolean loggedIn() throws Exception {
        try {
            return this.auth.accessTokenValid();
        } catch (Exception e) {
            throw e;
        }
    }

    public void login(String userName, String extension, String password, Callback callback) {

        HashMap<String, String> body = new HashMap<String, String>();
        body.put("username", userName);
        body.put("password", password);
        body.put("extension", extension);
        body.put("grant_type", "password");
        requestToken(TOKEN_ENDPOINT_URL, body, callback);
    }

    public Builder inflateRequest(HashMap<String, String> hm) throws IOException {
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

    public void setAuth(Response response) {
        try {
            this.auth.setData(jsonToHashMap(response));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void requestToken(String endpoint, HashMap<String, String> body, final Callback callback) {
        try {
            final String URL = server.value + endpoint;
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", apiKey());
            headers.put("Content-Type", ContentTypeSelection.FORM_TYPE_MARKDOWN.value.toString());
            request = inflateRequest(headers).url(URL).post(formBody(body)).build();
            final Callback c = new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    setAuth(response);
                    callback.onResponse(response);
                }
            };
            client.loadResponse(request, c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refresh(Callback callback) throws Exception {
        try {
            if (!this.auth.refreshTokenValid()) {
                throw new IOException("Refresh Token has Expired");
            } else {
                HashMap<String, String> body = new HashMap<String, String>();
                body.put("grant_type", "refresh_token");
                body.put("refresh_token", this.auth.refreshToken());
                requestToken(TOKEN_ENDPOINT_URL, body, callback);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void logout(Callback callback) {
        HashMap<String, String> body = new HashMap<String, String>();
        body.put("access_token", this.auth.access_token);
        this.requestToken(REVOKE_ENDPOINT_URL, body, callback);
        this.auth.reset();
    }


    public void sendRequest(String method, String apiURL, RequestBody body, HashMap<String, String> headerMap, Callback callback) {

        final String URL = server.value + apiURL;
        try {
            ensureAuthentication();
            request = client.createRequest(method, URL, body, inflateRequest(headerMap));
            client.loadResponse(request, callback);
        } catch (Exception e) {
            System.err.print("Failed APICall. Exception occured in Class:"
                    + this.getClass().getName() + "\n");
            e.printStackTrace();
        }

    }

    // TODO: 11/23/15 remove here and replace in helper class
    public void callLog(final Callback callback) {
        try {
            this.ensureAuthentication();
            final String url = "/restapi/v1.0/account/~/call-log";
            sendRequest("get", url, null, null, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, String> jsonToHashMap(Response response) throws IOException {
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
    }

    public enum ContentTypeSelection {
        FORM_TYPE_MARKDOWN("application/x-www-form-urlencoded"), JSON_TYPE_MARKDOWN(
                "application/json"), MULTIPART_TYPE_MARKDOWN("multipart/mixed;");
        protected MediaType value;

        private ContentTypeSelection(String contentType) {
            this.value = MediaType.parse(contentType);
        }
    }

    public enum Server {
        PRODUCTION("https://platform.ringcentral.com"), SANDBOX(
                "https://platform.devtest.ringcentral.com");
        private String value;

        Server(String url) {
            this.value = url;
        }
    }

}