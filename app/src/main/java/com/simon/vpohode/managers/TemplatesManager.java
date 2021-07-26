package com.simon.vpohode.managers;

import android.content.Context;
import android.database.Cursor;
import android.widget.ArrayAdapter;

import com.simon.vpohode.Item;

public class TemplatesManager {
    private Context context;
    private Cursor cursor;

    public TemplatesManager(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }


    public ArrayAdapter<String> spinnerConfig(){
        String[] array = new String[cursor.getCount()];
        cursor.moveToFirst();
        int counter = 0;
        do{
            array[counter++] = cursor.getString(cursor.getColumnIndex("name"));
        }while (cursor.moveToNext());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    public Item getItemFromTemplate(String templateName){
        cursor.moveToFirst();
        do{
            if(templateName.equals(cursor.getString(cursor.getColumnIndex("name")))){
                return LookManager.cursorToItem(cursor);
            }
        }while (cursor.moveToNext());

        return null;
    }
}
