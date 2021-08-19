package com.simon.vpohode;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.simon.vpohode.database.DBFields;
import com.simon.vpohode.database.DatabaseHelper;
import com.simon.vpohode.managers.LookManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Look {
    private int id;
    private String name;
    private List<Item> items;
    private double maxTemp;
    private double minTemp;
    private static final String RAW_QUERY_PART = "SELECT * FROM " + DatabaseHelper.TABLE+ " WHERE " + DBFields.ID.toFieldName() + " IN (";

    public void readFromDB(int[] itemIds, Context context){
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        StringBuilder stringBuilder = new StringBuilder(RAW_QUERY_PART);
        for(int id : itemIds){
            stringBuilder.append(id);
            if(id != itemIds[itemIds.length-1]){
                stringBuilder.append(",");
            }else{
                stringBuilder.append(")");
            }
        }
        Cursor cursor = db.rawQuery(stringBuilder.toString(),null);
        if(this.items == null){
            this.items = new ArrayList<>();
            if(cursor.moveToFirst()){
                do{
                    this.items.add(LookManager.cursorToItem(cursor));
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        databaseHelper.close();
    }

    public Look() {
    }
    public Look(Cursor cursor){
        this.id = cursor.getInt(cursor.getColumnIndex("_id"));
        this.name = cursor.getString(cursor.getColumnIndex("name"));
        this.maxTemp = cursor.getDouble(cursor.getColumnIndex("max"));
        this.minTemp = cursor.getDouble(cursor.getColumnIndex("min"));
        String itemsInString = cursor.getString(cursor.getColumnIndex("items"));
        try{
            JSONObject jsonObject = new JSONObject(itemsInString);
            JSONArray jsonArray = jsonObject.getJSONArray("ids");

        }catch (Exception e){
            System.out.println("JSON parser of list of items was failed");
            e.printStackTrace();
        }

    }

    public int countOfItems(){
        return this.items.size();
    }

}
