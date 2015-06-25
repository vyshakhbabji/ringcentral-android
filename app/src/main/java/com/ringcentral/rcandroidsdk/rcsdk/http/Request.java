package com.ringcentral.rcandroidsdk.rcsdk.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by andrew.pang on 6/25/15.
 */
public class Request extends Headers {

    private String method;
    private String url;
    private String query;
    private String body;

    Request(HashMap<String, String> parameters){
        Headers headers = new Headers();
        this.method = parameters.get("method");
        this.url = parameters.get("url");
        if(parameters.containsKey("query")){
            this.query = parameters.get("query");
        } else {
            this.query = "";
        }
        if(parameters.containsKey("body")){
            this.query = parameters.get("body");
        } else {
            this.query = "";
        }
//        headers.setHeader("ACCEPT", JSON_CONTENT_TYPE);
//        headers.setHeader("Content-Type", JSON_CONTENT_TYPE);
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

    public void setBody(String body){
        this.body = body;
    }

    public String getBody(){
        return this.body;
    }

    public boolean isPut(){
        return this.getMethod().equals("PUT");
    }

    public boolean isGet(){
        return this.getMethod().equals("GET");
    }

    public boolean isPost(){
        return this.getMethod().equals("POST")
    }

    public boolean isDelete(){
        return this.getMethod().equals("DELETE")
    }

    public byte[] getEncodedBody(){
        byte[] byteArray;
        try {
            StringBuilder data = new StringBuilder();
            data.append(URLEncoder.encode(this.body, "UTF-8"));
            byteArray = data.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return byteArray;
    }
    
//Not finished
    public void Send(){
        try {
            URL request = new URL(this.url);
            HttpsURLConnection httpConn = (HttpsURLConnection) request.openConnection();
            httpConn.setRequestMethod(this.method);
            if(this.method.equals("POST") || this.method.equals("PUT") || this.method.equals("DELETE")){
                httpConn.setDoOutput(true);
            }
            for(Map.Entry<String, String> entry: this.headers.entrySet()){
                httpConn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }


}
