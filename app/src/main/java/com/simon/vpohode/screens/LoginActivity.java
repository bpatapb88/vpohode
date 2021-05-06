package com.simon.vpohode.screens;

import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

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
            getTheme().applyStyle(R.style.OverlayThemeDark,true);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        logo = findViewById(R.id.logo);
        logo.setBackgroundResource(R.drawable.animation);
        frameAnimation = (AnimationDrawable) logo.getBackground();

        //hidden keyboard by default
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public void toastShow(View view){
        Toast.makeText(view.getContext(), "Pressed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        frameAnimation.start();
    }


}
