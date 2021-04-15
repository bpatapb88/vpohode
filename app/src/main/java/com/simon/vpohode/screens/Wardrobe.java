package com.simon.vpohode.screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.simon.vpohode.CustomAdapter;
import com.simon.vpohode.Managers.LayoutManager;
import com.simon.vpohode.Managers.ListViewManager;
import com.simon.vpohode.R;
import com.simon.vpohode.database.DatabaseHelper;

public class Wardrobe extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private CustomAdapter topItemAdapter;
    private ListView topItemList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean("theme", true)){
            getTheme().applyStyle(R.style.AppTheme,true);
        }else{
            getTheme().applyStyle(R.style.OverlayThemeRose,true);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe);

        topItemList = findViewById(R.id.list);
        //topItemList.setOnItemClickListener(LayoutManager.ClickItem(this,this));

        databaseHelper = new DatabaseHelper(getApplicationContext());

        //hidden keyboard by default
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        LayoutManager.invisible(R.id.action_settings,menu);
        LayoutManager.invisible(R.id.save,menu);
        LayoutManager.invisible(R.id.action_help,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        //finish();
        return super.onOptionsItemSelected(item);
    }

    public void goHome(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void addNewItem(View view){
        Intent intent = new Intent(this, ConfigItem.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        // open connection
        db = databaseHelper.getReadableDatabase();
        //get cursor from db

        // fill list depends of: is item top or not? 1=top 0=bottom
        topItemAdapter = LayoutManager.configListOfItems(this,db,1);
        topItemList.setAdapter(topItemAdapter);
        ListViewManager.getListViewSize(topItemList);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        // close connection
        topItemAdapter.getCursor().close();
        db.close();
    }
}
