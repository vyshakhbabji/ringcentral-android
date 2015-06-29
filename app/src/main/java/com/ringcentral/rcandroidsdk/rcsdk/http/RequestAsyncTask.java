package com.ringcentral.rcandroidsdk.rcsdk.http;

import android.os.AsyncTask;

import java.util.HashMap;

/**
 * Created by andrew.pang on 6/26/15.
 */
public class RequestAsyncTask extends AsyncTask<Void, Void, Void> {


    Request request;
    public RequestAsyncTask(Request request){
        this.request = request;
    }

    protected Void doInBackground(Void... params) {
        request.send();
        return null;
    }

    protected void onPostExecute(String result) {
        //delegate.OAuthProcessFinish(result);
    }

    protected void onProgressUpdate(String... progress) {
    }
}
