package com.bookpal.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bookpal.R;
import com.bookpal.database.DBAdapter;
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

import java.util.List;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText mEditTextName, mEditTextMobile, mEditTextEmail, mEditTextPassword;
    private AutoCompleteTextView mAutoCompleteTextViewLocality;
    private Button mButtonRegister;
    private LinearLayout mLinearLayoutMain;
    private ProgressBar mProgressBar;
    private static final String TAG = "SignUpActivity";
    private GPSTracker gps;
    private DatabaseReference mDatabase;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        linkViewId();
        constructAutoCompleteTextViewData();
        mContext = SignUpActivity.this;
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
                    Utility.setLoggedIn(mContext);
                    // save data in firebase database
                    writeNewUser(user.getUid());
                    // save user's data to SharedPreference also
                    Utility.saveUserDataToSharedPreference(mContext, mEditTextName.getText().toString().trim(), user.getUid(), mEditTextMobile.getText().toString().trim(), user.getEmail(), mAutoCompleteTextViewLocality.getText().toString().trim());

                    goToMainActivity();
                } else {
                    // User is signed out
                    Log.e(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    private void constructAutoCompleteTextViewData() {
        DBAdapter dbAdapter = new DBAdapter(this);
        dbAdapter.open();

        List<String> pincode = dbAdapter.GetPincodes();
        List<String> area = dbAdapter.GetArea();

        pincode.addAll(area);

        // close database
        dbAdapter.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, pincode);
        mAutoCompleteTextViewLocality.setAdapter(adapter);
    }

    private void linkViewId() {
        mEditTextName = (EditText) findViewById(R.id.editText_Name);
        mEditTextMobile = (EditText) findViewById(R.id.editText_mobile);
        mEditTextEmail = (EditText) findViewById(R.id.editText_email);
        mEditTextPassword = (EditText) findViewById(R.id.editText_password);
        mAutoCompleteTextViewLocality = (AutoCompleteTextView) findViewById(R.id.edittext_locality);
        mButtonRegister = (Button) findViewById(R.id.button_register);
        mLinearLayoutMain = (LinearLayout) findViewById(R.id.linear_layout_main);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

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

                        mAuth.createUserWithEmailAndPassword(mEditTextEmail.getText().toString().trim(), mEditTextPassword.getText().toString().trim())
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
                        Utility.showToastMessage(this, "Please enter Email id to complete the registration");
                        YoYo.with(Techniques.Shake)
                                .duration(700)
                                .playOn(mEditTextEmail);
                    } else if (!(mEditTextPassword.getText().length() > 0)) {
                        Utility.showToastMessage(this, "Please enter Password to complete the registration");
                        YoYo.with(Techniques.Shake)
                                .duration(700)
                                .playOn(mEditTextPassword);
                    } else if (!(mAutoCompleteTextViewLocality.getText().length() > 0)) {
                        Utility.showToastMessage(this, "Please enter correct pincode to complete the registration");
                        YoYo.with(Techniques.Shake)
                                .duration(700)
                                .playOn(mAutoCompleteTextViewLocality);
                    }
                } else {
                    Utility.showToastMessage(this, getResources().getString(R.string.no_internet_connection));
                }
                break;
        }
    }

    private boolean checkValidation() {
        if (mEditTextEmail.getText().length() > 0 && mEditTextPassword.getText().length() > 0 && mAutoCompleteTextViewLocality.getText().length() > 0) {
            return true;
        }
        return false;
    }

    // writes new data to firebase database
    private void writeNewUser(String userId) {
        Registration registrationData = new Registration();
        registrationData.setName(mEditTextName.getText().toString().trim());
        registrationData.setUserId(userId);
        registrationData.setMobile(mEditTextMobile.getText().toString().trim());
        registrationData.setEmail(mEditTextEmail.getText().toString().trim());
        registrationData.setPassword(mEditTextPassword.getText().toString().trim());
        registrationData.setLocality(mAutoCompleteTextViewLocality.getText().toString().trim());

        mDatabase.child("users").child("registration").child(userId).setValue(registrationData);
    }

    private void showProgressBar() {
        mLinearLayoutMain.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
        mLinearLayoutMain.setVisibility(View.VISIBLE);
    }

    private void goToMainActivity() {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(AppConstants.FROM_SIGN_UP_OR_SIGN_IN, AppConstants.FLAG_YES);
        startActivity(intent);
    }
}
