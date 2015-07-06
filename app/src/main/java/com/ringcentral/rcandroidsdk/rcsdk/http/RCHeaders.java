package com.ringcentral.rcandroidsdk.rcsdk.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by andrew.pang on 6/25/15.
 */
public class RCHeaders {
    HashMap<String, String> map;

    public static String CONTENT_TYPE = "Content-Type";
    public static final String HEADER_SEPARATOR = ":";
    public static final String AUTHORIZATION = "Authorization";
    public static final String ACCEPT = "accept";
    public static final String URL_ENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded";
    public static final String JSON_CONTENT_TYPE = "application/json";
    public static final String MULTIPART_CONTENT_TYPE = "multipart/mixed";

    public RCHeaders(){
        this.map = new HashMap<>();
    }

    public RCHeaders(HashMap<String, String> headers){
        this.map = headers;
    }

    public void setHeader(String key, String val){
        this.map.put(key, val);
    }

    public void setHeaders(HashMap<String, String> headers){
        for(Map.Entry<String, String> entry: headers.entrySet()){
            setHeader(entry.getKey(), entry.getValue());
        }
    }

    public String getHeader(String key){
        return this.map.get(key);
    }

    public boolean hasHeader(String key){
        return this.map.containsKey(key);
    }

    public HashMap<String, String> getHeaders() {
        return map;
    }

    public String[] getHeadersArray(){
        String[] array = new String[this.map.size()];
        int count = 0;
        for(Map.Entry<String, String> entry: this.map.entrySet()){
            array[count] = entry.getKey() + HEADER_SEPARATOR + entry.getValue();
            count++;
        }
        return array;
    }

    public String getContentType(){
        return hasHeader(CONTENT_TYPE) ? getHeader(CONTENT_TYPE):"";
    }

    public void setContentType(String contentType){
        this.map.put(CONTENT_TYPE, contentType);
    }

    public boolean isContentType(String contentType){
        return (this.CONTENT_TYPE.equals(contentType));
    }

    public boolean isJson(){
        return isContentType(JSON_CONTENT_TYPE);
    }

    public boolean isMultipart(){
        return isContentType(MULTIPART_CONTENT_TYPE);
    }

    public boolean isURLEncoded(){
        return isContentType(URL_ENCODED_CONTENT_TYPE);
    }

}
