package com.simon.vpohode.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.simon.vpohode.R;
import com.simon.vpohode.RecyclerData;
import com.simon.vpohode.RecyclerItemClickListener;
import com.simon.vpohode.RecyclerViewAdapter;
import com.simon.vpohode.database.DBFields;
import com.simon.vpohode.database.DatabaseHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class AddItemToLookActivity extends AppCompatActivity {

    private double term;
    private Integer[] look;
    private String nameLook;

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_to_look);

        Bundle extras = getIntent().getExtras();
        term = extras.getDouble("term");
        nameLook = extras.getString("name");
        look = (Integer[]) extras.get("look");
        String[] lookString = new String[look.length];
        for(int i = 0; i < look.length; i++){
            lookString[i] = look[i].toString();
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : lookString){
            stringBuilder.append(str);
            stringBuilder.append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1);

        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " + DBFields.ID.toFieldName() + " IN (" + stringBuilder.toString() + ")", null);
    }
}