package com.simon.vpohode.screens;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.littlemango.stacklayoutmanager.StackLayoutManager;
import com.simon.vpohode.Managers.ColorManager;
import com.simon.vpohode.Managers.CountBestTermIndex;
import com.simon.vpohode.Managers.LayoutManager;
import com.simon.vpohode.R;
import com.simon.vpohode.StackLayoutAdapter;
import com.simon.vpohode.database.DatabaseHelper;

import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private StackLayoutManager mStackLayoutManager;
    private Toast mToast;
    private SQLiteDatabase db;
    private DatabaseHelper databaseHelper;
    private ArrayList<String> text;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.testlayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Bundle extras = getIntent().getExtras();
        Double term = extras.getDouble("term");

        databaseHelper = new DatabaseHelper(getApplicationContext());
        db = databaseHelper.getReadableDatabase();

        ArrayList<int[]> looks = CountBestTermIndex.getLooks(db,term);
        ArrayList<int[]> finalLooks = new ArrayList<>();
        //check settings if needed count colors, default option - False
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean("sync", false)){
            for(int i = 0; i < looks.size(); i++){
                if(ColorManager.isLookMatch(StackLayoutAdapter.getImageResource(looks.get(i),db).colors)){
                    finalLooks.add(looks.get(i));
                }
            }
        }else{
            finalLooks = looks;
        }

        mToast = Toast.makeText(TestActivity.this, "Test activity started", Toast.LENGTH_SHORT);
        mRecyclerView = findViewById(R.id.recycleView);
        mStackLayoutManager = new StackLayoutManager();
        mRecyclerView.setLayoutManager(mStackLayoutManager);
        mRecyclerView.setAdapter(new StackLayoutAdapter(mToast,mStackLayoutManager,mRecyclerView, finalLooks, db));

        findViewById(R.id.toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                }
        });
        mStackLayoutManager.setItemChangedListener(new StackLayoutManager.ItemChangedListener() {
            @Override
            public void onItemChanged(int position) {
                getSupportActionBar().setTitle("Look # " + (position + 1));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        LayoutManager.invisible(R.id.save,menu);
        LayoutManager.invisible(R.id.search,menu);
        LayoutManager.invisible(R.id.action_settings,menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        // close connection
        db.close();

    }
}

