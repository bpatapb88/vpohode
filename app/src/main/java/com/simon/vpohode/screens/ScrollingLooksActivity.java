package com.simon.vpohode.screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.simon.vpohode.Item;
import com.simon.vpohode.Managers.LayoutManager;
import com.simon.vpohode.Managers.LookManager;
import com.simon.vpohode.MyAdapter;
import com.simon.vpohode.R;

import java.util.ArrayList;


public class ScrollingLooksActivity extends AppCompatActivity {
    public static ArrayList<Item[]> looks;
    private ViewPager2 pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        LayoutManager.setTheme(prefs, getTheme());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_looks);

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
    }
    public void goHome(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void useButtonClick(View view){
        LookManager.useLook(pager.getCurrentItem(),looks,view.getContext());
        finish();
    }
}
