package com.simon.vpohode.screens;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.simon.vpohode.Managers.ColorManager;
import com.simon.vpohode.Managers.LayoutManager;
import com.simon.vpohode.R;
import com.simon.vpohode.database.DatabaseHelper;

import java.util.ArrayList;

public class ShowItems extends AppCompatActivity {
    private ListView topItemList,topItemList12,topItemList13,botItemList;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private SimpleCursorAdapter simpleCursorAdapter;
    private ArrayList<Integer> botPallitra,topPallitra1,topPallitra2,topPallitra3;
    private ArrayList<Integer[]> listOfLooks;


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

        simpleCursorAdapter = LayoutManager.configListOfItems(this,db,0,term);
        botItemList.setAdapter(simpleCursorAdapter);
        botPallitra = ColorManager.savePallitra(simpleCursorAdapter.getCursor());

        if(term >= 20 ){
            topItemList12.setVisibility(View.GONE);
            topItemList13.setVisibility(View.GONE);

            simpleCursorAdapter = LayoutManager.configListOfItems(this,db,1,term, 1);
            topPallitra1 = ColorManager.savePallitra(simpleCursorAdapter.getCursor());
            topItemList.setAdapter(simpleCursorAdapter);

            Integer [][] array = new Integer[2][];
            array[0] = botPallitra.toArray(new Integer[0]);
            array[1] = topPallitra1.toArray(new Integer[0]);
            listOfLooks = ColorManager.colorLook(array);
            Log.i("Best collors", "first " + ColorManager.bestLooks(listOfLooks).get(0)[0]);

        }else if(term >= 9){
            topItemList13.setVisibility(View.GONE);

            simpleCursorAdapter = LayoutManager.configListOfItems(this,db,1,term,1);
            topPallitra1 = ColorManager.savePallitra(simpleCursorAdapter.getCursor());
            topItemList.setAdapter(simpleCursorAdapter);

            simpleCursorAdapter = LayoutManager.configListOfItems(this,db,1,term,2);
            topPallitra2 = ColorManager.savePallitra(simpleCursorAdapter.getCursor());
            topItemList12.setAdapter(simpleCursorAdapter);

            Integer [][] array = new Integer[3][];
            array[0] = botPallitra.toArray(new Integer[0]);
            array[1] = topPallitra1.toArray(new Integer[0]);
            array[2] = topPallitra2.toArray(new Integer[0]);

            listOfLooks = ColorManager.colorLook(array);
            Log.i("Best collors", "first " + ColorManager.bestLooks(listOfLooks).get(0)[0]);
        }else{
            simpleCursorAdapter = LayoutManager.configListOfItems(this,db,1,term,1);
            topPallitra1 = ColorManager.savePallitra(simpleCursorAdapter.getCursor());
            topItemList.setAdapter(simpleCursorAdapter);

            simpleCursorAdapter = LayoutManager.configListOfItems(this,db,1,term,2);
            topPallitra2 = ColorManager.savePallitra(simpleCursorAdapter.getCursor());
            topItemList12.setAdapter(simpleCursorAdapter);

            simpleCursorAdapter = LayoutManager.configListOfItems(this,db,1,term,3);
            topPallitra3 = ColorManager.savePallitra(simpleCursorAdapter.getCursor());
            topItemList13.setAdapter(simpleCursorAdapter);

            Integer [][] array = new Integer[4][];
            array[0] = botPallitra.toArray(new Integer[0]);
            array[1] = topPallitra1.toArray(new Integer[0]);
            array[2] = topPallitra2.toArray(new Integer[0]);
            array[3] = topPallitra3.toArray(new Integer[0]);

            listOfLooks = ColorManager.colorLook(array);

            ArrayList<Integer[]> test = ColorManager.bestLooks(listOfLooks);
            Log.i("Last test for today ", " size " + test.size());

            simpleCursorAdapter = LayoutManager.configListOfItems(this,db,0,term, test );
            botItemList.setAdapter(simpleCursorAdapter);
        }

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
        simpleCursorAdapter.getCursor().close();
    }
}
