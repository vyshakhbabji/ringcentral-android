#Table of contents

1. [Overview](#overview)
2. [Installation](#installation)
3. [Basic Usage](#basic-usage)
	1. [Initialization](##initialization)
	2. [Authentication](##authentication)
4. [Examples](#examples)
	1. [RingOut](##performing-a-ringout)
	2. [SMS](##sending-an-sms)
	3. [Call Log](##getting-the-call-log)


#Overview
This RingCentral Android SDK has been made to make Android development easier for developers who are using RingCentral Platform's suite of APIs. It handles authentication and the token lifecycle, makes API requests, and parses API responses. This documentation will help you get set up and going with some example API calls.
#Installation
##Android Studio Environment
###Android Manifest
Add these permissions to your AndroidManifest.xml
```java
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
```
###Gradle
Add these to your app's Gradle dependencies
```java
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'com.android.support:appcompat-v7:22.2.0'
    compile 'com.google.code.gson:gson:2.3.1'
    compile 'com.squareup.okhttp:okhttp:2.4.0'
    compile 'com.pubnub:pubnub-android:3.7.4'
}
```
#Basic Usage
##Initialization
Create an instance of the global SDK object in your application, and configure it with your unique API key, secret, and server URL.
#####Production:
```java
SDK = new SDK(appKey, appSecret, SDK.RC_SERVER_PRODUCTION);
```
#####Sandbox
```java
SDK = new SDK(appKey, appSecret, SDK.RC_SERVER_SANDBOX);
```
####Get Platform Singleton
```java
platform = SDK.getPlatform();
```
With the platform singleton and the SDK configured with the correct server URL and API key, your application can authenticate to access the features of the API.
##Authentication
Authentication is done by calling the `platform.authorize()` method with the username, extension(optional), and password. Also, because the login process is asynchronous, you have to call a `new Callback()` and pass that in as the last parameter. You can handle login success in the overriding of the Callback's `onResponse()`, such as performing updates to the user interface. To handle login failure, you can add error handling in `onFailure()`.
```java
SDK.platform.authorize(
	"username", // Phone number in full format
 	"extension", // Input "" if direct number is used
	"password",
	new Callback() {
		@Override
		public void onFailure(Request request, IOException e){
			e.printStackTrace //Handle exception
		} 
		@Override
		public void onResponse(Response response) throws IOException {
			RCResponse authResponse = new RCResponse(response);
            Map<String, String> responseMap = authResponse.getJson();
            // If HTTP response is not successful, throw exception
            if (!response.isSuccessful()){
                throw new IOException("Error code: " + authResponse.getStatus() + ". Error: " + responseMap.get("error") + ": " + responseMap.get("error_description"));
            }
            // Create RCResponse and parse the JSON response to set Auth data
            platform.setAuthData(responseMap);
            // Display options Activity
            Intent optionsIntent = new Intent(MainActivity.this, OptionsActivity.class);
            optionsIntent.putExtra("MyRcsdk", SDK);
            startActivity(optionsIntent);
		}
});
``` 
####Checking Authentication State
To check in your Application if the user is authenticated, you can call the platform singleton's `isAuthorized()` method which will handle refreshing tokens for you, or throw an exception if the refreshed Access Token is invalid.
```java
platform.isAuthorized();
```
##Performing API calls
To perform an authenticated API call, you should use the `get` `post` `put` or `delete` method of the platform singleton. For calling `get` and `post` requests, pass in a Hashmap for the body and the headers, and a Callback since Android HTTP requests are asynchronous. If your body needs to be encoded Form Data as key value pairs, add to the body HashMap with keys and values. Or else, just add the body string with they key as "body", 
```java
LinkedHashMap<String, String> body = new LinkedHashMap();
body.put("body", "BodyStringGoesHere")
```
For all API calls, create a HashMap for headers and add header-type 
as the key, and header values as the value.
```java
HashMap<String, String> headers = new HashMap();
// Add headers (e.g. "Content-Type", "url") 
headers.put("Content-Type", "application/json");
headers.put("url", "/restapi/v1.0/account/~/extension/~/sms");
```
Example post request, passing in the body, headers, and Callback: 
```java
platform.post(body, headers,
	new Callback() {
		@Override
		public void onFailure(Request request, IOException e) {
			e.printStackTrace();
		}
		@Override
		public void onResponse(Response response) throws IOException {
		if(!response.isSuccessful())
			throw new IOException("Unexpected code " + response);
		RCResponse rcResponse = new RCResponse(response);
			// Your code goes here
		}
});
```
#Examples
##Performing a RingOut
The RingOut POST API call has a helper function written so you can just input the "To", "From", and "Caller ID" phone numbers.
```java
platform.ringOut(
	"15101234567", // Phone number calling "To"
	"18881234567", // Phone number calling "From"
	"12223334444", // Caller ID number
	"True", // "True" or "False" states whether a prompt plays before a call
	new Callback() {
		@Override
                public void onFailure(Request request, IOException e) {
                        e.printStackTrace();
                }
                @Override
                public void onResponse(Response response) throws IOException {
                	RCResponse authResponse = new RCResponse(response);
                    String responseString = authResponse.getBody();
                    // If HTTP response is not successful, throw exception
                    if (!response.isSuccessful()) {
                        try {
                        JSONObject jsonObject = new JSONObject(responseString);
           				String errorCode = jsonObject.getString("errorCode");
                        String message = jsonObject.getString("message");
                        throw new IOException("Error code: "+ authResponse.getStatus() + ". Error: " + errorCode + ": " + message);
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                    // Your code goes here
                }
});	
```
##Sending an SMS
The send SMS POST API call has a helper function written so you can input the "To", and "From" phone number and SMS message.
```java
platform.sendSMS(
	"15101234567", // Phone number calling "To"
	"18881234567", // Phone number calling "From"
	"This is a sample text message",
	new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                        e.printStackTrace();
                }
                @Override
                public void onResponse(Response response) throws IOException {
                	RCResponse smsResponse = new RCResponse(response);
                    String responseString = smsResponse.getBody();
                    // If HTTP response is not successful, throw exception
                    if (!response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(responseString);
                            String errorCode = jsonObject.getString("errorCode");
                        	String message = jsonObject.getString("message");
                            throw new IOException("Error code: "+ smsResponse.getStatus() + ". Error: " + errorCode + ": " + message);
                        } catch (JSONException e){
                            e.printStackTrace();
                     	}
                    }
                	// Your code goes here
                }
});
```
##Getting the call log
The call log GET API call has a helper function written that returns the response in the Callback.
```java
platform.callLog(
	new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                        e.printStackTrace();
                }
                @Override
                public void onResponse(Response response) throws IOException {
                if(!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                RCResponse rcResponse = new RCResponse(response);                       
                	// Your code goes here
                }
});	
```	




