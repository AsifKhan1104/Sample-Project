package com.bookpal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bookpal.R;

public class SplashActivity extends AppCompatActivity implements Animation.AnimationListener {
    // Animation
    Animation mAnimation;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mImageView = (ImageView) findViewById(R.id.imageView_logo);
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
