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

    public HashMap getAuthJson(){
        Gson gson = new Gson();
        Type mapType = new TypeToken<HashMap<String, String>>() {}.getType();
        HashMap<String, String> jsonMap = null;
            jsonMap = gson.fromJson(this.getBodyString(), mapType);

        return jsonMap;
    }

    public String getBodyString() {
        String bodyString ="";
        try {
            bodyString = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bodyString;
    }

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

    public boolean isOK(){
        int status = this.response.code();
        return (status >= 200 && status < 300);
    }

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
