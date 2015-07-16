package com.ringcentral.rcandroidsdk;

import android.test.InstrumentationTestCase;

import com.ringcentral.rcandroidsdk.rcsdk.platform.Auth;
import com.ringcentral.rcandroidsdk.rcsdk.platform.Platform;

import java.util.HashMap;
import java.util.Map;


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

    public void testSetAuthDataAndGetAuthData() throws Exception {
//        Platform p = new Platform(appKey, appSecret, RC_SERVER_SANDBOX);
//        Map<String, String> authData = new HashMap<>();
//        authData.put("access_token", "value");
//        Auth a = new Auth();
//        a.setData(authData);
//        p.setAuthData(authData);
//        assertEquals(a.getData(), p.getAuthData());
//    }
    }

}
