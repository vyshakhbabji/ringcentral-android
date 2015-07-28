package com.ringcentral.rcandroidsdk;

import android.test.InstrumentationTestCase;

import com.ringcentral.rcandroidsdk.rcsdk.http.RCHeaders;
import com.ringcentral.rcandroidsdk.rcsdk.http.RCResponse;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by andrew.pang on 7/14/15.
 */
public class RCHeadersTest extends InstrumentationTestCase {

    public void testSetHeaderAndGetHeader() throws Exception {
        RCHeaders h = new RCHeaders();
        String key = "CONTENT_TYPE";
        String expected = "content-type";
        h.setHeader(key, expected);
        String reality = h.getHeader(key);
        assertEquals(expected, reality);
    }

    public void testSetHeadersAndGetHeaders() throws Exception{
        HashMap<String, String> headers = new HashMap<>();
        headers.put("a", "1");
        headers.put("b", "2");
        headers.put("c", "3");
        RCHeaders h = new RCHeaders();
        h.setHeaders(headers);
        assertEquals("1", h.getHeader("a"));
        assertEquals("2", h.getHeader("b"));
        assertEquals("3", h.getHeader("c"));
        assertEquals(headers, h.getHeaders());
    }

//    public void testSetHeadersAndGetHeaderArray() throws Exception {
//        RCHeaders h = new RCHeaders();
//        HashMap<String, String> map = new HashMap<>();
//        map.put("CONTENT_TYPE", "content-type");
//        map.put("AUTHORIZATION", "authorization");
//        map.put("URL_ENCODED_CONTENT_TYPE", "application/x-www-form-urlencoded");
//        h.setHeaders(map);
//        String[] reality = h.getHeadersArray();
//        String[] expected = new String[] {"AUTHORIZATION:authorization", "URL_ENCODED_CONTENT_TYPE:application/x-www-form-urlencoded", "CONTENT_TYPE:content-type"};
//        assertTrue(Arrays.equals(reality, expected));
//    }

    public void testGetContentTypeAndSetContentType() throws Exception{
        RCHeaders h = new RCHeaders();
        String expected = "test";
        h.setContentType(expected);
        assertEquals(expected, h.getContentType());
        String a = "abcd";
        assertFalse(a.equals(h.getContentType()));
    }
    public void testIsContentType() throws Exception {
        RCHeaders h = new RCHeaders();
        h.setContentType("fooBar");
        assertTrue(h.isContentType("fooBar"));
        assertFalse(h.isContentType("foor"));
    }

    public void testSpecialContentTypes() throws Exception {
        RCHeaders h = new RCHeaders();
        h.setContentType("application/json");
        assertTrue(h.isJson());
        h.setContentType("multipart/mixed");
        assertTrue(h.isMultipart());
        h.setContentType("application/x-www-form-urlencoded");
        assertTrue(h.isURLEncoded());
    }
}

