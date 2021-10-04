package com.simon.vpohode.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.simon.vpohode.R;
import com.simon.vpohode.RecyclerViewAdapter;
import com.simon.vpohode.database.DatabaseHelper;
import com.squareup.picasso.Picasso;

import java.io.File;

public class SelectLookActivity extends AppCompatActivity {
    private static final String CELSIUS_SYMBOL = "\u2103 ";
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private double term = 0;
    private int look_id = 0;
    LinearLayout rightLayout;
    LinearLayout leftLayout;
    private CardView[] cardViews;
    private ImageView[] imageViews;
    private TextView temperature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_look);
        temperature = findViewById(R.id.temperature);
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

        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getReadableDatabase();
        rightLayout = findViewById(R.id.right_layout);
        leftLayout = findViewById(R.id.left_layout);

        Bundle extras = getIntent().getExtras();
        term = extras.getDouble("term");

        look_id = extras.getInt("look_id");

        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_LOOKS + " WHERE _id =" + look_id, null);
        TextView nameOfLook = findViewById(R.id.name_look);


        if(cursor.moveToFirst()){
            double min = cursor.getDouble(cursor.getColumnIndex("min"));
            double max = cursor.getDouble(cursor.getColumnIndex("max"));
            String tempString = CELSIUS_SYMBOL +
                    RecyclerViewAdapter.prepareTemp(min) + ".." +
                    RecyclerViewAdapter.prepareTemp(max);
            temperature.setText(tempString);
            nameOfLook.setText(cursor.getString(cursor.getColumnIndex("name")));
            String items = cursor.getString(cursor.getColumnIndex("items"));
            Toast.makeText(this,"Items " + items,Toast.LENGTH_LONG).show();
            addItemCardsOnTheView(items);
        }

    }

    private void addItemCardsOnTheView(String items) {
        String cutLast;

        if(items.charAt(items.length() - 1) == ','){
            cutLast = items.substring(0, items.length() - 1);
        }else{
            cutLast = items;
        }

        Cursor itemsCursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE + " WHERE _id IN (" + cutLast + ")", null);
        int counter = 0;
        if(itemsCursor.moveToFirst()){
            do{
                LayoutInflater inflater = LayoutInflater.from(this);
                View view = inflater.inflate(R.layout.list_item,null);

                if(counter%2 == 0 ){
                    leftLayout.addView(view);
                    fillItemView(view,itemsCursor);
                }else{
                    rightLayout.addView(view);
                    fillItemView(view,itemsCursor);
                }

                cardViews[counter].setAlpha(1);
                cardViews[counter].setCardElevation(5);
                imageViews[counter].setBackgroundColor(itemsCursor.getInt(itemsCursor.getColumnIndex("color")));

                counter++;
                System.out.println(itemsCursor.getString(itemsCursor.getColumnIndex("name")));
            }while (itemsCursor.moveToNext());
        }
    }

    private void fillItemView(View view, Cursor itemsCursor) {
        ImageView photo = view.findViewById(R.id.imageViewPhoto);
        TextView name = view.findViewById(R.id.nameItem);
        TextView style = view.findViewById(R.id.styleItem);
        TextView brand = view.findViewById(R.id.brandItem);
        TextView usedItem = view.findViewById(R.id.usedItem);

        name.setText(itemsCursor.getString(itemsCursor.getColumnIndex("name")));
        String photoPath = itemsCursor.getString(itemsCursor.getColumnIndex("foto"));
        int styleInt = itemsCursor.getInt(itemsCursor.getColumnIndex("style"));
        System.out.println("style int " + styleInt);
        String styleString = getResources().getString(styleInt);
        String brandString = itemsCursor.getString(itemsCursor.getColumnIndex("brand"));
        if(brandString != null && !brandString.equals("")){
            brand.setText(brandString);
        }
        int usedInt = itemsCursor.getInt(itemsCursor.getColumnIndex("used"));
        String usedString = getResources().getString(R.string.used_times )+ ": " + usedInt;
        style.setText(styleString);
        usedItem.setText(usedString);
        if(photoPath != null){
            File fileFoto = new File(photoPath);
            Picasso.get().load(fileFoto).into(photo);
        }



    }

    public void goBack(View view){
        Intent intent = new Intent(this, LooksActivity.class);
        intent.putExtra("term", term);
        startActivity(intent);
    }
}