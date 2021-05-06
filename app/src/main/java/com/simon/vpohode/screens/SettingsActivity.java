package com.simon.vpohode.screens;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.simon.vpohode.Managers.LayoutManager;
import com.simon.vpohode.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        LayoutManager.setTheme(prefs, getTheme());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
    }
    public void goHome(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            SwitchPreferenceCompat myPref = (SwitchPreferenceCompat) findPreference("theme");
            myPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if((boolean)newValue){
                        Activity activity = getActivity();
                        //activity.recreate();
                        Intent intent = activity.getIntent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        activity.finish();
                        startActivity(intent);
                    }else{
                        Activity activity = getActivity();
                        //activity.recreate();
                        Intent intent = activity.getIntent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        activity.finish();
                        startActivity(intent);
                    }
                    return true;
                }
            });
        }
    }
}