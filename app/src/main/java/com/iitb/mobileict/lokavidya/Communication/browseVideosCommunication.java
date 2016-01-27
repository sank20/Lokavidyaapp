package com.iitb.mobileict.lokavidya.Communication;



import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;



/**
 * Created by sanket P on 23/1/16.
 */
public class browseVideosCommunication {


    public static final String JSON_AUTH_URL= "http://ruralict.cse.iitb.ac.in:8080/api/authenticate?username=admin&password=admin";

    public static String xAUTH_TOKEN;
    private static JSONArray JsonArray;

    private static void okhttpAuth(){

        Log.i("OKHTTP AUTH", "inside Auth");
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(JSON_AUTH_URL)
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

    public static JSONArray okhttpgetVideoJsonArray(String URL){

        okhttpAuth();
        Log.i("OKHTTP","auth done inside getjson");

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



}
