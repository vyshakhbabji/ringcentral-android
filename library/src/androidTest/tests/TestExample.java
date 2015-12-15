import android.content.Intent;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.ringcentral.rc_android_sdk.rcsdk.core.SDK;
import com.ringcentral.rc_android_sdk.rcsdk.platform.Platform;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import javax.inject.Singleton;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;



/**
 * Created by vyshakh.babji on 12/9/15.
 */
@RunWith(AndroidJUnit4.class)
public class TestExample {

    Platform platform;

    @Test
    public void testCheck(){
        assertEquals(true, true);
    }



    @Test
    public void initalizePlatform(){
        SDK sdk = new SDK("E0_nOAfbR7GkteYbDv93oA","UelNnk-1QYK0rHyvjJJ9yQx3Yl6vj3RvGmb0G2SH6ePw", Platform.Server.SANDBOX);
        platform = sdk.platform();
        assertNotNull(platform.loggedIn());
        assertEquals(platform.loggedIn(), false);

        platform.login("15856234138", "101", "P@ssw0rd", new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                if(response.isSuccessful()){
                    assertEquals(response.code(),200);
                }



            }
        });

    }





}
