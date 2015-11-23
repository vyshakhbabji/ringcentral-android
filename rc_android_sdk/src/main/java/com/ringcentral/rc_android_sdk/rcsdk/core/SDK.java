package com.ringcentral.rc_android_sdk.rcsdk.core;


import com.ringcentral.rc_android_sdk.rcsdk.http.Client;
import com.ringcentral.rc_android_sdk.rcsdk.platform.Platform;

/**
 * Created by vyshakh.babji on 11/5/15.
 */


public class SDK {
    Platform platform;
    Client client;

    public SDK(String appKey, String appSecret, Platform.Server server) {

        this.client = new Client();
        this.platform = new Platform(client, appKey, appSecret, server);
    }

    public Platform platform() {
        return this.platform;
    }
}
