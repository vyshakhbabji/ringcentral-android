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

    String appKey = "abcd123efg";
    String appSecret = "hij123klm";
    String username = "15856234166";
    String password = "P@ssw0rd";
    String extension = "";
    String RC_SERVER_PRODUCTION = "https://platform.ringcentral.com";
    String RC_SERVER_SANDBOX = "https://platform.devtest.ringcentral.com";

    Platform p;

    public void testGetApiKey() throws Exception {
        p = new Platform(appKey, appSecret, RC_SERVER_SANDBOX);
        String actual = p.getApiKey();
        String expected = "YWJjZDEyM2VmZzpoaWoxMjNrbG0=";
        assertEquals(expected, actual);
    }

    public void testApiUrl() throws Exception {
        p = new Platform(appKey, appSecret, RC_SERVER_SANDBOX);
        HashMap<String, String> options = new HashMap<>();
        options.put("addServer", "true");
        String actualUrl1 = p.apiURL("/restapi/v1.0/account/~/call-log", options);
        String actualUrl2 = p.apiURL("/test", options);
        String actualUrl3 = p.apiURL("/restapi/v1.0/account/~/message-store", options);
        options.put("addToken", "true");
        String actualUrl4 = p.apiURL("/test", options);
        options.remove("addToken");
        options.put("addMethod", "testMethod");
        String actualUrl5 = p.apiURL("/test", options);

        assertEquals("https://platform.devtest.ringcentral.com/restapi/v1.0/account/~/call-log", actualUrl1);
        assertEquals("https://platform.devtest.ringcentral.com/restapi/v1.0/test", actualUrl2);
        assertEquals("https://platform.devtest.ringcentral.com/restapi/v1.0/account/~/message-store", actualUrl3);
        assertEquals("https://platform.devtest.ringcentral.com/restapi/v1.0/test?access_token=", actualUrl4);
        assertEquals("https://platform.devtest.ringcentral.com/restapi/v1.0/test?_method=testMethod", actualUrl5);
    }


}
