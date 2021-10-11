package com.simon.vpohode;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.simon.vpohode.database.DBFields;
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
    private ArrayList<Item> itemsNew;

    public Look(Cursor cursor, SQLiteDatabase db) {
        this.id = cursor.getInt(cursor.getColumnIndex("_id"));
        this.name = cursor.getString(cursor.getColumnIndex("name"));
        this.max = cursor.getDouble(cursor.getColumnIndex("max"));
        this.min = cursor.getDouble(cursor.getColumnIndex("min"));
        this.items = cursor.getString(cursor.getColumnIndex("items"));
        this.itemsArray = fillItemsArray(this.items);
        this.itemsNew = fillItemsNew(this.itemsArray,db);
    }

    private ArrayList<Item> fillItemsNew(ArrayList<Integer> itemsArray, SQLiteDatabase db) {
        ArrayList<Item> result = new ArrayList<>();
        for(Integer itemId : itemsArray) {
            Item item = new Item();
            item = item.getItemById(itemId,db);
            result.add(item);
        }
        return result;
    }

    public Look(){}

    public Look getLookById(String id, Context context){
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_LOOKS + " where " + DBFields.ID.toFieldName() + " = " + id, null);
        cursor.moveToFirst();
        Look look = new Look(cursor, db);
        cursor.close();
        db.close();
        databaseHelper.close();
        return look;
    }



    private ArrayList<Integer> fillItemsArray(String itemsString){
        String[] itemsStringArray = itemsString.split(",");
        ArrayList<Integer> result = new ArrayList<>();
        for(String itemId: itemsStringArray){
            result.add(Integer.parseInt(itemId));
        }
        return result;
    }

}
