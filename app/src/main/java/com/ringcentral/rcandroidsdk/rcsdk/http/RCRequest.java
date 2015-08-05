package com.ringcentral.rcandroidsdk.rcsdk.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by andrew.pang on 6/25/15.
 */
public class RCRequest extends RCHeaders {

    String method;
    String url;
    String query;
    LinkedHashMap<String, String> body;
    public RCHeaders RCHeaders;

    OkHttpClient client = new OkHttpClient();
    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    public static final MediaType JSON_TYPE_MARKDOWN
            = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType MULTI_TYPE_MARKDOWN
            = MediaType.parse("multipart/mixed; boundary=Boundary_1_14413901_1361871080888");

    public RCRequest(LinkedHashMap<String, String> body, HashMap<String, String> headerMap){
        RCHeaders = new RCHeaders();
        this.method = headerMap.get("method");
        this.url = headerMap.get("url");
        if(headerMap.containsKey("query")){
            this.query = headerMap.get("query");
        } else {
            this.query = "";
        }
        if(headerMap.containsKey("Content-Type")){
            this.RCHeaders.setContentType(headerMap.get("Content-Type"));
        }
        if(body != null){
            this.body = body;
        } else {
            this.body = null;
        }
    }

    public void setMethod(String method){
        this.method = method;
    }

    public String getMethod(){
        return this.method;
    }

    public void setURL(String url){
        this.url = url;
    }

    public String getUrl(){
        return this.url;
    }

    public void setQuery(String query){
        this.query = query;
    }

    public String getQuery(){
        return this.query;
    }

    public void setBody(LinkedHashMap<String, String> body){
        this.body = body;
    }

    public LinkedHashMap<String, String> getBody(){
        return this.body;
    }

    public boolean isPut(){
        return this.getMethod().equals("PUT");
    }

    public boolean isGet(){
        return this.getMethod().equals("GET");
    }

    public boolean isPost(){
        return this.getMethod().equals("POST");
    }

    public boolean isDelete(){
        return this.getMethod().equals("DELETE");
    }

    /**
     * Encodes data in UTF-8 format to be passed in the request as form data.
     *
     * @return
     */
    public String getBodyString(){
        String body = "";
        try {
            StringBuilder data = new StringBuilder();
            int count = 0;
            if(!this.RCHeaders.isURLEncoded()){
                data.append("{ ");
            }
            for(Map.Entry<String, String> entry: this.body.entrySet()){
                if(this.RCHeaders.isURLEncoded()) {
                    if (count != 0) {
                        data.append("&");
                    }
                    data.append(entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
                    count++;
                }
                else{
                    if (count != 0) {
                        data.append(", ");
                    }
                    data.append(entry.getKey());
                    data.append(": ");
                    data.append(entry.getValue());
                    count++;
                }
            }
            if(!this.RCHeaders.isURLEncoded()){
                data.append(" }");
            }
            body = data.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return body;
    }

    /**
     * Makes an API call using OKHTTP request. Builds request from the headers
     * and method, and makes an asynchronous call which returns a response to the Callback.
     *
     * @param method
     * @param c Callback is overriden in a parent method that calls this method, and recieves the response.
     * @throws IOException
     */
    public void apiCall(String method, Callback c) throws IOException{
        Request.Builder requestBuilder = new Request.Builder();
        for(Map.Entry<String, String> entry: this.RCHeaders.map.entrySet()){
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        Request request = null;
        if(method.toUpperCase().equals("GET")) {
            request = requestBuilder
                    .url(this.url)
                    .build();
        }
        else if(method.toUpperCase().equals("DELETE")){
            request = requestBuilder
                    .url(this.url)
                    .delete()
                    .build();
        }
        else {
            MediaType mediaType;
            if(this.RCHeaders.isJson()){
                mediaType = JSON_TYPE_MARKDOWN;
            }
            else if(this.RCHeaders.isMultipart()){
                mediaType = MULTI_TYPE_MARKDOWN;
            }
            else{
                mediaType = MEDIA_TYPE_MARKDOWN;
            }
            if (method.toUpperCase().equals("POST")) {
                request = requestBuilder
                        .url(this.url)
                        .post(RequestBody.create(mediaType, this.getBodyString()))
                        .build();
            } else if (method.toUpperCase().equals("PUT")) {
                request = requestBuilder
                        .url(this.url)
                        .put(RequestBody.create(mediaType, this.getBodyString()))
                        .build();
            }
        }
        client.newCall(request).enqueue(c);
    }

    /**
     * Makes an apiCall with the GET method
     *
     * @param c
     * @throws IOException
     */
    public void get(Callback c) throws IOException {
        this.apiCall("GET", c);
    }

    /**
     * Makes an apiCall with the POST method
     *
     * @param c
     * @throws IOException
     */
    public void post(Callback c) throws Exception {
        this.apiCall("POST", c);
    }

    /**
     * Makes an apiCall with the PUT method
     *
     * @param c
     * @throws IOException
     */
    public void put(Callback c) throws Exception {
        this.apiCall("PUT", c);
    }

    /**
     * Makes an apiCall with the GET method
     *
     * @param c
     * @throws IOException
     */
    public void delete(Callback c) throws IOException {
        this.apiCall("DELETE", c);
    }

}
