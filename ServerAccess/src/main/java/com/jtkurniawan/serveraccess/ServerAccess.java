package com.jtkurniawan.serveraccess;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created By DICKY KURNIAWAN at 30/07/2020
 */
public class ServerAccess {
    private static final String TAG = "ServerAccess";

    private static final String json_request_timeout = "{\"status_code\":408,\"success\":false,\"message\":\"Sambungan terputus.\",\"data\":null}";
    private static final String json_download_failed = "{\"status_code\":408,\"success\":false,\"message\":\"Gagal Download.\",\"data\":null}";
    private static final String json_internal_server_error = "{\"status_code\":408,\"success\":false,\"message\":\"Terjadi gangguan di server. Silahkan hubungi Team IT Development.\",\"data\":null}";

    private static final String message_error_timeout = "Connection Timeout";
    private static final String message_error_internal_server = "There is a problem on the server";
    private static final String message_error_load_file = "Failed to get file from storage.";

    /**
     * Request to API Server
     * @param context Context
     * @param METHOD Use Method from this class
     * @param URL Url Link / API
     * @param AUTH if uses Authorization Bearer or something
     * @param BODY Content data
     * @param listener Callback Listener
     */
    public static void Call(final Context context, String METHOD, String URL, String AUTH, String BODY, final Listener listener){
        Log.e(TAG, "Call: " +
                "\nURL    : "+URL+
                "\nAUTH   : "+AUTH+
                "\nMETHOD : "+METHOD+
                "\nBODY   : "+(BODY!=null? JsonShowFormat(BODY):null));

        // FILTER NULL
        final String method = METHOD!=null?METHOD:METHOD_GET;
        final String url = URL!=null?URL:"";
        final String auth = AUTH!=null?AUTH:"";
        final String body = BODY!=null?BODY:"";

        RequestServer request = new RequestServer(context, method, url, auth, body, new call_bridge() {
            @Override
            public void onJSONResult(String string, int response) {
                boolean success = false;
                String message = "";
                if (!string.isEmpty()){
                    try {
                        JSONObject json = new JSONObject(string);
                        int code = json.getInt("status_code");
                        if (code == HttpURLConnection.HTTP_OK){
                            success = true;
                        } else if (code == HttpURLConnection.HTTP_CLIENT_TIMEOUT){
                            message = message_error_timeout;
                        }
                    } catch (JSONException e) {
                        message = message_error_internal_server;
                        string = BuildErrorJson(string,response);
                    }
                } else {
                    message = message_error_internal_server;
                    string = BuildErrorJson(string,response);
                }

                ResponseData result = new ResponseData(url,method,auth,body,response,message,string,success);

                if (success){
                    listener.OnRequestSuccess(result);
                } else {
                    listener.OnRequestFailed(result);
                }
            }
        });
        request.execute();
    }

    /**
     * Upload file to server
     * @param context Context
     * @param PATH path file with filename and extension
     * @param KEY API endpoint
     * @param URL URl Link
     * @param AUTH if uses Authorization Bearer or something
     * @param listener Callback Listener
     */
    public static void Upload(final Context context, String URL, String AUTH, String PATH, String KEY, String BODY, UploadListener listener){
        Log.e(TAG, "Upload: " +
                "\nURL    : "+URL+
                "\nAUTH   : "+AUTH+
                "\nBODY   : "+(BODY!=null? JsonShowFormat(BODY):null));
        final String url = URL!=null?URL:"";
        final String auth = AUTH!=null?AUTH:"";
        final String body = BODY!=null?BODY:"";
        RequestUpload request = new RequestUpload(context, body, PATH, KEY, auth, url, new upload_bridge() {
            @Override
            public void onJSONResult(String string, int response) {
                boolean success = false;
                String message = "";
                if (!string.isEmpty()){
                    try {
                        JSONObject json = new JSONObject(string);
                        int code = json.getInt("status_code");
                        if (code == HttpURLConnection.HTTP_OK){
                            success = true;
                        } else if (code == HttpURLConnection.HTTP_CLIENT_TIMEOUT){
                            message = message_error_timeout;
                        }
                    } catch (JSONException e) {
                        message = message_error_internal_server;
                        string = BuildErrorJson(string,response);
                    }
                } else {
                    message = message_error_internal_server;
                    string = BuildErrorJson(string,response);
                }

                ResponseData result = new ResponseData(url,METHOD_POST,auth,null,response,message,string,success);

                if (success){
                    listener.OnUploadSuccess(result);
                } else {
                    listener.OnUploadFailed(result);
                }
            }

            @Override
            public void onProgressResult(long[] value,boolean uploadComplete) {
                listener.OnUploadProgress(
                        (int)value[0], // Percent
                        (int)value[1], // Current Size
                        (int)value[2], // Total Size
                        uploadComplete // Buffer Status
                );
            }
        });
        request.execute();
    }

