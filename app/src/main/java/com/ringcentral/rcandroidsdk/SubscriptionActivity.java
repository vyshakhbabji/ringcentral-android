package com.ringcentral.rcandroidsdk;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.ringcentral.rcandroidsdk.rcsdk.SDK;
import com.ringcentral.rcandroidsdk.rcsdk.platform.Helpers;
import com.ringcentral.rcandroidsdk.rcsdk.platform.Platform;


public class SubscriptionActivity extends ActionBarActivity implements View.OnClickListener {

    SDK SDK;
    Platform platform;
    Helpers helpers;
    Button button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);
        Intent intent = getIntent();
        SDK = (SDK) intent.getSerializableExtra("MyRcsdk");
        platform = SDK.getPlatform();
        helpers = SDK.getHelpers();
        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(this);
//        byte[] a = new byte[0];
//        byte[] b = new byte[0];
//        String decryptedString = "";
//        try {
//            a = "Hb3NQslzsa2lzKw5HpjG+A==".getBytes("UTF-8");
//            b = "x+ujFJPXkuym1I/Sj8LbWSeOYAf7bgIC/zF7hH048dJBvBZD07XfvYbao5I/FBSKgf2lB0hxkGKWP48Z+P5+rB0gWDJv2lPN9MjmcwAERK5iP/ruLKLbtDSJJH28fuGU38hiRyUxAl8sgQvMVgTLY6AOv4ZLiqNY+zy/9Z3Qrl1zen9y4L+/dbOawETaLQ2Pfs9xJrPKeRS+yxQEZdcRC/L7zWzQCSFQzgfvr7mK0RzgZXRTd0uK5tvTFnn9k0Yn4v1yDH7Q26L38x8qUh1Sco60c5ivL2YuiBmEPw6xyO0=".getBytes("UTF-8");
//            byte[] key = Base64.decode(a, Base64.NO_WRAP);
//            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
//            byte[] data = Base64.decode(b, Base64.NO_WRAP);
//            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
//            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
//            byte[] decrypted = cipher.doFinal(data);
//            decryptedString = new String(decrypted);
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        System.out.println("SUBSCRIBE : " + " : " + decryptedString);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button1:
                helpers.subscribe();
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_subscription, menu);
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
