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
//        RCHeaders.setHeader("ACCEPT", JSON_CONTENT_TYPE);
//        RCHeaders.setHeader("Content-Type", JSON_CONTENT_TYPE);
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
//
//            data.append("grant_type=" + URLEncoder.encode(this.body.get("grant_type"), "UTF-8"));
//            data.append("&username=" + URLEncoder.encode(this.body.get("username"), "UTF-8"));
//            data.append("&password=" + URLEncoder.encode(this.body.get("password"), "UTF-8"));
            body = data.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return body;
    }

////Not finished
//    public RCResponse send() {
//        HttpsURLConnection httpConn = null;
//        RCResponse RCResponse = null;
//        int responseCode = 0;
//        String auth = this.RCHeaders.getHeader("authorization");
//        try {
//            URL request = new URL(this.url);
//            httpConn = (HttpsURLConnection) request.openConnection();
//            httpConn.setRequestMethod(this.method);
//            String ahah = httpConn.getRequestMethod();
//            if(this.method.equals("POST") || this.method.equals("PUT") || this.method.equals("DELETE")){
//                httpConn.setDoOutput(true);
//            }
//            for(Map.Entry<String, String> entry: this.RCHeaders.map.entrySet()){
//                httpConn.setRequestProperty(entry.getKey(), entry.getValue());
//            }
//
//            if(this.method.equals("POST")) {
//                OutputStream postStream = httpConn.getOutputStream();
//                byte[] encodedBody = this.getEncodedBody();
//                postStream.write(encodedBody, 0, encodedBody.length);
//                postStream.close();
//
//                responseCode = httpConn.getResponseCode();
////            String responseMessage = httpConn.getResponseMessage();
////            InputStream error = httpConn.getErrorStream();
////
////            InputStreamReader reader = new InputStreamReader(error);
////            BufferedReader in = new BufferedReader(reader);
////            StringBuffer content = new StringBuffer();
////            String line;
////            while ((line = in.readLine()) != null) {
////                content.append(line + "\n");
////            }
//
//            }
//
//            InputStream stream;
//            if(responseCode == 200) {
//                stream = httpConn.getInputStream();
//            } else {
//                stream = httpConn.getErrorStream();
//            }
//            InputStreamReader reader = new InputStreamReader(stream);
//            BufferedReader in = new BufferedReader(reader);
//            StringBuffer content = new StringBuffer();
//            String line;
//            while ((line = in.readLine()) != null) {
//                content.append(line + "\n");
//            }
//            in.close();
//            //this.body = content.toString();
//            Map<String, List<String>> header =  httpConn.getHeaderFields();
//            System.out.print("alksdjfl;ask");
//            RCResponse = new RCResponse(responseCode, content.toString(), header);
//        } catch (IOException e){
//            e.printStackTrace();
//        } finally {
//            httpConn.disconnect();
//        }
//        return RCResponse;
//    }





    ////OKHTTP
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

}