    public static void UploadWithUri(final Context context, String URL, String AUTH, Uri uri, String fileName, String KEY, String BODY, UploadListener listener){
        Log.e(TAG, "UploadFile: " +
                "\nFILE : "+uri.getPath()+
                "\nURL  : "+URL+
                "\nAUTH : "+AUTH);
        final String url = URL!=null?URL:"";
        final String auth = AUTH!=null?AUTH:"";
        final String body = BODY!=null?BODY:"";
        RequestUploadUri request = new RequestUploadUri(context, body, uri,fileName, KEY, auth, url, new upload_bridge() {
            @Override
            public void onJSONResult(String string, int response) {
                boolean success = false;
                String message = "";
                if (!string.isEmpty()){
                    try {
                        JSONObject json = new JSONObject(string);
                        int code = json.getInt("status_code");
                        if (code == HttpURLConnection.HTTP_OK){
                            success = true;
                        } else if (code == HttpURLConnection.HTTP_CLIENT_TIMEOUT){
                            message = message_error_timeout;
                        }
                    } catch (JSONException e) {
                        message = message_error_internal_server;
                        string = BuildErrorJson(string,response);
                    }
                } else {
                    message = message_error_internal_server;
                    string = BuildErrorJson(string,response);
                }

                ResponseData result = new ResponseData(url,METHOD_POST,auth,null,response,message,string,success);

                if (success){
                    listener.OnUploadSuccess(result);
                } else {
                    listener.OnUploadFailed(result);
                }
            }

            @Override
            public void onProgressResult(long[] value, boolean uploadComplete) {
                listener.OnUploadProgress(
                        (int)value[0], // Percent
                        (int)value[1], // Current Size
                        (int)value[2], // Total Size
                        uploadComplete // Buffer Status
                );
            }
        });
        request.execute();
    }

    /**
     * Download file from API
     * @param context Context
     * @param SERVER_URL Link URL Api
     * @param AUTH Auth access Bearer
     * @param URI URI path file storage
     * @param PATH String path
     * @param FILENAME String filename
     * @param FILETYPE String extension
     * @param listener Result Listener
     */
    public static void Download(final Context context, String SERVER_URL, String AUTH, Uri URI, String PATH, String FILENAME, String FILETYPE, final DownloadListener listener){
        Log.e(TAG, "RAW_DownloadFile: " +
                "\nURL      : "+SERVER_URL+
                "\nAUTH     : "+AUTH+
                "\nSave to  : "+PATH+ File.separator+FILENAME+FILETYPE);

        RequestDownload download = new RequestDownload(context, URI, AUTH, PATH, FILENAME, FILETYPE, SERVER_URL, new download_bridge() {
            @Override
            public void onFileResult(File file) {
                if (file!=null){
                    listener.OnDownloadSuccess(file);
                } else {
                    ResponseData result = new ResponseData(SERVER_URL,METHOD_POST,AUTH,null,408,message_error_load_file,json_download_failed,false);
                    listener.OnDownloadFailed(result);
                }
                Log.e(TAG, "Download onFileResult: file = "+(file!=null));
            }

            @Override
            public void onProgressResult(int[] value) {
                listener.OnDownloadProgress(value[0],value[1],value[2]);
            }

            @Override
            public void OnError(String string, int response) {
                String message = message_error_load_file;
                if (!string.isEmpty()){
                    try {
                        JSONObject json = new JSONObject(string);
                        int code = json.getInt("status_code");
                        if (code == HttpURLConnection.HTTP_CLIENT_TIMEOUT){
                            message = message_error_timeout;
                        }
                    } catch (JSONException e) {
                        message = message_error_internal_server;
                        string = BuildErrorJson(string,response);
                    }
                } else {
                    message = message_error_internal_server;
                    string = BuildErrorJson(string,response);
                }

                ResponseData result = new ResponseData(SERVER_URL,METHOD_GET,AUTH,null,response,message,string,false);
                listener.OnDownloadFailed(result);
            }
        });
        download.execute();
    }

    public static void GetAccessStream(final Context context, String METHOD, String URL, String AUTH, String BODY, StreamListener listener){
        Log.e(TAG, "ServerAccess: " +
                "\nURL    : "+URL+
                "\nAUTH   : "+AUTH+
                "\nMETHOD : "+METHOD+
                "\nBODY   : "+(BODY!=null? JsonShowFormat(BODY):null));

        // FILTER NULL
        final String method = METHOD!=null?METHOD:METHOD_GET;
        final String url = URL!=null?URL:"";
        final String auth = AUTH!=null?AUTH:"";
        final String body = BODY!=null?BODY:"";

        RequestStream stream = new RequestStream(context, method, url, auth, body, new stream_bridge() {
            @Override
            public void onStreamResult(InputStream is, String data, int response) {
                boolean success;
                String message = "";

                success = response == HttpURLConnection.HTTP_OK;

                if (!success){
                    message = message_error_internal_server;
                    data = BuildErrorJson(data,response);
                }

                ResponseData result = new ResponseData(url,method,auth,body,response,message,data,success);
                result.setInputStream(is);

                if (success){
                    listener.OnResultStream(result);
                } else {
                    listener.OnStreamError(result);
                }
            }
        });
        stream.execute();
    }

