package com.iitb.mobileict.lokavidya.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;

import com.iitb.mobileict.lokavidya.R;

public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 7000;



    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Stas","sadadsda");
            Intent i = new Intent(SplashScreen.this, Projects.class);
            startActivity(i);
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        if(getActionBar()!=null)
            getActionBar().hide();
        setContentView(R.layout.activity_splash);
        IntentFilter intentFilter = new IntentFilter(RegistrationIntentService.SPLASH_INTENT);
        registerReceiver(mIntentReceiver, intentFilter);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        Boolean b=  sharedPreferences.contains("idToken");
        Log.d("Splash Screen", b.toString());
        if(!b) {
            Log.d("Splash Screen", "Calling IdToken Activity");
            Intent intent = new Intent(SplashScreen.this,IdTokenActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            Log.d("Splash Screen", "Calling Projects Activity");
            Intent registrationintent = new Intent(SplashScreen.this, RegistrationIntentService.class);
            registrationintent.putExtra("flag", "splash");
            startService(registrationintent);
            Intent intent = new Intent(SplashScreen.this,IdTokenActivity.class);
            startActivity(intent);
            finish();
        }
        if(isNetworkAvailable())
        {
            Log.d("Splash Screen", "Calling RegistrationIntentService Activity");
            Intent intent = new Intent(SplashScreen.this, RegistrationIntentService.class);
            intent.putExtra("flag", "splash");
            startService(intent);
        }
        Intent alarmIntent = new Intent(SplashScreen.this,CustomAlarmService.class);
        alarmIntent.putExtra("broadcastReceiverIntent","SplashScreen");
        alarmIntent.putExtra("alarmTime", "10000");
        startService(alarmIntent);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(mIntentReceiver);
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}