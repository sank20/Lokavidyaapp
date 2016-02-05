package com.iitb.mobileict.lokavidya.ui;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class CustomAlarmService extends IntentService {

    public CustomAlarmService() {
        super("CustomAlarmService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            final String broadcastReceiverIntent = intent.getStringExtra("broadcastReceiverIntent");
            final String alarmTime = intent.getStringExtra("alarmTime");
            Log.d("Custom Alarm Service","alarmTime"+alarmTime);
            awakenAfter(alarmTime);
            Intent broadcastintent = new Intent();
            broadcastintent.setAction(RegistrationIntentService.SPLASH_INTENT);
            getApplicationContext().sendBroadcast(broadcastintent);
            Log.d("Custom Alarm Service", "alarmTime done");
        }
    }


    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void awakenAfter(String alarmTime) {
        try {
            Thread.sleep(Integer.parseInt(alarmTime));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
