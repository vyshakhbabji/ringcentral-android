package com.ringcentral.rcandroidsdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ringcentral.rcandroidsdk.rcsdk.SDK;
import com.ringcentral.rcandroidsdk.rcsdk.platform.Platform;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;


public class MainActivity extends Activity implements View.OnClickListener {

    Button button1, button2, button3, button4, button5;
    EditText editText1, editText2, editText3;

    String appKey = "xhK3uzISTEaEYhFAtadVug";
    String appSecret = "1YRoPu64TeCOe_ZJy3ggLwGg-QDQd6QaWpSyIT8AxmjA";
//    String username = "15856234166";
//    String password = "P@ssw0rd";
//    String extension = "";
    String RC_SERVER_PRODUCTION = "https://platform.ringcentral.com";
    String RC_SERVER_SANDBOX = "https://platform.devtest.ringcentral.com";

    Platform platform;
    SDK SDK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(this);
        editText1 = (EditText)findViewById(R.id.editText1);
        editText2 = (EditText)findViewById(R.id.editText2);
        editText3 = (EditText)findViewById(R.id.editText3);

//        button2 = (Button) findViewById(R.id.button2);
//        button2.setOnClickListener(this);
//        button3 = (Button) findViewById(R.id.button3);
//        button3.setOnClickListener(this);
//        button4 = (Button) findViewById(R.id.button4);
//        button4.setOnClickListener(this);
//        button5 = (Button) findViewById(R.id.button5);
//        button5.setOnClickListener(this);

        SDK = new SDK(appKey, appSecret, RC_SERVER_SANDBOX);
        platform = SDK.getPlatform();
//        Properties properties = new Properties();
//        InputStream input = null;
//        try{
//            input = new FileInputStream("config.properties");
//            properties.load(input);
//            System.out.println(properties.getProperty("username"));
//        } catch (IOException e){
//            e.printStackTrace();
//        } finally {
//            if (input != null) {
//                try {
//                    input.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button1:
                String username = editText1.getText().toString();
                String extension = editText2.getText().toString();
                String password = editText3.getText().toString();
                Context context = this;
                platform.authorize(username, extension, password,
                        new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {
                                e.printStackTrace();
                            }
                            @Override
                            public void onResponse(Response response) throws IOException {
                                if (!response.isSuccessful())
                                    throw new IOException("Unexpected code " + response);
                                //Parse JSON response to a map
                                String responseString = response.body().string();
                                Gson gson = new Gson();
                                Type mapType = new TypeToken<Map<String, String>>() {}.getType();
                                Map<String, String> responseMap = gson.fromJson(responseString, mapType);
                                platform.setAuthData(responseMap);
                                //Display next Activity
                                Intent smsIntent = new Intent(MainActivity.this, OptionsActivity.class);
                                smsIntent.putExtra("MyRcsdk", SDK);
                                startActivity(smsIntent);
                            }
                        });
                break;
//
//            case R.id.button2:
//                HashMap<String, String> body = null;
//                HashMap<String, String> headers = new HashMap<>();
//                headers.put("url", "/restapi/v1.0/account/~");
//                platform.get(body, headers,
//                        new Callback() {
//                            @Override
//                            public void onFailure(Request request, IOException e) {
//                                e.printStackTrace();
//                            }
//                            @Override
//                            public void onResponse(Response response) throws IOException {
//                                if (!response.isSuccessful())
//                                    throw new IOException("Unexpected code " + response);
//                                String responseString = response.body().string();
//                                System.out.print(responseString);
//                            }
//                        });
//                break;
//
//            case R.id.button3:
//                platform.logout(new Callback() {
//                    @Override
//                    public void onFailure(Request request, IOException e) {
//                        e.printStackTrace();
//                    }
//                    @Override
//                    public void onResponse(Response response) throws IOException {
//                        if (!response.isSuccessful())
//                            throw new IOException("Unexpected code " + response);
//                        String responseString = response.body().string();
//                        System.out.print(responseString);
//                    }
//                });
//                break;
//
//            case R.id.button4:
////                HashMap<String, String> body2 = new HashMap<>();
////                body2.put(
////                        "body", "{\n" +
////                        "  \"to\": {\"phoneNumber\": \"16502823614\"},\n" +
////                        "  \"from\": {\"phoneNumber\": \"15106907982\"},\n" +
////                        "  \"callerId\": {\"phoneNumber\": \"15856234166\"},\n" +
////                        "  \"playPrompt\": true\n" +
////                        "}");
////                HashMap<String, String> headers2 = new HashMap<>();
////                headers2.put("url", "/restapi/v1.0/account/~/extension/~/ringout");
////                headers2.put(RCHeaders.CONTENT_TYPE, RCHeaders.JSON_CONTENT_TYPE);
////                platform.post(body2, headers2);
//                Intent smsIntent = new Intent(this, OptionsActivity.class);
//                smsIntent.putExtra("MyRcsdk", SDK);
//                startActivity(smsIntent);
//                break;
//
//            case R.id.button5:
//                HashMap<String, String> body3 = new HashMap<>();
//                HashMap<String, String> headers3 = new HashMap<>();
//                body3.put("body", "{\n" +
//                        "  \"readStatus\": \"Unread\"\n" +
//                        "}");
//                headers3.put("url", "/restapi/v1.0/account/~/extension/~/message-store/1150481004");
//                headers3.put("Content-Type", "application/json");
//                platform.put(body3, headers3,
//                        new Callback() {
//                    @Override
//                    public void onFailure(Request request, IOException e) {
//                        e.printStackTrace();
//                    }
//                    @Override
//                    public void onResponse(Response response) throws IOException {
//                        if (!response.isSuccessful())
//                            throw new IOException("Unexpected code " + response);
//                        String responseString = response.body().string();
//                        System.out.print(responseString);
//                    }
//                    });
//                break;
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
