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

import android.os.AsyncTask;

import com.ringcentral.rc_android_sdk.rcsdk.platform.AuthException;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Request.Builder;
import com.squareup.okhttp.RequestBody;

import java.util.concurrent.ExecutionException;

/**
 * Created by vyshakh.babji on 11/9/15.
 */
public class Client {

    OkHttpClient client;

    public Client() {
        client = new OkHttpClient();
    }

    /**
     * Makes a OKHttp  call
     *
     * @param request
     * @return
     */
    //FIXME Name should be sendRequest
    //FIXME Take a look at reference -- this method should do a different thing
    public Call send(final Request request) {
        Call call;
        call = client.newCall(request);
        return call;
    }

    /**
     * Creates OKHttp Request
     *
     * @param method
     * @param URL
     * @param body
     * @param header
     * @return OKHttp Request
     * @throws AuthException
     */
    public Request createRequest(String method, String URL, RequestBody body, Builder header) throws AuthException {

        Request.Builder request = new Request.Builder();
        if (method.equalsIgnoreCase("get")) {
            request = header.url(URL);
        } else if (method.equalsIgnoreCase("delete")) {
            request = header.url(URL).delete();

        } else {
            if (method.equalsIgnoreCase("post")) {
                request = header.url(URL).post(body);

            } else if (method.equalsIgnoreCase("put")) {
                request = header.url(URL).put(body);
            } else
                throw new AuthException("Method not Allowed. Please Refer API Documentation. See\n" +
                        "     * <a href =\"https://developer.ringcentral.com/api-docs/latest/index.html#!#Resources.html\">Server Endpoint</a> for more information. ");
        }
        return request.build();
    }

    /**
     * Loads OKHttp Response synchronizing async api calls
     *
     * @param request
     * @param callback
     */
    //FIXME Async
    //FIXME Take a look at reference -- this method should do a different thing
    public void loadResponse(final Request request, final Callback callback) {
        try {
            new AsyncTask<String, Integer, Void>() {
                @Override
                protected Void doInBackground(String... params) {
                    try {
                        send(request).enqueue(callback);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
//                @Override
//                protected void onPostExecute(Void result) {
//                    super.onPostExecute(result);
//                    Log.e("ANSWER", "" + result);
//                }
            }.execute().get();
        } catch (InterruptedException e) {
            throw new AuthException("Error in Loading Response..", e);
        } catch (ExecutionException e) {
            throw new AuthException("Error in Executing Response..", e);
        }
        ;

    }

    public Headers getRequestHeader(Request request) {
        return request.headers();
    }

}