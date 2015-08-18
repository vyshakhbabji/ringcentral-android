package com.ringcentral.rcandroidsdk.rcsdk;

import com.ringcentral.rcandroidsdk.rcsdk.platform.Helpers;
import com.ringcentral.rcandroidsdk.rcsdk.platform.Platform;

import java.io.Serializable;

/**
 * Created by andrew.pang on 6/26/15.
 */
public class SDK implements Serializable{

    //OldPlatform platform;
    Platform platform;
    Helpers helpers;

    public SDK(String appKey, String appSecret, String server){
        //platform = new OldPlatform(appKey, appSecret, server);
        helpers = new Helpers(appKey, appSecret, server);
        platform = new Platform(appKey, appSecret, server);
    }
//
//    public OldPlatform getPlatform(){
//        return this.platform;
//    }

    public Helpers getHelpers() {
        return this.helpers;
    }

    public Platform getPlatform() {
        return platform;
    }

}
