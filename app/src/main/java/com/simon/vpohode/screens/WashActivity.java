package com.simon.vpohode.screens;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.preference.PreferenceManager;

import com.simon.vpohode.managers.LayoutManager;
import com.simon.vpohode.managers.ListViewManager;
import com.simon.vpohode.R;
import com.simon.vpohode.database.DBFields;
import com.simon.vpohode.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class WashActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private Cursor itemCursor;
    List<ItemInList> items;
    private ListView listViewWithCheckbox;
    ItemsListAdapter myItemsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        LayoutManager.setTheme(prefs, getTheme());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wash);

        //DB section
        databaseHelper = new DatabaseHelper(getApplicationContext());
        db = databaseHelper.getReadableDatabase();

        itemCursor =  DatabaseHelper.getCursorInWash(db);

        CardView washSelected = findViewById(R.id.wash_selected);
        CardView selectAll = findViewById(R.id.select_all);
        listViewWithCheckbox = findViewById(R.id.list);
        initItems();
        myItemsListAdapter = new ItemsListAdapter(this, items);
        listViewWithCheckbox.setAdapter(myItemsListAdapter);
        washSelected.setOnClickListener(view -> washSelectedMethod());
        selectAll.setOnClickListener(view -> {
            for (int i=0; i<items.size(); i++){
                items.get(i).checked = true;
            }
            listViewWithCheckbox.invalidateViews();
            Toast.makeText(WashActivity.this,
                    "Select all",
                    Toast.LENGTH_LONG).show();

        });
        ListViewManager.optimizeListViewSize(listViewWithCheckbox);


    }

    private void washSelectedMethod(){
        ContentValues cv = new ContentValues();

        final List<ItemInList> itemsNew = new ArrayList<>();
        for(int j = 0; j<items.size(); j++){
            itemsNew.add(items.get(j));
        }
        for (int i=itemsNew.size()-1; i+1>0; i--){
            if (itemsNew.get(i).isChecked()){
                items.remove(i);
                cv.put(DBFields.INWASH.toFieldName(), false);
                db.update(DatabaseHelper.TABLE, cv, DBFields.ID.toFieldName() + "=" + itemsNew.get(i).id, null);
                cv.clear();
            }
        }
        myItemsListAdapter.notifyDataSetChanged();
        listViewWithCheckbox.invalidateViews();
    }

    private void initItems(){
        items = new ArrayList<>();

        if(itemCursor.getCount() == 0) return;

        itemCursor.moveToFirst();
        do{
            int id = itemCursor.getInt(itemCursor.getColumnIndexOrThrow("_id"));
            String name = itemCursor.getString(itemCursor.getColumnIndexOrThrow("name"));
            String brand = itemCursor.getString(itemCursor.getColumnIndexOrThrow("brand"));
            int isTop = itemCursor.getInt(itemCursor.getColumnIndexOrThrow("istop"));
            int layer = itemCursor.getInt(itemCursor.getColumnIndexOrThrow("layer"));
            Drawable drawable = getIconByLayer(isTop,layer);

            ItemInList item = new ItemInList(drawable, name, false, brand,id);
            items.add(item);
        }while (itemCursor.moveToNext());

    }

    private Drawable getIconByLayer(int isTop, int layer ){
        Drawable drawable = null;
        if(isTop == 0){
            switch (layer){
                case 1: drawable = getDrawable(R.drawable.ic_layer1_bot);
                break;
                case 2: drawable = getDrawable(R.drawable.ic_layer2_bot);
                break;
                case 3: drawable = getDrawable(R.drawable.ic_layer_boots);
                break;
                default:
            }
        }else{
            switch (layer){
                case 1: drawable = getDrawable(R.drawable.ic_layer1);
                    break;
                case 2: drawable = getDrawable(R.drawable.ic_layer2);
                    break;
                case 3: drawable = getDrawable(R.drawable.ic_layer3);
                    break;
                default:
            }
        }
        if(drawable != null){
            drawable.setTint(getColor(R.color.colorPrimaryDark));
        }
        return drawable;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        // close connection
        databaseHelper.close();
        db.close();
    }

    public void goHome(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public class ItemInList {
        int id;
        boolean checked;
        Drawable itemDrawable;
        String itemName;
        String itemBrand;
        ItemInList(Drawable drawable, String name, boolean b, String brand, int idNumber){
            itemDrawable = drawable;
            itemName = name;
            checked = b;
            itemBrand = brand;
            id = idNumber;
        }

        public boolean isChecked(){
            return checked;
        }
    }

    static class ViewHolder {
        CheckBox checkBox;
        ImageView icon;
        TextView name;
        TextView brand;
    }

    public class ItemsListAdapter extends BaseAdapter {
        private Context context;
        private List<ItemInList> list;

        ItemsListAdapter(Context c, List<ItemInList> l) {
            context = c;
            list = l;
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        public boolean isChecked(int position) {
            return list.get(position).checked;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View rowView = convertView;

            // reuse views
            ViewHolder viewHolder = new ViewHolder();
            if (rowView == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                rowView = inflater.inflate(R.layout.activity_list_view_with_checkbox_item, null);
                viewHolder.checkBox = (CheckBox) rowView.findViewById(R.id.list_view_item_checkbox);
                viewHolder.icon = (ImageView) rowView.findViewById(R.id.imageView);
                viewHolder.name = (TextView) rowView.findViewById(R.id.list_view_item_text);
                viewHolder.brand = (TextView) rowView.findViewById(R.id.text3);
                rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) rowView.getTag();
            }
            viewHolder.icon.setImageDrawable(list.get(position).itemDrawable);
            viewHolder.checkBox.setChecked(list.get(position).checked);


            final String itemStr = list.get(position).itemName;
            viewHolder.name.setText(itemStr);

            final String brandStr = list.get(position).itemBrand;
            viewHolder.brand.setText(brandStr);

            viewHolder.checkBox.setTag(position);

            viewHolder.checkBox.setOnClickListener(view -> {
                boolean newState = !list.get(position).isChecked();
                list.get(position).checked = newState;
            });

            viewHolder.checkBox.setChecked(isChecked(position));

            return rowView;
        }
    }

}
