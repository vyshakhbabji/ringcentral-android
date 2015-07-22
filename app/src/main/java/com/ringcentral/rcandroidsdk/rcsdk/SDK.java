package com.ringcentral.rcandroidsdk.rcsdk;

import com.ringcentral.rcandroidsdk.rcsdk.platform.Platform;

import java.io.Serializable;

/**
 * Created by andrew.pang on 6/26/15.
 */
public class SDK implements Serializable{

    static String version = "";
    Platform platform;

    public SDK(String appKey, String appSecret, String server){
        platform = new Platform(appKey, appSecret, server);
    }

    public Platform getPlatform(){
        return this.platform;
    }

}
