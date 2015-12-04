package com.ringcentral.rc_android_sdk.rcsdk.http;

import android.os.AsyncTask;

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

    public Call send(final Request request) {
        Call call;
        call = client.newCall(request);
        return call;
    }

    public Request createRequest(String method, String URL, RequestBody body, Builder header) throws Exception {

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
            }
            else
                throw new Exception("Method not Allowed. Please Refer API Documentation. See\n" +
                        "     * <a href =\"https://developer.ringcentral.com/api-docs/latest/index.html#!#Resources.html\">Server Endpoint</a> for more information. ");
        }

        return request.build();
    }

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
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        };

    }

    public Headers getRequestHeader(Request request) {
        return request.headers();
    }

}