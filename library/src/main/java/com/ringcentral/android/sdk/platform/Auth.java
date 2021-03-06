/*
 * Copyright (c) 2015 RingCentral, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ringcentral.android.sdk.platform;


/**
 * /*
 * OAuth2 for RingCentral Developer Platform APIs
 */

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ringcentral.android.sdk.http.ApiException;
import com.ringcentral.android.sdk.http.ApiResponse;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class Auth {

    protected String access_token;
    protected Date expire_time;
    protected String expires_in;
    protected String owner_id;
    protected String refresh_token;
    protected Date refresh_token_expire_time;
    protected String refresh_token_expires_in;
    protected String scope;
    protected String token_type;

    public Auth() {
        this.token_type = "";

        this.access_token = "";
        this.expires_in = "";
        this.expire_time = new Date(01 / 01 / 0001);

        this.refresh_token = "";
        this.refresh_token_expires_in = "";
        this.refresh_token_expire_time = new Date(01 / 01 / 0001);

        this.scope = "";
        this.owner_id = "";
    }

    /**
     * Get Access Token
     *
     * @return access token
     */
    public String accessToken() {
        return this.access_token;
    }

    /**
     * Check validity of access token
     *
     * @return boolean value for validity of access token
     */
    public boolean accessTokenValid() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(this.expire_time);
        return isTokenDateValid(cal);
    }


    protected boolean isTokenDateValid(GregorianCalendar token_date) {
        boolean value = token_date.compareTo(new GregorianCalendar()) > 0;
        return (token_date.compareTo(new GregorianCalendar()) > 0);
    }

    /**
     * Get Refresh Token
     *
     * @return refresh token
     */
    public String refreshToken() {
        return this.refresh_token;
    }

    /**
     * Check validity of refresh token
     *
     * @return boolean value for validity of refresh token
     */
    public boolean refreshTokenValid() {
        GregorianCalendar cal = new GregorianCalendar();
        if (this.refresh_token_expire_time != null)
            cal.setTime(this.refresh_token_expire_time);
        return this.isTokenDateValid(cal);
    }


    /**
     * Resets the authorization data
     */
    public void reset() {
        this.token_type = "";

        this.access_token = "";
        this.expires_in = "";
        this.expire_time = new Date(01 / 01 / 0001);

        this.refresh_token = "";
        this.refresh_token_expires_in = "";
        this.refresh_token_expire_time = new Date(01 / 01 / 0001);

        this.scope = "";
        this.owner_id = "";
    }

    /**
     * Sets Authorization data
     *
     * @param authData
     * @return this
     */
    public Auth setData(HashMap<String, String> authData) {

        if (authData == null || authData.isEmpty())
            return this;

        if (authData.containsKey("token_type")) {
            this.token_type = authData.get("token_type");
        }
        if (authData.containsKey("scope")) {
            this.scope = authData.get("scope");
        }
        if (authData.containsKey("owner_id")) {
            this.owner_id = authData.get("owner_id");
        }
        if (authData.containsKey("access_token")) {
            this.access_token = authData.get("access_token");
        }
        if (authData.containsKey("expires_in")) {
            this.expires_in = authData.get("expires_in");
        }
        if (!authData.containsKey("expire_time")
                && authData.containsKey("expires_in")) {
            int expiresIn = Integer.parseInt(authData.get("expires_in"));
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.SECOND, expiresIn);
            this.expire_time = calendar.getTime();

        }

        /*
        Refresh token
         */

        if (authData.containsKey("refresh_token")) {
            this.refresh_token = authData.get("refresh_token");
        }
        if (authData.containsKey("refresh_token_expires_in")) {
            this.refresh_token_expires_in = authData
                    .get("refresh_token_expires_in");
        }
        if (!authData.containsKey("refresh_token_expire_time")
                && authData.containsKey("refresh_token_expires_in")) {
            int expiresIn = Integer.parseInt(authData
                    .get("refresh_token_expires_in"));
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.SECOND, expiresIn);
            this.refresh_token_expire_time = calendar.getTime();
        }
        return this;
    }

    protected HashMap<String, String> jsonToHashMap(ApiResponse response) throws ApiException {
        try {
            if (response.ok()) {
                Gson gson = new Gson();
                Type HashMapType = new TypeToken<HashMap<String, String>>() {
                }.getType();
                String responseString = null;
                responseString = response.body().string();
                Log.v("OAuth Response :", responseString);
                return gson.fromJson(responseString, HashMapType);
            } else {
                Log.v("Error Message: ", response.error());
            }
        } catch (ApiException | IOException e) {
            throw new ApiException("Illegal authentication Response data. Authentication Failed with response code " + response.code(), e);
        }
        return new HashMap<>();
    }


    public String tokenType() {
        return this.token_type;
    }

    public void expire_access() {
        //  this.access_token = "";
        this.expires_in = "0";
        this.expire_time = new Date(01 / 01 / 0001);
    }
}

