package com.ringcentral.rc_android_sdk.rcsdk.http;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
public abstract class APICallback implements Callback{

    @Override
    public void onResponse(Response response) throws IOException {
        //apiresponse = new APIResponse(response,response.request());
        onAPIResponse(new APIResponse(response, response.request()));
    }

    @Override
    public void onFailure(Request request, IOException e) {
        onAPIFailure(new APIResponse(null, request).request(), e);
    }

    public void onAPIResponse(APIResponse response) throws IOException {
//    	onResponse(response);
    }

    public void onAPIFailure(Request request , IOException e){
        //  onFailure(request, e);
    }
}