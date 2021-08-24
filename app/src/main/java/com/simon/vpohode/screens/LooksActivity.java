package com.simon.vpohode.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import com.simon.vpohode.CustomLookAdapter;
import com.simon.vpohode.R;
import com.simon.vpohode.database.DatabaseHelper;
import com.simon.vpohode.managers.LayoutManager;
import com.simon.vpohode.managers.ListViewManager;

public class LooksActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private ListView lookList;
    private CustomLookAdapter lookAdapter;
    double term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        LayoutManager.setTheme(prefs, getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_looks);
        databaseHelper = new DatabaseHelper(this);
        //hidden keyboard by default
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        lookList = findViewById(R.id.list_of_looks);

        Bundle extras = getIntent().getExtras();
        term = extras.getDouble("term");
    }

    @Override
    public void onResume() {
        super.onResume();
        db = databaseHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_LOOKS, null);
        lookAdapter = new CustomLookAdapter(this, cursor);
        lookList.setAdapter(lookAdapter);
        ListViewManager.optimizeListViewSize(lookList);

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // close connection

        db.close();
    }

    public void goHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void addNewLook(View view) {
        Intent intent = new Intent(this, AddLookActivity.class);
        intent.putExtra("term", term);
        startActivity(intent);
    }
}