package com.bookpal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bookpal.R;
import com.bookpal.database.DBAdapter;
import com.bookpal.utility.Utility;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.io.IOException;

public class SplashActivity extends AppCompatActivity implements Animation.AnimationListener {
    // Animation
    Animation mAnimation;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // copy database from assets to internal db
        try {
            DBAdapter dbAdapter = new DBAdapter(this).open();
            new Utility(this).copyDataBase();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        mImageView = (ImageView) findViewById(R.id.imageView_logo);

        YoYo.with(Techniques.FadeIn)
                .duration(1000)
                .playOn(findViewById(R.id.imageView_logo));
        // load the animation
        mAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.splash_anim);

        // To receive callback of animations
        mAnimation.setAnimationListener(this);

        // start the animation
        mImageView.startAnimation(mAnimation);
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == mAnimation) {

            // call next activity on animation end
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            finish();
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }
}
