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

### Call
Request to api with 4 method: GET,POST,PUT,DELETE or you can input other type method.
```java
ServerAccess.Call(context, [method], [api_url], [auth], [json_body], new ServerAccess.Listener() {
            @Override
            public void OnRequestSuccess(ResponseData responseData) {

            }

            @Override
            public void OnRequestFailed(ResponseData responseData) {

            }
        });
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT](https://choosealicense.com/licenses/mit/)
