package com.ringcentral.rc_android_sdk.rcsdk.http;

public abstract class APICallback { //FIXME ApiCallback
    abstract public void onResponse(APIResponse response);

    abstract public void onFailure(APIException e);
}