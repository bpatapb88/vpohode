package com.simon.vpohode.screens;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.simon.vpohode.APIs;
import com.simon.vpohode.BuildConfig;
import com.simon.vpohode.Item;
import com.simon.vpohode.Look;
import com.simon.vpohode.MyAdapter;
import com.simon.vpohode.R;
import com.simon.vpohode.database.DBLooksFields;
import com.simon.vpohode.database.DatabaseHelper;
import com.simon.vpohode.managers.LayoutManager;
import com.simon.vpohode.managers.LookManager;
import com.squareup.picasso.Picasso;

import java.io.File;

public class SelectLookActivity extends AppCompatActivity {
    private static final String CELSIUS_SYMBOL = "\u2103 ";
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private int lookId = 0;
    LinearLayout rightLayout;
    LinearLayout leftLayout;
    private CardView[] cardViews;
    private ImageView[] imageViews;
    private TextView temperature;
    private LookManager lookManager;

    private FloatingActionButton fb;
    private FloatingActionButton useLookButton;
    private FloatingActionButton editLookButton;
    private FloatingActionButton deleteLookButton;
    private boolean isFabShrieked;
    private FloatingActionButton[] floatArray;

    /**
     * part related to advertising
     */
    private InterstitialAd interstitialAd;
    private AlertDialog.Builder builder;
    private Look look;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        LayoutManager.setTheme(prefs, getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_look);

        //ad start
        MobileAds.initialize(this, APIs.GOOGLE_APPID);
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(APIs.GOOGLE_ADMOD);
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);
        //finish ad

        //close ad start
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                try{
                    Intent intent = new Intent(SelectLookActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }catch (Exception ignored){
                    ignored.printStackTrace();
                }
            }
        });
        builder = new AlertDialog.Builder(this);

        lookManager = new LookManager();

        temperature = findViewById(R.id.temperature);
        fb = findViewById(R.id.fab);
        useLookButton = findViewById(R.id.use_look);
        editLookButton = findViewById(R.id.edit_look);
        deleteLookButton = findViewById(R.id.delete_look);
        floatArray = new FloatingActionButton[]{useLookButton,editLookButton,deleteLookButton};
        isFabShrieked = false;
        changeFloatButtons(floatArray);

        fb.setOnClickListener(v -> {
            changeFloatButtons(floatArray);
        });
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
        lookId = extras.getInt("look_id");

        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_LOOKS + " WHERE _id =" + lookId, null);
        TextView nameOfLook = findViewById(R.id.name_look);
        if(cursor.moveToFirst()){
            look = new Look(cursor,db);
            String tempString = CELSIUS_SYMBOL +
                    MyAdapter.prepareTemp(look.getMin()) + ".." +
                    MyAdapter.prepareTemp(look.getMax());
            temperature.setText(tempString);
            nameOfLook.setText(look.getName());
            addItemCardsOnTheView(look.getItems());
        }

        builder = new AlertDialog.Builder(this);

        useLookButton.setOnClickListener(v -> {
            int itemInWashId = lookManager.isLookUseable(look);
            if(itemInWashId >= 0){
                Item item = new Item().getItemById(itemInWashId,db);
                Toast.makeText(v.getContext(), item.getName() + " " + getResources().getString(R.string.inwash), Toast.LENGTH_SHORT).show();
                return;
            }

            builder.setMessage(R.string.dialog_message).setTitle(R.string.dialog_title);
            builder.setCancelable(false)
                    .setPositiveButton(SelectLookActivity.this.getResources().getString(R.string.yes), (dialog, which) -> {
                        lookManager.useLook(look, SelectLookActivity.this);
                        if (interstitialAd.isLoaded()) {
                            interstitialAd.show();
                        } else {
                            SelectLookActivity.this.finish();
                        }
                    })
                    .setNegativeButton(SelectLookActivity.this.getResources().getString(R.string.no), (dialog, which) -> dialog.cancel());

            AlertDialog alert = builder.create();
            alert.setTitle(R.string.dialog_title);
            alert.show();
        });

        editLookButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AddLookActivity.class);
            intent.putExtra("look_id", lookId + "");
            startActivity(intent);
        });

        deleteLookButton.setOnClickListener(v -> {
            db.delete(DatabaseHelper.TABLE_LOOKS, DBLooksFields.ID.toFieldName() + "="+lookId,null);
            finish();
        });

    }

    private void changeFloatButtons(FloatingActionButton[] floatArray) {
        float rotation;
        if(isFabShrieked){
            rotation = 180f;
            for(FloatingActionButton button : floatArray){
                button.show();
            }
        }else{
            rotation = 0f;
            for(FloatingActionButton button : floatArray){
                button.hide();
            }
        }
        ViewCompat.animate(fb).
                rotation(rotation).
                start();
        isFabShrieked = !isFabShrieked;
    }

    private void addItemCardsOnTheView(String items) {
        Cursor itemsCursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE + " WHERE _id IN (" + items + ")", null);
        int counter = 0;
        if(itemsCursor.moveToFirst()){
            do{
                LayoutInflater inflater = LayoutInflater.from(this);
                View view = inflater.inflate(R.layout.list_item,null);
                if(counter%2 == 0 ){
                    leftLayout.addView(view);
                }else{
                    rightLayout.addView(view);
                }
                fillItemView(view,itemsCursor);
                cardViews[counter].setAlpha(1);
                cardViews[counter].setCardElevation(5);
                imageViews[counter].setBackgroundColor(itemsCursor.getInt(itemsCursor.getColumnIndex("color")));
                counter++;
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
        finish();
    }
}