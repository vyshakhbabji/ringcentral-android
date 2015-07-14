package com.ringcentral.rcandroidsdk;

import android.test.InstrumentationTestCase;

import com.ringcentral.rcandroidsdk.rcsdk.http.RCResponse;
import com.squareup.okhttp.Response;

/**
 * Created by andrew.pang on 7/14/15.
 */
public class RCResponseTest extends InstrumentationTestCase {

    public void testCheckStatus() throws Exception{
        RCResponse r = new RCResponse();
        r.setStatus(200);
        assertTrue(r.checkStatus());
        r.setStatus(205);
        assertTrue(r.checkStatus());
        r.setStatus(300);
        assertFalse(r.checkStatus());
    }
}
