#Table of contents

1.Installation
2.Basic Usage

#Installation

##STUB

#Basic Usage
##Initialization
Create an instance of the global SDK object in your application, and configure it with your unique API key, secret, and server URL.
```java
SDK = new SDK(appKey, appSecret, "https://platform.devtest.ringcentral.com";
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
			if(!response.isSuccessful())
				throw new IOException("Unexpected code " + response);
			// Create RCResponse and parse the JSON response to set Auth data
			RCResponse authResponse = new RCResponse(response);
			Map<String, String> responseMap = authResponse.getJson();
			platform.setAuthData(responseMap);
               		// Your code goes here
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
HashMap<String, String> body = new HashMap();
body.put("body", "BodyStringGoesHere")
```
For all API calls, create a HashMap for headers and add header-type 
as the key, and header values as the value.
```java
HashMap<String, String> headers = new HashMap();
headers.put("content-type", "application/json"); // Add headers this way
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
		// Your code goes here
		}
	});
```
	
	




