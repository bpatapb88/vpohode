package com.simon.vpohode.screens;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.slider.RangeSlider;
import com.simon.vpohode.Item;
import com.simon.vpohode.R;
import com.simon.vpohode.managers.LookManager;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class AddLookActivity extends AppCompatActivity {
    private RangeSlider rangeSlider;
    private static final String CELSIUS_SYMBOL = "\u2103 ";
    static List<Item[]> looks;
    private ImageView backImage;
    private double term;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_look);
        backImage = findViewById(R.id.back);
        backImage.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), LooksActivity.class);
            intent.putExtra("term", term);
            startActivity(intent);
        });

        ImageView tempImage = findViewById(R.id.temp_image);
        tempImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextField = new EditText(v.getContext());
                AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                        .setTitle("Title")
                        .setMessage("Message")
                        .setView(editTextField)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String editTextInput = editTextField.getText().toString();
                                Log.d("onclick","editext value is: "+ editTextInput);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }
        });

        rangeSlider = findViewById(R.id.range_slider);
        rangeSlider.setLabelFormatter(value -> CELSIUS_SYMBOL + (int)value);
        LinearLayout leftLayout = findViewById(R.id.left_layout);
        LinearLayout rightLayout = findViewById(R.id.right_layout);
        Bundle extras = getIntent().getExtras();
        term = extras.getDouble("term");
        if(term < 33 && term > -33){
            rangeSlider.setValues((float)term-2, (float)term + 2);
        }

        looks = LookManager.getLooks(term, getApplicationContext());
        if(looks.size() > 0){
            Item[] look = looks.get(0);
            for(int i = 0 ; i < look.length; i++){
                View itemView = View.inflate(this,R.layout.list_item_edit,null);
                TextView nameTextView = itemView.findViewById(R.id.nameItem);
                nameTextView.setText(look[i].getName());

                ImageView imageFoto = itemView.findViewById(R.id.imageViewPhoto);
                File fileFoto = new File(look[i].getFoto());
                Picasso.get().load(fileFoto).into(imageFoto);

                if(i%2 == 0){
                    leftLayout.addView(itemView);
                }else{
                    rightLayout.addView(itemView);
                }
            }
        }
    }

}