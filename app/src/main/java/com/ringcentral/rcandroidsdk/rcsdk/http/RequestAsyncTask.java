package com.ringcentral.rcandroidsdk.rcsdk.http;

import android.os.AsyncTask;

import com.ringcentral.rcandroidsdk.rcsdk.platform.Platform;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by andrew.pang on 6/26/15.
 */
public class RequestAsyncTask extends AsyncTask<Void, Void, Response> {

    Request request;
    public interface RequestResponse {
        void RequestResponseProcessFinish(Map result);
    }
    public RequestResponse delegate = null;
    public RequestAsyncTask(Request request){
        this.request = request;
    }

    protected Response doInBackground(Void... params) {
        return request.send();
    }

    protected void onPostExecute(Response result) {
        Map<String, String> responseMap = result.getJson();
        delegate.RequestResponseProcessFinish(responseMap);
    }

    protected void onProgressUpdate(String... progress) {
    }
}
