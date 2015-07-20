package com.ringcentral.rcandroidsdk;

import android.test.InstrumentationTestCase;

import com.ringcentral.rcandroidsdk.rcsdk.http.RCResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

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

    public void testSetStatusAndGetStatus() throws Exception{
        RCResponse r = new RCResponse();
        r.setStatus(200);
        assertEquals(200, r.getStatus());
        r.setStatus(304);
        assertEquals(304, r.getStatus());
        r.setStatus(0);
        assertEquals(0, r.getStatus());
    }

    public void testSetBodyAndGetBody() throws Exception{
        RCResponse r = new RCResponse();
        String expected = "This is a test";
        r.setBody(expected);
        assertEquals(expected, r.getBody());
        r.setBody("");
        assertEquals("", r.getBody());
    }

    public void testGetJson() throws Exception{
        RCResponse r = new RCResponse();
        Map<String, String> expected = new HashMap<>();
        expected.put("test", "a");
        expected.put("foo", "bar");
        String body = "{\"test\":\"a\", \"foo\":\"bar\"}";
        r.setBody(body);
        Map<String, String> actual = r.getJson();
        assertEquals(expected, actual);
    }
}
