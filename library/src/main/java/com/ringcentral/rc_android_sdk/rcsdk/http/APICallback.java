package com.ringcentral.rc_android_sdk.rcsdk.http;

import com.ringcentral.rc_android_sdk.rcsdk.platform.APIException;

public abstract class APICallback {
    abstract public void onResponse(APIResponse response);

    abstract public void onFailure(APIException e);
}