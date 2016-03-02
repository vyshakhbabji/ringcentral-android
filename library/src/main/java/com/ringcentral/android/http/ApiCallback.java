package com.ringcentral.android.http;

public abstract class ApiCallback { //FIXME ApiCallback
    abstract public void onResponse(ApiResponse response);

    abstract public void onFailure(ApiException e);
}