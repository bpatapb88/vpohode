package com.simon.vpohode.screens;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.simon.vpohode.LayoutManager;
import com.simon.vpohode.database.DBFields;
import com.simon.vpohode.database.DatabaseHelper;
import com.simon.vpohode.R;

public class Wardrobe extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private Cursor itemCursor;
    private SimpleCursorAdapter topItemAdapter, bottomItemAdapter;
    private ListView topItemList, bottomItemList;
    private TextView countTop,countBot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setTitle(getString(R.string.title_wardrobe));

        countTop = findViewById(R.id.header);
        countBot = findViewById(R.id.header2);
        topItemList = findViewById(R.id.list);
        bottomItemList = findViewById(R.id.list2);

        topItemList.setOnItemClickListener(LayoutManager.ClickItem(this,this));
        bottomItemList.setOnItemClickListener(LayoutManager.ClickItem(this,this));

        databaseHelper = new DatabaseHelper(getApplicationContext());

        //arrayAdapter = new ArrayAdapter<>(this, R.layout.two_line_list_item,topItemList);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        LayoutManager.invisible(R.id.action_settings,menu);
        LayoutManager.invisible(R.id.save,menu);

        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setQueryHint("Введи название!");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                topItemAdapter.getFilter().filter(s);
                bottomItemAdapter.getFilter().filter(s);
                return true;
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        finish();

        return super.onOptionsItemSelected(item);
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
        countTop.setText("На плечи: " + String.valueOf(db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE + " WHERE " + DBFields.ISTOP.toFieldName() + " = 1", null).getCount()));
        countBot.setText("На ноги: " + String.valueOf(db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE + " WHERE " + DBFields.ISTOP.toFieldName() + " = 0", null).getCount()));
        // fill list depends of: is item top or not? 1=top 0=bottom
        topItemAdapter = LayoutManager.configListOfItems(this,db,1);
        topItemList.setAdapter(topItemAdapter);
        bottomItemAdapter = LayoutManager.configListOfItems(this,db,0);
        bottomItemList.setAdapter(bottomItemAdapter);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        // close connection
        topItemAdapter.getCursor().close();
        bottomItemAdapter.getCursor().close();
        db.close();
    }
}