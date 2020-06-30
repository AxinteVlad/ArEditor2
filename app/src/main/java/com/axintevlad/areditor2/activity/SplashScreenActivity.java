package com.axintevlad.areditor2.activity;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.axintevlad.areditor2.R;
import com.rbddevs.splashy.Splashy;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Splashy splashy = new Splashy(this);
        splashy.setLogo(R.drawable.house)
                .setTitle("AR Editor")
                .setTitleColor("#546E7A")
                .setSubTitle("Bun venit!")
                .setBackgroundColor("#CFD8DC")
                .setFullScreen(true)
                .setAnimation(Splashy.Animation.GLOW_LOGO,800)
                .setTime(3000)
                .show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startMainActivity();
            }
        }, 3000);

    }

    public void startMainActivity(){
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
    }
}
