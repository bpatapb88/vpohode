package com.simon.vpohode.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.simon.vpohode.CustomItemsAdapter;
import com.simon.vpohode.Item;
import com.simon.vpohode.R;
import com.simon.vpohode.database.DBFields;
import com.simon.vpohode.database.DatabaseHelper;
import com.simon.vpohode.managers.LayoutManager;
import com.simon.vpohode.managers.ListViewManager;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class AddItemToLookActivity extends AppCompatActivity {
    private Integer[] look;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private ListView listViewItems;
    private EditText searchItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        LayoutManager.setTheme(prefs, getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_to_look);
        Bundle extras = getIntent().getExtras();
        look = (Integer[]) extras.get("look");
        listViewItems = findViewById(R.id.list);
        searchItem = findViewById(R.id.search_item);
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
        Cursor cursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " + DBFields.ID.toFieldName() + " NOT IN (" + stringBuilder.toString() + ") AND " + DBFields.INWASH.toFieldName() + " = 0", null);
        Toast.makeText(this,"We found " + cursor.getCount() + " items", Toast.LENGTH_SHORT).show();
        CustomItemsAdapter customItemsAdapter = new CustomItemsAdapter(this,cursor);
        customItemsAdapter.setFilterQueryProvider(constraint -> {
            if (constraint == null || constraint.length() == 0) {
                return db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " + DBFields.ID.toFieldName() + " NOT IN (" + stringBuilder.toString() + ") AND " + DBFields.INWASH.toFieldName() + " = 0", null);
            }
            else {
                return db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " + DBFields.INWASH.toFieldName() + " = 0 AND " + DBFields.ID.toFieldName() + " NOT IN (" + stringBuilder.toString() + ") AND " + DBFields.NAME.toFieldName() + " like ?", new String[]{"%" + constraint.toString() + "%"});
            }
        });
        listViewItems.setAdapter(customItemsAdapter);
        customItemsAdapter.notifyDataSetChanged();
        ListViewManager.optimizeListViewSize(listViewItems);
        listViewItems.setOnItemClickListener((parent, view, position, id) -> {
            int index = AddLookActivity.currentLook;
            Item item = new Item().getItemById((int) id, db);
            Item[] items;
            if(!AddLookActivity.potential_looks.isEmpty()){
                items = new Item[AddLookActivity.potential_looks.get(index).length + 1];
                for(int i = 0; i < AddLookActivity.potential_looks.get(index).length; i++){
                    items[i] = AddLookActivity.potential_looks.get(index)[i];
                }
                items[AddLookActivity.potential_looks.get(index).length] = item;
                AddLookActivity.potential_looks.set(index, items);
            }else{
                items = new Item[1];
                items[0] = item;
                AddLookActivity.potential_looks.add(items);
            }
            finish();
        });

        searchItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                customItemsAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Do nothing
            }
        });
    }
    public void goBack(View view) {
        this.finish();
    }
}