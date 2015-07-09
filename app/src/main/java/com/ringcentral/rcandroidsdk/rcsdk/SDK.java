package com.ringcentral.rcandroidsdk.rcsdk;

import android.os.AsyncTask;

import com.ringcentral.rcandroidsdk.rcsdk.platform.Platform;

import java.io.Serializable;

/**
 * Created by andrew.pang on 6/26/15.
 */
public class SDK implements Serializable{

    static String version = "";
    Platform p;

    public SDK(String appKey, String appSecret, String server){
        p = new Platform(appKey, appSecret, server);
    }

    public Platform getPlatform(){
        return this.p;
    }

}
