package com.ringcentral.rcandroidsdk;

import android.test.InstrumentationTestCase;

import com.ringcentral.rcandroidsdk.rcsdk.http.RCResponse;

import java.util.List;

import static org.mockito.Mockito.*;
import static com.google.dexmaker.*;

/**
 * Created by andrew.pang on 7/14/15.
 */
public class RCResponseTest extends InstrumentationTestCase {

    @Override
    protected void setUp()throws Exception {

        super.setUp();

        System.setProperty(“dexmaker.dexcache”,getInstrumentation().getTargetContext().getCacheDir().getPath());

    }

    /*
    *
     */
    public void testCheckStatus() throws Exception{
        RCResponse mockedResponse = mock(RCResponse.class);
        int a = 3+2;
        //mockedResponse.add("one");
        //verify(mockedResponse).add("one");
        //when(mockedResponse.getStatus()).thenReturn(200);
        //mockedResponse.setStatus(200);
        //System.out.print(mockedResponse.getStatus());
//        RCResponse rcResponse = new RCResponse(mockedResponse);
//        rcResponse.setStatus(200);
//        assertTrue(rcResponse.checkStatus());
//        r.setStatus(205);
//        assertTrue(r.checkStatus());
//        r.setStatus(300);
//        assertFalse(r.checkStatus());
    }
}
