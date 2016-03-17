# RingCentral Android SDK

[![Build Status](https://travis-ci.org/vyshakhbabji/ringcentral-android.svg)](https://travis-ci.org/vyshakhbabji/ringcentral-android)
[![Bintray][bintray-version-svg]][bintray-version-link]
[![License][license-svg]][license-link]



## Overview

This RingCentral Android SDK has been made to make Android development easier for developers who are using RingCentral Platform's suite of APIs. 
It handles authentication and the token lifecycle, makes API requests, and parses API responses. This documentation will help you get set up and going with some example API calls.

## Installation

## Android Studio Environment

### Install SDK

You can install the RingCentral SDK via Bintray.

#### Via Bintray

To add this SDK to your project from JCenter, add this line to your Gradle dependencies for your app. Here is the link to the online repository: https://bintray.com/ringcentral/maven/
Add these to your app's Gradle dependencies:

```java

buildscript {
    repositories {
        jcenter()
        maven {
            url "http://dl.bintray.com/ringcentral/maven"
        }
         
        <----
        other repositories
        ---->
    
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:1.2.3'
        <---
            other classpaths
        ----->
    }
}


dependencies {
    compile 'com.ringcentral.android:ringcentral-android:0.7.1'
     <---
                other dependencies
     ---->
    
}
```


### Configure App Permissions

Add the follow permissions to your app module's `AndroidManifest.xml` (`app/src/main/AndroidManifest.xml`) within the `<manifest>` tag to utilize RingCentral capabilties:

```java
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
```

### Import library

To import the SDK to your app, e.g. `MainActivity`, add this line:

```java
import com.ringcentral.android.sdk.*;
```

## Usage

## Initialization

Create an instance of the global SDK object in your application, and configure it with your unique API key, secret, and server URL.
```java
  
  SDK sdk = new SDK( <appkey>, <appsecret>, Platform.Server.SANDBOX or Platform.Server.SANDBOX);
  Platform platform = sdk.platform();

```

##### Production:

```java
sdk = new SDK(appKey, appSecret, Platform.Server.PRODUCTION);
```
##### Sandbox

```java
sdk = new SDK(appKey, appSecret, Platform.Server.SANDBOX);
```

## Get Platform Singleton

```java
Platform platform = sdk.platform();
```

## Authentication

Authentication is done by calling the `platform.login()` method with the username, extension(optional), and password. Also, because the login process is asynchronous, you have to call a `new ApiCallback()` and pass that in as the last parameter. You can handle login success in the overriding of the Callback's `onResponse()`, such as performing updates to the user interface. To handle login failure, you can add error handling in `onFailure()`.

```java
 platform.login("username", "ext", "password", new ApiCallback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        //handle exception
                    }
                    @Override
                    public void onResponse(Response response) throws IOException {
                       //your code
                    }
                });
```

## Checking Authentication State

To check in your Application if the user is authenticated, you can call the platform objects's `loggedIn()` method which will handle refreshing tokens for you, or throw an exception if the refreshed token is invalid.

```java
platform.loggedIn(); //returns boolean 
```

##Performing API calls

To perform an authenticated API call, you should use the `get` `post` `put` or `delete` method of the platform object.

Example for sending GET, POST, PUT or DELETE request

```java
        String payload = "" or JSONObject payload = {} ;

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), payload.getBytes());

        try {
            
            //get request body= null, headers=null 
            platform.get(API.CALLLOG.value, null, null, callback);
            
            or
            //post request 
            platform.post(API ENDPOINT , body, header , callback);
            
            or 
            
            //put request
            platform.put(API ENDPOINT , body, header , callback);
            
            or 
            
            //delete request
            platform.delete(API ENDPOINT , body, header , callback);
            
            
            or
            
            platfrom.send(HTTP METHOD, API ENDPOINT, RequestBody body, HashMap headerMap, ApiCallback callback) 

            }catch (ApiException e) {
                    //handle exception
            }
```

<!---

## Android Demo

https://github.com/vyshakhbabji/ringcentral-android-sdk-demoapp

## Logging Issues 

Feel free to log the issues [here](https://github.com/ringcentral/ringcentral-android/issues)

## License

RINGCENTRAL ringcental-android SDK is available under an MIT-style license. See [LICENSE.md](LICENSE.md) for details.

RINGCENTRAL ringcentral-android &copy; by Vyshakh Babji, Andrew Pang



 [build-status-svg]: https://travis-ci.org/ringcentral/ringcentral-android.svg?branch=master
 [build-status-link]: https://travis-ci.org/ringcentral/ringcentral-android
 [bintray-version-svg]: https://img.shields.io/bintray/v/ringcentral/maven/ringcentral-android.svg
 [bintray-version-link]: https://bintray.com/ringcentral/maven/ringcentral-android/view
 [license-svg]: https://img.shields.io/badge/license-MIT-blue.svg
 [license-link]: https://github.com/ringcentral/ringcentral-android/blob/master/LICENSE.md
