package com.simon.vpohode.screens;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.simon.vpohode.Item;
import com.simon.vpohode.Managers.LookManager;
import com.simon.vpohode.Managers.LayoutManager;
import com.simon.vpohode.R;
import com.simon.vpohode.database.DatabaseHelper;

import java.util.ArrayList;

public class LooksActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private DatabaseHelper databaseHelper;
    private TextView textView1,textView2,textView3,textView4, title;


    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_looks);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        title = findViewById(R.id.title);


        Bundle extras = getIntent().getExtras();
        Double term = extras.getDouble("term");
        databaseHelper = new DatabaseHelper(getApplicationContext());
        db = databaseHelper.getReadableDatabase();
        ArrayList<Item[]> looks = LookManager.getLooks(term, getApplicationContext());

        if(looks.size() > 0){
            textView1.setText("Item1 id = " + looks.get(0)[0].getId());
            textView2.setText("Item1 id = " + looks.get(0)[1].getId());
            textView3.setText("Item1 id = " + looks.get(0)[2].getId());
            title.setText("Мы подобрали для вас " + looks.size() + " луков");
//            textView4.setText("Item1 id = " + looks.get(0)[3].getId());
        }


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


