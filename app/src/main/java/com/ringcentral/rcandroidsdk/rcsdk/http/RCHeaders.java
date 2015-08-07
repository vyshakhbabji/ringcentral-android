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

    /**
     * Sets multiple headers with a hashmap of keys and values.
     *
     */
    public void setHeaders(HashMap<String, String> headers){
        for(Map.Entry<String, String> entry: headers.entrySet()){
            setHeader(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Returns the header value for a certain header key
     * @return
     */
    public String getHeader(String key){
        return this.map.get(key);
    }

    /**
     * Checks if the map of headers contains a certain header
     * @return
     */
    public boolean hasHeader(String key){
        return this.map.containsKey(key);
    }

    /**
     * Returns a HashMap of all the headers
     * @return
     */
    public HashMap<String, String> getHeaders() {
        return map;
    }

    /*
    * Creates a string array from all the headers
    public String[] getHeadersArray(){
        String[] array = new String[this.map.size()];
        int count = 0;
        for(Map.Entry<String, String> entry: this.map.entrySet()){
            array[count] = entry.getKey() + ":" + entry.getValue();
            count++;
        }
        return array;
    }
    */

    /**
     * Returns the content-type header's value
     * @return
     */
    public String getContentType(){
        return hasHeader(CONTENT_TYPE) ? getHeader(CONTENT_TYPE):"";
    }

    /**
     * Sets the header's content-type
     * @param contentType
     */
    public void setContentType(String contentType){
        this.map.put(CONTENT_TYPE, contentType);
    }

    /**
     * Returns true if the content-type matches the passed in parameter content-type
     *
     * @return
     */
    public boolean isContentType(String contentType){
        return (this.map.get(CONTENT_TYPE).contains(contentType));
    }

    /**
     * Returns true if the content-type headers is "application/json"
     *
     * @return
     */
    public boolean isJson(){
        return isContentType(JSON_CONTENT_TYPE);
    }
    /**
     * Returns true if the content-type headers is "multipart/mixed"
     *
     * @return
     */
    public boolean isMultipart(){
        return isContentType(MULTIPART_CONTENT_TYPE);
    }
    /**
     * Returns true if the content-type headers is "application/x-www-form-urlencoded"
     *
     * @return
     */
    public boolean isURLEncoded(){
        return isContentType(URL_ENCODED_CONTENT_TYPE);
    }

}
