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
import com.ringcentral.rc_android_sdk.rcsdk.platform.AuthException;
import com.ringcentral.rc_android_sdk.rcsdk.platform.Platform;
import com.ringcentral.rc_android_sdk.rcsdk.subscription.Subscription;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

/**
 * Created by vyshakh.babji on 12/4/15.
 */
public class Helpers {

    public enum API {
        CALLLOG("/restapi/v1.0/account/~/call-log"), SMS(
                "/restapi/v1.0/account/~/extension/~/sms"), RINGOUT("/restapi/v1.0/account/~/extension/~/ringout");

        private String value;

        API(String url) {
            this.value = url;
        }
    }


    Platform platform;

    public Helpers(Platform platform) {
        this.platform = platform;
    }

    /**
     * Call-log helper
     * @param callback
     */
    public void callLog(final APICallback callback) {
        try {
            platform.get(API.CALLLOG.value, null, null, callback);
        } catch (AuthException e) {
            e.printStackTrace();
        }
    }

    /**
     * sendSMS Helper
     * @param to
     * @param from
     * @param message
     * @param callback
     */
    public void sendSMS(String to, String from, String message, APICallback callback) {

        String payload = "{\"to\": [{\"phoneNumber\":\" "+to+"\"}]," +
                "\"from\": {\"phoneNumber\":\" "+from+"\"}," +
                "\"text\":\"" +message+"\"}";

        RequestBody body = RequestBody.create(MediaType.parse("application/json"),payload.getBytes());

        try {
            platform.post(API.SMS.value, body, null, callback);
        } catch (AuthException e) {
           e.printStackTrace();
        }

    }


<<<<<<< HEAD
    public void ringout(String to, String from, String callerID, String hasprompt, APICallback callback) {
=======
    public void ringout(String to, String from, String callerID, String hasprompt, Callback callback) {
>>>>>>> f35ebd5902482eda5b90874d7e689071c019ffdc

         if(hasprompt==""||hasprompt==null)
             hasprompt=String.valueOf(true);


        String payload = "{\"to\": [{\"phoneNumber\":\" "+to+"\"}]," +
                "\"from\": {\"phoneNumber\":\" "+from+"\"}," +
                "\"callerId\": {\"phoneNumber\":\" "+callerID+"\"}," +
                "\"playPrompt\": true}";

        RequestBody body = RequestBody.create(MediaType.parse("application/json"),payload.getBytes());

        try {
            platform.post(API.RINGOUT.value, body, null, callback);
        } catch (AuthException e) {
            e.printStackTrace();
        }

    }


<<<<<<< HEAD
    public void subscribe(APICallback callback){
=======
    public void subscribe(Callback callback){
>>>>>>> f35ebd5902482eda5b90874d7e689071c019ffdc

        String payload = "{\r\n  \"eventFilters\": [ \r\n    \"/restapi/v1.0/account/~/extension/~/presence\", \r\n    \"/restapi/v1.0/account/~/extension/~/message-store\" \r\n  ], \r\n  \"deliveryMode\": { \r\n    \"transportType\": \"PubNub\", \r\n    \"encryption\": \"false\" \r\n  } \r\n}";
        String url = "/restapi/v1.0/subscription";
        Log.v("Subscribe payload", payload);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"),payload.getBytes());
        try {
            platform.post(url, body, null,callback);
        } catch (AuthException e) {
            e.printStackTrace();
        }
    }


    public void removeSubscription(int subscriptionId, APICallback callback) {

        final Subscription subscription = new Subscription(platform);
        String url =  "/restapi/v1.0/subscription" + subscriptionId;
        try {
            platform.delete(url, null, null, callback);
        } catch (AuthException e) {
            e.printStackTrace();
        }
    }
}
