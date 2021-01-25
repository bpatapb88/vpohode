package com.simon.vpohode.Managers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;

import com.simon.vpohode.CustomAdapter;
import com.simon.vpohode.R;
import com.simon.vpohode.Styles;
import com.simon.vpohode.Templates;
import com.simon.vpohode.database.DBFields;
import com.simon.vpohode.database.DatabaseHelper;
import com.simon.vpohode.screens.ConfigItem;

import java.util.ArrayList;

public class LayoutManager {

    public static void invisible(int item, Menu menu){
        MenuItem menuItem = menu.findItem(item);
        menuItem.setVisible(false);
    }
    public static ArrayAdapter<Styles> spinnerConfig(Styles[] input, Context context){
        ArrayAdapter<Styles> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, input);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    public static ArrayAdapter<Templates> spinnerConfig(Templates[] input, Context context){
        ArrayAdapter<Templates> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, input);
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
        Cursor itemCursor =  DatabaseHelper.getCursoreByIsTop(db,istop);

        // which column will be in ListView
        String[] headers = new String[] {
                DBFields.NAME.toFieldName(),
                DBFields.TERMID.toFieldName(),
                DBFields.STYLE.toFieldName()};
        // create adapter, send cursor
        SimpleCursorAdapter ItemAdapter = new SimpleCursorAdapter(contex,
                R.layout.two_line_list_item,
                itemCursor,
                headers,
                new int[]{R.id.text1, R.id.text2, R.id.text3/*, R.id.imageView*/}, 0);
        // устанавливаем провайдер фильтрации

        CustomAdapter customAdapter = new CustomAdapter(contex,itemCursor);

        customAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                if (constraint == null || constraint.length() == 0) {
                    return db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " + DBFields.ISTOP.toFieldName() + " = " + istop, null);
                }
                else {
                    return db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " + DBFields.ISTOP.toFieldName() + " = " + istop + " AND " +
                            DBFields.NAME.toFieldName() + " like ?", new String[]{"%" + constraint.toString() + "%"});
                }
            }
        });

        return customAdapter;
    }
}
