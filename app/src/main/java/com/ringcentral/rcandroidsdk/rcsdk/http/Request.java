package com.ringcentral.rcandroidsdk.rcsdk.http;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by andrew.pang on 6/25/15.
 */
public class Request extends Headers{

    String method;
    String url;
    String query;
    HashMap<String, String> body;
    public Headers headers;

    public Request(HashMap<String, String> body, HashMap<String, String> headerMap){
        headers = new Headers();
        this.method = headerMap.get("method");
        this.url = headerMap.get("url");
        if(headerMap.containsKey("query")){
            this.query = headerMap.get("query");
        } else {
            this.query = "";
        }
        if(body != null){
            this.body = body;
        } else {
            this.body = null;
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

    public byte[] getEncodedBody(){
        byte[] byteArray = null;
        try {
            StringBuilder data = new StringBuilder();

            data.append("grant_type=" + URLEncoder.encode(this.body.get("grant_type"), "UTF-8"));
            data.append("&username=" + URLEncoder.encode(this.body.get("username"), "UTF-8"));
            data.append("&password=" + URLEncoder.encode(this.body.get("password"), "UTF-8"));
            byteArray = data.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

//Not finished
    public Response send() {
        HttpsURLConnection httpConn = null;
        Response response = null;
        int responseCode = 0;
        String auth = this.headers.getHeader("authorization");
        try {
            URL request = new URL(this.url);
            httpConn = (HttpsURLConnection) request.openConnection();
            httpConn.setRequestMethod(this.method);
            String ahah = httpConn.getRequestMethod();
            if(this.method.equals("POST") || this.method.equals("PUT") || this.method.equals("DELETE")){
                httpConn.setDoOutput(true);
            }
            for(Map.Entry<String, String> entry: this.headers.map.entrySet()){
                httpConn.setRequestProperty(entry.getKey(), entry.getValue());
            }

            OutputStream postStream = httpConn.getOutputStream();
            byte[] encodedBody = this.getEncodedBody();
            postStream.write(encodedBody, 0, encodedBody.length);
            postStream.close();

            responseCode = httpConn.getResponseCode();
//            String responseMessage = httpConn.getResponseMessage();
//            InputStream error = httpConn.getErrorStream();
//
//            InputStreamReader reader = new InputStreamReader(error);
//            BufferedReader in = new BufferedReader(reader);
//            StringBuffer content = new StringBuffer();
//            String line;
//            while ((line = in.readLine()) != null) {
//                content.append(line + "\n");
//            }



            InputStream stream;
            if(responseCode == 200) {
                stream = httpConn.getInputStream();
            } else {
                stream = httpConn.getErrorStream();
            }
            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader in = new BufferedReader(reader);
            StringBuffer content = new StringBuffer();
            String line;
            while ((line = in.readLine()) != null) {
                content.append(line + "\n");
            }
            in.close();
            //this.body = content.toString();
            Map<String, List<String>> header =  httpConn.getHeaderFields();
            System.out.print("alksdjfl;ask");
            response = new Response(responseCode, content.toString(), header);
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            httpConn.disconnect();
        }
        return response;
    }


}
