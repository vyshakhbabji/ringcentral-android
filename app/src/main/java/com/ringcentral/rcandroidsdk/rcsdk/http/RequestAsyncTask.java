package com.ringcentral.rcandroidsdk.rcsdk.http;

import android.os.AsyncTask;

import java.util.Map;

/**
 * Created by andrew.pang on 6/26/15.
 */
public class RequestAsyncTask extends AsyncTask<Void, Void, RCResponse> {

    RCRequest RCRequest;
    public interface RequestResponse {
        void RequestResponseProcessFinish(boolean isAuth, Map result);
    }
    public RequestResponse delegate = null;
    public RequestAsyncTask(RCRequest RCRequest){
        this.RCRequest = RCRequest;
    }

    protected RCResponse doInBackground(Void... params) {
        //return RCRequest.send();
        return null;
    }

    protected void onPostExecute(RCResponse result) {
//        Map<String, String> responseMap = result.getJson();
//        if (RCRequest.isAuth == true) {
//            delegate.RequestResponseProcessFinish(true, responseMap);
//        } else{
//            delegate.RequestResponseProcessFinish(false, responseMap);
//        }
    }

    protected void onProgressUpdate(String... progress) {
    }
}
