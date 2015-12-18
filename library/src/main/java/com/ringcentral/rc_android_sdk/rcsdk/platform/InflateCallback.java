package com.ringcentral.rc_android_sdk.rcsdk.platform;

import com.ringcentral.rc_android_sdk.rcsdk.http.APIException;
import com.squareup.okhttp.Request;

public abstract class InflateCallback {
    abstract public void onResponse(Request request);

    abstract public void onFailure(APIException e);
}