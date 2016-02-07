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
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(!sharedPreferences.getString("idToken","N/A").equals("N/A")&&sharedPreferences.getString("lokavidyaToken","N/A").equals("N/A"))
        {
            //Calling Projects Activity if already signed in
            Intent projectsIntent= new Intent(LokavidyaAuthenticationActivity.this,Projects.class);
            Log.d(TAG,"Signed In.Calling Projects Activity");
            startActivity(projectsIntent);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lokavidya_authentication);
        signInButton =(SignInButton)findViewById(R.id.sign_in_button);
        signUpButton =(SignInButton)findViewById(R.id.sign_up_button);

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
        setGooglePlusButtonText(signInButton,"Sign In");
        setGooglePlusButtonText(signUpButton,"Sign Up");
        registerReceiver(signInBroadcastReceiver, new IntentFilter(TAG));

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
            Log.d(TAG,"Inside Broadcast Reciever IdToken");
            Log.d(TAG,"Registered"+intent.getBooleanExtra("registered", false));

            if(intent.getBooleanExtra("connectionError",false))
            {
                Toast.makeText(getApplicationContext(),"Error in connecting to the server",Toast.LENGTH_LONG).show();
            }
            if(!intent.getBooleanExtra("registered", false)|| !intent.getStringExtra("arg").equals("success"))
            {
                Toast.makeText(getApplicationContext(),"You have to Sign Up first.",Toast.LENGTH_LONG).show();
            }
            else {
                Intent projectsIntent= new Intent(LokavidyaAuthenticationActivity.this,Projects.class);
                startActivity(projectsIntent);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LokavidyaAuthenticationActivity.this);
                sharedPreferences.edit().putString("lokavidyaToken", intent.getStringExtra("lokavidyaToken")).commit();
                finish();
            }
            }
    };

}
