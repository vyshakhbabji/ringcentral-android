package com.ringcentral.rcandroidsdk.oldsdk;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Created by andrew.pang on 6/26/15.
 */
public class RCResponse extends RCHeaders {

    Response response;
    RCHeaders headers;
    int status;
    String statusText;
    String body;

    public RCResponse(){
        this.response = null;
        this.status = 0;
        this.statusText = "";
        this.body = "";
    }
    public RCResponse(Response response){
        try {
            this.response = response;
            this.status = response.code();
            this.statusText = response.message();
            this.body = response.body().string();
            this.headers = new RCHeaders();
            Headers okHttpHeader = response.headers();
            for(String name: okHttpHeader.names()){
                this.headers.setHeader(name, okHttpHeader.get(name));
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public boolean checkStatus(){
        return ((this.status >= 200) && (this.status < 300));
    }

    public String getBody(){
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getStatus(){
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Parses JSON string to a HashMap<String, String> that is used to set authorization data
     * @return
     */
    public HashMap getJson(){
        Gson gson = new Gson();
        Type mapType = new TypeToken<HashMap<String, String>>() {}.getType();
        HashMap<String, String> jsonMap = gson.fromJson(this.body, mapType);
        return jsonMap;
    }

//    public String getResponses(){
//        if (!this.isMultipart()){
//            Log.e("Error", "RCResponse is not Batch (Multipart)");
//        }
//
//
//    }


}

