package com.simon.vpohode.screens;

import androidx.appcompat.app.AppCompatActivity;

import com.simon.vpohode.CustomItemsAdapter;
import com.simon.vpohode.Item;
import com.simon.vpohode.R;
import com.simon.vpohode.database.DBFields;
import com.simon.vpohode.database.DatabaseHelper;
import com.simon.vpohode.managers.ListViewManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class AddItemToLookActivity extends AppCompatActivity {

    private double term;
    private Integer[] look;
    private String nameLook;

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private ListView listViewItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_to_look);

        Bundle extras = getIntent().getExtras();
        term = extras.getDouble("term");
        nameLook = extras.getString("name");
        look = (Integer[]) extras.get("look");
        listViewItems = findViewById(R.id.list);
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
        CustomItemsAdapter customItemsAdapter = new CustomItemsAdapter(this,cursor);
        listViewItems.setAdapter(customItemsAdapter);
        ListViewManager.optimizeListViewSize(listViewItems);
        listViewItems.setOnItemClickListener((parent, view, position, id) -> {

            int index = AddLookActivity.currentLook;
            System.out.println("Info------ \nindex" + index +
            "\nAddLookActivity.looks.get(index).length " + AddLookActivity.looks.get(index).length
            );
            Item item = new Item().getItemById((int) id, db);
            Item[] items = new Item[AddLookActivity.looks.get(index).length + 1];
            for(int i=0; i < AddLookActivity.looks.get(index).length; i++){
                items[i] = AddLookActivity.looks.get(index)[i];
            }
            items[AddLookActivity.looks.get(index).length] = item;
            AddLookActivity.looks.set(index, items);
            finish();
        });
    }

    public void finish(View view) {
        this.finish();
    }

    public void testFunction(View view) {
        System.out.println("Test access to previos activity \ncurrent look - " + AddLookActivity.currentLook);
        System.out.println("Count of looks - " + AddLookActivity.looks.size());
    }
}