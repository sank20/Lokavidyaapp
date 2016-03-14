package com.iitb.mobileict.lokavidya.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.iitb.mobileict.lokavidya.R;
import com.iitb.mobileict.lokavidya.services.SignInService;

public class LokavidyaAuthenticationActivity extends FragmentActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {


    enum AuthenticationState{
        LOGGED_OUT, SIGNIN,SIGNUP
    }
    boolean fromUpload;
    private Button skipButton;

    AuthenticationState state;
    private static final String TAG = "LokavidyaAuthenticationActivity";
    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;
    SignInButton signInButton;
    SignInButton signUpButton;
    GoogleApiClient mGoogleApiClient;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {




        Intent i= getIntent();
        fromUpload=i.getBooleanExtra("fromUpload",false);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.edit().putBoolean("Skip",false).commit();
        if(!sharedPreferences.getString("idToken","N/A").equals("N/A")&&sharedPreferences.getString("lokavidyaToken","N/A").equals("N/A"))
        {
            //Calling Projects Activity if already signed in
            Intent projectsIntent= new Intent(LokavidyaAuthenticationActivity.this,Projects.class);
            Log.d(TAG,"Signed In.Calling Projects Activity");
            startActivity(projectsIntent);
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lokavidya_authentication);
        signInButton =(SignInButton)findViewById(R.id.sign_in_button);
        signUpButton =(SignInButton)findViewById(R.id.sign_up_button);
        skipButton= (Button) findViewById(R.id.skipbutton);
        skipButton.setOnClickListener(this);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        signInButton.setScopes(gso.getScopeArray());
        signInButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        setGooglePlusButtonText(signInButton,"Sign In with Google");
        setGooglePlusButtonText(signUpButton,"Sign Up with Google");
        registerReceiver(signInBroadcastReceiver, new IntentFilter(TAG));
        skipButton= (Button) findViewById(R.id.skipbutton);


    }


    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {

        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                state=AuthenticationState.SIGNIN;
                signIn();
                break;
            case R.id.sign_up_button:
                state=AuthenticationState.SIGNUP;
                signUp();
                break;
            case R.id.skipbutton:
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LokavidyaAuthenticationActivity.this);
                sharedPreferences.edit().putBoolean("Skip",true).commit();
                Intent i= new Intent(this,Projects.class);
                startActivity(i);
                finish();
        }
    }

    private void signUp() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(),"Connection Error",Toast.LENGTH_LONG).show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN &&state ==AuthenticationState.SIGNIN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);

        }
        else if(requestCode == RC_SIGN_IN &&state ==AuthenticationState.SIGNUP)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignUpResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.i(TAG, "Result:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.d(TAG, "Inside Sign In Result");
            String idToken = result.getSignInAccount().getIdToken();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LokavidyaAuthenticationActivity.this);
            sharedPreferences.edit().putString("idToken", idToken).commit();
            Intent signInServiceIntent= new Intent(LokavidyaAuthenticationActivity.this,SignInService.class);
            signInServiceIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startService(signInServiceIntent);
            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    private void handleSignUpResult(GoogleSignInResult result) {
        Log.i(TAG, "Result:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.d(TAG, "Inside Sign Up Result");
            String idToken = result.getSignInAccount().getIdToken();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LokavidyaAuthenticationActivity.this);
            sharedPreferences.edit().putString("idToken", idToken).commit();
            Intent projectsIntent= new Intent(LokavidyaAuthenticationActivity.this,SurveyActivity.class);
            projectsIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(projectsIntent);
            finish();
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {

        } else {

        }
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }

    private final BroadcastReceiver signInBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Inside Broadcast Reciever IdToken");
            Log.d(TAG, "Registered" + intent.getBooleanExtra("registered", false));

            if(intent.getBooleanExtra("connectionError",false))
            {
                Toast.makeText(getApplicationContext(),"Error in connecting to the server",Toast.LENGTH_LONG).show();
            }
            if(!intent.getBooleanExtra("registered", false)|| !intent.getStringExtra("arg").equals("success"))
            {
                Toast.makeText(getApplicationContext(),"You have to Sign Up first.",Toast.LENGTH_LONG).show();
            }
            else {
                if(fromUpload){
                    Log.i("back to signin","You have come here from the upload activity");
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LokavidyaAuthenticationActivity.this);
                    sharedPreferences.edit().putString("lokavidyaToken", intent.getStringExtra("lokavidyaToken")).commit();
                    finish();
                }else {
                    Log.i("signin","the real signin process/not guest");
                    Intent projectsIntent = new Intent(LokavidyaAuthenticationActivity.this, Projects.class);
                    projectsIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(projectsIntent);
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LokavidyaAuthenticationActivity.this);
                    sharedPreferences.edit().putString("lokavidyaToken", intent.getStringExtra("lokavidyaToken")).commit();
                    finish();
                }
            }
            }
    };


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(signInBroadcastReceiver);
    }

}
