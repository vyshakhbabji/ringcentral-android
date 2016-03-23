package com.ringcentral.android.sdk.platform;

import com.ringcentral.android.sdk.http.ApiException;
import com.squareup.okhttp.Request;

public interface InflateCallback {
    void onResponse(Request request);

    void onFailure(ApiException e);
}