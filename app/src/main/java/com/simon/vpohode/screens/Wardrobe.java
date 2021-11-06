package com.simon.vpohode.screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.simon.vpohode.CustomItemsAdapter;
import com.simon.vpohode.managers.LayoutManager;
import com.simon.vpohode.managers.ListViewManager;
import com.simon.vpohode.R;
import com.simon.vpohode.database.DatabaseHelper;

public class Wardrobe extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private CustomItemsAdapter topItemAdapter;
    private ListView listViewItems;
    private EditText searchItem;
    LayoutInflater inflater;
    private int sortBy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        LayoutManager.setTheme(prefs, getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe);
        searchItem = findViewById(R.id.search_item);
        listViewItems = findViewById(R.id.list);
        sortBy = 0; //default sort is first
        databaseHelper = new DatabaseHelper(this);
        //hidden keyboard by default
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        inflater = Wardrobe.this.getLayoutInflater();
    }

    public void goHome(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void addNewItem(View view){
        Intent intent = new Intent(this, ConfigItem.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        // open connection
        db = databaseHelper.getReadableDatabase();
        //get cursor from db

        // fill list depends of: is item top or not? 1=top 0=bottom
        topItemAdapter = LayoutManager.configListOfItems(this,db, sortBy);
        listViewItems.setAdapter(topItemAdapter);
        topItemAdapter.notifyDataSetChanged();
        listViewItems.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(view.getContext(), ConfigItem.class);
            intent.putExtra("id", id);
            view.getContext().startActivity(intent);
        });
        ListViewManager.optimizeListViewSize(listViewItems);
        searchItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                topItemAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Do nothing
            }
        });

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        // close connection
        topItemAdapter.getCursor().close();
        db.close();
    }

    public void showFilter(View view) {
        View layout = inflater.inflate(R.layout.fragment_content_wardrobe,null);
        Spinner sortBySpinner = layout.findViewById(R.id.sortBySpinner);
        String[] sortedOptions = getResources().getStringArray(R.array.sortOptions);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, sortedOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortBySpinner.setAdapter(adapter);
        sortBySpinner.setSelection(sortBy);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        builder.setTitle(getResources().getString(R.string.sorted_by));
        builder.setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.apply), (dialog, which) -> {
                    sortBy = sortBySpinner.getSelectedItemPosition();
                    topItemAdapter = LayoutManager.configListOfItems(view.getContext(),db, sortBy);
                    listViewItems.setAdapter(topItemAdapter);
                    topItemAdapter.notifyDataSetChanged();
                })
                .setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> dialog.cancel());

        builder.create().show();
    }
}
