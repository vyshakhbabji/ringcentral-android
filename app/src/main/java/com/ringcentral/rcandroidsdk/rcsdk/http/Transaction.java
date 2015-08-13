package com.ringcentral.rcandroidsdk.rcsdk.http;

import com.squareup.okhttp.Request;

import java.util.Map;

/**
 * Created by andrew.pang on 8/13/15.
 */
public class Transaction {

    Request.Builder requestBuilder = new Request.Builder();
    String method;
    String url;

    public void addHeader
    public void addHeaders(Map<String, String> map){
        for(Map.Entry<String, String> entry: map.entrySet()){
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
    }
}
