package com.ringcentral.rcandroidsdk.rcsdk.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Created by andrew.pang on 6/26/15.
 */
public class RCResponse extends RCHeaders {

    int status;
    String body;
    Map<String, List<String>> header;

    public RCResponse(int status, String body, Map<String, List<String>> header){
        this.status = status;
        this.body = body;
        this.header = header;
        //Add some more
        //HashMap<String, String> h = (HashMap) header;
        //this.setHeaders(h);
    }

    public boolean checkStatus(){
        return ((this.status >= 200) && (this.status < 300));
    }

    public String getBody(){
        return this.body;
    }

    public int getStatus(){
        return this.status;
    }

    public Map getJson(){
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> ser = gson.fromJson(this.body, mapType);
        return ser;
    }

//    public String getResponses(){
//        if (!this.isMultipart()){
//            Log.e("Error", "RCResponse is not Batch (Multipart)");
//        }
//
//
//    }


}

