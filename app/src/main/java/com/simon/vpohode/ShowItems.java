package com.simon.vpohode;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ShowItems extends AppCompatActivity {
    private ListView topItemList;
    private TextView listOfItems;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private Cursor itemCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showitem);
        topItemList = findViewById(R.id.list);
        listOfItems = findViewById(R.id.listofitems);
        databaseHelper = new DatabaseHelper(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        Double term = extras.getDouble("term");

        double bestTopIndex = 0;
        double bestBottomIndex = 0;

        // connection to DB
        db = databaseHelper.getReadableDatabase();
        //get cursor from db to have list of termindexes
        String[] headers = new String[] {DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_STYLE, DatabaseHelper.COLUMN_TOP,};
        itemCursor =  db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE + " WHERE " + DatabaseHelper.COLUMN_TOP + " = 1", null);
        double min = Integer.MAX_VALUE;
        if (itemCursor.moveToFirst()){

            do {
                if (min > Math.abs((30 - term)/9 - itemCursor.getDouble(4)*2)) {
                    min = Math.abs((30 - term)/9 - itemCursor.getDouble(4)*2);
                    bestTopIndex = itemCursor.getDouble(4);
                }
            }
            while (itemCursor.moveToNext());
        }

        min = Integer.MAX_VALUE;
        itemCursor =  db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE + " WHERE " + DatabaseHelper.COLUMN_TOP + " = 0", null);
        if (itemCursor.moveToFirst()){
            do {
                if (min > Math.abs((30 - term)/9 - itemCursor.getDouble(4))) {
                    min = Math.abs((30 - term)/9 - itemCursor.getDouble(4));
                    bestBottomIndex = itemCursor.getDouble(4);
                }
            }
            while (itemCursor.moveToNext());
        }
        itemCursor =  db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE + " WHERE " + DatabaseHelper.COLUMN_TOP + " = 1 AND " + DatabaseHelper.COLUMN_TERMID + " = " + bestTopIndex, null);
        SimpleCursorAdapter itemAdapter = new SimpleCursorAdapter(this, R.layout.two_line_list_item, itemCursor, headers, new int[]{R.id.text1, R.id.text2, R.id.text3}, 0);
        //db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " + DatabaseHelper.COLUMN_NAME + " like ?", new String[]{"%" + constraint.toString() + "%"});
        topItemList.setAdapter(itemAdapter);
        itemCursor =  db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE, null);
        String names = "";
        String names1 = "";
        if (itemCursor.moveToFirst()) {
            do {
                if((itemCursor.getInt(3) == 1)&& (itemCursor.getDouble(4) == bestTopIndex)) {
                    names += itemCursor.getString(1) + " или\n";
                }else if((itemCursor.getInt(3) == 0)&& (itemCursor.getDouble(4) == bestBottomIndex)){
                    names1 += itemCursor.getString(1) + " или\n";
                }
            } while (itemCursor.moveToNext());
        }
        listOfItems.setText("На низ вам рекомендую: " + names1.substring(0,names1.length() - 5));
    }


    public void wardrobe(View view){
        Intent intent = new Intent(this, Wardrobe.class);
        startActivity(intent);
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
