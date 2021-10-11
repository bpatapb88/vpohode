package com.simon.vpohode.screens;

import androidx.annotation.NonNull;
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
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.simon.vpohode.MyAdapter;
import com.simon.vpohode.R;
import com.simon.vpohode.Look;
import com.simon.vpohode.database.DBLooksFields;
import com.simon.vpohode.database.DatabaseHelper;
import com.simon.vpohode.managers.LayoutManager;

import java.util.ArrayList;

public class LooksActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private RecyclerView recyclerView;
    private ArrayList<Look> recyclerDataArrayList;
    double term;

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

        recyclerView = findViewById(R.id.list_of_looks);
        Bundle extras = getIntent().getExtras();
        term = extras.getDouble("term");
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
        SearchView sv = findViewById(R.id.mSearch);
        MyAdapter myAdapter = new MyAdapter(this,recyclerDataArrayList);
        LinearLayoutManager manager = new LinearLayoutManager(this);

        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(manager);
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
                // this method is called
                // when the item is moved.
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // this method is called when we swipe our item to right direction.
                // on below line we are getting the item at a particular position.
                Look deletedCourse = recyclerDataArrayList.get(viewHolder.getAdapterPosition());

                // below line is to get the position
                // of the item at that position.
                int position = viewHolder.getAdapterPosition();

                // this method is called when item is swiped.
                // below line is to remove item from our array list.
                recyclerDataArrayList.remove(viewHolder.getAdapterPosition());
                if(!deleteFromDB(deletedCourse.getId())){
                    Toast.makeText(viewHolder.itemView.getContext(), "Not found item with id - " +  deletedCourse.getId(), Toast.LENGTH_LONG).show();
                }

                // below line is to notify our item is removed from adapter.
                myAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());

                // below line is to display our snackbar with action.
                Snackbar.make(recyclerView, deletedCourse.getName(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // adding on click listener to our action of snack bar.
                        // below line is to add our item to array list with a position.
                        recyclerDataArrayList.add(position, deletedCourse);
                        insertToDB(deletedCourse);
                        // below line is to notify item is
                        // added to our adapter class.
                        myAdapter.notifyItemInserted(position);
                    }
                }).show();
            }
            // at last we are adding this
            // to our recycler view.
        }).attachToRecyclerView(recyclerView);


    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // close connection
        db.close();
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
}