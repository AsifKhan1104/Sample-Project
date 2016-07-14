package com.bookpal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bookpal.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 11;
    private static final String TAG = "SignInActivity";
    private Button mButton_signUp, mButton_logIn, mButton_google_login;
    private TextView mTextView_fb_login;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        linkViewId();
    }

    private void linkViewId() {
        mButton_signUp = (Button) findViewById(R.id.button_signUp);
        mButton_logIn = (Button) findViewById(R.id.button_logIn);
        mButton_google_login = (Button) findViewById(R.id.button_google);
        mTextView_fb_login = (TextView) findViewById(R.id.textView_fb);

        // on click listeners
        mButton_signUp.setOnClickListener(this);
        mButton_logIn.setOnClickListener(this);
        mButton_google_login.setOnClickListener(this);
        mTextView_fb_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_signUp:
                Intent intent1 = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent1);
                break;
            case R.id.button_logIn:
                Intent intent2 = new Intent(SignInActivity.this, ManualSignInActivity.class);
                startActivity(intent2);
                break;
            case R.id.button_google:
                //signInGoogle();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }
}
