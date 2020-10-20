package com.simon.vpohode.screens;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

        topItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        bottomItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
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
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        // open connection
        db = databaseHelper.getReadableDatabase();
        //get cursor from db
        itemCursor =  db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE + " WHERE " + DBFields.ISTOP.toFieldName() + " = 1", null);
        // which column will be in ListView
        countTop.setText("На плечи: " + String.valueOf(itemCursor.getCount()));
        String[] headers = new String[] {DBFields.NAME.toFieldName(), DBFields.TERMID.toFieldName(), DBFields.ISTOP.toFieldName(),};
        // create adapter, send cursor
        topItemAdapter = new SimpleCursorAdapter(this, R.layout.two_line_list_item,
                itemCursor, headers, new int[]{R.id.text1, R.id.text2, R.id.text3}, 0);
        itemCursor =  db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE + " WHERE " + DBFields.ISTOP.toFieldName() + " = 0", null);
        countBot.setText("На ноги: " + String.valueOf(itemCursor.getCount()));
        bottomItemAdapter = new SimpleCursorAdapter(this, R.layout.two_line_list_item,
                itemCursor, headers, new int[]{R.id.text1, R.id.text2, R.id.text3}, 0);
        // устанавливаем провайдер фильтрации
        topItemAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {

                if (constraint == null || constraint.length() == 0) {
                    return db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " + DBFields.ISTOP.toFieldName() + " = 1", null);
                }
                else {
                    return db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " + DBFields.ISTOP.toFieldName() + " = 1 AND " +
                            DBFields.NAME.toFieldName() + " like ?", new String[]{"%" + constraint.toString() + "%"});
                }
            }
        });

        bottomItemAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {

                if (constraint == null || constraint.length() == 0) {
                    return db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " + DBFields.ISTOP.toFieldName() + " = 0", null);
                }
                else {
                    return db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " + DBFields.ISTOP.toFieldName() + " = 0 AND " +
                            DBFields.NAME.toFieldName() + " like ?", new String[]{"%" + constraint.toString() + "%"});
                }
            }
        });

        topItemList.setAdapter(topItemAdapter);
        bottomItemList.setAdapter(bottomItemAdapter);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        // close connection
        db.close();
        itemCursor.close();
    }
}
