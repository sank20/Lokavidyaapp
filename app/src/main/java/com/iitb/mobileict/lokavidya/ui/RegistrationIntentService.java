package com.iitb.mobileict.lokavidya.ui;

/**
 * Created by SidRama on 15/01/16.
 */
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import com.iitb.mobileict.lokavidya.R;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    String gcmToken, placeId, args;
    String serverURL = "http://192.168.1.134:8080";
    static String SURVEY_INTENT = "Survey";
    static String SPLASH_INTENT = "Splash";

    Context context;

    public RegistrationIntentService() {
        super(TAG);
        Log.e("RIS", "In RegistrationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        placeId = intent.getStringExtra("placeId");
        args = intent.getStringExtra("flag");
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            gcmToken= instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
//            Master.setToken(gcmToken);
            Log.i(TAG, "GCM Registration Token: " + gcmToken);
            sendRegistrationToServer(gcmToken);

            // Subscribe to topic channels
            //   subscribeTopics(token);
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        //  Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        //  LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(String token) {
        JSONObject obj = new JSONObject();
        SharedPreferences sharedPreferences;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        try {
            obj.put("token",token);
            obj.put("number","91"+sharedPreferences.getString("mobilenumber","0000000000"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AsyncT task = new AsyncT(this.getBaseContext());
        task.execute(obj);
        Log.d(TAG,"done uploading");

    }

    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }


    class AsyncT extends AsyncTask<JSONObject, String, Void> {
        Context context;

        AsyncT(Context context) {
            this.context = context;
        }

        protected Void doInBackground(JSONObject... params) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            GetJSON getJson = new GetJSON();
            Log.e("Place ID", placeId);
            Log.e("id Token", sharedPreferences.getString("idToken", "id token"));
            Log.e("gcm Token", gcmToken);
            serverURL += "/app/authenticate?google=true&username=admin&password=admin&googlePlaceId="
                    + placeId + "&affiliation=IITB&idTokenString="
                    + sharedPreferences.getString("idToken", "token")
                    + "&gcmToken=" + gcmToken;

            Log.e("RegisterIntentServ url", serverURL);
            String response = getJson.getJSONFromUrl(serverURL, null, "POST", false, null, null);
            Log.d(TAG, response);
            return null;

        }

        protected void onPostExecute(String message) {
            Log.d("LokaAuthenticationTask", "PostExecute response message:" + message);
            SharedPreferences sharedPref;
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(message);
                sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                if (jsonObject != null) {
                    Intent i = new Intent();
                    if (jsonObject.has("token"))
                    {
                        i.putExtra("arg", "success");
                    }
                    else
                    {
                        i.putExtra("arg", "failure");
                    }
                    if(args == "survey")
                    {
                        i.setAction(SURVEY_INTENT);
                        context.sendBroadcast(i);
                    }
                    else
                    {
                        i.setAction(SPLASH_INTENT);
                        context.sendBroadcast(i);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

class QuickstartPreferences {

    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";

}
