package com.ringcentral.rc_android_sdk.rcsdk;


import com.ringcentral.rc_android_sdk.rcsdk.platform.Platform;

/**
 * Created by andrew.pang on 6/26/15.
 */
import com.ringcentral.rc_android_sdk.rcsdk.platform.Platform;

public class SDK {

    Platform platform;

    public SDK(String appKey, String appSecret, Platform.Server server) {
        platform = new Platform(appKey, appSecret, server);
    }

    public Platform platform() {
        return this.platform;
    }
}

