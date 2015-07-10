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
import com.ringcentral.rcandroidsdk.rcsdk.http.RCHeaders;
import com.ringcentral.rcandroidsdk.rcsdk.http.RCResponse;
import com.ringcentral.rcandroidsdk.rcsdk.platform.Platform;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;


public class OptionsActivity extends ActionBarActivity implements View.OnClickListener {

    SDK SDK;
    Platform platform;
    Button button1, button2, button3, button4, button5;
    TextView textView1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        Intent intent = getIntent();
        SDK = (SDK) intent.getSerializableExtra("MyRcsdk");
        platform = SDK.getPlatform();
        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(this);
        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(this);
        button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(this);
        button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(this);
        button5 = (Button) findViewById(R.id.button5);
        button5.setOnClickListener(this);
        textView1 = (TextView) findViewById(R.id.textView1);

    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button1:
                HashMap<String, String> body = null;
                HashMap<String, String> headers = new HashMap<>();
                headers.put("url", "/restapi/v1.0/account/~");
                platform.get(body, headers,
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
                                String responseString = versionResponse.getBody();
                                Message msg = handler.obtainMessage();
                                msg.what = 1;
                                msg.obj = responseString;
                                handler.sendMessage(msg);

                            }
                        });
                break;

            case R.id.button2:
                HashMap<String, String> callLogBody = null;
                HashMap<String, String> callLogHeaders = new HashMap<>();
                callLogHeaders.put("url", "/restapi/v1.0/account/~/call-log");
                platform.get(callLogBody, callLogHeaders,
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
                                String responseString = callLogResponse.getBody();
                                Message msg = handler.obtainMessage();
                                msg.what = 1;
                                msg.obj = responseString;
                                handler.sendMessage(msg);

                            }
                        });
                break;

            case R.id.button3:
                HashMap<String, String> messageStoreBody = null;
                HashMap<String, String> messageStoreHeaders = new HashMap<>();
                messageStoreHeaders.put("url", "/restapi/v1.0/account/~/extension/~/message-store");
                platform.get(messageStoreBody, messageStoreHeaders,
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
                                String responseString = messageStoreResponse.getBody();
                                Message msg = handler.obtainMessage();
                                msg.what = 1;
                                msg.obj = responseString;
                                handler.sendMessage(msg);

                            }
                        });
                break;

            case R.id.button4:
                //Display RingOut Activity
                Intent ringOutIntent = new Intent(OptionsActivity.this, RingOutActivity.class);
                ringOutIntent.putExtra("MyRcsdk", SDK);
                startActivity(ringOutIntent);
                break;

            case R.id.button5:
                //Display SMS Activity
                Intent smsIntent = new Intent(OptionsActivity.this, SMSActivity.class);
                smsIntent.putExtra("MyRcsdk", SDK);
                startActivity(smsIntent);
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
        getMenuInflater().inflate(R.menu.menu_display_sm, menu);
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
