package com.ringcentral.rc_android_sdk.rcsdk.subscription;

import android.util.Base64;
import android.util.Log;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.ringcentral.rc_android_sdk.rcsdk.http.APIResponse;
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
            e.printStackTrace();
        }
        System.out.println(decryptedString);
        return decryptedString;
    }

    public void removeSubscription() throws IOException {

        System.out.println("Subscription ID: " + subscription.id);
        String url = SUBSCRIPTION_END_POINT + subscription.id;
        platform.sendRequest("delete", url, null, null, new com.squareup.okhttp.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                    e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                    Log.v("Unsubscribe", String.valueOf(response.code()));

            }
        });
        this.unsubscribe();

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
            e.printStackTrace();
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
