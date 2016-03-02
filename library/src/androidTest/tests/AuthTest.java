import android.support.test.runner.AndroidJUnit4;

import com.ringcentral.android.sdk.platform.Auth;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import static org.junit.Assert.assertNotNull;

/**
 * Created by vyshakh.babji on 12/9/15.
 */

@RunWith(AndroidJUnit4.class)
public class AuthTest {

    Auth auth;

    @Before
    public void setUp() throws Exception {
        auth = new Auth();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testAccessToken() throws Exception {
        assertEquals(auth.accessToken(), "");
    }

    @Test
    public void testAccessTokenValid() throws Exception {
        assertEquals(auth.accessTokenValid(), false);
    }


    @Test
    public void testRefreshToken() throws Exception {
        assertEquals(auth.refreshToken(), "");
    }

    @Test
    public void testRefreshTokenValid() throws Exception {
        assertEquals(auth.refreshTokenValid(), false);
    }

    @Test
    public void testReset() throws Exception {

    }

    @Test
    public void testSetData() throws Exception {
        assertNotNull(auth.setData(null));

        HashMap hm = new HashMap();
        hm.put("access_token", "abc");

        auth.setData(hm);
        assertEquals(auth.accessToken(), "abx");
        assertNotEquals(auth.accessToken(), "abx");


        hm.put("refresh_token", "wxy");
        auth.setData(hm);
        assertEquals(auth.refreshToken(), "wxy");
        assertNotEquals(auth.refreshToken(), "wxz");


    }

    @Test
    public void testTokenType() throws Exception {

    }

    @Test
    public void testExpire_access() throws Exception {

    }
}