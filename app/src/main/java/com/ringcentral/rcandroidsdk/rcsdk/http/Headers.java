package com.ringcentral.rcandroidsdk.rcsdk.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by andrew.pang on 6/25/15.
 */
public class Headers {
    HashMap<String, String> headers;

    static final String HEADER_SEPARATOR = ":";
    String CONTENT_TYPE = "content-type";
    static final String AUTHORIZATION = "authorization";
    static final String ACCEPT = "accept";
    static final String URL_ENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded";
    static final String JSON_CONTENT_TYPE = "application/json";
    static final String MULTIPART_CONTENT_TYPE = "multipart/mixed";

    public Headers(){
        this.headers = new HashMap<>();
    }

    public Headers(HashMap<String, String> headers){
        this.headers = headers;
    }

    public void setHeader(String key, String val){
        this.headers.put(key, val);
    }

    public void setHeaders(HashMap<String, String> headers){
        for(Map.Entry<String, String> entry: headers.entrySet()){
            setHeader(entry.getKey(), entry.getValue());
        }
    }

    public String getHeader(String key){
        return this.headers.get(key);
    }

    public boolean hasHeader(String key){
        return this.headers.containsKey(key);
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public String[] getHeadersArray(){
        String[] array = new String[this.headers.size()];
        int count = 0;
        for(Map.Entry<String, String> entry: this.headers.entrySet()){
            array[count] = entry.getKey() + HEADER_SEPARATOR + entry.getValue();
            count++;
        }
        return array;
    }

    public String getContentType(){
        return hasHeader(CONTENT_TYPE) ? getHeader(CONTENT_TYPE):"";
    }

    public void setContentType(String contentType){
        this.CONTENT_TYPE = contentType;
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
