package com.ringcentral.rcandroidsdk.rcsdk.subscription;
import android.util.Base64;

import com.pubnub.api.*;
import org.json.*;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by andrew.pang on 7/15/15.
 */
public class Subscription {

    Pubnub pubnub;
    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public String encryptionKey = "";
    public String responseMessage = "";
    public Subscription(String subscribeKey, String secretKey, String encryptionKey) {
        pubnub = new Pubnub("", subscribeKey, secretKey);
        this.encryptionKey = encryptionKey;
    }


    public void subscribe(String address) {

        try {
            pubnub.subscribe(address, new Callback() {

                        @Override
                        public void connectCallback(String channel, Object message) {

                            System.out.println("SUBSCRIBE : CONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        @Override
                        public void disconnectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : DISCONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        @Override
                        public void reconnectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : RECONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "

                                    + message.toString());
                        }

                        @Override
                        public void successCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : " + channel + " : "
                                    + message.getClass() + " : " + message.toString());
                        }

                        @Override
                        public void errorCallback(String channel, PubnubError error) {
                            System.out.println("SUBSCRIBE : ERROR on channel " + channel
                                    + " : " + error.toString());
                        }

                    }
            );
        } catch (PubnubException e) {
            e.printStackTrace();
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

    public String notify(String message){
        byte[] key = Base64.decode(this.encryptionKey, Base64.NO_WRAP);
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
