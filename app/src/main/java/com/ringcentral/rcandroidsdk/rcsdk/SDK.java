package com.ringcentral.rcandroidsdk.rcsdk;

import com.ringcentral.rcandroidsdk.rcsdk.platform.Helpers;
import com.ringcentral.rcandroidsdk.rcsdk.platform.Platform;
import com.ringcentral.rcandroidsdk.rcsdk.platform.Platform2;

import java.io.Serializable;

/**
 * Created by andrew.pang on 6/26/15.
 */
public class SDK implements Serializable{

    //Platform platform;
    Platform2 platform2;
    Helpers helpers;

    public SDK(String appKey, String appSecret, String server){
        //platform = new Platform(appKey, appSecret, server);
        helpers = new Helpers(appKey, appSecret, server);
        platform2 = new Platform2(appKey, appSecret, server);
    }
//
//    public Platform getPlatform(){
//        return this.platform;
//    }

    public Helpers getHelpers() {
        return this.helpers;
    }

    public Platform2 getPlatform2() {
        return platform2;
    }

}
