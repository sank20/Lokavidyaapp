package com.iitb.mobileict.lokavidya.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.iitb.mobileict.lokavidya.Communication.Settings;
import com.iitb.mobileict.lokavidya.R;
import com.iitb.mobileict.lokavidya.ui.GetJSON;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SignInService extends IntentService {

    public SignInService() {
        super("SignInService");

    }

    private static final String TAG = "SignInService";
    private static final String[] TOPICS = {"global"};
    String gcmToken, placeId, args;
    String serverURL = "http://"+ Settings.serverURL;
    static String SURVEY_INTENT = "com.iitb.mobileict.lokavidya.ui.SurveyActivity";
    static String SPLASH_INTENT = "com.iitb.mobileict.lokavidya.ui.SplashScreen";
    static String ID_TOKEN_INTENT="com.iitb.mobileict.lokavidya.ui.IdTokenActivity";

    Context context;

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG,"Inside Registration Service");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        placeId = intent.getStringExtra("placeId");

        args = intent.getStringExtra("flag");
        Log.d(TAG,"Args are :"+args);
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


    class AsyncT extends AsyncTask<JSONObject, String, String> {
        Context context;

        AsyncT(Context context) {
            this.context = context;
        }

        protected String doInBackground(JSONObject... params) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            GetJSON getJson = new GetJSON();
            Log.d(TAG,"Printing idToken: "+sharedPreferences.getString("idToken", ""));
            Log.e("Place ID", "PlaceId :"+placeId);
            Log.e("id Token", sharedPreferences.getString("idToken", "id token"));
            Log.e("gcm Token", gcmToken);
            serverURL += "/api/authenticate?google=true&username=admin&password=admin&idTokenString="
                    + sharedPreferences.getString("idToken", "");
            Log.e("RegisterIntentServerUrl", serverURL);
            String response = getJson.getJSONFromUrl(serverURL, new JSONObject(), "POST", false, null, null);
            Log.d(TAG, response);
            return response;

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
                        Log.d(TAG, jsonObject.getString("token"));
                        Log.d(TAG, "Storing token in shared Preferences : " + jsonObject.getString("token"));
                        sharedPref.edit().putString("token",jsonObject.getString("token")).commit();
                        String email = jsonObject.getString("token").split(":")[0];
                        Log.d(TAG, "Storing emailid in shared Preferences : " + email);
                        sharedPref.edit().putString("emailid",email).commit();
                        Log.d(TAG,sharedPref.getString("idToken", ""));
                        i.putExtra("arg", "success");
                    }
                    else
                    {
                        i.putExtra("arg", "failure");
                    }
                    i.setAction("LokavidyaAuthenticationActivity");
                    i.putExtra("registered",true);
                    context.sendBroadcast(i);

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