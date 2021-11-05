# Server Access

Simple get data from api.


## Installation
### Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

### Step 2. Add the dependency [![](https://jitpack.io/v/redarchid/ServerAccess.svg)](https://jitpack.io/#redarchid/ServerAccess)

```gradle
dependencies {
	implementation 'com.github.redarchid:ServerAccess:0.1.0'
}
```

## How to use it
there are 4 functions that can be called in this ServerAccess: Call, Upload, Download, GetStreamAccess, Raw.
Then the data that has been received will be entered into ResponseData.

### Call
Request data from api with 4 method: GET,POST,PUT,PATCH or you can input other type method.
```java
ServerAccess.Call(Context, [METHOD], [URL API], [header AUTH], [JSON BODY], new ServerAccess.Listener() {
            @Override
            public void OnRequestSuccess(ResponseData responseData) {
		//...write some code
            }

            @Override
            public void OnRequestFailed(ResponseData responseData) {
	    	//...write some code
            }
        });
```
Response: If Success
```json
{
	"status_code":200,
	"success":true,
	"message":"",
	"data":{
		[result_data]
	}
}
```
Response: If Error/Failed
```json
{
	"status_code":422, 
	"success":false,
	"message":"Error Message",
	"data":null
}
```

example:
```java
ServerAccess.Call(
	this, // Context
	ServerAccess.METHOD_POST, // Method
	"http://127.0.0.1/requestdata/api", // URL API
	"xgahusbd78bt617btusbqt728b12", // Auth
	body.toString(), // JSON body
	new ServerAccess.Listener() { // Result listener
                @Override
                public void OnRequestSuccess(ResponseData response) {
                    log.d(TAG,response.getResultData());
                }

                @Override
                public void OnRequestFailed(ResponseData response) {
		    log.e(TAG,response.getResultData());
                }
            });
```

### Upload
Upload file to server w/o body.</br>
There are 2 types for upload: <b>Using File path</b> and <b>Using File Uri</b>.
</br>
#### Upload using File Path
```java
ServerAccess.Upload(Context, [URL API], [header AUTH], [FILE PATH], [KEY QUERY], [JSON BODY], new ServerAccess.UploadListener() {
		@Override 
		public void OnUploadSuccess(ResponseData response) {
			//... write some code
		}
			
		@Override 
		public void OnUploadProgress(int percent, int current_size, int total_size, boolean uploadComplete) {
			//... write some code
		}
			
		@Override 
		public void OnUploadFailed(ResponseData response) {
			//... write some code
		}
	});
```

#### Upload using File Uri
```java
ServerAccess.UploadWithUri(Context, [URL API], [header AUTH], [File Uri], [File Name], [KEY QUERY], [JSON BODY], new ServerAccess.UploadListener() {
                @Override
                public void OnUploadSuccess(ResponseData response) {
                    	//... write some code
                }

                @Override 
		public void OnUploadProgress(int percent, int current_size, int total_size, boolean uploadComplete) {
			//... write some code
		}

                @Override
                public void OnUploadFailed(ResponseData response) {
			//... write some code
                }
	});
```

### Download
Download any file from server and save it to Storage.
If you are using File Uri, you can set File Path, Name, Extension to null.
```java
ServerAccess.Download(Context, [URL API], [header Auth], [File Uri], [File Path], [File Name], [File Extension], new ServerAccess.DownloadListener() {
		@Override
		public void OnDownloadSuccess(File file) {
			//... write some code
		}

            	@Override
            	public void OnDownloadProgress(int percent, int current_size, int total_size) {
                	//... write some code
            	}

            	@Override
            	public void OnDownloadFailed(ResponseData response) {
                	//... write some code
            	}
        });
```

### GetAccessStream
Get Input Stream from API.
```java
ServerAccess.GetAccessStream(Context, [METHOD], [URL API], [header AUTH], [JSON BODY], new ServerAccess.StreamListener() {
            	@Override
            	public void OnResultStream(ResponseData responseData) {
                	//.. write some code
            	}

            	@Override
            	public void OnStreamError(ResponseData response) {
                	//.. write some code
            	}
        });
```

### Raw
All the above functions use laravel rest api as the basic structure. if you take data from other api sources you can use this function with the result of data that is not processed from this class. you can set the header manually or use the basic header that has been provided.
```java
ServerAccess.Raw(Context, [METHOD], [URL API], [HEADERS], [JSON BODY], new ServerAccess.RawListener() {
            	@Override
            	public void OnRequestSuccess(ResponseData response) {
                	//... write some code
            	}

            	@Override
            	public void OnRequestFailed(ResponseData response) {
			//... write some code
            	}
        });

```

Example:
```java
ServerAccess.Raw(
		context, 
		ServerAccess.METHOD_GET, 
		URLCons.URL_LOGIN(context), 
		ServerAccess.DEFAULT_HEADERS(), 
		json.toString(), 
		new ServerAccess.RawListener() {
            	@Override
            	public void OnRequestSuccess(ResponseData response) {
			//... write some code
            	}

            	@Override
            	public void OnRequestFailed(ResponseData response) {
			//... write some code
            	}
        });
```


## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT](https://choosealicense.com/licenses/mit/)
