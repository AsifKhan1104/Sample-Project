package com.bookpal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bookpal.R;
import com.bookpal.model.Registration;
import com.bookpal.utility.AppConstants;
import com.bookpal.utility.GPSTracker;
import com.bookpal.utility.Utility;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText mEditTextName, mEditTextMobile, mEditTextEmail, mEditTextPassword, mEditTextLocation;
    private Button mButtonRegister;
    private LinearLayout mLinearLayoutMain;
    private ProgressBar mProgressBar;
    private static final String TAG = "SignUpActivity";
    private GPSTracker gps;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        linkViewId();
        gps = new GPSTracker(this);
        // Get firebase database instance to read / write data
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Get firebase auth object for creating new user
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Utility.setLoggedIn(SignUpActivity.this);
                    // save data in firebase database
                    writeNewUser(user.getUid());

                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(AppConstants.FROM_SIGN_UP, AppConstants.FLAG_YES);
                    startActivity(intent);
                } else {
                    // User is signed out
                    Log.e(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    private void linkViewId() {
        mEditTextName = (EditText) findViewById(R.id.editText_Name);
        mEditTextMobile = (EditText) findViewById(R.id.editText_mobile);
        mEditTextEmail = (EditText) findViewById(R.id.editText_email);
        mEditTextPassword = (EditText) findViewById(R.id.editText_password);
        mEditTextLocation = (EditText) findViewById(R.id.editText_location);
        mButtonRegister = (Button) findViewById(R.id.button_register);
        mLinearLayoutMain = (LinearLayout) findViewById(R.id.linearLayout_Main);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mButtonRegister.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_register:
                if (Utility.isNetworkConnected(this)) {
                    if (checkValidation()) {
                        showProgressBar();

                        mAuth.createUserWithEmailAndPassword(mEditTextEmail.getText().toString(), mEditTextPassword.getText().toString())
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        hideProgressBar();

                                        // If sign in fails, display a message to the user. If sign in succeeds
                                        // the auth state listener will be notified and logic to handle the
                                        // signed in user can be handled in the listener.
                                        if (!task.isSuccessful()) {
                                            Utility.showToastMessage(SignUpActivity.this, task.getException().getMessage());
                                        }
                                    }
                                });
                    } else if (!(mEditTextEmail.getText().length() > 0)) {
                        Utility.showToastMessage(this, "Please enter Email id to complete the registration");
                        YoYo.with(Techniques.Shake)
                                .duration(700)
                                .playOn(mEditTextEmail);
                    } else if (!(mEditTextPassword.getText().length() > 0)) {
                        Utility.showToastMessage(this, "Please enter Password to complete the registration");
                        YoYo.with(Techniques.Shake)
                                .duration(700)
                                .playOn(mEditTextPassword);
                    }
                } else {
                    Utility.showToastMessage(this, getResources().getString(R.string.no_internet_connection));
                }
                break;
        }
    }

    private boolean checkValidation() {
        if (mEditTextEmail.getText().length() > 0 && mEditTextPassword.getText().length() > 0) {
            return true;
        }
        return false;
    }

    // writes new data to firebase database
    private void writeNewUser(String userId) {
        Registration registrationData = new Registration();
        registrationData.setName(mEditTextName.getText().toString());
        registrationData.setUserId(userId);
        registrationData.setMobile(mEditTextMobile.getText().toString());
        registrationData.setEmail(mEditTextEmail.getText().toString());
        registrationData.setPassword(mEditTextPassword.getText().toString());
        registrationData.setLatitude(String.valueOf(gps.getLatitude()));
        registrationData.setLongitude(String.valueOf(gps.getLongitude()));

        mDatabase.child("users").child(userId).child("registration").setValue(registrationData);
    }

    private void showProgressBar() {
        mLinearLayoutMain.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
        mLinearLayoutMain.setVisibility(View.VISIBLE);
    }
}
