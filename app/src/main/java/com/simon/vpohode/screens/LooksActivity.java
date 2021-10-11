package com.simon.vpohode.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.simon.vpohode.MyAdapter;
import com.simon.vpohode.R;
import com.simon.vpohode.Look;
import com.simon.vpohode.comparators.LookEmanComparator;
import com.simon.vpohode.comparators.LookIdComparator;
import com.simon.vpohode.comparators.LookNameComparator;
import com.simon.vpohode.comparators.LookSizeComparator;
import com.simon.vpohode.comparators.LookWarmComparator;
import com.simon.vpohode.database.DBLooksFields;
import com.simon.vpohode.database.DatabaseHelper;
import com.simon.vpohode.managers.LayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LooksActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private RecyclerView recyclerView;
    private ArrayList<Look> recyclerDataArrayList;
    double term;
    private LayoutInflater inflater;
    private int sortBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        LayoutManager.setTheme(prefs, getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_looks);
        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getReadableDatabase();
        //hidden keyboard by default
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        sortBy = 0;
        recyclerView = findViewById(R.id.list_of_looks);
        Bundle extras = getIntent().getExtras();
        term = extras.getDouble("term");
        inflater = this.getLayoutInflater();
    }

    @Override
    public void onResume() {
        super.onResume();
        cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_LOOKS, null);
        recyclerDataArrayList = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                recyclerDataArrayList.add(new Look(cursor,db));
            }while (cursor.moveToNext());
        }
        refreshRecyclerView(new LookIdComparator());
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // close connection
        db.close();
    }

    private void refreshRecyclerView(Comparator<Look> comparator){
        Collections.sort(recyclerDataArrayList, comparator);
        MyAdapter myAdapter = new MyAdapter(this,recyclerDataArrayList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(manager);
        SearchView sv = findViewById(R.id.mSearch);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                myAdapter.getFilter().filter(newText);
                return false;
            }
        });
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Look deletedCourse = recyclerDataArrayList.get(viewHolder.getAdapterPosition());
                int position = viewHolder.getAdapterPosition();
                recyclerDataArrayList.remove(viewHolder.getAdapterPosition());
                if(!deleteFromDB(deletedCourse.getId())){
                    Toast.makeText(viewHolder.itemView.getContext(), "Not found item with id - " +  deletedCourse.getId(), Toast.LENGTH_LONG).show();
                }
                myAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                Snackbar.make(recyclerView, deletedCourse.getName(), Snackbar.LENGTH_LONG).setAction(getResources().getString(R.string.Undo), v -> {
                    recyclerDataArrayList.add(position, deletedCourse);
                    insertToDB(deletedCourse);
                    myAdapter.notifyItemInserted(position);
                }).show();
            }
        }).attachToRecyclerView(recyclerView);


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

    public boolean deleteFromDB(int id){
        return db.delete(DatabaseHelper.TABLE_LOOKS, DBLooksFields.ID.toFieldName() + "="+id,null) > 0;
    }

    public boolean insertToDB(Look look){
        ContentValues cv = new ContentValues();
        cv.put(DBLooksFields.ID.toFieldName(), look.getId());
        cv.put(DBLooksFields.NAME.toFieldName(), look.getName());
        cv.put(DBLooksFields.TERMMAX.toFieldName(), look.getMax());
        cv.put(DBLooksFields.TERMMIN.toFieldName(), look.getMin());
        cv.put(DBLooksFields.ITEMS.toFieldName(), look.getItems());
        return db.insert(DatabaseHelper.TABLE_LOOKS,null,cv) > 0;
    }

    public void SortLooks(View view) {
        View layout = inflater.inflate(R.layout.fragment_content,null);
        Spinner sortBySpinner = layout.findViewById(R.id.sortBySpinner);
        String[] sortedOptions = getResources().getStringArray(R.array.sortOptionsLook);
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
                    switch (sortBy) {
                        case 1:
                            refreshRecyclerView(new LookNameComparator());
                            break;
                        case 2:
                            refreshRecyclerView(new LookEmanComparator());
                            break;
                        case 3:
                            refreshRecyclerView(new LookSizeComparator());
                            break;
                        case 4:
                            refreshRecyclerView(new LookWarmComparator());
                            break;
                        default:
                            refreshRecyclerView(new LookIdComparator());
                    }

                })
                .setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> dialog.cancel());

        builder.create().show();
    }
}