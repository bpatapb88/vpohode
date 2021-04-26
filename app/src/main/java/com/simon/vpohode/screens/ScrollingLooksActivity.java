package com.simon.vpohode.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.simon.vpohode.Item;
import com.simon.vpohode.Managers.LookManager;
import com.simon.vpohode.MyAdapter;
import com.simon.vpohode.R;

import java.util.ArrayList;


public class ScrollingLooksActivity extends AppCompatActivity {
    public static ArrayList<Item[]> looks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_looks);

        Bundle extras = getIntent().getExtras();
        Double term = extras.getDouble("term");
        looks = LookManager.getLooks(term, getApplicationContext());

        ViewPager2 pager=(ViewPager2)findViewById(R.id.pager);
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
}
