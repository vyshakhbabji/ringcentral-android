package com.ringcentral.rcandroidsdk;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.ringcentral.rcandroidsdk.rcsdk.Rcsdk;
import com.ringcentral.rcandroidsdk.rcsdk.platform.Platform;

import java.util.HashMap;


public class MainActivity extends Activity implements View.OnClickListener {

    Button button1, button2;

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
                headers.put("url", "https://platform.devtest.ringcentral.com/restapi/v1.0/account/~");
                platform.get(body, headers);
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
