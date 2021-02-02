package com.simon.vpohode.screens;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.simon.vpohode.Item;
import com.simon.vpohode.LooksAdapter;
import com.simon.vpohode.Managers.LookManager;
import com.simon.vpohode.Managers.LayoutManager;
import com.simon.vpohode.R;
import com.simon.vpohode.database.DatabaseHelper;

import java.util.ArrayList;

public class LooksActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private DatabaseHelper databaseHelper;
    private TextView title;
    private Button next,back;
    private LooksAdapter itemsAdapter;
    private ListView listOfItems;
    private Integer showinglook = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_looks);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        title = findViewById(R.id.title);
        next = findViewById(R.id.next);
        back = findViewById(R.id.back);

        listOfItems = findViewById(R.id.list_items);

        Bundle extras = getIntent().getExtras();
        Double term = extras.getDouble("term");
        databaseHelper = new DatabaseHelper(getApplicationContext());
        db = databaseHelper.getReadableDatabase();

        listOfItems = findViewById(R.id.list_items);
        final ArrayList<Item[]> looks2 = LookManager.getLooks(term, getApplicationContext());

        if(looks2 != null) {
            fillLook2(looks2.get(0),looks2.size());
        }else{
            Toast.makeText(this, "Нет подходящего набора", Toast.LENGTH_SHORT).show();
            finish();
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showinglook ++;
                if(showinglook < looks2.size()) {
                    fillLook2(looks2.get(showinglook), looks2.size());
                }else{
                    Toast.makeText(view.getContext(), "Это Последний набор", Toast.LENGTH_SHORT).show();
                    showinglook --;
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showinglook --;
                if(showinglook >= 0) {
                    fillLook2(looks2.get(showinglook), looks2.size());
                }else{
                    Toast.makeText(view.getContext(), "Это самый первый набор", Toast.LENGTH_SHORT).show();
                    showinglook++;
                }
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
        db.close();
    }

    public void fillLook2 (final Item[] look, Integer size){
        title.setText("Мы подобрали для вас: " + (showinglook+1) + "/" + size);

        ArrayList<Item> preparedLook = new ArrayList<>();
        for(Item item: look){
            preparedLook.add(item);
        }
        itemsAdapter = new LooksAdapter(this, preparedLook);
        listOfItems.setAdapter(itemsAdapter);
    }
}


