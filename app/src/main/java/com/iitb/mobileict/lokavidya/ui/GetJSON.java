package com.iitb.mobileict.lokavidya.ui;

import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Vishesh on 24-12-2015.
 */


public class GetJSON {
    static InputStream is = null;
    static JSONObject jObj = null;
    HttpURLConnection linkConnection = null;
    String status;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    public GetJSON() {

    }


    public String getJSONFromUrl(String url,JSONObject obj,String method,boolean auth,String emailid,String password) {

        status="exception";

        try {

            URL linkurl = new URL(url);

            linkConnection = (HttpURLConnection) linkurl.openConnection();

            if(auth==true) {
                String basicAuth = "Basic " + new String(Base64.encode((emailid+":"+password).getBytes(), Base64.NO_WRAP));
                linkConnection.setRequestProperty("Authorization", basicAuth);
            }

            linkConnection.setDefaultUseCaches(false);

            if(method.equals("GET"))
            {
                linkConnection.setRequestMethod("GET");
                linkConnection.setRequestProperty("Accept", "application/json");
                linkConnection.setDoInput(true);
            }

            if(method.equals("POST"))
            {
                linkConnection.setRequestMethod("POST");
                linkConnection.setRequestProperty("Content-Type", "application/json");
                linkConnection.setRequestProperty("Accept", "application/json");
                linkConnection.setDoOutput(true);
                linkConnection.setDoInput(true);
                Log.d("Inside GetJSON","JSON 1");
                String o = obj.toString();
                OutputStreamWriter w = new OutputStreamWriter(linkConnection.getOutputStream());
                Log.d("Inside GetJSON","JSON 2");
                w.write(o);
                w.flush();
                w.close();
            }
            Log.d("Inside GetJSON","JSON 3");


            status=String.valueOf(linkConnection.getResponseCode());

            Log.d("Inside GetJSON","Status is "+status);
            if(status.equals("200"))
            {

                Log.d("Inside GetJSON","JSON 4");
                is=linkConnection.getInputStream();
            }
            else
            { status="exception";
                return  status;
            }


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            status="exception";
            return status;
        }catch (IOException e) {
            e.printStackTrace();
            status="exception";
            return status;
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            is.close();
            status = sb.toString();


            try {
                new JSONObject(status);

                return status;
            } catch (Exception e) {

                status="exception";
                return status;
            }


        } catch (Exception e) {


        }
        finally {
            if (linkConnection != null) {
                linkConnection.disconnect();
            }
        }

        return status;
    }
    public static Map toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }


    public static Map toMap(JSONArray array) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        for(int i=0; i<array.length();i++){

            map.put(array.getJSONObject(i).getString("name"),array.getJSONObject(i).getString("unitRate") );

        }
        return map;
    }


    public static List toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }


    public static Map categoryMap(JSONObject object) throws JSONException
    {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext())
        {

            String key = keysItr.next();
            Object category =object.get(key);
            if(category instanceof JSONArray)
            {
                for(int i=0; i<((JSONArray)category).length();i++)
                {
                    map.put(((JSONArray)category).getJSONObject(i).getString("name"),((JSONArray)category).getJSONObject(i).getString("unitRate") );
                }
            }

        }
        return map;
    }


}
