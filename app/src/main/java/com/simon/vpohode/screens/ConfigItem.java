package com.simon.vpohode.screens;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.jaredrummler.android.colorpicker.ColorShape;
import com.simon.vpohode.Item;
import com.simon.vpohode.Managers.ImageManager;
import com.simon.vpohode.Managers.LayoutManager;
import com.simon.vpohode.R;
import com.simon.vpohode.Styles;
import com.simon.vpohode.Templates;
import com.simon.vpohode.database.DBFields;
import com.simon.vpohode.database.DatabaseHelper;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class ConfigItem extends AppCompatActivity implements ColorPickerDialogListener {

    EditText nameBox;
    ImageView colorView,imageLayer1,imageLayer2,imageLayer3;

    ImageButton imageItem;
    Spinner spinner, spinnerTemplate;
    Button delButton, saveButton, btColor;
    RadioGroup radGrpLayer;
    RadioButton radioButtonLayer3;
    Integer[] radioButtonsLayers;
    DatabaseHelper sqlHelper;
    SeekBar seekBar;
    ColorDrawable colorInicator;
    SQLiteDatabase db;
    Cursor userCursor;
    LinearLayout layoutTop;
    Space x;
    Switch topBot;
    boolean newImage = false;
    boolean newColor = false;
    long userId=0;
    private Uri uri;
    private static final int firstId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        layoutTop = findViewById(R.id.layoutTop);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setTitle(getString(R.string.title_item));

        colorView = findViewById(R.id.colorView);
        imageItem = findViewById(R.id.image_of_item);
        nameBox = findViewById(R.id.name);
        spinner = findViewById(R.id.Style);
        spinnerTemplate = findViewById(R.id.Template);
        radGrpLayer = findViewById(R.id.radios2);
        btColor = findViewById(R.id.btnColor);
        delButton = findViewById(R.id.deleteButton);
        saveButton = findViewById(R.id.btnsave);
        x = findViewById(R.id.spacer);
        imageLayer1 = findViewById(R.id.imageLayer1);
        imageLayer2 = findViewById(R.id.imageLayer2);
        imageLayer3 = findViewById(R.id.imageLayer3);
        radioButtonLayer3 = findViewById(R.id.layer3);
        seekBar = findViewById(R.id.seekBar);
        topBot = findViewById(R.id.top_bot);

        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.getWritableDatabase();

        radioButtonsLayers = new Integer[]{R.id.layer1, R.id.layer2, R.id.layer3};
        //hidden keyboard by default
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        seekBar.setProgress(1);
        // configure spinner
        spinner.setAdapter(LayoutManager.spinnerConfig(Styles.values(),this));
        spinnerTemplate.setAdapter(LayoutManager.spinnerConfig(Templates.values(),this));

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            userId = extras.getLong("id");
        }
        // if 0, add
        if (userId > 0) {
            spinnerTemplate.setVisibility(View.GONE);
            // get item by id from db
            userCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                    DBFields.ID.toFieldName() + "=?", new String[]{String.valueOf(userId)});
            userCursor.moveToFirst();
            nameBox.setText(userCursor.getString(1));
            //termidBox.setText(String.valueOf(userCursor.getDouble(4)));
            seekBar.setProgress((int) userCursor.getDouble(4) - 1);
            spinner.setSelection(Styles.getOrdinalByString(userCursor.getString(2)));
            colorView.setBackgroundColor(userCursor.getInt(6));
            imageItem.setBackgroundColor(userCursor.getInt(6));
            if(userCursor.getString(7) != null){
                imageItem.setImageBitmap(ImageManager.loadImageFromStorage(userCursor.getString(7)));
            }
            radGrpLayer.check(radioButtonsLayers[userCursor.getInt(5)-1]);
            if (userCursor.getInt(3) == 1){
                topBot.setChecked(false);
                reBuildIcons(false);
            }else{
                topBot.setChecked(true);
                reBuildIcons(true);
            }

            userCursor.close();
        } else {
            // hide button Delete, It will be new Item
            delButton.setVisibility(View.GONE);
            x.setVisibility(View.GONE);
        }

        // if Save button clicked do next:

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean allIsOk = true;
                if(nameBox.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Название не выбрано", Toast.LENGTH_SHORT).show();
                    allIsOk=false;
                }else if(!newColor && userId==0) {
                    Toast.makeText(getApplicationContext(), "Цвет не выбран", Toast.LENGTH_SHORT).show();
                    allIsOk=false;
                }else {
                    ContentValues cv = new ContentValues();
                    cv.put(DBFields.NAME.toFieldName(), nameBox.getText().toString());
                    cv.put(DBFields.TERMID.toFieldName(), Double.valueOf(seekBar.getProgress() + 1));
                    cv.put(DBFields.STYLE.toFieldName(), spinner.getSelectedItem().toString());
                    if (!topBot.isChecked()) {
                        cv.put(DBFields.ISTOP.toFieldName(), 1);
                    }else {
                        cv.put(DBFields.ISTOP.toFieldName(), 0);
                    }

                    if (radGrpLayer.getCheckedRadioButtonId() == R.id.layer1) {
                        cv.put(DBFields.LAYER.toFieldName(), 1);
                    } else if (radGrpLayer.getCheckedRadioButtonId() == R.id.layer2) {
                        cv.put(DBFields.LAYER.toFieldName(), 2);
                    } else if(radGrpLayer.getCheckedRadioButtonId() == R.id.layer3){
                        cv.put(DBFields.LAYER.toFieldName(), 3);
                    }else{
                        Toast.makeText(getApplicationContext(), "Выберете слой", Toast.LENGTH_SHORT).show();
                        allIsOk=false;
                    }

                    if (newColor) {
                        colorInicator = (ColorDrawable) colorView.getBackground();
                        cv.put(DBFields.COLOR.toFieldName(), colorInicator.getColor());
                    }
                    //save image if image was changed or new
                    if (newImage) {
                        if (userId > 0) {
                            ImageManager.deleteImagesById(userId, db);
                        }
                        Bitmap bm = ((BitmapDrawable) imageItem.getDrawable()).getBitmap();
                        cv.put(DBFields.FOTO.toFieldName(), ImageManager.saveToInternalStorage(bm, getApplicationContext()));
                    }else{
                        if (userId == 0) {
                            Toast.makeText(getApplicationContext(), "Добавьте фото", Toast.LENGTH_SHORT).show();
                            allIsOk=false;
                        }
                    }

                    // update or insert DB
                    if(allIsOk) {
                        if (userId > 0) {
                            db.update(DatabaseHelper.TABLE, cv, DBFields.ID.toFieldName() + "=" + userId, null);
                        } else {
                            db.insert(DatabaseHelper.TABLE, null, cv);
                        }
                        cv.clear();
                        goHome();
                    }
                }

            }
        });

        imageItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.startPickImageActivity(ConfigItem.this);
            }
        });
        /*btReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageItem.setImageResource(0);
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        LayoutManager.invisible(R.id.search,menu);
        LayoutManager.invisible(R.id.save,menu);
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
    public void onResume() {
        super.onResume();

        spinnerTemplate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                Item selectedTemplate = Templates.fillTemplate(spinnerTemplate.getSelectedItemPosition());
                if (spinnerTemplate.getSelectedItemPosition() != 0) {

                        nameBox.setText(selectedTemplate.getName());
                        double x = selectedTemplate.getTermid() - 1;
                        seekBar.setProgress((int) x);
                        //termidBox.setText("" + (int)x);
                        spinner.setSelection(Styles.getOrdinalByString(selectedTemplate.getStyle()));
                        if(selectedTemplate.getTop() == 0){
                            topBot.setChecked(false);
                        }else{
                            topBot.setChecked(true);
                        }

                        switch (selectedTemplate.getLayer()) {
                            case 1:
                                radGrpLayer.check(R.id.layer1);
                                break;
                            case 2:
                                radGrpLayer.check(R.id.layer2);
                                break;
                            case 3:
                                radGrpLayer.check(R.id.layer3);
                        }

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        topBot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                reBuildIcons(b);
            }
        });
    }

    public void delete(View view){
        boolean deleted = ImageManager.deleteImagesById(userId, db);
        Toast.makeText(this, "Удалена " + deleted, Toast.LENGTH_SHORT).show();
        db.delete(DatabaseHelper.TABLE, "_id = ?", new String[]{String.valueOf(userId)});
        goHome();
    }
    private void goHome(){
        // close connection
        db.close();
        // move to main activity
        Intent intent = new Intent(this, Wardrobe.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void createColorPickerDialog(long id) {
            colorInicator = (ColorDrawable) colorView.getBackground();
            int color = Color.RED;

            if(id>0){
                colorInicator = (ColorDrawable) colorView.getBackground();
                color=colorInicator.getColor();
            }else if(newColor){
                colorInicator = (ColorDrawable) colorView.getBackground();
                color=colorInicator.getColor();
            }

        ColorPickerDialog.newBuilder()
                .setColor(color)
                .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                .setAllowCustom(true)
                .setAllowPresets(true)
                .setShowColorShades(true)
                .setSelectedButtonText(R.string.select)
                .setPresetsButtonText(R.string.colorPresent)
                .setCustomButtonText(R.string.colorCustom)
                .setDialogTitle(R.string.colorSet)
                .setColorShape(ColorShape.SQUARE)
                .setDialogId((int) id)
                .show(this);


    }
    public void onClickColor(View view) {
                createColorPickerDialog(userId);
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color,hsv);
        newColor = true;
        colorView.setBackgroundColor(Color.HSVToColor(hsv));
        imageItem.setBackgroundColor(Color.HSVToColor(hsv));
    }

    @Override
    public void onDialogDismissed(int dialogId) {
       /* if(newColor)
        Toast.makeText(this, "Цвет выбран", Toast.LENGTH_SHORT).show();*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {
            Uri imageuri = CropImage.getPickImageResultUri(this, data);
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageuri)) {
                uri = imageuri;
            } else {
                startCrop(imageuri);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageItem.setImageURI(result.getUri());
            newImage = true;
        }
    }
    private void startCrop(Uri imageuri) {
        CropImage.activity(imageuri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    private void reBuildIcons(boolean isBotChecked){
        if(isBotChecked){
            imageLayer1.setImageResource(R.drawable.ic_layer1_bot);
            imageLayer2.setImageResource(R.drawable.ic_layer2_bot);
            imageLayer3.setImageResource(R.drawable.ic_layer_boots);
            //radioButtonLayer3.setText("Обувь");
        } else {
            //radioButtonLayer3.setText("Третий слой");
            imageLayer1.setImageResource(R.drawable.ic_layer1);
            imageLayer2.setImageResource(R.drawable.ic_layer2);
            imageLayer3.setImageResource(R.drawable.ic_layer3);
        }

    }
}
