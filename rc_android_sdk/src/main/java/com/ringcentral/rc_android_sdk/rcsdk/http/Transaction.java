package com.ringcentral.rc_android_sdk.rcsdk.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Created by andrew.pang on 8/13/15.
 */
public class Transaction {

    public Request request;
    public Response response;

    public Transaction(Response response){
        request = response.request();
        this.response = response;
    }

    /**
     * Parses authentication Json to return a HashMap used for setting Auth data
     */
    public HashMap getAuthJson(){
        Gson gson = new Gson();
        Type mapType = new TypeToken<HashMap<String, String>>() {}.getType();
        HashMap<String, String> jsonMap = null;
            jsonMap = gson.fromJson(this.getBodyString(), mapType);

        return jsonMap;
    }

    /**
     * Returns the response body as a string
     * @return
     */
    public String getBodyString() {
        String bodyString ="";
        try {
            bodyString = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bodyString;
    }

    /**
     * Returns the response body as a JSONObject
     */
    public JSONObject getJsonObject(){
        JSONObject object = null;
        try {
            object = new JSONObject(response.body().string());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * Checks if the HTTP status code of the response is successful
     */
    public boolean isOK(){
        int status = this.response.code();
        return (status >= 200 && status < 300);
    }

    /**
     * Returns an error message with the response code and error message
     */
    public String getError(){
        if(this.getResponse() == null){
            return null;
        }

        if(this.isOK()){
            return null;
        }
        String message = this.getResponse().code() + " " + this.getResponse().message();

        return message;
    }


    public Response getResponse() {
        return response;
    }

    public Request getRequest() {
        return request;
    }

}
