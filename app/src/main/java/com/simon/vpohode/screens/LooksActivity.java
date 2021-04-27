package com.simon.vpohode.screens;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.simon.vpohode.Item;
import com.simon.vpohode.LooksAdapter;
import com.simon.vpohode.Managers.LayoutManager;
import com.simon.vpohode.Managers.LookManager;
import com.simon.vpohode.R;

import java.util.ArrayList;

public class LooksActivity extends AppCompatActivity {


    private Button next,back, usethislook;
    private LooksAdapter itemsAdapter;
    private ListView listOfItems;
    private Integer showingLook = 0;
    private Toolbar toolbar;
    public static ArrayList<Item[]> looks2;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean("theme", true)){
            getTheme().applyStyle(R.style.AppTheme,true);
        }else{
            getTheme().applyStyle(R.style.OverlayThemeRose,true);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_looks);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        next = findViewById(R.id.next);
        back = findViewById(R.id.back);
        usethislook = findViewById(R.id.usethislook);

        Bundle extras = getIntent().getExtras();
        Double term = extras.getDouble("term");

        listOfItems = findViewById(R.id.list_items);

        looks2 = LookManager.getLooks(term, getApplicationContext());

        if(looks2 != null) {
            if(!MainActivity.rain.equals(""))
                Toast.makeText(this, getResources().getString(R.string.umbrella), Toast.LENGTH_SHORT).show();
            fillLook2(looks2.get(0),looks2.size(),this,this);
            toolbar.setTitle(getResources().getString(R.string.sets) + ": " + 1 + "/" + looks2.size());
        }else{
            String lacks = LookManager.message.substring(0, LookManager.message.length() - 1);
            Toast.makeText(this, getResources().getString(R.string.no_match) + " " + lacks, Toast.LENGTH_SHORT).show();
            LookManager.message = "";
            finish();
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showingLook++;
                if(showingLook < looks2.size()) {
                    fillLook2(looks2.get(showingLook), looks2.size(),view.getContext(), getActivity());
                }else{
                    Toast.makeText(view.getContext(), getResources().getString(R.string.last_look), Toast.LENGTH_SHORT).show();
                    showingLook--;
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showingLook--;
                if(showingLook >= 0) {
                    fillLook2(looks2.get(showingLook), looks2.size(),view.getContext(), getActivity());
                }else{
                    Toast.makeText(view.getContext(), getResources().getString(R.string.first_look), Toast.LENGTH_SHORT).show();
                    showingLook++;
                }
            }
        });

        usethislook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LookManager.useLook(showingLook, looks2, v.getContext());
                finish();
            }
        });

        findViewById(R.id.toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        LayoutManager.invisible(R.id.save,menu);
        LayoutManager.invisible(R.id.search,menu);
        LayoutManager.invisible(R.id.action_settings,menu);
        LayoutManager.invisible(R.id.action_help,menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // close connection
    }

    public void fillLook2 (final Item[] look, Integer size,final Context context, final Activity activity){
        toolbar.setTitle(getResources().getString(R.string.sets) + ": " + (showingLook +1) + "/" + size);

        final ArrayList<Item> preparedLook = new ArrayList<>();
        for(Item item: look){
            preparedLook.add(item);
        }
        itemsAdapter = new LooksAdapter(this, preparedLook);
        listOfItems.setAdapter(itemsAdapter);
        listOfItems.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                id = preparedLook.get(position).getId();
                Intent intent = new Intent(context, ConfigItem.class);
                intent.putExtra("id", id);
                activity.startActivity(intent);
            }
        });
    }

    private Activity getActivity() {
        Context context = this;
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }
}


