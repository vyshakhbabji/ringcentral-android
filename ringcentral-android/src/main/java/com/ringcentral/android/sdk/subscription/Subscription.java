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
package com.ringcentral.android.sdk.subscription;

import android.util.Base64;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;
import com.ringcentral.android.sdk.core.RingCentralException;
import com.ringcentral.android.sdk.http.ApiCallback;
import com.ringcentral.android.sdk.http.ApiException;
import com.ringcentral.android.sdk.http.ApiResponse;
import com.ringcentral.android.sdk.platform.Platform;
import com.ringcentral.android.sdk.subscription.SubscriptionPayload.DeliveryMode;
import com.ringcentral.android.sdk.utils.Helpers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Subscription {


    public IDeliveryMode deliveryMode = new IDeliveryMode();
    public String id = "";
    public Pubnub pubnubObj;
    String creationTime = "";
    ArrayList<String> eventFilters = new ArrayList<>();
    String expirationTime = "";
    int expiresIn = 0;
    Platform platform;
    String status = "";
    Subscription subscription;
    String SUBSCRIPTION_URL = "/restapi/v1.0/subscription/";
    String uri = "";
    SubscriptionCallback subscriptionCallback;
    Future future= null;

    ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();

    public Subscription(Platform platform) {

        this.platform = platform;
        this.subscription = this;
    }

    public void addEvents(String[] events) {
        for (String event : events) {
            this.eventFilters.add(event);
        }
    }

    private ArrayList<String> getFullEventFilters() {
        return this.eventFilters;
    }

    public Pubnub getPubnub() {
        return pubnubObj;
    }

    public boolean isSubscribed() {
        return !(this.deliveryMode.subscriberKey.equals("") && this.deliveryMode.address
                .equals(""));
    }

    public String decrypt(String message) {
        byte[] key = Base64.decode(subscription.deliveryMode.encryptionKey, Base64.NO_WRAP);
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        byte[] data = Base64.decode(message, Base64.NO_WRAP);
        String decryptedString = "";
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] decrypted = cipher.doFinal(data);
            decryptedString = new String(decrypted);
        } catch (Exception e) {
            throw new RingCentralException(e);
        }
        return decryptedString;
    }

    private void renew(final ApiCallback callback) {
        if (!subscription.isSubscribed()) throw new Error("No subscription");
        if (subscription.getFullEventFilters().size() == 0) throw new Error("Events are undefined");
        SubscriptionPayload subscriptionPayload = new SubscriptionPayload(subscription.getFullEventFilters().toArray(new String[0]), new DeliveryMode("PubNub", "false"));
        String payload = new GsonBuilder().create().toJson(subscriptionPayload);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), payload.toString().getBytes());

        platform.put(SUBSCRIPTION_URL + this.id, body, null, new ApiCallback() {
            @Override
            public void onResponse(ApiResponse response) {
                String responseBody;
                try {
                    responseBody = response.text();
                    updateSubscription(new JSONObject(responseBody));
                    subscribeToPubnub();
                    callback.onResponse(response);
                } catch (IOException | JSONException e) {
                    callback.onFailure(new ApiException(e));
                }
            }

            @Override
            public void onFailure(ApiException e) {
                reset();
                onFailure(e);
            }
        });
    }


    private void reset() {
        exec.shutdown();
        if (this.isSubscribed()) this.unsubscribe();
    }

    private void renewSubscription(final ApiCallback callback) {
        if (future != null && !future.isDone()){
            future.cancel(true);
            return;
        }

        future = exec.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    public void run() {
                        renew(callback);
                    }
                }).start();
            }
        }, this.expiresIn - 60, this.expiresIn - 30, TimeUnit.SECONDS);
    }


    public void remove(final ApiCallback callback) {
        String url = SUBSCRIPTION_URL + subscription.id;
        platform.delete(url, null, null, new ApiCallback() {
            @Override
            public void onResponse(ApiResponse response) {
                unsubscribe();
                callback.onResponse(response);
            }

            @Override
            public void onFailure(ApiException e) {
                callback.onFailure(e);
            }
        });
    }

    private void setEvents(String[] events) {
        this.eventFilters = new ArrayList<String>(Arrays.asList(events));
    }

    public void subscribe(final ApiCallback callback) {
        SubscriptionPayload subscriptionPayload = new SubscriptionPayload(subscription.getFullEventFilters().toArray(new String[0]), new DeliveryMode("PubNub", "false"));
        String payload = new GsonBuilder().create().toJson(subscriptionPayload);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), payload.toString().getBytes());
        platform.post(SUBSCRIPTION_URL, body, null, new ApiCallback() {
            @Override
            public void onResponse(ApiResponse response) {
                String responseBody;
                try {
                    responseBody = response.text();
                    updateSubscription(new JSONObject(responseBody));
                    renewSubscription(callback);
                    subscribeToPubnub();
                    callback.onResponse(response);
                } catch (IOException| JSONException e) {
                    callback.onFailure(new ApiException(e));
                }
            }

            @Override
            public void onFailure(ApiException e) {
                callback.onFailure(e);
            }
        });
    }

    public void onNotification(final SubscriptionCallback c) {
        subscriptionCallback = c;
    }

    private void subscribeToPubnub(){

        pubnubObj = new Pubnub("", subscription.deliveryMode.subscriberKey,
                deliveryMode.secretKey);
        try {
            pubnubObj.subscribe(deliveryMode.address, new com.pubnub.api.Callback() {
                @Override
                public void connectCallback(String channel, Object message) {
                    subscriptionCallback.connectCallback(channel, message);
                }

                @Override
                public void disconnectCallback(String channel, Object message) {
                    subscriptionCallback.disconnectCallback(channel,message);
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    subscriptionCallback.errorCallback(channel,error);
                }

                @Override
                public void reconnectCallback(String channel, Object message) {
                    subscriptionCallback.reconnectCallback(channel,message);
                }

                @Override
                public void successCallback(String channel, Object message) {
                    String decryptedString = subscription.decrypt(message.toString());
                    try {
                        subscriptionCallback.successCallback(new JSONObject(decryptedString), channel, message);
                    } catch (JSONException e) {
                        subscriptionCallback.errorCallback(channel, new RingCentralException(e));
                    }
                }
            });
        } catch (PubnubException e) {
            throw new RingCentralException(e);
        }
    }


    public void unsubscribe() {
        if ((this.pubnubObj != null) && this.isSubscribed()) {
            exec.shutdown();
            this.pubnubObj.unsubscribe(deliveryMode.address);
        }
    }

    private void updateSubscription(JSONObject responseJson)
            throws JSONException {
        id = responseJson.getString("id");
        JSONObject deliveryMode = responseJson.getJSONObject("deliveryMode");
        this.expiresIn = (int) responseJson.get("expiresIn");
        this.deliveryMode.encryptionKey = deliveryMode
                .getString("encryptionKey");
        this.deliveryMode.address = deliveryMode.getString("address");
        this.deliveryMode.subscriberKey = deliveryMode
                .getString("subscriberKey");
    }

    public class IDeliveryMode {
        public String address = "";
        public boolean encryption = false;
        public String encryptionKey = "";
        public String secretKey = "";
        public String subscriberKey = "";
        public String transportType = "Pubnub";
    }

}
