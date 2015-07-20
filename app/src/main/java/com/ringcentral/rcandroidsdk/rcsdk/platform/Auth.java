package com.ringcentral.rcandroidsdk.rcsdk.platform;

import android.text.format.Time;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by andrew.pang on 6/25/15.
 */
public class Auth implements Serializable{

    String token_type;

    String access_token;
    String expires_in;
    Date expire_time;

    String refresh_token;
    String refresh_token_expires_in;
    Date refresh_token_expire_time;

    String scope;
    String owner_id;

    public Auth(){
        token_type = "";
        access_token = "";
        expires_in = "";
        expire_time = null;
        refresh_token = "";
        refresh_token_expires_in = "";
        refresh_token_expire_time = null;
        scope = "";
        owner_id = "";
    }

    public void setData(Map<String, String> authData) {
        // Misc
        if (authData.containsKey("token_type")) {
            this.token_type = authData.get("token_type");
        }
        if (authData.containsKey("scope")) {
            this.scope = authData.get("scope");
        }
        if (authData.containsKey("owner_id")) {
            this.owner_id = authData.get("owner_id");
        }
        // Access Token
        if (authData.containsKey("access_token")) {
            this.access_token = authData.get("access_token");
        }
        if (authData.containsKey("expires_in")) {
            this.expires_in = authData.get("expires_in");
        }
        if (!authData.containsKey("expire_time") && authData.containsKey("expires_in")) {
            int expiresIn = Integer.parseInt(authData.get("expires_in"));
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.SECOND, expiresIn);
            this.expire_time = calendar.getTime();
        }
        // Refresh Token
        if (authData.containsKey("refresh_token")) {
            this.refresh_token = authData.get("refresh_token");
        }
        if (authData.containsKey("refresh_token_expires_in")) {
            this.refresh_token_expires_in = authData.get("refresh_token_expires_in");
        }
        if (!authData.containsKey("refresh_token_expire_time") && authData.containsKey("refresh_token_expires_in")) {
            int expiresIn = Integer.parseInt(authData.get("refresh_token_expires_in"));
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.SECOND, expiresIn);
            this.refresh_token_expire_time = calendar.getTime();
        }
    }

    public Auth getData(){
        return this;
    }

    public void reset(){
        this.token_type = "";
        this.access_token = "";
        this.expires_in = "";
        this.expire_time = null;
        this.refresh_token = "";
        this.refresh_token_expires_in = "";
        this.refresh_token_expire_time = null;
        this.scope = "";
        this.owner_id = "";
    }

    public String getAccessToken(){
        return this.access_token;
    }

    public String getRefreshToken(){
        return this.refresh_token;
    }

    public String getTokenType(){
        return this.token_type;
    }

    public boolean isAccessTokenValid(){
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(this.expire_time);
        return isTokenDateValid(cal);
    }

    public boolean isRefreshTokenValid(){
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(this.refresh_token_expire_time);
        return isTokenDateValid(cal);
    }

    public boolean isTokenDateValid(GregorianCalendar token_date){
        return (token_date.compareTo(new GregorianCalendar()) > 0);
    }


}
