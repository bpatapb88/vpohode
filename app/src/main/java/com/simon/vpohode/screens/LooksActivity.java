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
    private Toast mToast;



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

        switch (look.length){
            case 2:
                textView1.setText(look[0].getName() + " style " + look[0].getStyle());
                imageView1.setImageURI(Uri.parse(look[0].getFoto()));
                imageView1.setOnClickListener(setListener(context,look[0].getId()));
                imageView11.setBackgroundColor(look[0].getColor());

                textView2.setText(look[1].getName() + " style " + look[1].getStyle());
                imageView2.setImageURI(Uri.parse(look[1].getFoto()));
                imageView2.setOnClickListener(setListener(context,look[1].getId()));
                imageView21.setBackgroundColor(look[1].getColor());

                item3.setVisibility(View.GONE);
                item4.setVisibility(View.GONE);
                break;
            case 3:
                textView1.setText(look[0].getName() + " style " + look[0].getStyle());
                imageView1.setImageURI(Uri.parse(look[0].getFoto()));
                imageView1.setOnClickListener(setListener(context,look[0].getId()));
                imageView11.setBackgroundColor(look[0].getColor());

                textView2.setText(look[1].getName() + " style " + look[1].getStyle());
                imageView2.setImageURI(Uri.parse(look[1].getFoto()));
                imageView2.setOnClickListener(setListener(context,look[1].getId()));
                imageView21.setBackgroundColor(look[1].getColor());

                textView3.setText(look[2].getName() + " style " + look[2].getStyle());
                imageView3.setImageURI(Uri.parse(look[2].getFoto()));
                imageView3.setOnClickListener(setListener(context,look[2].getId()));
                imageView31.setBackgroundColor(look[2].getColor());

                item4.setVisibility(View.GONE);
                break;
            case 4:
                textView1.setText(look[0].getName() + " style " + look[0].getStyle());
                imageView1.setImageURI(Uri.parse(look[0].getFoto()));
                imageView1.setOnClickListener(setListener(context,look[0].getId()));
                imageView11.setBackgroundColor(look[0].getColor());

                textView2.setText(look[1].getName() + " style " + look[1].getStyle());
                imageView2.setImageURI(Uri.parse(look[1].getFoto()));
                imageView2.setOnClickListener(setListener(context,look[1].getId()));
                imageView21.setBackgroundColor(look[1].getColor());

                textView3.setText(look[2].getName() + " style " + look[2].getStyle());
                imageView3.setImageURI(Uri.parse(look[2].getFoto()));
                imageView3.setOnClickListener(setListener(context,look[2].getId()));
                imageView31.setBackgroundColor(look[2].getColor());

                textView4.setText(look[3].getName() + " style " + look[3].getStyle());
                imageView4.setImageURI(Uri.parse(look[3].getFoto()));
                imageView4.setOnClickListener(setListener(context,look[3].getId()));
                imageView41.setBackgroundColor(look[3].getColor());

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


