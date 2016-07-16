package com.bookpal.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bookpal.R;
import com.bookpal.model.Registration;
import com.bookpal.utility.AppConstants;
import com.bookpal.utility.Utility;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ManualSignInActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ManualSignInActivity";
    private EditText mEditTextEmail, mEditTextPassword;
    private Button mButtonLogin;
    private ProgressBar mProgressBar;
    private LinearLayout mLinearLayoutMain;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_sign_in);

        linkViewId();
        mContext = ManualSignInActivity.this;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Utility.setLoggedIn(mContext);
                    // read user data from firebase database and save in Shared Preference
                    readAndSaveUserData(user.getUid());
                } else {
                    // User is signed out
                    Log.e(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    private void readAndSaveUserData(String uid) {
        mDatabase.child("users").child(uid).child("registration").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Registration userData = dataSnapshot.getValue(Registration.class);
                Utility.saveUserDataToSharedPreference(mContext, userData.getName(), userData.getUserId(), userData.getMobile(), userData.getEmail(), userData.getLatitude(), userData.getLongitude());
                goToMainActivity();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "getUser:onCancelled", databaseError.toException());
                goToMainActivity();
            }
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(AppConstants.FROM_SIGN_UP_OR_SIGN_IN, AppConstants.FLAG_YES);
        startActivity(intent);
    }

    private void linkViewId() {
        mEditTextEmail = (EditText) findViewById(R.id.editText_email);
        mEditTextPassword = (EditText) findViewById(R.id.editText_password);
        mButtonLogin = (Button) findViewById(R.id.button_login);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mLinearLayoutMain = (LinearLayout) findViewById(R.id.linearLayout_Main);

        mButtonLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_login:
                if (Utility.isNetworkConnected(this)) {
                    if (checkValidation()) {
                        showProgressBar();

                        mAuth.signInWithEmailAndPassword(mEditTextEmail.getText().toString().trim(), mEditTextPassword.getText().toString().trim())
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        // If sign in fails, display a message to the user. If sign in succeeds
                                        // the auth state listener will be notified and logic to handle the
                                        // signed in user can be handled in the listener.
                                        if (!task.isSuccessful()) {
                                            hideProgressBar();
                                            Utility.showToastMessage(mContext, task.getException().getMessage());
                                        }
                                    }
                                });
                    } else if (!(mEditTextEmail.getText().length() > 0)) {
                        Utility.showToastMessage(this, "Please enter Email id to login");
                        YoYo.with(Techniques.Shake)
                                .duration(700)
                                .playOn(mEditTextEmail);
                    } else if (!(mEditTextPassword.getText().length() > 0)) {
                        Utility.showToastMessage(this, "Please enter Password to login");
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

    private void showProgressBar() {
        mLinearLayoutMain.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
        mLinearLayoutMain.setVisibility(View.VISIBLE);
    }

    private boolean checkValidation() {
        if (mEditTextEmail.getText().length() > 0 && mEditTextPassword.getText().length() > 0) {
            return true;
        }
        return false;
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
}
