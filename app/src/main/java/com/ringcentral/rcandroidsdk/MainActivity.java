package com.ringcentral.rcandroidsdk;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.ringcentral.rcandroidsdk.rcsdk.Rcsdk;
import com.ringcentral.rcandroidsdk.rcsdk.http.RCHeaders;
import com.ringcentral.rcandroidsdk.rcsdk.platform.Platform;

import java.util.HashMap;


public class MainActivity extends Activity implements View.OnClickListener {

    Button button1, button2, button3, button4, button5;

    String appKey = "xhK3uzISTEaEYhFAtadVug";
    String appSecret = "1YRoPu64TeCOe_ZJy3ggLwGg-QDQd6QaWpSyIT8AxmjA";
    String username = "15856234166";
    String password = "P@ssw0rd";
    String extension = "";
    String RC_SERVER_PRODUCTION = "https://platform.ringcentral.com";
    String RC_SERVER_SANDBOX = "https://platform.devtest.ringcentral.com";

    Platform platform;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        Rcsdk rcsdk = new Rcsdk(appKey, appSecret, RC_SERVER_SANDBOX);
        platform = rcsdk.getPlatform();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button1:
                platform.authorize(username, extension, password);
                break;

            case R.id.button2:
                HashMap<String, String> body = null;
                HashMap<String, String> headers = new HashMap<>();
                headers.put("url", "/restapi/v1.0/account/~");
                platform.get(body, headers);
                break;

            case R.id.button3:
                platform.logout();
                break;

            case R.id.button4:
                HashMap<String, String> body2 = new HashMap<>();
                body2.put("body", "{\n" +
                        "  \"to\": [{\"phoneNumber\": \"15106907982\"}],\n" +
                        "  \"from\": {\"phoneNumber\": \"15856234166\"},\n" +
                        "  \"text\": \"Test SMS message from Platform server\"\n" +
                        "}");
                HashMap<String, String> headers2 = new HashMap<>();
                headers2.put("url", "/restapi/v1.0/account/~/extension/~/sms");
                headers2.put(RCHeaders.CONTENT_TYPE, RCHeaders.JSON_CONTENT_TYPE);
                platform.post(body2, headers2);
//                Intent smsIntent = new Intent(this, DisplaySMSActivity.class);
//                smsIntent.putExtra("MyPlatform", platform);
//                startActivity(smsIntent);
                break;

            case R.id.button5:
                HashMap<String, String> body3 = new HashMap<>();
                HashMap<String, String> headers3 = new HashMap<>();
                headers3.put("url", "/restapi/v1.0/account/~/extension/~/message-store/1150177004");
                platform.delete(body3, headers3);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
