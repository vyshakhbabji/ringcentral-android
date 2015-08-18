package com.ringcentral.rcandroidsdk.oldsdk;

import android.test.InstrumentationTestCase;

import com.ringcentral.rcandroidsdk.oldsdk.RCRequest;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by andrew.pang on 7/7/15.
 */
public class RCRequestTest extends InstrumentationTestCase {

    public void testGetBodyString() throws Exception{
//        LinkedHashMap<String, String> body = new LinkedHashMap<>();
//        HashMap<String, String> header = new HashMap<>();
//        body.put("body", "foo foo foo bar bar bar");
//        RCRequest request = new RCRequest(body, header);
//        String reality = request.getBodyString();
//        String expected = "foo foo foo bar bar bar";
//
//        LinkedHashMap<String, String> body2 = new LinkedHashMap<>();
//        body2.put("grant_type", "foo");
//        body2.put("password", "foobar");
//        body2.put("username", "foofoo");
//        RCRequest request2 = new RCRequest(body2, header);
//        String reality2 = request2.getBodyString();
//        String expected2 = "grant_type=foo&password=foobar&username=foofoo";
//
//        assertEquals(expected, reality);
//        assertEquals(expected2, reality2);
    }

    public void testIsMethods() throws Exception{
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
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
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        HashMap<String, String> header = new HashMap<>();
        RCRequest request = new RCRequest(body, header);
        LinkedHashMap<String, String> newBody = new LinkedHashMap<>();
        newBody.put("test", "foo");
        request.setBody(newBody);
        assertEquals(newBody, request.getBody());
    }


}
