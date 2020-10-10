package com.simon.vpohode;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Wardrobe extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private Cursor itemCursor;
    private SimpleCursorAdapter topItemAdapter, bottomItemAdapter;
    private ListView topItemList, bottomItemList;
    private TextView countTop,countBot;
    private EditText topItemFilter,botItemFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe);
        countTop = (TextView)findViewById(R.id.header);
        countBot = findViewById(R.id.header2);
        topItemList = (ListView)findViewById(R.id.list);
        bottomItemList = (ListView)findViewById(R.id.list2);
        topItemFilter = (EditText)findViewById(R.id.topItemFilter);
        botItemFilter = findViewById(R.id.botItemFilter);

        topItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        bottomItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        databaseHelper = new DatabaseHelper(getApplicationContext());
    }

    public void addNewItem(View view){
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        // open connection
        db = databaseHelper.getReadableDatabase();
        //get cursor from db
        itemCursor =  db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE + " WHERE " + DatabaseHelper.COLUMN_TOP + " = 1", null);
        // which column will be in ListView
        countTop.setText("На плечи: " + String.valueOf(itemCursor.getCount()));
        String[] headers = new String[] {DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_TERMID, DatabaseHelper.COLUMN_TOP,};
        // create adapter, send cursor
        topItemAdapter = new SimpleCursorAdapter(this, R.layout.two_line_list_item,
                itemCursor, headers, new int[]{R.id.text1, R.id.text2, R.id.text3}, 0);
        itemCursor =  db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE + " WHERE " + DatabaseHelper.COLUMN_TOP + " = 0", null);
        countBot.setText("На ноги: " + String.valueOf(itemCursor.getCount()));
        bottomItemAdapter = new SimpleCursorAdapter(this, R.layout.two_line_list_item,
                itemCursor, headers, new int[]{R.id.text1, R.id.text2, R.id.text3}, 0);

        if(!topItemFilter.getText().toString().isEmpty())
            topItemAdapter.getFilter().filter(topItemFilter.getText().toString());

        // установка слушателя изменения текста
        topItemFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) { }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            // при изменении текста выполняем фильтрацию
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                topItemAdapter.getFilter().filter(s.toString());
            }
        });
        // устанавливаем провайдер фильтрации
        topItemAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {

                if (constraint == null || constraint.length() == 0) {
                    return db.rawQuery("select * from " + DatabaseHelper.TABLE, null);
                }
                else {
                    return db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                            DatabaseHelper.COLUMN_NAME + " like ?", new String[]{"%" + constraint.toString() + "%"});
                }
            }
        });



        topItemList.setAdapter(topItemAdapter);
        bottomItemList.setAdapter(bottomItemAdapter);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        // close connection
        db.close();
        itemCursor.close();
    }
}
