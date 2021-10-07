package com.simon.vpohode.managers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FilterQueryProvider;

import com.simon.vpohode.CustomItemsAdapter;
import com.simon.vpohode.R;
import com.simon.vpohode.Styles;
import com.simon.vpohode.database.DBFields;
import com.simon.vpohode.database.DatabaseHelper;
import com.simon.vpohode.screens.ConfigItem;

public class LayoutManager {

    public static ArrayAdapter<String> spinnerConfig(Styles[] input, Context context){
        String[] inputString = new String[input.length];
        for(int i = 0; i<input.length;i++){
            inputString[i] = context.getResources().getString(input[i].toInt());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, inputString);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    public static CustomItemsAdapter configListOfItems(Context contex, final SQLiteDatabase db, int sortBy){
        //get cursor from db
        Cursor itemCursor =  DatabaseHelper.getCursorWardrobe(db, sortBy);

        // create adapter, send cursor
        CustomItemsAdapter customItemsAdapter = new CustomItemsAdapter(contex,itemCursor);

        // устанавливаем провайдер фильтрации
        customItemsAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                if (constraint == null || constraint.length() == 0) {
                    return DatabaseHelper.getCursorWardrobe(db, sortBy);
                }
                else {
                    return db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " + DBFields.INWASH.toFieldName() + " = 0 AND " + DBFields.NAME.toFieldName() + " like ? " + DatabaseHelper.getOrderString(sortBy), new String[]{"%" + constraint.toString() + "%"});
                }
            }
        });
        return customItemsAdapter;
    }


    public static void setTheme(SharedPreferences preferences, Resources.Theme theme){
        if(preferences.getBoolean("theme", false)){
            theme.applyStyle(R.style.OverlayThemeDark,true);
        }else{
            theme.applyStyle(R.style.AppTheme,true);
        }
    }
}
