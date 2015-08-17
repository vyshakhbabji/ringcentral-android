package com.ringcentral.rcandroidsdk.rcsdk.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by andrew.pang on 8/13/15.
 */
public class Transaction {

    public Request request;
    public Response response;

    public String getBodyString() {
        return bodyString;
    }

    String bodyString;

    public Transaction(Response response){
        request = response.request();
        this.response = response;
        try {
            bodyString = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap getAuthJson(){
        Gson gson = new Gson();
        Type mapType = new TypeToken<HashMap<String, String>>() {}.getType();
        HashMap<String, String> jsonMap = null;
            jsonMap = gson.fromJson(bodyString, mapType);

        return jsonMap;
    }


}
