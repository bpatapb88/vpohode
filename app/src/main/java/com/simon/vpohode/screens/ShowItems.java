package com.simon.vpohode.screens;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.simon.vpohode.CountBestTermIndex;
import com.simon.vpohode.LayoutManager;
import com.simon.vpohode.database.DBFields;
import com.simon.vpohode.database.DatabaseHelper;
import com.simon.vpohode.R;

public class ShowItems extends AppCompatActivity {
    private ListView topItemList,botItemList;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private Cursor itemCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showitem);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setTitle(getString(R.string.show_item));

        topItemList = findViewById(R.id.list);
        botItemList = findViewById(R.id.list2);
        // make list clickable, but after save Wardrobe will be open
        topItemList.setOnItemClickListener(LayoutManager.ClickItem(this,this));
        botItemList.setOnItemClickListener(LayoutManager.ClickItem(this,this));

        databaseHelper = new DatabaseHelper(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        Double term = extras.getDouble("term");

        double bestTopIndex = 0;
        double bestBottomIndex = 0;

        // connection to DB
        db = databaseHelper.getReadableDatabase();
        //get cursor from db to have list of termindexes
        String[] headers = new String[] {DBFields.NAME.toFieldName(), DBFields.STYLE.toFieldName(), DBFields.TERMID.toFieldName(),};
        itemCursor = DatabaseHelper.getCursoreByIsTop(db,1);
        CountBestTermIndex counter = new CountBestTermIndex();

        itemCursor = DatabaseHelper.getCursoreByIsTop(db,1);
        bestTopIndex = counter.getTopIndex(itemCursor,term);
        itemCursor = DatabaseHelper.getCursoreByIsTop(db,0);
        bestBottomIndex = counter.getBotIndex(itemCursor,term);

        itemCursor = db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE + " WHERE " + DBFields.ISTOP.toFieldName() + " = 1 AND " + DBFields.TERMID.toFieldName() + " = " + bestTopIndex, null);
        SimpleCursorAdapter itemAdapter = new SimpleCursorAdapter(this, R.layout.two_line_list_item, itemCursor, headers, new int[]{R.id.text1, R.id.text2, R.id.text3}, 0);
        //db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " + DatabaseHelper.COLUMN_NAME + " like ?", new String[]{"%" + constraint.toString() + "%"});
        topItemList.setAdapter(itemAdapter);

        itemCursor = db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE + " WHERE " + DBFields.ISTOP.toFieldName() + " = 0 AND " + DBFields.TERMID.toFieldName() + " = " + bestBottomIndex, null);
        SimpleCursorAdapter itemAdapter2 = new SimpleCursorAdapter(this, R.layout.two_line_list_item, itemCursor, headers, new int[]{R.id.text1, R.id.text2, R.id.text3}, 0);
        botItemList.setAdapter(itemAdapter2);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        LayoutManager.invisible(R.id.save,menu);
        LayoutManager.invisible(R.id.search,menu);
        LayoutManager.invisible(R.id.action_settings,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.search:
                //TODO write action for Search
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        // open connection
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // close connection
        db.close();
        itemCursor.close();
    }
}
