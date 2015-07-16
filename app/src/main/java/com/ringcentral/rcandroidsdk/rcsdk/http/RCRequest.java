package com.ringcentral.rcandroidsdk.rcsdk.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
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
    HashMap<String, String> body;
    public RCHeaders RCHeaders;

    OkHttpClient client = new OkHttpClient();
    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    public static final MediaType JSON_TYPE_MARKDOWN
            = MediaType.parse("application/json; charset=utf-8");
    public Response callResponse;
    public String responseString;
    public Map<String, String> responseMap;

    public RCRequest(HashMap<String, String> body, HashMap<String, String> headerMap){
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

    public void setBody(HashMap<String, String> body){
        this.body = body;
    }

    public HashMap<String, String> getBody(){
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
     * Encodes data to be passed in the request as form data
     * @return
     */
    public String getBodyString(){
        String body = "";
        try {
            StringBuilder data = new StringBuilder();
            int count = 0;
            for(Map.Entry<String, String> entry: this.body.entrySet()){
                if(entry.getKey() == "body"){
                    data.append(entry.getValue());
                    break;
                }
                if (count != 0){
                    data.append("&");
                }
                data.append(entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
                count++;
            }
            body = data.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return body;
    }

    /**
     * Sets the request headers and url endpoint to make a GET call using OKHTTP
     *
     * @param c Callback is made in method that calls this method, to receive the response
     * @throws IOException
     */
    public void get(Callback c) throws IOException {
        Request.Builder requestBuilder = new Request.Builder();
        for(Map.Entry<String, String> entry: this.RCHeaders.map.entrySet()){
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        Request request = requestBuilder
                .url(this.url)
                .build();
        client.newCall(request).enqueue(c);
    }

    /**
     * Sets the request headers and url endpoint to make a POST call using OKHTTP.
     * Checks the header to see what type of markdown the POST body is.
     *
     * @param c Callback is made in method that calls this method, to receive the response
     * @throws IOException
     */
    public void post(Callback c) throws Exception {
        Request.Builder requestBuilder = new Request.Builder();
        for(Map.Entry<String, String> entry: this.RCHeaders.map.entrySet()){
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        Request request;
        if(this.RCHeaders.map.containsValue("application/json")){
            request = requestBuilder
                    .url(this.url)
                    .post(RequestBody.create(JSON_TYPE_MARKDOWN, this.getBodyString()))
                    .build();
        } else {
            request = requestBuilder
                    .url(this.url)
                    .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, this.getBodyString()))
                    .build();
        }
        client.newCall(request).enqueue(c);
    }

    /**
     * Sets the request headers and url endpoint to make a PUT call using OKHTTP.
     * Checks the header to see what type of markdown the PUT body is.
     *
     * @param c Callback is made in method that calls this method, to receive the response
     * @throws IOException
     */
    public void put(Callback c) throws Exception {
        Request.Builder requestBuilder = new Request.Builder();
        for(Map.Entry<String, String> entry: this.RCHeaders.map.entrySet()){
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        Request request;
        if(this.RCHeaders.map.containsValue("application/x-www-form-urlencoded")){
            request = requestBuilder
                    .url(this.url)
                    .put(RequestBody.create(MEDIA_TYPE_MARKDOWN, this.getBodyString()))
                    .build();
        } else {
            request = requestBuilder
                    .url(this.url)
                    .put(RequestBody.create(JSON_TYPE_MARKDOWN, this.getBodyString()))
                    .build();
        }
        client.newCall(request).enqueue(c);
    }

    /**
     * Sets the request headers and url endpoint to make a DELETE call using OKHTTP.
     *
     * @param c Callback is made in method that calls this method, to receive the response
     * @throws IOException
     */
    public void delete(Callback c) throws IOException {
        Request.Builder requestBuilder = new Request.Builder();
        for(Map.Entry<String, String> entry: this.RCHeaders.map.entrySet()){
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        Request request = requestBuilder
                .url(this.url)
                .delete()
                .build();
        client.newCall(request).enqueue(c);
    }

}
