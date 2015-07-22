#Table of contents

1.
2.
3.
4.

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
                        // Your code here
		}
	});
``` 