    /**
     * Request to API Server
     * @param context Context
     * @param METHOD Use Method from this class
     * @param URL Url Link / API
     * @param HEADERS header properties
     * @param BODY Content data
     * @param listener Callback Listener
     */
    public static void Raw(final Context context, String METHOD, String URL, HashMap<String,String> HEADERS, String BODY, final RawListener listener){
        Log.e(TAG, "ServerAccess: " +
                "\nURL    : "+URL+
                "\nMETHOD : "+METHOD+
                "\nBODY   : "+(BODY!=null? JsonShowFormat(BODY):null));

        // FILTER NULL
        final String method = METHOD!=null?METHOD:METHOD_GET;
        final String url = URL!=null?URL:"";
        final String body = BODY!=null?BODY:"";

        RequestRAW request = new RequestRAW(context, method, url, HEADERS, body, new raw_bridge() {
            @Override
            public void onRawResult(String data, int response) {
                ResponseData result = new ResponseData();
                result.setUrl(url);
                result.setMethod(method);
                result.setBody(body);
                result.setResultData(data);
                result.setResponseCode(response);
                result.setSuccess(response == HttpURLConnection.HTTP_OK);

                if (response == HttpURLConnection.HTTP_OK){
                    listener.OnRequestSuccess(result);
                } else {
                    listener.OnRequestFailed(result);
                }
            }
        });
        request.execute();
    }

    //===============================================================================================================================
    private static class RequestServer extends AsyncTask<String, String, String> {

        Context context;

        String method;

        String link;
        String auth;
        String body;

        int response = 0;

        call_bridge listener;

        public RequestServer(@NonNull Context context, String method, String link, String auth, String body, call_bridge listener) {
            this.context = context;
            this.method = method;
            this.link = link;
            this.auth = auth;
            this.body = body;
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() { super.onPreExecute(); }

        @Override
        protected String doInBackground(String... strings) {
            String data = "";

            try {
                data = SendData();
            } catch (IOException e) {
                Log.e(TAG, "RequestServer doInBackground IOException: "+e.getMessage(), e);
                try { // DISCONNECTED
                    JSONObject json = new JSONObject(json_request_timeout);
                    response = 408;
                    data = json.toString();
                } catch (JSONException j){
                    Log.e(TAG, "RequestServer doInBackground JSONException: "+j.getMessage(), j);
                } catch (NullPointerException j){
                    Log.e(TAG, "RequestServer doInBackground NullPointerException: "+j.getMessage(), j);
                }
            }

            return data;
        }

        private String SendData() throws IOException {
            URL url = new URL(link);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);

            conn.setRequestMethod(method);
            conn.setRequestProperty("Authorization",auth);
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("Accept","application/json");

            conn.setDoInput(true);
            if (!method.equals(METHOD_GET)){
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(body);

                writer.flush();
                writer.close();
                os.close();
            }

            int responseCode=conn.getResponseCode();
            String result;
            if (responseCode == HttpURLConnection.HTTP_OK){
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                int result2 = bis.read();
                while(result2 != -1) {
                    buf.write((byte) result2);
                    result2 = bis.read();
                }
                result = buf.toString();
            } else {
                BufferedInputStream bis = new BufferedInputStream(conn.getErrorStream());
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                int result2 = bis.read();
                while(result2 != -1) {
                    buf.write((byte) result2);
                    result2 = bis.read();
                }
                result = buf.toString();
            }

            response = conn.getResponseCode();

            Log.e(TAG,"RESULT REQUEST "+method+":" +
                    "\n=============================================================================================\n"+
                    "\nURL   : "+conn.getURL().toString()+
                    "\nAUTH  : "+conn.getRequestProperty("Authorization")+
                    "\nMETHOD: "+conn.getRequestMethod()+" --> Response: "+conn.getResponseCode()+
                    "\nBODY  : "+JsonShowFormat(body)+
                    "\nRESULT: "+JsonShowFormat(result)+
                    "\n\n=============================================================================================");

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            listener.onJSONResult(s,response);
        }
    }

    //========================================= UPLOAD =============================================
    private static class RequestUpload extends AsyncTask<String, String, String> {

        Context context;
        String auth;
        String body;
        String key;
        String path;
        String link_url;
        upload_bridge listener;

        int response = 0;

