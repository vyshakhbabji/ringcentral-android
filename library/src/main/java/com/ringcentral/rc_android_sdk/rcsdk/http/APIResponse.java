/*
 * Copyright (c) 2015 RingCentral, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ringcentral.rc_android_sdk.rcsdk.http;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ringcentral.rc_android_sdk.rcsdk.platform.AuthException;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class APIResponse {

    protected Request request;
    protected Response response;

    public APIResponse(Response response) {
        this.request = response.request();
        this.response = response;
    }

    public ResponseBody body() {
        return this.response.body();
    }

    protected String getContentType() {
        return this.response.headers().get("Content-Type");
    }

    protected boolean isContentType(String contentType) {
        return getContentType().toString().equalsIgnoreCase(contentType);
    }

    public JsonElement json() throws AuthException {
        JsonElement jObject = new JsonObject();
        try {
            JsonParser parser = new JsonParser();
            jObject = parser.parse(body().string());
            return jObject;
        } catch (Exception e) {
           throw  new AuthException("Exception occured while converting the HTTP response to JSON in Class:  " , e);  //FIXME :Fixed
        }
       // return jObject;
    }

    public boolean ok() {
        return (statusCode() >= 200 && statusCode() < 300);
    }

    public Request request() {
        return this.request;
    }


    public Response response() {
        return this.response;
    }


    public int statusCode() {
        return this.response.code();
    }

    public String text() throws IOException {
        return body().string();

    }
}