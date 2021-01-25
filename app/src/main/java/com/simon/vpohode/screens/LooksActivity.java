package com.simon.vpohode.screens;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.simon.vpohode.Item;
import com.simon.vpohode.Managers.LookManager;
import com.simon.vpohode.Managers.LayoutManager;
import com.simon.vpohode.R;
import com.simon.vpohode.database.DatabaseHelper;

import java.util.ArrayList;

public class LooksActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private DatabaseHelper databaseHelper;
    private TextView textView1,textView2,textView3,textView4, title;
    private Button next,back;
    private Integer showinglook = 0;
    private ImageView imageView1,imageView2,imageView3,imageView4, imageView11, imageView21, imageView31, imageView41;
    private LinearLayout item3,item4;




    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_looks);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        title = findViewById(R.id.title);
        next = findViewById(R.id.next);
        back = findViewById(R.id.back);
        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);

        imageView11 = findViewById(R.id.imageView11);
        imageView21 = findViewById(R.id.imageView21);
        imageView31 = findViewById(R.id.imageView31);
        imageView41 = findViewById(R.id.imageView41);

        item3 = findViewById(R.id.item3);
        item4 = findViewById(R.id.item4);

        final Context context = this;
        Bundle extras = getIntent().getExtras();
        Double term = extras.getDouble("term");
        databaseHelper = new DatabaseHelper(getApplicationContext());
        db = databaseHelper.getReadableDatabase();
        final ArrayList<Item[]> looks = LookManager.getLooks(term, getApplicationContext());
        if(looks.size() > 0) {
            fillLook(looks.get(0),looks.size(),context);
        }else{
            Toast.makeText(this, "Нет подходящего набора", Toast.LENGTH_SHORT).show();
            finish();
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showinglook ++;
                if(showinglook < looks.size()) {
                    fillLook(looks.get(showinglook), looks.size(),context);
                }else{
                    Toast.makeText(view.getContext(), "Это Последний набор", Toast.LENGTH_SHORT).show();
                    showinglook --;
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showinglook --;
                if(showinglook >= 0) {
                    fillLook(looks.get(showinglook), looks.size(), context);
                }else{
                    Toast.makeText(view.getContext(), "Это самый первый набор", Toast.LENGTH_SHORT).show();
                    showinglook++;
                }
            }
        });

        findViewById(R.id.toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        LayoutManager.invisible(R.id.save,menu);
        LayoutManager.invisible(R.id.search,menu);
        LayoutManager.invisible(R.id.action_settings,menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        // close connection
        db.close();
    }

    public void fillLook (final Item[] look, Integer size, final Context context){
        title.setText("Мы подобрали для вас: " + (showinglook+1) + "/" + size);

        TextView[] textViews = {textView1,textView2,textView3,textView4};
        ImageView[] imageViewsFoto = {imageView1,imageView2,imageView3,imageView4};
        ImageView[] imageViewsColor = {imageView11,imageView21,imageView31,imageView41};

        for(int i = 0; i < look.length; i++){
            textViews[i].setText(look[i].getName() + " style " + look[i].getStyle());
            if(look[i].getFoto() != null){
                imageViewsFoto[i].setImageURI(Uri.parse(look[i].getFoto()));
            }
            imageViewsFoto[i].setOnClickListener(setListener(context,look[i].getId()));
            imageViewsColor[i].setBackgroundColor(look[i].getColor());
        }

        switch (look.length){
            case 2:
                item3.setVisibility(View.GONE);
                item4.setVisibility(View.GONE);
                break;
            case 3:
                item4.setVisibility(View.GONE);
                break;
        }
    }

    public View.OnClickListener setListener (final Context context, final Integer id){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,ConfigItem.class);
                intent.putExtra("id", Long.valueOf(id));
                startActivity(intent);
            }
        };
    }
}


