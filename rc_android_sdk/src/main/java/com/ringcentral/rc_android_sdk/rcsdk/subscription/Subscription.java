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
package com.ringcentral.rc_android_sdk.rcsdk.subscription;

import android.util.Base64;
import android.util.Log;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;

import com.ringcentral.rc_android_sdk.rcsdk.platform.AuthException;
import com.ringcentral.rc_android_sdk.rcsdk.platform.Platform;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Subscription {

    public class IDeliveryMode {
        public String address = "";
        public boolean encryption = false;
        public String encryptionKey = "";
        public String secretKey = "";
        public String subscriberKey = "";
        public String transportType = "Pubnub";
    }
    String creationTime = "";
    public IDeliveryMode deliveryMode = new IDeliveryMode();
    ArrayList<String> eventFilters = new ArrayList<>();
    String expirationTime = "";
    int expiresIn = 0;
    public String id = "";
    Platform platform;
    public Pubnub pubnub;

    String status = "";
    Subscription subscription;

    String SUBSCRIPTION_END_POINT = "/restapi/v1.0/subscription/";

    String uri = "";

    public Subscription(Platform platform) {

        this.platform = platform;
        this.subscription = this;
    }

    public void addEvents(String[] events) {
        for (String event : events) {
            this.eventFilters.add(event);
        }
    }

    private ArrayList getFullEventFilters() {
        return this.eventFilters;
    }

    public Pubnub getPubnub() {
        return pubnub;
    }

    boolean isSubscribed() {
        return !(this.deliveryMode.subscriberKey.equals("") && this.deliveryMode.address
                .equals(""));
    }

    public String notify(String message, String encryptionKey) {
       // Security.addProvider(new BouncyCastleProvider());
        System.out.println(message);
        byte[] key = Base64.decode(encryptionKey,Base64.NO_WRAP);
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        byte[] data = Base64.decode(message,Base64.NO_WRAP);
        String decryptedString = "";
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] decrypted = cipher.doFinal(data);
            decryptedString = new String(decrypted);
        } catch (Exception e) {
            throw new AuthException("Subscription Notification Failed.");
        }
        System.out.println(decryptedString);
        return decryptedString;
    }

    public void removeSubscription() throws AuthException {

        System.out.println("Subscription ID: " + subscription.id);
        String url = SUBSCRIPTION_END_POINT + subscription.id;
        platform.sendRequest("delete", url, null, null, new com.squareup.okhttp.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                throw new AuthException("Failed to remove subscription");
            }

            @Override
            public void onResponse(Response response) throws IOException {

                if (response.isSuccessful()) {
                    unsubscribe();
                    Log.v("Unsubscribe", String.valueOf(response.code()));
                }
                else
                    throw new AuthException("Failed to remove subscription and unsubscribe");


            }
        });
    }

    public void setEvents(String[] events) {
        this.eventFilters = new ArrayList<String>(Arrays.asList(events));
    }

    public void subscribe(JSONObject subscriptionResponse, Callback c) {
        try {
            updateSubscription(subscriptionResponse);
            pubnub = new Pubnub("", deliveryMode.subscriberKey,
                    deliveryMode.secretKey);
            pubnub.subscribe(this.deliveryMode.address, c);
        } catch (Exception e) {
            throw new AuthException("Failed to remove subscription",e);
        }
    }

    public void unsubscribe() {
        if ((this.pubnub != null) && this.isSubscribed())
            this.pubnub.unsubscribe(deliveryMode.address);
        System.out.println("Unsubscribed!!! ");
    }

    public void updateSubscription(JSONObject responseJson)
            throws JSONException {
        id = responseJson.getString("id");
        JSONObject deliveryMode = responseJson.getJSONObject("deliveryMode");
        this.deliveryMode.encryptionKey = deliveryMode
                .getString("encryptionKey");
        this.deliveryMode.address = deliveryMode.getString("address");
        this.deliveryMode.subscriberKey = deliveryMode
                .getString("subscriberKey");
        this.deliveryMode.secretKey = "sec-c-ZDNlYjY0OWMtMWFmOC00OTg2LWJjMTMtYjBkMzgzOWRmMzUz";// deliveryMode.getString("secretKey");
    }

}
