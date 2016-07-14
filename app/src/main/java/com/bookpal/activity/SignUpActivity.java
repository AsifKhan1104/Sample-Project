package com.bookpal.activity;

import android.location.Location;
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
import com.bookpal.utility.GPSTracker;
import com.bookpal.utility.Utility;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText mEditTextFullName, mEditTextMobile, mEditTextEmail, mEditTextPassword, mEditTextLocation;
    private Button mButtonRegister;
    private LinearLayout mLinearLayoutMain;
    private ProgressBar mProgressBar;
    private static final String TAG = "SignUpActivity";
    private GPSTracker gps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        linkViewId();
        gps = new GPSTracker(this);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.e(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.e(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    private void linkViewId() {
        mEditTextFullName = (EditText) findViewById(R.id.editText_fullName);
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
                if (checkValidation()) {
                    mLinearLayoutMain.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);

                    mAuth.createUserWithEmailAndPassword(mEditTextEmail.getText().toString(), mEditTextPassword.getText().toString())
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    mProgressBar.setVisibility(View.GONE);
                                    mLinearLayoutMain.setVisibility(View.VISIBLE);

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
                break;
        }
    }

    private boolean checkValidation() {
        if (mEditTextEmail.getText().length() > 0 && mEditTextPassword.getText().length() > 0) {
            return true;
        }
        return false;
    }
}
