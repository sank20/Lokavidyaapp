package com.iitb.mobileict.lokavidya.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.iitb.mobileict.lokavidya.R;


public class SurveyActivity extends AppCompatActivity implements PlaceSelectionListener, OnConnectionFailedListener {


    private static final String TAG = "SurveyActivity";

    private GoogleApiClient mGoogleApiClient;
    PlaceAutocompleteFragment autocompleteFragment;
    EditText contactNumber;
    EditText affiliation;
    Button done;
    String survey = "Survey", placeId;
    ActionBar actionBar;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    ProgressDialog progressDialog;


    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"Inside Broadcast Reciever Survey");
            if(intent.getAction().equals(RegistrationIntentService.SURVEY_INTENT)) {
                progressDialog.dismiss();
                if(intent.getStringExtra("arg").equals("success"))
                {
                    startActivity(new Intent(SurveyActivity.this, Projects.class));
                    finish();
                }
                else
                {
                    startActivity(new Intent(SurveyActivity.this, IdTokenActivity.class));
                    finish();
                }
               // finish();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(survey);
        actionBar.show();

        // Retrieve the PlaceAutocompleteFragment.
        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        // Register a listener to receive callbacks when a place has been selected or an error has
        // occurred.
        autocompleteFragment.setOnPlaceSelectedListener(this);
        autocompleteFragment.setHint("Where are you from?");


        IntentFilter intentFilter = new IntentFilter(RegistrationIntentService.SURVEY_INTENT);
        registerReceiver(mIntentReceiver,intentFilter );



        
//        LocalBroadcastManager.getInstance(this).registerReceiver(mIntentReceiver,
//                new IntentFilter(RegistrationIntentService.SURVEY_INTENT));
        //Variable Definitions
        contactNumber = (EditText) findViewById(R.id.edit_contact);
        affiliation = (EditText) findViewById(R.id.edit_affiliation);
        done = (Button) findViewById(R.id.button_done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Inside Done On Click");
                try {
                    if (true) //TODO validation
                    {
                        String number = contactNumber.getText().toString();
                        if (number.length() != 10) {
                            contactNumber.setError("Please enter a valid phone number");
                            contactNumber.setFocusable(true);
                        } else if(number.length()==10){
                            progressDialog = new ProgressDialog(SurveyActivity.this);
                            progressDialog.show();
                            progressDialog.setCancelable(true);
                            Intent intent = new Intent(SurveyActivity.this, RegistrationIntentService.class);
                            intent.putExtra("placeId", placeId);
                            Log.d(TAG,contactNumber.getText().toString());
                            intent.putExtra("contactNo",contactNumber.getText().toString());
                            intent.putExtra("flag", "survey");
                            if (checkPlayServices()) {
                                Log.d(TAG, "Starting Intent to the Service.");
                                startService(intent);
                            }
                        }
                    }
                } catch (Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(SurveyActivity.this, "something went wrong. Please check your internet connectivity and try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .addApi(AppIndex.API).build();

    }

    private boolean checkPlayServices()
    {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (apiAvailability.isUserResolvableError(resultCode))
            {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else
            {
                Log.i("Splash screen", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    // TODO: Please implement GoogleApiClient.OnConnectionFailedListener to
    // handle connection failures.

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        autocompleteFragment.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onPlaceSelected(Place place) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString("googleid", place.getId());

        placeId = place.getId().toString();
        sharedPreferences.edit().putString("placeId", placeId).commit();

        Log.i("Description", "Place Selected: " + place.getName() + "---------" +
                place.getAddress() + "----------" + place.getLatLng() + "-------" + placeId);
    }

    @Override
    public void onError(Status status) {

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(mIntentReceiver);
    }

}
