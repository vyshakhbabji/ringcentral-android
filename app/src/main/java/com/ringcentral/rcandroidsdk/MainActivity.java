package com.ringcentral.rcandroidsdk;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.ringcentral.rcandroidsdk.rcsdk.Rcsdk;
import com.ringcentral.rcandroidsdk.rcsdk.platform.Platform;


public class MainActivity extends Activity {

    String appKey = "xhK3uzISTEaEYhFAtadVug";
    String appSecret = "1YRoPu64TeCOe_ZJy3ggLwGg-QDQd6QaWpSyIT8AxmjA";
    String username = "15856234166";
    String password = "P@ssw0rd";
    String extension = "";
    String RC_SERVER_PRODUCTION = "https://platform.ringcentral.com";
    String RC_SERVER_SANDBOX = "https://platform.devtest.ringcentral.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Rcsdk rcsdk = new Rcsdk(appKey, appSecret, RC_SERVER_SANDBOX);
        Platform platform = rcsdk.getPlatform();
        platform.authorize(username, extension, password);
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
