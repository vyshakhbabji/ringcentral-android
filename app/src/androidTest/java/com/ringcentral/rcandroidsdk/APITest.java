package com.ringcentral.rcandroidsdk;

import android.test.InstrumentationTestCase;

import com.ringcentral.rcandroidsdk.rcsdk.SDK;
import com.ringcentral.rcandroidsdk.rcsdk.http.RCResponse;
import com.ringcentral.rcandroidsdk.rcsdk.platform.Platform;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by andrew.pang on 7/29/15.
 */
public class APITest extends InstrumentationTestCase {

    public void testApi() throws Exception{
        SDK sdk = new SDK("xhK3uzISTEaEYhFAtadVug", "1YRoPu64TeCOe_ZJy3ggLwGg-QDQd6QaWpSyIT8AxmjA", "SANDBOX");
        final Platform platform = sdk.getPlatform();
        platform.authorize("15856234166", "", "P@ssw0rd",
                new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(Response response) throws IOException {
                        RCResponse authResponse = new RCResponse(response);
                        HashMap<String, String> responseMap = authResponse.getJson();
                        platform.setAuthData(responseMap);
                        //Test Authorization
                        assertTrue(authResponse.checkStatus());

                        //Test Send SMS
                        platform.sendSMS("16502823614", "15856234166", "Test Message",
                                new Callback() {
                                    @Override
                                    public void onFailure(Request request, IOException e) {
                                    }
                                    @Override
                                    public void onResponse(Response response) throws IOException {
                                        RCResponse smsResponse = new RCResponse(response);
                                        assertTrue(smsResponse.checkStatus());
                                        try {
                                            JSONObject json = new JSONObject(smsResponse.getBody());
                                            String messageId = json.getString("id");
                                            //Test Delete Message
                                            HashMap<String, String> deleteHeader = new HashMap<>();
                                            deleteHeader.put("method", "DELETE");
                                            deleteHeader.put("url", "/restapi/v1.0/account/~/extension/~/message-store/" + messageId);
                                            platform.delete(deleteHeader,
                                                    new Callback() {
                                                        @Override
                                                        public void onFailure(Request request, IOException e) {

                                                        }
                                                        @Override
                                                        public void onResponse(Response response) throws IOException {
                                                            RCResponse deleteResponse = new RCResponse(response);
                                                            assertTrue(deleteResponse.checkStatus());
                                                        }
                                                    });
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                        );

                        //Test GET Account Info
                        platform.accountInfo(
                                new Callback() {
                                    @Override
                                    public void onFailure(Request request, IOException e) {
                                    }

                                    @Override
                                    public void onResponse(Response response) throws IOException {
                                        RCResponse callLogResponse = new RCResponse(response);
                                        assertTrue(callLogResponse.checkStatus());
                                    }
                                }
                        );

                        //Test GET Call Log
                        platform.callLog(
                                new Callback() {
                                    @Override
                                    public void onFailure(Request request, IOException e) {
                                    }

                                    @Override
                                    public void onResponse(Response response) throws IOException {
                                        RCResponse callLogResponse = new RCResponse(response);
                                        assertTrue(callLogResponse.checkStatus());
                                    }
                                }
                        );

                        //Test GET Message Store
                        platform.messageStore(
                                new Callback() {
                                    @Override
                                    public void onFailure(Request request, IOException e) {
                                    }

                                    @Override
                                    public void onResponse(Response response) throws IOException {
                                        RCResponse messageStoreResponse = new RCResponse(response);
                                        assertTrue(messageStoreResponse.checkStatus());
                                    }
                                }
                        );

                    }
                }
        );
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }


}
