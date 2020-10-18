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
    private ListView topItemList,botItemList;
    private TextView listOfItems;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private Cursor itemCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showitem);
        topItemList = findViewById(R.id.list);
        botItemList = findViewById(R.id.list2);
        databaseHelper = new DatabaseHelper(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        Double term = extras.getDouble("term");

        double bestTopIndex = 0;
        double bestBottomIndex = 0;

        // connection to DB
        db = databaseHelper.getReadableDatabase();
        //get cursor from db to have list of termindexes
        String[] headers = new String[] {DBFields.NAME.toFieldName(), DBFields.STYLE.toFieldName(), DBFields.ISTOP.toFieldName(),};
        itemCursor = db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE + " WHERE " + DBFields.ISTOP.toFieldName() + " = 1", null);

        CountBestTermIndex counter = new CountBestTermIndex();
        bestTopIndex = counter.getTopIndex(itemCursor,term);
        itemCursor = db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE + " WHERE " + DBFields.ISTOP.toFieldName() + " = 0", null);
        bestBottomIndex = counter.getBotIndex(itemCursor,term);

        itemCursor = db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE + " WHERE " + DBFields.ISTOP.toFieldName() + " = 1 AND " + DBFields.TERMID.toFieldName() + " = " + bestTopIndex, null);
        SimpleCursorAdapter itemAdapter = new SimpleCursorAdapter(this, R.layout.two_line_list_item, itemCursor, headers, new int[]{R.id.text1, R.id.text2, R.id.text3}, 0);
        //db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " + DatabaseHelper.COLUMN_NAME + " like ?", new String[]{"%" + constraint.toString() + "%"});
        topItemList.setAdapter(itemAdapter);

        itemCursor = db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE + " WHERE " + DBFields.ISTOP.toFieldName() + " = 0 AND " + DBFields.TERMID.toFieldName() + " = " + bestBottomIndex, null);
        SimpleCursorAdapter itemAdapter2 = new SimpleCursorAdapter(this, R.layout.two_line_list_item, itemCursor, headers, new int[]{R.id.text1, R.id.text2, R.id.text3}, 0);
        botItemList.setAdapter(itemAdapter2);
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
