package com.jtkurniawan.serveraccess;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class ResponseData implements Parcelable {

    private String url;
    private String method;
    private String auth;
    private String body;
    private int responseCode;
    private String message;
    private String resultData;
    private boolean success;
    private InputStream inputStream = null;

    public ResponseData() {}

    public ResponseData(String url, String method, String auth, String body, int responseCode, String message, String resultData, boolean success) {
        this.url = url;
        this.method = method;
        this.auth = auth;
        this.body = body;
        this.responseCode = responseCode;
        this.message = message;
        this.resultData = resultData;
        this.success = success;
    }

    protected ResponseData(Parcel in){
        url = in.readString();
        method = in.readString();
        auth = in.readString();
        body = in.readString();
        responseCode = in.readInt();
        resultData = in.readString();
        success = in.readInt()==1;
    }

    public static final Creator<ResponseData> CREATOR = new Creator<ResponseData>() {
        @Override
        public ResponseData createFromParcel(Parcel source) {
            return new ResponseData(source);
        }

        @Override
        public ResponseData[] newArray(int size) {
            return new ResponseData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(method);
        dest.writeString(auth);
        dest.writeString(body);
        dest.writeInt(responseCode);
        dest.writeString(resultData);
        dest.writeInt(success?1:0);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResultData() {
        return resultData;
    }

    public void setResultData(String resultData) {
        this.resultData = resultData;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public boolean isServerError(){
        return responseCode==500;
    }

    public boolean isUnprocessableEntity(){
        return responseCode==422;
    }

    public String getServerException(){
        StringBuilder builder = new StringBuilder();
        builder.append("Server Error -> ");

        try {
            JSONObject json = new JSONObject(resultData);
            JSONObject error = json.getJSONObject(ServerAccess.JSON_ERROR_STACK_TRACE);
            if (error.has("exception")){
                builder.append(error.getString("exception"));
                builder.append(" - ");
            }
            if (error.has("message")){
                builder.append(error.getString("message"));
                builder.append(" ");
            }
            if (error.has("file")){
                builder.append("(");
                builder.append(error.getString("file"));
            }
            if (error.has("line")){
                builder.append(":");
                builder.append(error.getString("line"));
                builder.append(")");
            }
        } catch (JSONException e){
            builder.append("ErrorException");
        }

        return builder.toString();
    }

    public String getServerExceptionJSON(){
        String result;
        try {
            JSONObject json = new JSONObject(resultData);
            JSONObject error = json.getJSONObject(ServerAccess.JSON_ERROR_STACK_TRACE);
            result = error.toString();
        } catch (JSONException e){
            result = resultData;
        }

        return result;
    }
}
