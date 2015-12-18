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
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class APIResponse { //FIXME ApiResponse

    protected Request request;
    protected Response response;

    public APIResponse(Response response, Request request) {
        this.request = request;
        this.response = response;
    }

    public APIResponse(Request request) {
        this.request = request;
    }

    APIResponse(Response response) {
        this.response = response;
        this.request = response.request();
    }

    public ResponseBody body() {
        //FIXME Add a guard for undefined response :Fixed
        if(response==null){
            return null;
        }
        return this.response.body();
    }

    protected String getContentType() {
        return this.response.headers().get("Content-Type");
    }

    protected boolean isContentType(String contentType) {
        return getContentType().equalsIgnoreCase(contentType);
    }

    public JsonElement json() throws APIException {
        JsonElement jObject = new JsonObject();
        try {
            JsonParser parser = new JsonParser();
            jObject = parser.parse(body().string());
            return jObject;
        } catch (Exception e) {
            throw new APIException("Exception occured while converting the HTTP response to JSON in Class:  ", e);
        }
    }

    public boolean ok() {
        return (code() >= 200 && code() < 300);
    }

    public Request request() {
        return this.request;
    }


    public Response response() {
        return this.response;
    }


    public int code() {
        return this.response.code();
    }

    public String text() throws IOException {
        return body().string();

    }

    //FIXME Naming
    public String error() {
        String message = "";
        if (!response.isSuccessful()) {
            message = "HTTP error code: " + response.code() + "\n";

            try {

                String msg = response.body().string();
                JSONObject data = new JSONObject(msg);

                if (data == null) {
                    message = "Unknown response reason phrase";
                }

                if (data.getString("message") != null)
                    message = message + data.getString("message");

                if (data.getString("error_description") != null)
                    message = message + data.getString("error_description");

                if (data.getString("description") != null)
                    message = message + data.getString("description");


            } catch (JSONException | IOException e) {
                message = message + " and additional error happened during JSON parse " + e.getMessage();
            }
        } else {
            message = "";
        }
        return message;


    }

    //FIXME Remove
    public Headers getRequestHeader(Request request) {
        return request.headers();
    }

//    protected APIException handleErrorResponse(Response response, Request request)
//            throws IOException, InterruptedException {
//
//        final int statusCode=response.code();
//        final String reasonPhrase=response.message();
//
//        APIException exception = null;
//        try {
//            exception = new APIException(response);
//
//        } catch (Exception e) {
//            // If the errorResponseHandler doesn't work, then check for error
//            // responses that don't have any content
//            if (statusCode == 413) {
//                exception = new APIException("Request entity too large");
//
//                exception.setStatusCode(statusCode);
//                exception.setErrorType(APIException.ErrorType.Client);
//                exception.setErrorCode("Request entity too large");
//            } else if (statusCode == 503 && "Service Unavailable".equalsIgnoreCase(reasonPhrase)) {
//                exception = new APIException("Service unavailable");
//                exception.setStatusCode(statusCode);
//                exception.setErrorType(APIException.ErrorType.Service);
//                exception.setErrorCode("Service unavailable");
//            } else if (e instanceof IOException) {
//                throw (IOException) e;
//            } else {
//                String errorMessage = "Unable to unmarshall error response (" + e.getMessage() + "). Response Code: "
//                        + statusCode + ", Response Text: " + reasonPhrase;
//                throw new APIException(errorMessage, e);
//            }
//        }
//
//        exception.setStatusCode(statusCode);
//        exception.fillInStackTrace();
//        return exception;
//    }


}