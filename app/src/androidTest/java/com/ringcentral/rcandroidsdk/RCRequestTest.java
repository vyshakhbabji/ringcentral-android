package com.ringcentral.rcandroidsdk;

import android.test.InstrumentationTestCase;

import com.ringcentral.rcandroidsdk.rcsdk.http.RCRequest;

import java.util.HashMap;

/**
 * Created by andrew.pang on 7/7/15.
 */
public class RCRequestTest extends InstrumentationTestCase {

    public void testGetBodyString() throws Exception{
        HashMap<String, String> body = new HashMap<>();
        HashMap<String, String> header = new HashMap<>();
        body.put("body", "foo foo foo bar bar bar");
        RCRequest request = new RCRequest(body, header);
        String reality = request.getBodyString();
        String expected = "foo foo foo bar bar bar";

        HashMap<String, String> body2 = new HashMap<>();
        body2.put("grant_type", "foo");
        body2.put("password", "foobar");
        body2.put("username", "foofoo");
        RCRequest request2 = new RCRequest(body2, header);
        String reality2 = request2.getBodyString();
        String expected2 = "username=foofoo&password=foobar&grant_type=foo";

        assertEquals(expected, reality);
        assertEquals(expected2, reality2);
    }

    public void testIsMethods() throws Exception{
        HashMap<String, String> body = new HashMap<>();
        HashMap<String, String> header = new HashMap<>();
        header.put("method", "GET");
        RCRequest request = new RCRequest(body, header);
        assertTrue(request.isGet() && !request.isPost() && !request.isDelete() && !request.isPut());
        request.setMethod("POST");
        assertTrue(request.isPost() && !request.isGet() && !request.isDelete() && !request.isPut());
        request.setMethod("PUT");
        assertTrue(request.isPut() && !request.isGet() && !request.isDelete() && !request.isPost());
        request.setMethod("DELETE");
        assertTrue(request.isDelete() && !request.isPut() && !request.isGet() && !request.isPost());
    }

    public void testGetSetBody() throws Exception{
        HashMap<String, String> body = new HashMap<>();
        HashMap<String, String> header = new HashMap<>();
        RCRequest request = new RCRequest(body, header);
        HashMap<String, String> newBody = new HashMap<>();
        newBody.put("test", "foo");
        request.setBody(newBody);
        assertEquals(newBody, request.getBody());
    }
}
