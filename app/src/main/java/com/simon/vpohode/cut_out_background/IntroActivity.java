package com.simon.vpohode.cut_out_background;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.simon.vpohode.R;


import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_magic_title), getString(R.string.intro_magic_description), R.drawable.intro_magic_wand, getResources().getColor(R.color.intro_magic_background_color)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_manual_title), getString(R.string.intro_manual_description), R.drawable.intro_pencil, getResources().getColor(R.color.intro_manual_background_color)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_zoom_title), getString(R.string.intro_zoom_description), R.drawable.intro_magnifier, getResources().getColor(R.color.intro_zoom_background_color)));

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        setVibrate(false);

        setFadeAnimation();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent = new Intent(this, CutBGActivity.class);
        intent.putExtra(CutOut.CUTOUT_EXTRA_SOURCE, (Bundle) getIntent().getExtras().get(CutOut.CUTOUT_EXTRA_SOURCE));


        startActivity(intent);
        System.out.println(getIntent().getParcelableExtra(CutOut.CUTOUT_EXTRA_SOURCE).toString());
        // Do something when users tap on Done button.

    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}