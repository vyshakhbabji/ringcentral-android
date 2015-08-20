package com.ringcentral.rc_android_sdk;

import android.test.InstrumentationTestCase;

import com.ringcentral.rcandroidsdk.oldsdk.RCResponse;
import com.ringcentral.rcandroidsdk.rcsdk.SDK;
import com.ringcentral.rcandroidsdk.rcsdk.http.Transaction;
import com.ringcentral.rcandroidsdk.rcsdk.platform.Helpers;
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
        final Helpers helpers= sdk.getHelpers();
        helpers.authorize("15856234166", "", "P@ssw0rd",
                new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(Response response) throws IOException {
                        Transaction transaction = new Transaction(response);
                        HashMap<String, String> responseMap = transaction.getAuthJson();
                        helpers.setAuthData(responseMap);
                        //Test Authorization
                        assertTrue(transaction.isOK());

                        //Test Send SMS
                        helpers.sendSMS("16502823614", "15856234166", "Test Message",
                                new Callback() {
                                    @Override
                                    public void onFailure(Request request, IOException e) {
                                    }

                                    @Override
                                    public void onResponse(Response response) throws IOException {
                                        Transaction transaction = new Transaction(response);
                                        assertTrue(transaction.isOK());
                                        try {
                                            JSONObject json = new JSONObject(transaction.getBodyString());
                                            String messageId = json.getString("id");
                                            //Test Delete Message
                                            HashMap<String, String> deleteHeader = new HashMap<>();
                                            deleteHeader.put("method", "DELETE");
                                            String url= "/restapi/v1.0/account/~/extension/~/message-store/" + messageId;
                                            helpers.delete(url, deleteHeader,
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
                        helpers.accountInfo(
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
                        helpers.callLog(
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
                        helpers.messageStore(
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
