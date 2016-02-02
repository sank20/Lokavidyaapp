package com.iitb.mobileict.lokavidya.ui;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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



public class SurveyActivity extends FragmentActivity implements PlaceSelectionListener, OnConnectionFailedListener {
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
            if(intent.getAction().equals(RegistrationIntentService.SURVEY_INTENT)) {
                progressDialog.dismiss();
                if(intent.getStringExtra("arg").equals("success"))
                {
                    startActivity(new Intent(SurveyActivity.this, Projects.class));
                }
                else
                {
                    startActivity(new Intent(SurveyActivity.this, IdTokenActivity.class));
                }
                finish();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mIntentReceiver,
                new IntentFilter(RegistrationIntentService.SURVEY_INTENT));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(survey);
        actionBar.show();

        // Retrieve the PlaceAutocompleteFragment.
        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        // Register a listener to receive callbacks when a place has been selected or an error has
        // occurred.
        autocompleteFragment.setOnPlaceSelectedListener(this);
        autocompleteFragment.setHint("Where are you from?");

        //Variable Definitions
        contactNumber = (EditText) findViewById(R.id.edit_contact);
        affiliation = (EditText) findViewById(R.id.edit_affiliation);
        done = (Button) findViewById(R.id.button_done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(true) //TODO validation
                {
                    progressDialog = new ProgressDialog(SurveyActivity.this);
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                    Intent intent = new Intent(SurveyActivity.this, RegistrationIntentService.class);
                    intent.putExtra("placeId", placeId);
                    intent.putExtra("flag", "survey");
                    if (checkPlayServices())
                    {
                        startService(intent);
                    }
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

//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        mGoogleApiClient.connect();
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Survey Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app deep link URI is correct.
//                Uri.parse("android-app://com.iitb.mobileict.lokavidya.ui.survey/http/host/path")
//        );
//        AppIndex.AppIndexApi.start(mGoogleApiClient, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Survey Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app deep link URI is correct.
//                Uri.parse("android-app://com.example.melroy.survey/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(mGoogleApiClient, viewAction);
//        mGoogleApiClient.disconnect();
    }

}
