package com.ringcentral.rcandroidsdk;

import android.test.InstrumentationTestCase;

import com.ringcentral.rcandroidsdk.rcsdk.http.RCHeaders;

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
//
//    public void testSetHeadersAndGetHeaderArray() throws Exception {
//        RCHeaders h = new RCHeaders();
//        HashMap<String, String> map = new HashMap<>();
//        map.put("CONTENT_TYPE", "content-type");
//        map.put("AUTHORIZATION", "authorization");
//        map.put("URL_ENCODED_CONTENT_TYPE", "application/x-www-form-urlencoded");
//        h.setHeaders(map);
//        String[] reality = h.getHeadersArray();
//        String[] expected = new String[] {"AUTHORIZATION:authorization", "URL_ENCODED_CONTENT_TYPE:application/x-www-form-urlencoded", "CONTENT_TYPE:content-type"};
//        String expectedStr = expected.toString();
//        assertEquals(expectedStr, reality.toString());
//    }

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

