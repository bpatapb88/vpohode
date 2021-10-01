package com.simon.vpohode.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.simon.vpohode.CustomLookAdapter;
import com.simon.vpohode.R;
import com.simon.vpohode.RecyclerData;
import com.simon.vpohode.RecyclerDataLook;
import com.simon.vpohode.RecyclerItemClickListener;
import com.simon.vpohode.RecyclerViewAdapter;
import com.simon.vpohode.database.DBLooksFields;
import com.simon.vpohode.database.DatabaseHelper;
import com.simon.vpohode.managers.LayoutManager;

import java.util.ArrayList;

public class LooksActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<RecyclerDataLook> recyclerDataArrayList;

    private CustomLookAdapter lookAdapter;
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
                recyclerDataArrayList.add(new RecyclerDataLook(cursor));
            }while (cursor.moveToNext());
        }
        recyclerViewAdapter = new RecyclerViewAdapter(recyclerDataArrayList, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        lookAdapter = new CustomLookAdapter(this, cursor);
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,recyclerView,new RecyclerItemClickListener.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(view.getContext(), SelectLookActivity.class);
                intent.putExtra("term", term);
                intent.putExtra("look_id", recyclerDataArrayList.get(position).getId());
                view.getContext().startActivity(intent);
            }
            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

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
                RecyclerDataLook deletedCourse = recyclerDataArrayList.get(viewHolder.getAdapterPosition());

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
                recyclerViewAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());

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
                        recyclerViewAdapter.notifyItemInserted(position);
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

    public boolean insertToDB(RecyclerDataLook recyclerDataLook){
        ContentValues cv = new ContentValues();
        cv.put(DBLooksFields.ID.toFieldName(),recyclerDataLook.getId());
        cv.put(DBLooksFields.NAME.toFieldName(), recyclerDataLook.getName());
        cv.put(DBLooksFields.TERMMAX.toFieldName(), recyclerDataLook.getMax());
        cv.put(DBLooksFields.TERMMIN.toFieldName(), recyclerDataLook.getMin());
        cv.put(DBLooksFields.ITEMS.toFieldName(), recyclerDataLook.getItems());
        return db.insert(DatabaseHelper.TABLE_LOOKS,null,cv) > 0;
    }
}