package com.ringcentral.rc_android_sdk.rcsdk.http;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class APIResponse {

    protected Request request;
    protected Response response;

    public APIResponse(Response response) {
        this.request = response.request();
        this.response = response;
    }

    public ResponseBody body() {
        return this.response.body();
    }

    public String error() {

        String message = "";
        if (!ok()) {
            message = "HTTP error code: " + statusCode() + "\n";

            try {
                JSONObject data = new JSONObject(body().string());

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

    protected String getContentType() {
        return this.response.headers().get("Content-Type");
    }

    protected boolean isContentType(String contentType) {
        return getContentType().toString().equalsIgnoreCase(contentType);
    }

    public JsonElement json() {
        JsonElement jObject = new JsonObject();
        try {
            JsonParser parser = new JsonParser();
            jObject = parser.parse(body().string());
            return jObject;
        } catch (Exception e) {
            System.err
                    .print("Exception occured while converting the HTTP response to JSON in Class:  " + e.getStackTrace());
        }
        return jObject;
    }

    public boolean ok() {
        return (statusCode() >= 200 && statusCode() < 300);
    }

    public Request request() {
        return this.request;
    }


    public Response response() {
        return this.response;
    }


    public int statusCode() {
        return this.response.code();
    }

    public String text() throws IOException {
        return body().string();

    }

    //multipart
}