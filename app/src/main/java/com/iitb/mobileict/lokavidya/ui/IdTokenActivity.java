package com.iitb.mobileict.lokavidya.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.iitb.mobileict.lokavidya.R;

import org.json.JSONException;
import org.json.JSONObject;

public class IdTokenActivity  extends FragmentActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener{
    private static final String TAG = "IdTokenActivity";
    private static final int RC_GET_TOKEN = 9002;

    private GoogleApiClient mGoogleApiClient;
    private TextView mIdTokenTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        sharedPref.edit().remove("token");
        sharedPref.edit().remove("timeout");
        setContentView(R.layout.activity_id_token);

        // Views
        mIdTokenTextView = (TextView) findViewById(R.id.detail);

        // Button click listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);

        // For sample only: make sure there is a valid server client ID.
        validateServerClientID();

        // [START configure_signin]
        // Request only the user's ID token, which can be used to identify the
        // user securely to your backend. This will contain the user's basic
        // profile (name, profile picture URL, etc) so you should not need to
        // make an additional call to personalize your application.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        // [END configure_signin]

        // Build GoogleAPIClient with the Google Sign-In API and the above options.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void getIdToken() {
        // Show an account picker to let the user choose a Google account from the device.
        // If the GoogleSignInOptions only asks for IDToken and/or profile and/or email then no
        // consent screen will be shown here.
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GET_TOKEN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.d(TAG, "signOut:onResult:" + status);
                        updateUI(false);
                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.d(TAG, "revokeAccess:onResult:" + status);
                        updateUI(false);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GET_TOKEN) {
            // [START get_id_token]
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d(TAG, "onActivityResult:GET_TOKEN:success:" + result.getStatus().isSuccess());

            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                String idToken = acct.getIdToken();

                // Show signed-in UI.
                Context context = this.getApplicationContext();
                Log.d(TAG, "idToken:" + idToken);
                updateUI(true);
                LokaAuthenticationTask lokaAuthenticationTask = new LokaAuthenticationTask(context);
                JSONObject authParams = new JSONObject();
                try {
                    authParams.put("google", true);
                    authParams.put("username", "admin");
                    authParams.put("password", "admin");
                    authParams.put("userIdToken", idToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String url = "http://192.168.1.134:8080/api/authenticate?google=true&username=admin&password=admin&idTokenString="+idToken;
                lokaAuthenticationTask.execute(new String[]{url});
                // TODO(user): send token to server and validate server-side
            } else {
                // Show signed-out UI.
                updateUI(false);
            }
            // [END get_id_token]
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            ((TextView) findViewById(R.id.status)).setText("Signed In");

            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            ((TextView) findViewById(R.id.status)).setText("Signed Out");
            mIdTokenTextView.setText(getString(R.string.id_token_fmt, "null"));

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    /**
     * Validates that there is a reasonable server client ID in strings.xml, this is only needed
     * to make sure users of this sample follow the README.
     */
    private void validateServerClientID() {
        String serverClientId = getString(R.string.server_client_id);
        String suffix = ".apps.googleusercontent.com";
        if (!serverClientId.trim().endsWith(suffix)) {
            String message = "Invalid server client ID in strings.xml, must end with " + suffix;

            Log.w(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                getIdToken();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.disconnect_button:
                revokeAccess();
                break;
        }
    }
}

//---------------------------------------------------------------------------------------------

//-----------------------Class for Authenticating -------------------------------------

/****************

 ***************************************************/
class LokaAuthenticationTask extends AsyncTask<String, String, String> {
    String response;

    Context context;
    LokaAuthenticationTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        Log.e("Lokavidya Auth", "Pre execute");

    }

    @Override
    protected String doInBackground(String... kurl) {
        GetJSON getJSON = new GetJSON();
        Log.d("Async Task ", kurl[0]);
        response = getJSON.getJSONFromUrl(kurl[0], new JSONObject(), "POST", false, "", "");
        Log.d("Lokavidya Auth", "Token is " + response);
        return response;
    }

    @Override
    protected void onPostExecute(String message) {
        Log.d("LokaAuthenticationTask", "PostExecute response message:" + message);
        Intent i = new Intent( context,Projects.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        SharedPreferences sharedPref;
        JSONObject jsonObject= null;
        try {
            jsonObject = new JSONObject(message);
            sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            if(jsonObject!=null) {
                sharedPref.edit().putString("expires",jsonObject.getString("expires")).putString("token", jsonObject.getString("token")).commit();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        context.startActivity(i);
    }
}
//---------------------------------------------------------------------------------------------
