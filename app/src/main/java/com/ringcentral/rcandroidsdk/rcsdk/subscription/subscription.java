package com.ringcentral.rcandroidsdk.rcsdk.subscription;
import com.pubnub.api.*;
import org.json.*;

/**
 * Created by andrew.pang on 7/15/15.
 */
public class Subscription {

    Pubnub pubnub;

    public Subscription(String subscribeKey, String secretKey) {
        pubnub = new Pubnub("", subscribeKey, secretKey);
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
