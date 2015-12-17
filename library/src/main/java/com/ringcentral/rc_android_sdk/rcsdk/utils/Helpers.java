/*
 * Copyright (c) 2015 RingCentral, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.ringcentral.rc_android_sdk.rcsdk.utils;

import android.util.Log;

import com.ringcentral.rc_android_sdk.rcsdk.http.APICallback;
import com.ringcentral.rc_android_sdk.rcsdk.platform.APIException;
import com.ringcentral.rc_android_sdk.rcsdk.platform.Platform;
import com.ringcentral.rc_android_sdk.rcsdk.subscription.Subscription;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

/**
 * Created by vyshakh.babji on 12/4/15.
 */
public class Helpers {

    Platform platform;


    public Helpers(Platform platform) {
        this.platform = platform;
    }

    /**
     * Call-log helper
     *
     * @param callback
     */
    public void callLog(final APICallback callback) throws APIException {
        try {
            platform.get(API.CALLLOG.value, null, null, callback);
        } catch (APIException e) {
            throw new APIException("Call-log failed", e);
        }
    }

    /**
     * sendSMS Helper
     *
     * @param to
     * @param from
     * @param message
     * @param callback
     */
    public void sendSMS(String to, String from, String message, APICallback callback) throws APIException {

        String payload = "{\"to\": [{\"phoneNumber\":\" " + to + "\"}]," +
                "\"from\": {\"phoneNumber\":\" " + from + "\"}," +
                "\"text\":\"" + message + "\"}";

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), payload.getBytes());

        try {
            platform.post(API.SMS.value, body, null, callback);
        } catch (APIException e) {
            throw new APIException("Sending SMS failed", e);
        }

    }

    public void ringout(String to, String from, String callerID, String hasprompt, APICallback callback) throws APIException {

        if (hasprompt == "" || hasprompt == null)
            hasprompt = String.valueOf(true);


        String payload = "{\"to\": [{\"phoneNumber\":\" " + to + "\"}]," +
                "\"from\": {\"phoneNumber\":\" " + from + "\"}," +
                "\"callerId\": {\"phoneNumber\":\" " + callerID + "\"}," +
                "\"playPrompt\": true}";

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), payload.getBytes());

        try {
            platform.post(API.RINGOUT.value, body, null, callback);
        } catch (APIException e) {
            throw new APIException("Ringout failed", e);
        }

    }

    public void subscribe(APICallback callback) throws APIException {
        String payload = "{\r\n  \"eventFilters\": [ \r\n    \"/restapi/v1.0/account/~/extension/~/presence\", \r\n    \"/restapi/v1.0/account/~/extension/~/message-store\" \r\n  ], \r\n  \"deliveryMode\": { \r\n    \"transportType\": \"PubNub\", \r\n    \"encryption\": \"false\" \r\n  } \r\n}";
        String url = "/restapi/v1.0/subscription";
        Log.v("Subscribe payload", payload);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), payload.getBytes());
        try {
            platform.post(url, body, null, callback);
        } catch (APIException e) {
            throw new APIException("Subscription failed", e);
        }

    }

    public void removeSubscription(int subscriptionId, APICallback callback) throws APIException {
        final Subscription subscription = new Subscription(platform);
        String url = "/restapi/v1.0/subscription" + subscriptionId;
        try {
            platform.delete(url, null, null, callback);
        } catch (APIException e) {
            throw new APIException("Remove subscription Failed ", e);
        }

    }


    public enum API {
        CALLLOG("/restapi/v1.0/account/~/call-log"), SMS(
                "/restapi/v1.0/account/~/extension/~/sms"), RINGOUT("/restapi/v1.0/account/~/extension/~/ringout");

        private String value;

        API(String url) {
            this.value = url;
        }
    }
}
