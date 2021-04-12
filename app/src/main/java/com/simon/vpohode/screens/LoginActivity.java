package com.simon.vpohode.screens;

import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.simon.vpohode.R;

public class LoginActivity extends AppCompatActivity {

    private ImageView logo;
    private AnimationDrawable frameAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean("theme", true)){
            getTheme().applyStyle(R.style.AppTheme,true);
        }else{
            getTheme().applyStyle(R.style.OverlayThemeRose,true);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        logo = findViewById(R.id.logo);
        logo.setBackgroundResource(R.drawable.animation);
        frameAnimation = (AnimationDrawable) logo.getBackground();
    }

    @Override
    public void onResume() {
        super.onResume();
        frameAnimation.start();
    }


}
