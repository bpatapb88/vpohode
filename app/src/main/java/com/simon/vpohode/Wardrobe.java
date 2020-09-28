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

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor, userCursor2;
    SimpleCursorAdapter userAdapter,userAdapter2;
    ListView userList,userList2;
    TextView header;
    EditText userFilter;
    
    /* [Vlad] 
       1. желательно уже по названию понимать что какая переменная делает, я смотрю и сразу не понимаю какая разница между userList1 и userList2, нужно долго
       разбираться чтобы понять, так и адаптер, курсоры. Вечная проблема - выбрать подходящее имя переменной. 
       2. У переменных доступ package private, на практике не так часто это нужно, в твоём случае мне кажется почти все эти переменные используются только в этом классе,
       поэтому должен быть доступ private 
       3. Вроде некоторые из этих переменных используются только в одном методе, лучше создавать локальную переменную. 
     */

    // [Vlad] Почему здесь тип протектед?
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe); // [Vlad] что такое R?
        header = (TextView)findViewById(R.id.header);
        userList = (ListView)findViewById(R.id.list);
        userList2 = (ListView)findViewById(R.id.list2);
        userFilter = (EditText)findViewById(R.id.userFilter);

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        userList2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        databaseHelper = new DatabaseHelper(getApplicationContext());
    }

    // [VLAD] add чего?
    public void add(View view){
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }

    // [Vlad] Достаточно длинный метод, желательно разбить на больше мелких
    @Override
    public void onResume() {
        super.onResume();
        // open connection
        db = databaseHelper.getReadableDatabase();
        //get cursor from db
        userCursor =  db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE + " WHERE " + DatabaseHelper.COLUMN_TOP + " = 1", null);
        // which column will be in ListView
        String[] headers = new String[] {DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_TERMID, DatabaseHelper.COLUMN_TOP,};
        // create adapter, send cursor
        userAdapter = new SimpleCursorAdapter(this, R.layout.two_line_list_item,
                userCursor, headers, new int[]{R.id.text1, R.id.text2, R.id.text3}, 0);
        userCursor =  db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE + " WHERE " + DatabaseHelper.COLUMN_TOP + " = 0", null);
        userAdapter2 = new SimpleCursorAdapter(this, R.layout.two_line_list_item,
                userCursor, headers, new int[]{R.id.text1, R.id.text2, R.id.text3}, 0);

        // [Vlad] Сразу не оч понятно зачем нужна эта проверка
        if(!userFilter.getText().toString().isEmpty())
            userAdapter.getFilter().filter(userFilter.getText().toString());

        // установка слушателя изменения текста
        userFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) { }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            // при изменении текста выполняем фильтрацию
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userAdapter.getFilter().filter(s.toString());
            }
        });
        // устанавливаем провайдер фильтрации
        userAdapter.setFilterQueryProvider(new FilterQueryProvider() {
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
        header.setText("На плечи: " + String.valueOf(userCursor.getCount()));
        userList.setAdapter(userAdapter);
        userList2.setAdapter(userAdapter2);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        // close connection
        db.close();
        userCursor.close();
    }
}
