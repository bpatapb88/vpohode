package com.simon.vpohode.screens;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;


import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.simon.vpohode.BuildConfig;
import com.simon.vpohode.Item;
import com.simon.vpohode.Managers.LayoutManager;
import com.simon.vpohode.Managers.LookManager;
import com.simon.vpohode.MyAdapter;
import com.simon.vpohode.R;

import java.util.ArrayList;


public class ScrollingLooksActivity extends AppCompatActivity {
    public static ArrayList<Item[]> looks;
    private ViewPager2 pager;
    public InterstitialAd interstitialAd; //ad
    AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        LayoutManager.setTheme(prefs, getTheme());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_looks);

        //ad start
        MobileAds.initialize(this, BuildConfig.GOOGLE_APPID);
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(BuildConfig.GOOGLE_ADMOD);
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);
        //finish ad

        //close ad start
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                try{
                    Intent intent = new Intent(ScrollingLooksActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }catch (Exception e){

                }
            }
        });


        Bundle extras = getIntent().getExtras();
        Double term = extras.getDouble("term");
        looks = LookManager.getLooks(term, getApplicationContext());
        if(looks == null){
            String lacks = LookManager.message.substring(0, LookManager.message.length() - 1);
            Toast.makeText(this, getResources().getString(R.string.no_match) + " " + lacks, Toast.LENGTH_SHORT).show();
            LookManager.message = "";
            finish();
        }else if(looks.size() == 1){
            TabLayout tabLayout = findViewById(R.id.tab_layout);
            tabLayout.setVisibility(View.GONE);
        }

        if(!MainActivity.rain.equals(""))
            Toast.makeText(this, getResources().getString(R.string.umbrella), Toast.LENGTH_SHORT).show();

        pager=(ViewPager2)findViewById(R.id.pager);
        FragmentStateAdapter pageAdapter = new MyAdapter(this, looks);
        pager.setAdapter(pageAdapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        TabLayoutMediator tabLayoutMediator= new TabLayoutMediator(tabLayout, pager, new TabLayoutMediator.TabConfigurationStrategy(){
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
               tab.setText("" + (position+1));
            }
        });
        tabLayoutMediator.attach();

        builder = new AlertDialog.Builder(this);
    }
    public void goHome(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void useButtonClick(View view){
        builder.setMessage(R.string.dialog_message).setTitle(R.string.dialog_title);
        builder.setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LookManager.useLook(pager.getCurrentItem(),looks,view.getContext());
                        if(interstitialAd.isLoaded()){
                            interstitialAd.show();
                        }else{
                            finish();
                        }
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.setTitle(R.string.dialog_title);
        alert.show();
    }
}
