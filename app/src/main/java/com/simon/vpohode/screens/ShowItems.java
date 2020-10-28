package com.simon.vpohode.screens;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.simon.vpohode.LayoutManager;
import com.simon.vpohode.R;
import com.simon.vpohode.database.DBFields;
import com.simon.vpohode.database.DatabaseHelper;

public class ShowItems extends AppCompatActivity {
    private ListView topItemList,topItemList12,topItemList13,botItemList;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private Cursor itemCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        double bestTopIndex = 0;
        double bestBottomIndex = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showitem);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setTitle(getString(R.string.show_item));

        topItemList = findViewById(R.id.list);
        topItemList12 = findViewById(R.id.list12);
        topItemList13 = findViewById(R.id.list13);
        botItemList = findViewById(R.id.list2);
        // make list clickable, but after save Wardrobe will be open
        topItemList.setOnItemClickListener(LayoutManager.ClickItem(this,this));
        topItemList12.setOnItemClickListener(LayoutManager.ClickItem(this,this));
        topItemList13.setOnItemClickListener(LayoutManager.ClickItem(this,this));
        botItemList.setOnItemClickListener(LayoutManager.ClickItem(this,this));

        databaseHelper = new DatabaseHelper(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        Double term = extras.getDouble("term");
        // connection to DB
        db = databaseHelper.getReadableDatabase();

        String[] headers = new String[] {DBFields.NAME.toFieldName(), DBFields.STYLE.toFieldName(), DBFields.TERMID.toFieldName(),};

        if(term >= 20 ){
            topItemList12.setVisibility(View.GONE);
            topItemList13.setVisibility(View.GONE);
            topItemList.setAdapter(LayoutManager.configListOfItems(this,db,1,term));

        }else if(term >= 9){
            topItemList13.setVisibility(View.GONE);
            topItemList.setAdapter(LayoutManager.configListOfItems(this,db,1,term,1));
            topItemList12.setAdapter(LayoutManager.configListOfItems(this,db,1,term,2));
        }else{
            topItemList.setAdapter(LayoutManager.configListOfItems(this,db,1,term,1));
            topItemList12.setAdapter(LayoutManager.configListOfItems(this,db,1,term,2));
            topItemList13.setAdapter(LayoutManager.configListOfItems(this,db,1,term,3));
        }

        botItemList.setAdapter(LayoutManager.configListOfItems(this,db,0,term));

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
    }
}
