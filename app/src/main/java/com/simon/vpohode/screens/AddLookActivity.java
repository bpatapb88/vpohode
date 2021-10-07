package com.simon.vpohode.screens;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.RangeSlider;
import com.simon.vpohode.Item;
import com.simon.vpohode.Look;
import com.simon.vpohode.R;
import com.simon.vpohode.database.DBLooksFields;
import com.simon.vpohode.database.DatabaseHelper;
import com.simon.vpohode.managers.LayoutManager;
import com.simon.vpohode.managers.LookManager;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class AddLookActivity extends AppCompatActivity {
    private RangeSlider rangeSlider;
    private static final String CELSIUS_SYMBOL = "\u2103 ";
    static List<Item[]> looks;
    private double term;
    private boolean isFabShrinked;
    private FloatingActionButton saveLookButton;
    private FloatingActionButton addItemButton;
    private FloatingActionButton refreshLookButton;
    private FloatingActionButton fb;
    static int currentLook = 0;
    private LinearLayout leftLayout;
    private LinearLayout rightLayout;
    private EditText nameLook;
    private CardView[] cardViews;
    private ImageView[] imageViews;
    private LookManager lookManager;
    private ImageView tempImage;
    private ImageView backImage;
    private String lookId;
    private Look editLook;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        LayoutManager.setTheme(prefs, getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_look);
        fb = findViewById(R.id.fab);
        isFabShrinked = true;
        saveLookButton = findViewById(R.id.save_look);
        saveLookButton.hide();
        addItemButton = findViewById(R.id.add_item);
        addItemButton.hide();
        refreshLookButton = findViewById(R.id.refresh_look);
        refreshLookButton.hide();
        nameLook = findViewById(R.id.name_look);
        tempImage = findViewById(R.id.temp_image);
        rangeSlider = findViewById(R.id.range_slider);
        rangeSlider.setLabelFormatter(value -> CELSIUS_SYMBOL + (int)value);
        leftLayout = findViewById(R.id.left_layout);
        rightLayout = findViewById(R.id.right_layout);
        backImage = findViewById(R.id.back);

        cardViews = new CardView[]{findViewById(R.id.colorCard1),
                findViewById(R.id.colorCard2),
                findViewById(R.id.colorCard3),
                findViewById(R.id.colorCard4),
                findViewById(R.id.colorCard5),
                findViewById(R.id.colorCard6)};
        imageViews = new ImageView[]{findViewById(R.id.colorView1),
                findViewById(R.id.colorView2),
                findViewById(R.id.colorView3),
                findViewById(R.id.colorView4),
                findViewById(R.id.colorView5),
                findViewById(R.id.colorView6)};

        Bundle extras = getIntent().getExtras();
        term = extras.getDouble("term");
        lookId = extras.getString("look_id");

        lookManager = new LookManager();
        if(lookId == null){
            looks = lookManager.getLooks(term, this);
        }else {
            looks = lookManager.getLooks(lookId,this);
            editLook = new Look().getLookById(lookId,this);
            nameLook.setText(editLook.getName());
            rangeSlider.setValues((float)editLook.getMin(), (float)editLook.getMax());
        }

        backImage.setOnClickListener(v -> {
            finish();
        });
        fb.setOnClickListener(v -> {
            if(isFabShrinked){
                ViewCompat.animate(fb).
                        rotation(180f).
                        start();
                isFabShrinked = false;
                saveLookButton.show();
                addItemButton.show();
                if(editLook == null){
                    refreshLookButton.show();
                }
            }else{
                hideFabs();
            }

        });
        tempImage.setOnClickListener(v -> {
            View itemView = View.inflate(v.getContext(),R.layout.set_temperature_range,null);
            EditText minEditText = itemView.findViewById(R.id.min_temp);
            EditText maxEditText = itemView.findViewById(R.id.max_temp);
            List<Float> values = rangeSlider.getValues();
            minEditText.setText(Math.round(values.get(0)) + "");
            maxEditText.setText(Math.round(values.get(1)) + "");
            AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                    .setTitle("Задай диапазон температур")
                    .setView(itemView)
                    .setPositiveButton("OK", (dialogInterface, i) -> {
                        int minValue = Integer.parseInt(minEditText.getText().toString());
                        int maxValue = Integer.parseInt(maxEditText.getText().toString());
                        if(minValue >= -35 && maxValue <= 35 && minValue<=maxValue){
                            rangeSlider.setValues((float)minValue,(float)maxValue);
                        }
                        Log.d("onclick","range value is: "+ minValue + " - " + maxValue);
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
            dialog.show();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {
        super.onResume();
        if(!looks.isEmpty()){
            leftLayout.removeAllViewsInLayout();
            rightLayout.removeAllViewsInLayout();
            Item[] look = looks.get(currentLook);
            fillLook(look);
        }else{
            String lacks = lookManager.message.substring(0, lookManager.message.length() - 1);
            Toast.makeText(this, getResources().getString(R.string.no_match) + " " + lacks, Toast.LENGTH_SHORT).show();
        }

        if(term < 33 && term > -33){
            rangeSlider.setValues((float)term-2, (float)term + 2);
        }else if(term >= 33){
            rangeSlider.setValues(31.f, 35.f);
        }else {
            rangeSlider.setValues(-35.f, -31.f);
        }

        refreshLookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((looks.size() - currentLook) > 1) {
                    leftLayout.removeAllViewsInLayout();
                    rightLayout.removeAllViewsInLayout();
                    currentLook++;
                    Item[] look = looks.get(currentLook);
                    AddLookActivity.this.fillLook(look);
                } else if ((looks.size() - currentLook) == 1 && looks.size() > 1) {
                    leftLayout.removeAllViewsInLayout();
                    rightLayout.removeAllViewsInLayout();
                    currentLook = 0;
                    Item[] look = looks.get(currentLook);
                    AddLookActivity.this.fillLook(look);
                } else {
                    double currentTemp = (rangeSlider.getValues().get(0) + rangeSlider.getValues().get(1)) / 2;
                    if (currentTemp != term) {
                        looks = lookManager.getLooks(currentTemp, AddLookActivity.this.getApplicationContext());
                        if(!looks.isEmpty()){
                            term = currentTemp;
                            this.onClick(v);
                        }
                    }
                }
                AddLookActivity.this.hideFabs();
            }
        });

        addItemButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddItemToLookActivity.class);
            Integer[] itemsID = null;
            if(!looks.isEmpty()){
                itemsID = Stream.of(looks.get(currentLook)).map(Item::getId).toArray(Integer[]::new);
            }
            intent.putExtra("look", itemsID);
            startActivity(intent);
        });

        saveLookButton.setOnClickListener(v -> {
            if(nameLook.getText().toString().equals("")){
                Toast.makeText(this,"Name is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            int countLeft = leftLayout.getChildCount();
            int countRight = rightLayout.getChildCount();
            TextView templeView;
            StringBuilder items = new StringBuilder();
            for(int i = 0; i < countLeft; i++){
                templeView = leftLayout.getChildAt(i).findViewById(R.id.item_id);
                items.append(templeView.getText().toString()).append(",");
            }
            for(int i = 0; i < countRight; i++){
                templeView = rightLayout.getChildAt(i).findViewById(R.id.item_id);
                items.append(templeView.getText().toString()).append(",");
            }

            DatabaseHelper databaseHelper = new DatabaseHelper(v.getContext());
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(DBLooksFields.NAME.toFieldName(), nameLook.getText().toString());
            cv.put(DBLooksFields.TERMMAX.toFieldName(), rangeSlider.getValues().get(1));
            cv.put(DBLooksFields.TERMMIN.toFieldName(), rangeSlider.getValues().get(0));
            cv.put(DBLooksFields.ITEMS.toFieldName(), items.substring(0, items.length() - 1));
            if(!isNameExistCheck(nameLook.getText().toString(),db) || editLook != null && editLook.getName().equals(nameLook.getText().toString())){
                if(editLook == null){
                    db.insert(DatabaseHelper.TABLE_LOOKS,null,cv);
                }else{
                    db.update(DatabaseHelper.TABLE_LOOKS, cv, DBLooksFields.ID.toFieldName() + "=" + editLook.getId(), null);
                }
                db.close();
                databaseHelper.close();
                Intent intent = new Intent(this, LooksActivity.class);
                intent.putExtra("term", term);
                startActivity(intent);
            }else{
                Toast.makeText(this,"This name already exist", Toast.LENGTH_SHORT).show();
                db.close();
                databaseHelper.close();
            }
        });
    }

    private boolean isNameExistCheck(String name, SQLiteDatabase db){
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_LOOKS, null);
        if(cursor.moveToFirst()){
            do{
                if(cursor.getString(cursor.getColumnIndex("name")).equals(name)){
                    return true;
                }
            }while (cursor.moveToNext());
        }
        return false;
    }

    private void fillLook(Item[] look){
        
        for(int i = 0 ; i < look.length; i++){
            View itemView = View.inflate(this,R.layout.list_item_edit,null);
            TextView nameTextView = itemView.findViewById(R.id.nameItem);
            TextView itemIdTextView = itemView.findViewById(R.id.item_id);
            itemIdTextView.setText(look[i].getId() + "");
            nameTextView.setText(look[i].getName());
            ImageView deleteItem = itemView.findViewById(R.id.delete_item);

            cardViews[i].setAlpha(1);
            cardViews[i].setCardElevation(5);
            imageViews[i].setBackgroundColor(look[i].getColor());

            ImageView imageFoto = itemView.findViewById(R.id.imageViewPhoto);
            if(look[i].getFoto() != null){
                File fileFoto = new File(look[i].getFoto());
                Picasso.get().load(fileFoto).into(imageFoto);
            }
            if(i%2 == 0){
                deleteItem.setOnClickListener(v -> {
                    removeItemFromLook(Integer.parseInt(itemIdTextView.getText().toString()));
                    leftLayout.removeView(itemView);
                });
                leftLayout.addView(itemView);
            }else{
                deleteItem.setOnClickListener(v -> {
                    removeItemFromLook(Integer.parseInt(itemIdTextView.getText().toString()));
                    rightLayout.removeView(itemView);
                });
                rightLayout.addView(itemView);
            }
        }
    }

    private void hideFabs(){
        ViewCompat.animate(fb).
                rotation(0f).
                start();
        isFabShrinked = true;
        saveLookButton.hide();
        addItemButton.hide();
        refreshLookButton.hide();
    }

    private void removeItemFromLook(int itemId){
        List<Item> listItems = Arrays.asList(looks.get(currentLook));
        int length = listItems.size();
        int index = 0;
        Item[] newArray = new Item[length-1];
        for(int i =0; i<length; i++){
            if(listItems.get(i).getId() != itemId){
                newArray[index] = listItems.get(i);
                index++;
            }
        }
        looks.set(currentLook,newArray);
    }
}