package com.simon.vpohode.Managers;

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

import com.simon.vpohode.CustomAdapter;
import com.simon.vpohode.R;
import com.simon.vpohode.Styles;
import com.simon.vpohode.database.DBFields;
import com.simon.vpohode.database.DatabaseHelper;
import com.simon.vpohode.screens.ConfigItem;

public class LayoutManager {

    public static void invisible(int item, Menu menu){
        MenuItem menuItem = menu.findItem(item);
        menuItem.setVisible(false);
    }
    public static ArrayAdapter<String> spinnerConfig(Styles[] input, Context context){
        String[] inputString = new String[input.length];
        for(int i = 0; i<input.length;i++){
            inputString[i] = context.getResources().getString(input[i].toInt());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, inputString);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    public static AdapterView.OnItemClickListener ClickItem(final Context context, final Activity activity){
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, ConfigItem.class);
                intent.putExtra("id", id);
                activity.startActivity(intent);
            }
        };

    }

    public static CustomAdapter configListOfItems(Context contex, final SQLiteDatabase db, final int istop){
        //get cursor from db
        Cursor itemCursor =  DatabaseHelper.getCursorWardrobe(db);
        // create adapter, send cursor
        CustomAdapter customAdapter = new CustomAdapter(contex,itemCursor);
        // устанавливаем провайдер фильтрации
        customAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                if (constraint == null || constraint.length() == 0) {
                    return DatabaseHelper.getCursorWardrobe(db);
                }
                else {
                    return db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " + DBFields.INWASH.toFieldName() + " = 0 AND " + DBFields.NAME.toFieldName() + " like ?", new String[]{"%" + constraint.toString() + "%"});
                }
            }
        });
        return customAdapter;
    }


    public static CustomAdapter configListOfItemsInWash(Context contex, final SQLiteDatabase db){
        //get cursor from db
        Cursor itemCursor =  DatabaseHelper.getCursorInWash(db);
        // create adapter, send cursor
        CustomAdapter customAdapter = new CustomAdapter(contex,itemCursor);

        return customAdapter;
    }

    public static void setTheme(SharedPreferences preferences, Resources.Theme theme){
        if(preferences.getBoolean("theme", true)){
            theme.applyStyle(R.style.OverlayThemeDark,true);
        }else{
            theme.applyStyle(R.style.AppTheme,true);
        }
    }
}
