package com.simon.vpohode;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.simon.vpohode.database.DBFields;
import com.simon.vpohode.database.DatabaseHelper;
import com.simon.vpohode.managers.LookManager;

import java.util.ArrayList;
import java.util.List;

public class Look {
    public List<Item> items;
    private double temperature;
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
        items = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                items.add(LookManager.cursorToItem(cursor));
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        databaseHelper.close();
    }

}
