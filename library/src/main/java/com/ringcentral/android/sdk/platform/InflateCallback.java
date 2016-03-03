package com.ringcentral.android.sdk.platform;

import com.ringcentral.android.sdk.http.ApiException;
import com.squareup.okhttp.Request;

public abstract class InflateCallback {
    abstract public void onResponse(Request request);

    abstract public void onFailure(ApiException e);
}