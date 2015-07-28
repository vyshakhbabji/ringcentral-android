package com.ringcentral.rcandroidsdk.rcsdk;

import com.ringcentral.rcandroidsdk.rcsdk.platform.Platform;

import java.io.Serializable;

/**
 * Created by andrew.pang on 6/26/15.
 */
public class SDK implements Serializable{

    static final String RC_SERVER_PRODUCTION = "https://platform.ringcentral.com";
    static final String RC_SERVER_SANDBOX = "https://platform.devtest.ringcentral.com";
    Platform platform;

    public SDK(String appKey, String appSecret, String server){
        platform = new Platform(appKey, appSecret, server);
    }

    public Platform getPlatform(){
        return this.platform;
    }

}
