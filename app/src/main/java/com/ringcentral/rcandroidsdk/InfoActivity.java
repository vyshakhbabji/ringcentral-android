package com.ringcentral.rcandroidsdk;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ringcentral.rcandroidsdk.rcsdk.SDK;
import com.ringcentral.rcandroidsdk.rcsdk.http.RCResponse;
import com.ringcentral.rcandroidsdk.rcsdk.platform.Helpers;
import com.ringcentral.rcandroidsdk.rcsdk.platform.Platform;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class InfoActivity extends ActionBarActivity implements View.OnClickListener{

    SDK SDK;
    Platform platform;
    Helpers helpers;
    Button button1, button2, button3;
    TextView textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Intent intent = getIntent();
        SDK = (SDK) intent.getSerializableExtra("MyRcsdk");
        platform = SDK.getPlatform();
        helpers = SDK.getHelpers();
        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(this);
        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(this);
        button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(this);
        textView1 = (TextView) findViewById(R.id.textView1);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button1:
                helpers.accountInfo(
                        new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Response response) throws IOException {
                                if (!response.isSuccessful())
                                    throw new IOException("Unexpected code " + response);
                                RCResponse versionResponse = new RCResponse(response);
                                String responseString = "";
                                String body = versionResponse.getBody();
                                try {
                                    JSONObject jsonObject = new JSONObject(body);
                                    JSONObject serviceInfo = jsonObject.getJSONObject("serviceInfo");
                                    JSONObject servicePlan = serviceInfo.getJSONObject("servicePlan");
                                    responseString += "Service Plan: " + "\n" + servicePlan.getString("name") + "\n\n";
                                    JSONObject billingPlan = serviceInfo.getJSONObject("billingPlan");
                                    responseString += "Billing Plan: " + "\n" + billingPlan.getString("name") + "\n\n";
                                    responseString += "Main Number: " + "\n" + jsonObject.getString("mainNumber") + "\n\n";

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Message msg = handler.obtainMessage();
                                msg.what = 1;
                                msg.obj = responseString;
                                handler.sendMessage(msg);

                            }
                        });
                break;

            case R.id.button2:
                helpers.callLog(
                        new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Response response) throws IOException {
                                if (!response.isSuccessful())
                                    throw new IOException("Unexpected code " + response);
                                RCResponse callLogResponse = new RCResponse(response);
                                String responseString = "";
                                String body = callLogResponse.getBody();
                                try {
                                    JSONObject jsonObject = new JSONObject(body);
                                    JSONArray records = jsonObject.getJSONArray("records");
                                    for (int i = 0; i < records.length(); i++) {
                                        JSONObject record = records.getJSONObject(i);
                                        JSONObject to = record.getJSONObject("to");
                                        responseString += "To: " + to.getString("phoneNumber") + "                                       ";
                                        responseString += "Duration: " + record.getString("duration");
                                        responseString += "\n";
                                        JSONObject from = record.getJSONObject("from");
                                        responseString += "From: " + from.getString("phoneNumber") + " ";
                                        responseString += "\n\n";
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Message msg = handler.obtainMessage();
                                msg.what = 1;
                                msg.obj = responseString;
                                handler.sendMessage(msg);

                            }
                        });
                break;

            case R.id.button3:
                helpers.messageStore(
                        new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Response response) throws IOException {
                                if (!response.isSuccessful())
                                    throw new IOException("Unexpected code " + response);
                                RCResponse messageStoreResponse = new RCResponse(response);
                                String responseString = "";
                                String body = messageStoreResponse.getBody();
                                try {
                                    JSONObject jsonObject = new JSONObject(body);
                                    JSONArray records = jsonObject.getJSONArray("records");
                                    for (int i = 0; i < records.length(); i++) {
                                        JSONObject record = records.getJSONObject(i);
                                        JSONArray to = record.getJSONArray("to");
                                        JSONObject phoneNumber = to.getJSONObject(0);
                                        responseString += "To: " + phoneNumber.getString("phoneNumber") + "                                    ";
                                        responseString += "Status: " + record.getString("readStatus");
//                                        responseString += "\n";
//                                        JSONObject from =  record.getJSONObject("from");
//                                        responseString += "From: " + from.getString("phoneNumber") + " ";
                                        responseString += "\n\n";
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Message msg = handler.obtainMessage();
                                msg.what = 1;
                                msg.obj = responseString;
                                handler.sendMessage(msg);

                            }
                        });
                break;

        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                textView1.setText((String)msg.obj);
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
