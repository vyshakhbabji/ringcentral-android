package com.ringcentral.rcandroidsdk.rcsdk.subscription;
import android.util.Base64;

import com.pubnub.api.*;
import org.json.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by andrew.pang on 7/15/15.
 */
public class Subscription {

    public Pubnub pubnub;
    public String encryptionKey = "";
    ArrayList<String> eventFilters = new ArrayList<>();
    HashMap<String, String> deliveryMode = new HashMap<>();
    public String responseMessage = "";

    public Subscription(String subscribeKey, String secretKey, String encryptionKey) {
        pubnub = new Pubnub("", subscribeKey, secretKey);
        this.encryptionKey = encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public Pubnub getPubnub() {
        return pubnub;
    }

    public void register(HashMap<String, String> options, Callback c){
        if(this.isSubscribed()){
            this.renew(options, c);
        } else{
            this.subscribe(options, c);
        }
    }
    public void subscribe(HashMap<String, String> options, Callback c) {
        try {
            if(options.containsKey("address")) {
                String address = options.get("address");
                pubnub.subscribe(address, c);
            }
        } catch (PubnubException e) {
            e.printStackTrace();
        }
    }

    public void renew(HashMap<String, String> options, Callback c){

    }

    public void addEvents(String[] events) {
        for(String event:events){
            this.eventFilters.add(event);
        }
    }

    public void setEvents(String[] events){
        this.eventFilters = new ArrayList<String>(Arrays.asList(events));
    }

    boolean isSubscribed(){
        return this.deliveryMode.containsKey("subscriberKey") && this.deliveryMode.containsKey("address");
    }

    public void unsubscribe(HashMap<String, String> options) {
        if (pubnub != null && this.isSubscribed()) {
            if (options.containsKey("address")) {
                String address = options.get("address");
                this.pubnub.unsubscribe(address);
            }
        }
    }
//    public void presence(String channel){
//        Callback callback = new Callback() {
//            @Override
//            public void connectCallback(String channel, Object message) {
//                System.out.println("CONNECT on channel:" + channel
//                        + " : " + message.getClass() + " : "
//                        + message.toString());
//            }
//
//            @Override
//            public void disconnectCallback(String channel, Object message) {
//                System.out.println("DISCONNECT on channel:" + channel
//                        + " : " + message.getClass() + " : "
//                        + message.toString());
//            }
//
//            @Override
//            public void reconnectCallback(String channel, Object message) {
//                System.out.println("RECONNECT on channel:" + channel
//                        + " : " + message.getClass() + " : "
//                        + message.toString());
//            }
//
//            @Override
//            public void successCallback(String channel, Object message) {
//                System.out.println(channel + " : "
//                        + message.getClass() + " : " + message.toString());
//            }
//
//            @Override
//            public void errorCallback(String channel, PubnubError error) {
//                System.out.println("ERROR on channel " + channel
//                        + " : " + error.toString());
//            }
//        };
//
//        try {
//            pubnub.presence(channel, callback);
//        } catch (PubnubException e) {
//            System.out.println(e.toString());
//        }
//    }

    public String notify(String message, String encryptionKey){
        byte[] key = Base64.decode(encryptionKey, Base64.NO_WRAP);
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        byte[] data = Base64.decode(message, Base64.NO_WRAP);
        String decryptedString = "";
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] decrypted = cipher.doFinal(data);
            decryptedString = new String(decrypted);
        } catch(Exception e){
            e.printStackTrace();
        }
        return decryptedString;
    }

}
