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
    String encryptionKey = "";
    public Subscription(String subscribeKey, String secretKey, String encryptionKey) {
        pubnub = new Pubnub("", subscribeKey, secretKey);
        this.encryptionKey = encryptionKey;
    }

    public void subscribe(String channel) {
        try {
            pubnub.subscribe(channel, new Callback() {

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

                        public void reconnectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : RECONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        @Override
                        public void successCallback(String channel, Object message) {
                            byte[] a = new byte[0];
                            byte[] b = new byte[0];
                            byte[] decrypted = new byte[0];
                            try {
                                a = "Hb3NQslzsa2lzKw5HpjG+A==".getBytes("UTF-8");
                                b = "x+ujFJPXkuym1I/Sj8LbWSeOYAf7bgIC/zF7hH048dJBvBZD07XfvYbao5I/FBSKgf2lB0hxkGKWP48Z+P5+rB0gWDJv2lPN9MjmcwAERK5iP/ruLKLbtDSJJH28fuGU38hiRyUxAl8sgQvMVgTLY6AOv4ZLiqNY+zy/9Z3Qrl1zen9y4L+/dbOawETaLQ2Pfs9xJrPKeRS+yxQEZdcRC/L7zWzQCSFQzgfvr7mK0RzgZXRTd0uK5tvTFnn9k0Yn4v1yDH7Q26L38x8qUh1Sco60c5ivL2YuiBmEPw6xyO0=".getBytes("UTF-8");

                                byte[] key = Base64.encode(a, Base64.DEFAULT);
                                SecretKeySpec skeySpec = new SecretKeySpec(key, "AES/ECB/PKCS5Padding");
                                byte[] data = Base64.encode(b, Base64.DEFAULT);
                                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                                cipher.init(Cipher.DECRYPT_MODE, skeySpec);
                                decrypted = cipher.doFinal(data);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            String decryptedString = decrypted.toString();
                            System.out.println("SUBSCRIBE : " + channel + " : "
                                    + message.getClass() + " : " + decryptedString);
                           // System.out.println(message.toString());
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

    public void presence(String channel){
        Callback callback = new Callback() {
            @Override
            public void connectCallback(String channel, Object message) {
                System.out.println("CONNECT on channel:" + channel
                        + " : " + message.getClass() + " : "
                        + message.toString());
            }

            @Override
            public void disconnectCallback(String channel, Object message) {
                System.out.println("DISCONNECT on channel:" + channel
                        + " : " + message.getClass() + " : "
                        + message.toString());
            }

            @Override
            public void reconnectCallback(String channel, Object message) {
                System.out.println("RECONNECT on channel:" + channel
                        + " : " + message.getClass() + " : "
                        + message.toString());
            }

            @Override
            public void successCallback(String channel, Object message) {
                System.out.println(channel + " : "
                        + message.getClass() + " : " + message.toString());
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                System.out.println("ERROR on channel " + channel
                        + " : " + error.toString());
            }
        };

        try {
            pubnub.presence(channel, callback);
        } catch (PubnubException e) {
            System.out.println(e.toString());
        }
    }

}
