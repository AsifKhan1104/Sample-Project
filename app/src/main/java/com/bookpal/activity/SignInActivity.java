package com.bookpal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.bookpal.R;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    private Button button_signUp, button_logIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        linkViewId();
    }

    private void linkViewId() {
        button_signUp = (Button) findViewById(R.id.button_signUp);
        button_logIn = (Button) findViewById(R.id.button_logIn);

        // on click listeners
        button_signUp.setOnClickListener(this);
        button_logIn.setOnClickListener(this);
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
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }
}