        RequestUpload(Context context, String body, String path, String key, String auth, String link_url, upload_bridge listener) {
            this.context = context;
            this.body = body;
            this.path = path;
            this.key = key;
            this.auth = auth;
            this.link_url = link_url;
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String data = "";

            try {
                data = callserver();
            } catch (IOException e) {
                Log.e(TAG, "doInBackground REQUEST PUT: "+e.getMessage(), e);
                try {
                    JSONObject json = new JSONObject(json_request_timeout);
                    data = json.toString();
                } catch (JSONException j){
                    Log.e(TAG, "doInBackground: ", j);
                }
            }

            return data;
        }

        private String callserver() throws IOException {
            try {
                String sourceFileUri = path;

                HttpURLConnection conn = null;
                DataOutputStream dos = null;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1 * 1024 * 1024; // SET LIMIT BUFFER TO 1 MB
                File sourceFile = new File(sourceFileUri);

                if (sourceFile.isFile()) {

                    try {
                        String upLoadServerUri = link_url;

                        // open a URL connection to the Servlet
                        FileInputStream fileInputStream = new FileInputStream(sourceFile);
                        URL url = new URL(upLoadServerUri);

                        // Open a HTTP connection to the URL
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true); // Allow Inputs
                        conn.setDoOutput(true); // Allow Outputs
                        conn.setUseCaches(false); // Don't use a Cached Copy
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Authorization",auth);
                        conn.setRequestProperty("Accept","application/json");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("ENCTYPE", "multipart/form-data");
//                        conn.setRequestProperty("Transfer-Encoding","chunked");
                        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                        conn.setRequestProperty(key, sourceFileUri);

//                        conn.setChunkedStreamingMode(maxBufferSize); // Send data by Chunk/Parts

                        dos = new DataOutputStream(conn.getOutputStream());
                        dos.writeBytes(twoHyphens + boundary + lineEnd);

                        //============= UPLOAD FILE
                        dos.writeBytes("Content-Disposition: form-data; name=\""+key+"\";filename=\"" + sourceFileUri + "\"" + lineEnd);

                        dos.writeBytes(lineEnd);

                        // create a buffer of maximum size
                        bytesAvailable = fileInputStream.available();

                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];

                        Log.e(TAG, "CHECK SIZE BUFFER FILE: "+buffer.length+" B / "+ GetSizeInKB(buffer.length)+" KB / "+ GetSizeInMB(buffer.length)+" MB");

                        // read file and write it into form...
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                        long total = 0;
                        long maxSize = sourceFile.length();

                        while (bytesRead > 0) {
                            dos.write(buffer, 0, bufferSize);
                            total += bufferSize;
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                            Log.e(TAG, "UPLOAD FILE : "+total+" / "+bytesAvailable+" ("+(total*100/maxSize)+"%)");

                            try {
                                long publish_percent = (total*100/maxSize);

                                JSONObject json = new JSONObject();
                                json.put("percent",publish_percent);
                                json.put("current_size",total);
                                json.put("total_size",maxSize);
                                json.put("complete",false);
                                publishProgress(json.toString());
                            } catch (JSONException e){
                                Log.e(TAG, "callserver: "+e.getMessage(), e);
                            }
                        }

                        dos.writeBytes(lineEnd);

                        try {
                            JSONObject json = new JSONObject();
                            json.put("percent",100);
                            json.put("current_size",total);
                            json.put("total_size",maxSize);
                            json.put("complete",true);
                            publishProgress(json.toString());
                        } catch (JSONException e){
                            Log.e(TAG, "callserver: "+e.getMessage(), e);
                        }

                        //============= POST FORM DATA
                        if (!body.isEmpty()){
                            try {
                                JSONObject json = new JSONObject(body);
                                Iterator<String> keys = json.keys();
                                while (keys.hasNext()){
                                    String key = keys.next();
                                    String value = String.valueOf(json.get(key));

                                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                                    dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                                    dos.writeBytes("Content-Type: text/plain" + lineEnd);
                                    dos.writeBytes(lineEnd);
                                    dos.writeBytes(value);
                                    dos.writeBytes(lineEnd);
                                }
                            } catch (JSONException e){
                                Log.e(TAG, "callserver: "+e.getMessage(),e );
                            }
                        }

                        //============= CLOSING
                        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                        // close the streams //
                        fileInputStream.close();
                        dos.flush();
                        dos.close();

                        int responseCode=conn.getResponseCode();

                        String result = "";
                        if (responseCode == HttpURLConnection.HTTP_OK){
                            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                            ByteArrayOutputStream buf = new ByteArrayOutputStream();
                            int result2 = bis.read();
                            while(result2 != -1) {
                                buf.write((byte) result2);
                                result2 = bis.read();
                            }
                            result = buf.toString();
                        } else {
                            BufferedInputStream bis = new BufferedInputStream(conn.getErrorStream());
                            ByteArrayOutputStream buf = new ByteArrayOutputStream();
                            int result2 = bis.read();
                            while(result2 != -1) {
                                buf.write((byte) result2);
                                result2 = bis.read();
                            }
                            result = buf.toString();
                        }

                        Log.e(TAG,"RESULT REQUEST UPLOAD FILE:" +
                                "\n=============================================================================================\n"+
                                "\nURL   : "+conn.getURL().toString()+
                                "\nAUTH  : "+conn.getRequestProperty("Authorization")+
                                "\nMETHOD: "+conn.getRequestMethod()+" --> Response: "+conn.getResponseCode()+
                                "\nBODY  : "+JsonShowFormat(body)+
                                "\nRESULT: "+JsonShowFormat(result)+
                                "\n\n============================================================================================="
                        );

                        response = conn.getResponseCode();

                        return result;

                    } catch (Exception e) {
                        // dialog.dismiss();
                        Log.e(TAG, "Upload File Ongoing Error: "+e.getMessage(), e);
                    }
                } else {
                    Log.e(TAG, "Upload File: File not found - "+path);
                }
            } catch (Exception ex) {
                // dialog.dismiss();
                Log.e(TAG, "Upload File Error: "+ex.getMessage(), ex);
            }
            return "";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            try {
                JSONObject data = new JSONObject(values[0]);
                long percent = data.getLong("percent");
                long current_size = data.getLong("current_size");
                long total_size = data.getLong("total_size");
                boolean complete = data.getBoolean("complete");
                long[] progress = new long[]{percent,current_size,total_size};
                listener.onProgressResult(progress,complete);
            } catch (JSONException e){
                Log.e(TAG, "onProgressUpdate: "+e.getMessage(), e);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            listener.onJSONResult(s,response);
            super.onPostExecute(s);
        }
    }

    private static class RequestUploadUri extends AsyncTask<String, String, String> {

        Context context;
        String auth;
        String body;
        String key;
        Uri uri;
        String fileName;
        String link_url;
        upload_bridge listener;

        int response = 0;

        RequestUploadUri(Context context, String body, Uri uri, String fileName, String key, String auth, String link_url, upload_bridge listener) {
            this.context = context;
            this.body = body;
            this.key = key;
            this.auth = auth;
            this.uri = uri;
            this.fileName = fileName;
            this.link_url = link_url;
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String data = "";

            try {
                data = callserver();
            } catch (IOException e) {
                Log.e(TAG, "doInBackground REQUEST PUT: "+e.getMessage(), e);
                try {
                    JSONObject json = new JSONObject(json_request_timeout);
                    data = json.toString();
                } catch (JSONException j){
                    Log.e(TAG, "doInBackground: ", j);
                }
            }

            return data;
        }

        private String callserver() throws IOException {
            try {
                HttpURLConnection conn = null;
                DataOutputStream dos = null;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 4 * 1024 * 1024;

                InputStream inputStream = context.getContentResolver().openInputStream(uri);

                if (inputStream==null){
                    Log.e(TAG, "Upload File: File not found - "+uri.getPath());
                    return "";
                }

                try {
                    String upLoadServerUri = link_url;

                    // open a URL connection to the Servlet
                    URL url = new URL(upLoadServerUri);

                    // Open a HTTP connection to the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Authorization",auth);
                    conn.setRequestProperty("Accept","application/json");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty(key, uri.getPath());

                    dos = new DataOutputStream(conn.getOutputStream());
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    //============= UPLOAD FILE
                    dos.writeBytes("Content-Disposition: form-data; name=\""+key+"\";filename=\"" + fileName + "\"" + lineEnd);

                    dos.writeBytes(lineEnd);

                    // create a buffer of maximum size
                    bytesAvailable = inputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    Log.e(TAG, "CHECK SIZE FILE: "+buffer.length+" B / "+ GetSizeInKB(buffer.length)+" KB / "+ GetSizeInMB(buffer.length)+" MB");

                    // read file and write it into form...
                    bytesRead = inputStream.read(buffer, 0, bufferSize);
                    long total = 0;

                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = inputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = inputStream.read(buffer, 0, bufferSize);
                        Log.e(TAG, "UPLOAD FILE : "+bytesRead+" / "+buffer.length);
                    }

                    dos.writeBytes(lineEnd);

                    //============= POST FORM DATA
                    if (!body.isEmpty()){
                        try {
                            JSONObject json = new JSONObject(body);
                            Iterator<String> keys = json.keys();
                            while (keys.hasNext()){
                                String key = keys.next();
                                String value = String.valueOf(json.get(key));

                                dos.writeBytes(twoHyphens + boundary + lineEnd);
                                dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                                dos.writeBytes("Content-Type: text/plain" + lineEnd);
                                dos.writeBytes(lineEnd);
                                dos.writeBytes(value);
                                dos.writeBytes(lineEnd);
                            }
                        } catch (JSONException e){
                            Log.e(TAG, "callserver: "+e.getMessage(),e );
                        }
                    }

                    //============= CLOSING
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // close the streams //
                    inputStream.close();
                    dos.flush();
                    dos.close();

                    int responseCode=conn.getResponseCode();

                    String result = "";
                    if (responseCode == HttpURLConnection.HTTP_OK){
                        BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                        ByteArrayOutputStream buf = new ByteArrayOutputStream();
                        int result2 = bis.read();
                        while(result2 != -1) {
                            buf.write((byte) result2);
                            result2 = bis.read();
                        }
                        result = buf.toString();
                    } else {
                        BufferedInputStream bis = new BufferedInputStream(conn.getErrorStream());
                        ByteArrayOutputStream buf = new ByteArrayOutputStream();
                        int result2 = bis.read();
                        while(result2 != -1) {
                            buf.write((byte) result2);
                            result2 = bis.read();
                        }
                        result = buf.toString();
                    }

                    Log.e(TAG,"RESULT REQUEST UPLOAD FILE:" +
                            "\n=============================================================================================\n"+
                            "\nURL   : "+conn.getURL().toString()+
                            "\nAUTH  : "+conn.getRequestProperty("Authorization")+
                            "\nMETHOD: "+conn.getRequestMethod()+" --> Response: "+conn.getResponseCode()+
                            "\nBODY  : "+JsonShowFormat(body)+
                            "\nRESULT: "+JsonShowFormat(result)+
                            "\n\n============================================================================================="
                    );

                    response = conn.getResponseCode();

                    return result;

                } catch (Exception e) {
                    // dialog.dismiss();
                    Log.e(TAG, "Upload File Ongoing Error: "+e.getMessage(), e);
                }
            } catch (Exception ex) {
                // dialog.dismiss();
                Log.e(TAG, "Upload File Error: "+ex.getMessage(), ex);
            }
            return "";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            listener.onJSONResult(s,response);
            super.onPostExecute(s);
        }
    }

    //=========================================== DOWNLOAD =========================================
    private static class RequestDownload extends AsyncTask<String, Integer, String> {

        Context context;
        Uri uri;

        String auth;
        String path;
        String filename;
        String format;
        String link_url;
        download_bridge listener;
        int response = HttpURLConnection.HTTP_OK;

        RequestDownload(Context context, @NonNull Uri uri, String auth, String path, String filename, String format, String link_url, download_bridge listener) {
            this.context = context;
            this.uri = uri;
            this.auth = auth;
            this.path = path;
            this.filename = filename;
            this.format = format;
            this.link_url = link_url;
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String data = "";

            try {
                data = callserver();
            } catch (IOException e) {
                Log.e(TAG, "RequestDownload doInBackground: "+e.getMessage(),e );
            }

            return data;
        }

        private String callserver() throws IOException {
            int count = -1;

            try{
                URL url = new URL(link_url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setUseCaches(true);
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod(METHOD_GET);
                conn.setRequestProperty("Authorization",auth);
                conn.setRequestProperty("Content-Type","application/json");

                if (conn.getResponseCode()!= HttpURLConnection.HTTP_OK){
                    Log.e(TAG, "callserver ERROR RESPONSE: "+conn.getResponseCode());
                    return "";
                }

                // progress bar
                int lengthofFile = conn.getContentLength();

                // download file
                InputStream input = new BufferedInputStream(conn.getInputStream());

                // output
                OutputStream output;
                if (uri==null){
                    output = new FileOutputStream(path+ File.separator+filename+format);
                } else {
                    output = context.getContentResolver().openOutputStream(uri);
                }

                byte data[] = new byte[4096];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;

                    // progress loading
                    publishProgress((int) ((total * 100) / lengthofFile),(int)total,lengthofFile);

                    int percent = (int) ((total * 100) / lengthofFile);

                    // writing file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // close stream
                output.close();
                input.close();

                Log.e(TAG,"RESULT REQUEST DOWNLOAD FILE:" +
                        "\n=============================================================================================\n"+
                        "\nURL: "+conn.getURL().toString()+
                        "\nAUTH: "+conn.getRequestProperty("Authorization")+
                        "\nMETHOD: "+conn.getRequestMethod()+" --> Response: "+conn.getResponseCode()+
                        "\nRESULT: "+path+filename+
                        "\n\n============================================================================================="
                );

                // CHECK RESPONSE
                response = conn.getResponseCode();

                if (response != HttpURLConnection.HTTP_OK){
                    BufferedInputStream bis = new BufferedInputStream(conn.getErrorStream());
                    ByteArrayOutputStream buf = new ByteArrayOutputStream();

                    int read = bis.read();
                    while(read != -1) {
                        buf.write((byte) read);
                        read = bis.read();
                    }
                    return buf.toString();
                }

            } catch (Exception e){
                Log.e(TAG, "RequestDownload callserver: "+e.getMessage(), e);
                response = HttpURLConnection.HTTP_CLIENT_TIMEOUT;
            }

            return "";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int[] result = new int[]{values[0],values[1],values[2]};
            listener.onProgressResult(result);
        }

        @Override
        protected void onPostExecute(String s) {
            // CHECK RESPONSE
            if (response == HttpURLConnection.HTTP_OK){
                // get file
                File getfile;
                if (uri== null){
                    getfile = new File(path+ File.separator+filename+format);
                } else {
                    getfile = new File(uri.toString());
                }

                listener.onFileResult(getfile);
            } else {
                listener.OnError(s,response);
            }
            super.onPostExecute(s);
        }
    }

    //============================================ STREAM ==========================================
    private static class RequestStream extends AsyncTask<String, Void, InputStream>{

        Context context;
        String method;

        String link;
        String auth;
        String body;

        int response = 0;
        String data;

        stream_bridge listener;

        public RequestStream(Context context, String method, String link, String auth, String body, stream_bridge listener) {
            this.context = context;
            this.method = method;
            this.link = link;
            this.auth = auth;
            this.body = body;
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream stream = null;

            try {
                stream = SendData();
            } catch (IOException e) {
                Log.e(TAG, "RequestStream doInBackground: "+e.getMessage(), e);
                try {
                    JSONObject json = new JSONObject(json_request_timeout);
                    data = json.toString();
                } catch (JSONException j){
                    Log.e(TAG, "RequestStream doInBackground: "+j.getMessage(), j);
                } catch (NullPointerException j){
                    Log.e(TAG, "RequestStream doInBackground: "+j.getMessage(), j);
                }
            }

            return stream;
        }

        private InputStream SendData() throws IOException {
            URL url = new URL(link);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(true);
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod(method);
            conn.setRequestProperty("Authorization",auth);
            conn.setRequestProperty("Content-Type","application/json");
//            conn.setRequestProperty("Accept","application/json");

            conn.setDoInput(true);
            if (!method.equals(METHOD_GET)){
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(body);

                writer.flush();
                writer.close();
                os.close();
            }

            int responseCode=conn.getResponseCode();
            InputStream stream;
            if (responseCode == HttpURLConnection.HTTP_OK){
                stream = new BufferedInputStream(conn.getInputStream());
                data = "";
            } else {
                stream = new BufferedInputStream(conn.getErrorStream());
                BufferedInputStream bis = new BufferedInputStream(conn.getErrorStream());
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                int result2 = bis.read();
                while(result2 != -1) {
                    buf.write((byte) result2);
                    result2 = bis.read();
                }
                data = buf.toString();
            }

            response = conn.getResponseCode();

            Log.e(TAG,"RESULT REQUEST "+method+":" +
                    "\n=============================================================================================\n"+
                    "\nURL   : "+conn.getURL().toString()+
                    "\nAUTH  : "+conn.getRequestProperty("Authorization")+
                    "\nMETHOD: "+conn.getRequestMethod()+" --> Response: "+conn.getResponseCode()+
                    "\nBODY  : "+JsonShowFormat(body)+
                    "\nRESULT: "+JsonShowFormat(data)+
                    "\n\n=============================================================================================");

            return stream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            super.onPostExecute(inputStream);
            listener.onStreamResult(inputStream,data,response);
        }
    }
    //============================================== RAW ===========================================
    private static class RequestRAW extends AsyncTask<String, String, String> {

        Context context;

        String method;

        HashMap<String,String> headers;
        String link;
        String body;

        int response = 0;

        raw_bridge listener;

        public RequestRAW(@NonNull Context context, String method, String link, HashMap<String,String> headers, String body, raw_bridge listener) {
            this.context = context;
            this.method = method;
            this.link = link;
            this.headers = headers;
            this.body = body;
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() { super.onPreExecute(); }

        @Override
        protected String doInBackground(String... strings) {
            String data = "";

            try {
                data = SendData();
            } catch (IOException e) {
                Log.e(TAG, "RequestServer doInBackground IOException Response("+response+"): "+e.getMessage(), e);
                try {
                    JSONObject json = new JSONObject(json_request_timeout);
                    data = json.toString();
                } catch (JSONException j){
                    Log.e(TAG, "RequestServer doInBackground JSONException Response("+response+"): "+j.getMessage(), j);
                } catch (NullPointerException j){
                    Log.e(TAG, "RequestServer doInBackground NullPointerException Response("+response+"): "+j.getMessage(), j);
                }
            }

            return data;
        }

        private String SendData() throws IOException {
            URL url = new URL(link);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);

            conn.setRequestMethod(method);

            for (String key : headers.keySet()){
                conn.setRequestProperty(key,headers.get(key));
            }

            conn.setDoInput(true);
            if (!method.equals(METHOD_GET)){
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(body);

                writer.flush();
                writer.close();
                os.close();
            }

            response = conn.getResponseCode();

            int responseCode=conn.getResponseCode();
            String result;
            if (responseCode == HttpURLConnection.HTTP_OK){
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                int result2 = bis.read();
                while(result2 != -1) {
                    buf.write((byte) result2);
                    result2 = bis.read();
                }
                result = buf.toString();
            } else {
                BufferedInputStream bis = new BufferedInputStream(conn.getErrorStream());
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                int result2 = bis.read();
                while(result2 != -1) {
                    buf.write((byte) result2);
                    result2 = bis.read();
                }
                result = buf.toString();
            }

            Log.e(TAG,"RESULT REQUEST "+method+":" +
                    "\n=============================================================================================\n"+
                    "\nURL   : "+conn.getURL().toString()+
                    "\nMETHOD: "+conn.getRequestMethod()+" --> Response: "+conn.getResponseCode()+
                    "\nBODY  : "+JsonShowFormat(body)+
                    "\nRESULT: "+JsonShowFormat(result)+
                    "\n\n=============================================================================================");

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            listener.onRawResult(s,response);
        }
    }

    //============================================ LISTENER ========================================
    public interface Listener{
        void OnRequestSuccess(ResponseData response);
        void OnRequestFailed(ResponseData response);
//        void OnRequestSuccess(String string);
//        void OnRequestFailed(String string);
    }

    public interface ServerListener{
        void OnRequestSuccess(ResponseData response);
        void OnRequestFailed(ResponseData response);
    }

    public interface UploadListener{
        void OnUploadSuccess(ResponseData response);
        void OnUploadProgress(int percent, int current_size, int total_size, boolean uploadComplete);
        void OnUploadFailed(ResponseData response);
    }

    public interface DownloadListener {
        void OnDownloadSuccess(File file);
        void OnDownloadProgress(int percent, int current_size, int total_size);
        void OnDownloadFailed(ResponseData response);
    }

    public interface StreamListener{
        void OnResultStream(ResponseData responseData);
        void OnStreamError(ResponseData response);
    }

    public interface RawListener{
        void OnRequestSuccess(ResponseData response);
        void OnRequestFailed(ResponseData response);
    }

    private interface call_bridge{
        void onJSONResult(String string, int response);
    }

    private interface upload_bridge{
        void onJSONResult(String string, int response);
        void onProgressResult(long[] value, boolean uploadComplete);
    }

    private interface download_bridge{
        void onFileResult(File file);
        void onProgressResult(int[] value);
        void OnError(String string, int response);
    }

    private interface stream_bridge{
        void onStreamResult(InputStream is, String data, int response);
    }

    private interface raw_bridge{
        void onRawResult(String data, int response);
    }

    private static String BuildErrorJson(String data, int response){
        JSONObject json = new JSONObject();
        try {
            json.put(JSON_STATUS_CODE,response);
            json.put(JSON_SUCCESS,false);
            json.put(JSON_MESSAGE,message_error_internal_server);
            json.put(JSON_ERROR_STACK_TRACE,new JSONObject(data));
        } catch (JSONException e){
            return json_internal_server_error;
        }
        return json.toString();
    }

    public static String JsonShowFormat(String text){
        StringBuilder json = new StringBuilder();
        String indentString = "";

        for (int i = 0; i < text.length(); i++) {
            char letter = text.charAt(i);
            switch (letter) {
                case '{':
                case '[':
                    json.append("\n" + indentString + letter + "\n");
                    indentString = indentString + "\t";
                    json.append(indentString);
                    break;
                case '}':
                case ']':
                    indentString = indentString.replaceFirst("\t", "");
                    json.append("\n" + indentString + letter);
                    break;
                case ',':
                    json.append(letter + "\n" + indentString);
                    break;

                default:
                    json.append(letter);
                    break;
            }
        }

        return json.toString();
    }

    public static long GetSizeInKB(long length_byte){
        return length_byte / 1024;
    }

    public static long GetSizeInMB(long length_byte){
        return GetSizeInKB(length_byte) / 1024;
    }

    public static long GetSizeInGB(long length_byte){
        return GetSizeInMB(GetSizeInKB(length_byte)) / 1024;
    }

    public static HashMap<String,String> DEFAULT_HEADERS(){
        HashMap<String,String> headers = new HashMap<>();

        headers.put("Content-Type","application/json");
        headers.put("Accept","application/json");

        return headers;
    }

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_PATCH = "PATCH";

    public static final String JSON_STATUS_CODE = "status_code";
    public static final String JSON_SUCCESS = "success";
    public static final String JSON_MESSAGE = "message";
    public static final String JSON_ERROR_STACK_TRACE = "error_stack_trace";
}
