package com.simon.vpohode;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class TestLayout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testlayout);
        Toolbar toolbar = findViewById(R.id.bottomAppBar);
        setSupportActionBar(toolbar);

    }
}
