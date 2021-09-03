package com.simon.vpohode;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.simon.vpohode.database.DatabaseHelper;

public class CustomLookAdapter extends CursorAdapter {
    private final LayoutInflater mLayoutInflater;

    public CustomLookAdapter(Context context, Cursor c) {
        super(context, c);
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mLayoutInflater.inflate(R.layout.look_form, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        CardView cardView = view.findViewById(R.id.cardItem);
        int index = cursor.getInt(cursor.getColumnIndex("_id"));

        //Look look = new Look(cursor);
        TextView textName = view.findViewById(R.id.name);
        textName.setText(cursor.getString(cursor.getColumnIndex("name")));
        TextView temp = view.findViewById(R.id.temp);
        String tempText = prepareTemp(cursor.getDouble(cursor.getColumnIndex("min")))
                + ".."
                + prepareTemp(cursor.getDouble(cursor.getColumnIndex("max")));
        temp.setText(tempText);
        TextView descriptionTextView = view.findViewById(R.id.description);
        String description = "Items - " + cursor.getString(cursor.getColumnIndex("items"));
        descriptionTextView.setText(description);
    }

    private String prepareTemp(Double value){
        return value >=0 ? "+" + value : value.toString();
    }
}
