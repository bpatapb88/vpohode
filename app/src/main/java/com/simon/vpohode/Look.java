package com.simon.vpohode;

import android.database.Cursor;

import com.simon.vpohode.database.DatabaseHelper;

import java.util.ArrayList;

import lombok.Data;

@Data
public class Look {
    private int id;
    private String name;
    private double max;
    private double min;
    private String items;
    private ArrayList<Integer> itemsArray;

    public Look(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndex("_id"));
        this.name = cursor.getString(cursor.getColumnIndex("name"));
        this.max = cursor.getDouble(cursor.getColumnIndex("max"));
        this.min = cursor.getDouble(cursor.getColumnIndex("min"));
        this.items = cursor.getString(cursor.getColumnIndex("items"));
        this.itemsArray = getItemsArray(this.items);
    }

    private ArrayList<Integer> getItemsArray(String itemsString){
        String[] itemsStringArray = itemsString.split(",");
        ArrayList<Integer> result = new ArrayList<>();
        for(String itemId: itemsStringArray){
            result.add(Integer.parseInt(itemId));
        }
        return result;
    }
}
