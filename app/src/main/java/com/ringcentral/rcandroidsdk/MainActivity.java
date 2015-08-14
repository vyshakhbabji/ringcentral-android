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
import com.ringcentral.rcandroidsdk.rcsdk.http.RCResponse;
import com.ringcentral.rcandroidsdk.rcsdk.http.Transaction;
import com.ringcentral.rcandroidsdk.rcsdk.platform.Helpers;
import com.ringcentral.rcandroidsdk.rcsdk.platform.Platform;
import com.ringcentral.rcandroidsdk.rcsdk.platform.Platform2;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;


public class MainActivity extends Activity implements View.OnClickListener {

    Button button1, button2, button3, button4, button5;
    EditText editText1, editText2, editText3;

    String appKey = "xhK3uzISTEaEYhFAtadVug";
    String appSecret = "1YRoPu64TeCOe_ZJy3ggLwGg-QDQd6QaWpSyIT8AxmjA";

    Platform platform;
    Platform2 platform2;
    Helpers helpers;
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

        SDK = new SDK(appKey, appSecret, "SANDBOX");
        //platform = SDK.getPlatform();
        platform2 = SDK.getPlatform2();
        helpers = SDK.getHelpers();
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
                //Hardcoded for ease of testing
                username = "15856234166";
                password = "P@ssw0rd";
                platform2.authorize(username, extension, password,
//                        new Callback() {
//                            @Override
//                            public void onFailure(Request request, IOException e) {
//                                e.printStackTrace();
//                            }
//                            @Override
//                            public void onResponse(Response response) throws IOException {
//                                RCResponse authResponse = new RCResponse(response);
//                                HashMap<String, String> responseMap = authResponse.getJson();
//                                // If HTTP response is not successful, throw exception
//                                if (!response.isSuccessful()) {
//                                    throw new IOException("Error code: " + authResponse.getStatus() + ". Error: " + responseMap.get("error") + ": " + responseMap.get("error_description"));
//                                }
//                                // Create RCResponse and parse the JSON response to set Auth data
//                                helpers.setAuthData(responseMap);
//                                // Display options Activity
//                                Intent optionsIntent = new Intent(MainActivity.this, OptionsActivity.class);
//                                optionsIntent.putExtra("MyRcsdk", SDK);
//                                startActivity(optionsIntent);
//                            }
//                        });
                        //TEST Platform 2
                        new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {
                                e.printStackTrace();
                            }
                            @Override
                            public void onResponse(Response response) throws IOException {
                                Transaction transaction = new Transaction(response);
                                // If HTTP response is not successful, throw exception
                                //if (!response.isSuccessful())

                                // Create RCResponse and parse the JSON response to set Auth data
                                platform2.setAuthData(transaction.getAuthJson());
                                // Display options Activity
                                Intent optionsIntent = new Intent(MainActivity.this, OptionsActivity.class);
                                optionsIntent.putExtra("MyRcsdk", SDK);
                                startActivity(optionsIntent);
                            }
                        });
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
