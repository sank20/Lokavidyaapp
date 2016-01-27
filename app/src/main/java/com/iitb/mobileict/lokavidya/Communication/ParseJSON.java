package com.iitb.mobileict.lokavidya.Communication;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sanket P on 23/1/16.
 */
public class ParseJSON {
    public static String[] ids;
    public static String[] names;
    public static String[] emails;

    //public static final String JSON_ARRAY = "result";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";

    private static JSONArray array ;
    private static JSONObject object;

    private String json;

    /*public ParseJSON(String json){
        this.json = json;
    }*/

    /*protected void parseJSON(){
        JSONObject jsonObject=null;
        try {
          //  jsonObject = new JSONObject(json);
           // users = jsonObject.getJSONArray(JSON_ARRAY);
            JSONArray array= new JSONArray(json);

            ids = new String[users.length()];
            names = new String[users.length()];
            emails = new String[users.length()];

            for(int i=0;i<users.length();i++){
                JSONObject jo = users.getJSONObject(i);
                ids[i] = jo.getString(KEY_ID);
                names[i] = jo.getString(KEY_NAME);
                emails[i] = jo.getString(KEY_EMAIL);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/
    public static JSONArray getJsonArray(String json){
        try {
             array = new JSONArray(json);


        }
        catch (JSONException je){
            je.printStackTrace();
        }
        return array;
    }

    public static JSONObject getJSONObject(String json){
        try {
            object= new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;

    }


}

