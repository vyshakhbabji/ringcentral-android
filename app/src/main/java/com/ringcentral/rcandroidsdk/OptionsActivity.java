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
import com.ringcentral.rcandroidsdk.rcsdk.platform.Helpers;
import com.ringcentral.rcandroidsdk.rcsdk.platform.Platform;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;


public class OptionsActivity extends ActionBarActivity implements View.OnClickListener {

    SDK SDK;
    Platform platform;
    Helpers helpers;
    Button button1, button2, button3, button4, button5;
    TextView textView1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        Intent intent = getIntent();
        SDK = (SDK) intent.getSerializableExtra("MyRcsdk");
        //platform = SDK.getPlatform();
        helpers = SDK.getHelpers();
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

    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button1:
                //Display RingOut Activity
                Intent ringOutIntent = new Intent(OptionsActivity.this, RingOutActivity.class);
                ringOutIntent.putExtra("MyRcsdk", SDK);
                startActivity(ringOutIntent);
                break;

            case R.id.button2:
                //Display SMS Activity
                Intent smsIntent = new Intent(OptionsActivity.this, SMSActivity.class);
                smsIntent.putExtra("MyRcsdk", SDK);
                startActivity(smsIntent);
                break;

            case R.id.button3:
//                HashMap<String, String> body = new HashMap<>();
//                body.put("body", "--Boundary_1_14413901_1361871080888\n" +
//                        "Content-Type: application/json\n" +
//                        "\n" +
//                        "{\n" +
//                        "  \"to\":[{\"phoneNumber\":\"16502823614\"}],\n" +
//                        "  \"faxResolution\":\"High\",\n" +
//                        "  \"sendTime\":\"2013-02-26T09:31:20.882Z\"\n" +
//                        "}\n" +
//                        "\n" +
//                        "--Boundary_1_14413901_1361871080888\n" +
//                        "Content-Type: text/plain\n" +
//                        "\n" +
//                        "Hello, World!\n" +
//                        "\n" +
//                        "--Boundary_1_14413901_1361871080888--");
//                HashMap<String, String> headers = new HashMap<>();
//                headers.put("url", "/restapi/v1.0/account/~/extension/~/fax");
//                headers.put("Content-Type", "multipart/mixed");
//                platform.post(body, headers,
//                        new Callback() {
//                            @Override
//                            public void onFailure(Request request, IOException e) {
//
//                            }
//
//                            @Override
//                            public void onResponse(Response response) throws IOException {
//                                RCResponse messageStoreResponse = new RCResponse(response);
//                                String body = messageStoreResponse.getBody();
//                                Message msg = handler.obtainMessage();
//                                msg.what = 1;
//                                msg.obj = body;
//                                handler.sendMessage(msg);
//                            }
//                        }
//                );
                break;


            case R.id.button4:
                Intent subscriptionIntent = new Intent(OptionsActivity.this, SubscriptionActivity.class);
                subscriptionIntent.putExtra("MyRcsdk", SDK);
                startActivity(subscriptionIntent);
                break;

            case R.id.button5:
                Intent infoIntent = new Intent(OptionsActivity.this, InfoActivity.class);
                infoIntent.putExtra("MyRcsdk", SDK);
                startActivity(infoIntent);
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
