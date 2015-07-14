package com.ringcentral.rcandroidsdk;

import android.test.InstrumentationTestCase;

import com.ringcentral.rcandroidsdk.rcsdk.platform.Platform;

/**
 * Created by andrew.pang on 7/7/15.
 */
public class PlatformTest extends InstrumentationTestCase {

    String appKey = "xhK3uzISTEaEYhFAtadVug";
    String appSecret = "1YRoPu64TeCOe_ZJy3ggLwGg-QDQd6QaWpSyIT8AxmjA";
    String username = "15856234166";
    String password = "P@ssw0rd";
    String extension = "";
    String RC_SERVER_PRODUCTION = "https://platform.ringcentral.com";
    String RC_SERVER_SANDBOX = "https://platform.devtest.ringcentral.com";

    Platform p;

//    public void testAuthorize() throws Exception{
//        p = new Platform(appKey, appSecret, RC_SERVER_SANDBOX);
//        p.authorize(username, extension, password);
//        //String token = platform.getAccessToken();
//        //assertTrue(platform.auth.access_token.equals(""));
//    }
//
//    public void testAccessToken() throws Exception{
//        String token = p.getAccessToken();
//    }
}
