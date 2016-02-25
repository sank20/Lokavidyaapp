package com.iitb.mobileict.lokavidya.ui;


import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.iitb.mobileict.lokavidya.Communication.postmanCommunication;
import com.iitb.mobileict.lokavidya.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class FeedbackActivity extends AppCompatActivity {
    AppCompatSpinner spinnerFeedbackType;
    ArrayList<String> arrayListFeedbackContent;
    EditText editTextDescription;
    Button buttonSubmitFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);


        spinnerFeedbackType = (AppCompatSpinner) findViewById(R.id.spinner_feedback);

        arrayListFeedbackContent = new ArrayList<String>();
        arrayListFeedbackContent.add("Select One");
        arrayListFeedbackContent.add("Feedback");
        arrayListFeedbackContent.add("Complaint");
        arrayListFeedbackContent.add("Bug Report");
        arrayListFeedbackContent.add("Suggestions");

        ArrayAdapter<String> arrayAdapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayListFeedbackContent);
        arrayAdapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFeedbackType.setAdapter(arrayAdapterSpinner);

        editTextDescription = (EditText) findViewById(R.id.editText_feedback_description);

        buttonSubmitFeedback = (Button) findViewById(R.id.button_submit_feedback);
        buttonSubmitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(FeedbackActivity.this);
                if (spinnerFeedbackType.getSelectedItem().toString().trim().equals("Select One")) {
                    Toast.makeText(FeedbackActivity.this, "Please choose one feedback type!", Toast.LENGTH_SHORT).show();

                } else if (editTextDescription.getText().toString().trim().isEmpty()) {
                    Toast.makeText(FeedbackActivity.this, "Please fill in the description!", Toast.LENGTH_SHORT).show();
                } else {

                    //Code for RAM Usage
                    ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                    ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                    activityManager.getMemoryInfo(mi);
                    final long availableMegs = mi.availMem / 1048576L;
                    JSONObject jsonObject = new JSONObject();
                    boolean isAnonymous=sharedPreferences.getBoolean("Skip",false) ;
                    try {
                        jsonObject.put("category", spinnerFeedbackType.getSelectedItem().toString());
                        JSONObject messagedescription = new JSONObject();

                        messagedescription.put("Spinner Feedback Type",spinnerFeedbackType.getSelectedItem().toString());
                        messagedescription.put("Description", editTextDescription.getText().toString().trim());
                        messagedescription.put("Brand", Build.BRAND);
                        messagedescription.put("Device", Build.DEVICE);
                        messagedescription.put("Board", Build.BOARD);
                        messagedescription.put("Hardware", Build.HARDWARE);
                        messagedescription.put("Host", Build.HOST);
                        messagedescription.put("Manufacturer", Build.MANUFACTURER);
                        messagedescription.put("Model", Build.MODEL);
                        messagedescription.put("Product", Build.PRODUCT);
                        messagedescription.put("User", Build.USER);
                        messagedescription.put("Serial Number", Build.SERIAL);
                        messagedescription.put("Time", Build.TIME);
                        messagedescription.put("Unknown Values", Build.UNKNOWN);
                        messagedescription.put("Type", Build.TYPE);
                        messagedescription.put("CPU Usage", readUsage());
                        messagedescription.put("RAM Usage", availableMegs);
                      //  messagedescription.put("Base OS",Build.VERSION.BASE_OS);
                        messagedescription.put("SDK",Build.VERSION.SDK_INT);
                        messagedescription.put("Codename",Build.VERSION.CODENAME);

                        jsonObject.put("messagedescription", messagedescription.toString());
                        jsonObject.put("anonymous",isAnonymous);
                        jsonObject.put("userlogin",sharedPreferences.getString("emailid",null));
                        jsonObject.put("platform","android");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i("Feedback Data", jsonObject.toString());

                    //TODO

                    new sendFeedbackTask(getApplicationContext()).execute(jsonObject.toString(),sharedPreferences.getString("token", "NA"));
                }

            }
        });
    }

    //Code for fetching the CPU Usage
    private float readUsage() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();

            String[] toks = load.split(" +");  // Split on one or more spaces

            long idle1 = Long.parseLong(toks[4]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            try {
                Thread.sleep(360);
            } catch (Exception e) {
            }

            reader.seek(0);
            load = reader.readLine();
            reader.close();

            toks = load.split(" +");

            long idle2 = Long.parseLong(toks[4]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            return (float) (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    class sendFeedbackTask extends AsyncTask<String, Void, Void> {

        Context context;

        sendFeedbackTask(Context context) {
            this.context = context;
        }


        @Override
        protected Void doInBackground(String... params) {
            System.out.println("token sent:"+params[1]);
            postmanCommunication.okhttpFeedback(params[0], params[1]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(context, "Thank You for your " + spinnerFeedbackType.getSelectedItem().toString() + "! We will act on it shortly!", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}