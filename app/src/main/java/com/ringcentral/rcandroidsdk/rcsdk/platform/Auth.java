package com.ringcentral.rcandroidsdk.rcsdk.platform;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by andrew.pang on 6/25/15.
 */
public class Auth {

    String token_type;

    String access_token;
    String expires_in;
    String expire_time;

    String refresh_token;
    String refresh_token_expires_in;
    String refresh_token_expire_time;

    String scope;
    String owner_id;

    public Auth(){
        token_type = "";
        access_token = "";
        expires_in = "";
        expire_time = "";
        refresh_token = "";
        refresh_token_expires_in = "";
        refresh_token_expire_time = "";
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
        if (parameters.containsKey("expire_time")) {
            this.expire_time = parameters.get("expire_time");
        }
        // Refresh Token
        if (parameters.containsKey("refresh_token")) {
            this.refresh_token = parameters.get("refresh_token");
        }
        if (parameters.containsKey("refresh_token_expires_in")) {
            this.refresh_token_expires_in = parameters.get("refresh_token_expires_in");
        }
        if (parameters.containsKey("refresh_token_expire_time")) {
            this.refresh_token_expire_time = parameters.get("refresh_token_expire_time");
        }
    }

    public Map<String, String> getData(){
        Map<String, String> map = new HashMap<>();
        map.put("token_type", this.token_type);
        map.put("access_token", this.access_token);
        map.put("expires_in", this.expires_in);
        map.put("expire_time", this.expire_time);
        map.put("refresh_token", this.refresh_token);
        map.put("refresh_token_expires_in", this.refresh_token_expires_in);
        map.put("refresh_token_expire_time", this.refresh_token_expire_time);
        map.put("scope", this.scope);
        map.put("owner_id", this.owner_id);
        return map;
    }

    public void reset(){
        this.token_type = "";
        this.access_token = "";
        this.expires_in = "";
        this.expire_time = "";
        this.refresh_token = "";
        this.refresh_token_expires_in = "";
        this.refresh_token_expire_time = "";
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


}
