package com.simon.vpohode.screens;

import androidx.appcompat.app.AppCompatActivity;
import com.simon.vpohode.R;
import com.simon.vpohode.database.DBFields;
import com.simon.vpohode.database.DatabaseHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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
        StringBuilder stringBuilder = new StringBuilder();
        if(look != null){
            String[] lookString = new String[look.length];
            for(int i = 0; i < look.length; i++){
                lookString[i] = look[i].toString();
            }

            for (String str : lookString){
                stringBuilder.append(str);
                stringBuilder.append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length()-1);
        }


        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " + DBFields.ID.toFieldName() + " NOT IN (" + stringBuilder.toString() + ")", null);
        Toast.makeText(this,"We found " + cursor.getCount() + " items", Toast.LENGTH_SHORT).show();
    }

    public void finish(View view) {
        this.finish();
    }
}