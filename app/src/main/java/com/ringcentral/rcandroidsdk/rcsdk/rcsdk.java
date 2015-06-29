package com.ringcentral.rcandroidsdk.rcsdk;

import android.os.AsyncTask;

import com.ringcentral.rcandroidsdk.rcsdk.platform.Platform;

/**
 * Created by andrew.pang on 6/26/15.
 */
public class Rcsdk {

    static String version = "";
    Platform p;

    public Rcsdk(String appKey, String appSecret, String server){
        p = new Platform(appKey, appSecret, server);
    }

    public Platform getPlatform(){
        return this.p;
    }

}
