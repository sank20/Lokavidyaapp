package com.iitb.mobileict.lokavidya.Communication;



import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;


/**
 * Created by sanket P on 23/1/16.
 */
public class postmanCommunication {


    public static String idTokenString;
    public static final String JSON_AUTH_URL= "http://"+Settings.serverURL+"/api/authenticate?username=admin&password=admin&google=true&idTokenString=";

    public static String xAUTH_TOKEN;
    private static JSONArray JsonArray;

    private static void okhttpAuth(){

        if(xAUTH_TOKEN!=null)
        {
            return;
        }


        Log.i("OKHTTP AUTH", "inside Auth" +JSON_AUTH_URL+idTokenString);

        OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(JSON_AUTH_URL + idTokenString)
                    .post(null)
                    .addHeader("cache-control", "no-cache")
                    .addHeader("postman-token", "164e22d7-22ee-68a7-8e95-0d354064a9d1")
                    .build();

        Log.i("OKHTTP AUTH", "req assigned");

        try {
            Response response = client.newCall(request).execute();
            Log.i("OKHTTP AUTH", "execute finished");

            String json= response.body().string();
            Log.i("OKHTTPauth are u json?",json);
            JSONObject authjson= new JSONObject(json);
            xAUTH_TOKEN= authjson.getString("token");
            Log.i("OKHTTP AUTH", "Success");
        } catch (IOException e) {

            e.printStackTrace();
        }catch (JSONException j){
            j.printStackTrace();
        }


    }

    public static JSONArray okhttpgetVideoJsonArray(String URL,String xtoken){

       //okhttpAuth();
        Log.i("OKHTTP","auth done inside getjson"+URL);
        xAUTH_TOKEN = xtoken;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URL)
                .get()
                .addHeader("x-auth-token", xAUTH_TOKEN)
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "7397dcc6-b813-bae2-9157-64c796b02427")
                .build();

        Log.i("Okhttp request","done");
        try {
            Response response = client.newCall(request).execute();
            JsonArray = new JSONArray(response.body().string());
            Log.i("OKHTTP","jsonarray received");
        } catch (IOException e) {
            e.printStackTrace();
        }catch (JSONException j){
            j.printStackTrace();
        }
        return JsonArray;
    }


    public static JSONObject okhttpgetTutorial(String URL,String id,String xtoken){
        //okhttpAuth();
        Log.i("OKHTTP","auth done inside getjson"+URL);
        xAUTH_TOKEN = xtoken;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL+"/"+id)
                .get()
                .addHeader("x-auth-token", xAUTH_TOKEN)
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "7397dcc6-b813-bae2-9157-64c796b02427")
                .build();
        JSONObject jsonObject=null;
        Log.i("Okhttp request", "done");
        try {
            Response response = client.newCall(request).execute();

            jsonObject = new JSONObject(response.body().string());
            //JsonArray = new JSONArray(response.body().string());
            Log.i("OKHTTP", "jsonarray received");
        } catch (IOException e) {
            e.printStackTrace();
        }catch (JSONException j){
            j.printStackTrace();
        }
        return jsonObject;
    }


   /* public static void okhttpUpload(HashMap<String,String> info,String URL){
        okhttpAuth();// authorize
        OkHttpClient client = new OkHttpClient();

        File zip= new File (info.get("file"));
        MediaType mediaType = MediaType.parse("multipart/form-data; boundary=---011000010111000001101001");
        RequestBody body = RequestBody.create(mediaType, "-----011000010111000001101001\r\nContent-Disposition: form-data; name=\"tutorialName\"\r\n\r\n"+info.get("name")+"\r\n-----011000010111000001101001\r\nContent-Disposition: form-data; name=\"tutorialDescription\"\r\n\r\n"+info.get("description")+"\r\n-----011000010111000001101001\r\nContent-Disposition: form-data; name=\"language\"\r\n\r\n"+info.get("language")+"\r\n-----011000010111000001101001\r\nContent-Disposition: form-data; name=\"categoryId\"\r\n\r\n"+info.get("categoryId")+"\r\n-----011000010111000001101001\r\nContent-Disposition: form-data; name=\"keywords\"\r\n\r\n"+info.get("keywords")+"\r\n-----011000010111000001101001\r\nContent-Disposition: form-data; name=\"file\"; filename=\""+ zip +"\"\r\nContent-Type: application/zip\r\n\r\n\r\n-----011000010111000001101001--");
        Request request = new Request.Builder()
                .url(URL) //http://192.168.1.134:8080/api/tutorials/upload
                .post(body)
                .addHeader("content-type", "multipart/form-data; boundary=---011000010111000001101001")
                .addHeader("x-auth-token", xAUTH_TOKEN)
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "a703e104-a40f-e187-ad89-501772041d5c")
                .build();

        try {
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/

    public static Boolean okhttpUpload( HashMap<String,String> info, String serverURL,String xtoken) {
        //okhttpAuth();// authorize
        xAUTH_TOKEN=xtoken;
        OkHttpClient client = new OkHttpClient();
        File file= new File (info.get("file"));
        Log.d("Http upload XAUTH",xAUTH_TOKEN);
        try {

            RequestBody requestBody = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addFormDataPart("file", file.getName(),
                            RequestBody.create(MediaType.parse("application/zip"), file))
                    .addFormDataPart("tutorialName", info.get("name"))
                    .addFormDataPart("tutorialDescription", info.get("description"))
                    .addFormDataPart("language", info.get("language"))
                    .addFormDataPart("categoryId", info.get("categoryId"))
                    .addFormDataPart("keywords", info.get("keywords"))
                    .build();

            Request request = new Request.Builder()
                    .url(serverURL)
                    .post(requestBody)
                    .addHeader("x-auth-token", xAUTH_TOKEN)
                    .build();

            client.newCall(request).execute();

            return true;
        } catch (Exception ex) {
            // Handle the error
        }
        return false;
    }



}
