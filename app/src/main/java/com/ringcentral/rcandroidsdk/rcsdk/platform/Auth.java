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

    public void setData(Map<String, String> parameters) {
        // Misc
        if (parameters.containsKey("token_type")) {
            this.token_type = parameters.get("token_type");
        }
        if (parameters.containsKey("scope")) {
            this.scope = parameters.get("scope");
        }
        if (parameters.containsKey("owner_id")) {
            this.owner_id = parameters.get("owner_id");
        }
        // Access Token
        if (parameters.containsKey("access_token")) {
            this.access_token = parameters.get("access_token");
        }
        if (parameters.containsKey("expires_in")) {
            this.expires_in = parameters.get("expires_in");
        }
        if (!parameters.containsKey("expire_time") && parameters.containsKey("expires_in")) {
            int expiresIn = Integer.parseInt(parameters.get("expires_in"));
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.SECOND, expiresIn);
            this.expire_time = calendar.getTime();
        }
        // Refresh Token
        if (parameters.containsKey("refresh_token")) {
            this.refresh_token = parameters.get("refresh_token");
        }
        if (parameters.containsKey("refresh_token_expires_in")) {
            this.refresh_token_expires_in = parameters.get("refresh_token_expires_in");
        }
        if (!parameters.containsKey("refresh_token_expire_time") && parameters.containsKey("refresh_token_expires_in")) {
            int expiresIn = Integer.parseInt(parameters.get("refresh_token_expires_in"));
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
    //Need to implement comparing with time
    public boolean isAccessTokenValid(){
        return true;
    }

    public boolean isRefreshTokenValid(){
        return true;
    }

    public boolean isTokenDateValid(GregorianCalendar token_date){
        return (token_date.compareTo(new GregorianCalendar()) > 1);
    }


}
