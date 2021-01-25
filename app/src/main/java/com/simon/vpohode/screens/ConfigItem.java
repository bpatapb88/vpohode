package com.simon.vpohode.screens;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
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

    TextView textcolor;
    EditText nameBox,termidBox;
    ImageButton imageItem;
    Spinner spinner, spinnerTemplate;
    Button delButton,colorButton, btReset;
    RadioGroup radGrpTop, radGrpLayer;
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    LinearLayout layoutTop;
    Space x;
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

        imageItem = findViewById(R.id.image_of_item);
        textcolor = findViewById(R.id.textColor);
        nameBox = findViewById(R.id.name);
        termidBox = findViewById(R.id.termid);
        spinner = findViewById(R.id.Style);
        spinnerTemplate = findViewById(R.id.Template);
        radGrpTop = findViewById(R.id.radios);
        radGrpLayer = findViewById(R.id.radios2);
        btReset = findViewById(R.id.bt_reset);
        delButton = findViewById(R.id.deleteButton);
        colorButton = findViewById(R.id.color);
        x = findViewById(R.id.spacer);

        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.getWritableDatabase();

        //hidden keyboard by default
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // configure spinner
        spinner.setAdapter(LayoutManager.spinnerConfig(Styles.values(),this));
        spinnerTemplate.setAdapter(LayoutManager.spinnerConfig(Templates.values(),this));

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            userId = extras.getLong("id");
        }
        // if 0, add
        if (userId > 0) {
            // get item by id from db
            userCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                    DBFields.ID.toFieldName() + "=?", new String[]{String.valueOf(userId)});
            userCursor.moveToFirst();
            nameBox.setText(userCursor.getString(1));
            termidBox.setText(String.valueOf(userCursor.getDouble(4)));
            spinner.setSelection(Styles.getOrdinalByString(userCursor.getString(2)));
            //textcolor.setTextColor(userCursor.getInt(6));
            textcolor.setBackgroundColor(userCursor.getInt(6));
            textcolor.setText("#" + (Integer.toHexString(userCursor.getInt(6)).substring(2).toUpperCase()));
            if(userCursor.getString(7) != null){
                imageItem.setImageBitmap(ImageManager.loadImageFromStorage(userCursor.getString(7)));
            }
            if (userCursor.getInt(3) == 1){
                radGrpTop.check(R.id.top);
                if(userCursor.getInt(5) == 1){
                    radGrpLayer.check(R.id.layer1);
                }else if(userCursor.getInt(5) == 2 ){
                    radGrpLayer.check(R.id.layer2);
                }else {
                    radGrpLayer.check(R.id.layer3);
                }
            }else{
                radGrpTop.check(R.id.bottom);
                layoutTop.setVisibility(View.GONE);
            }

            userCursor.close();
        } else {
            // hide button Delete, It will be new Item
            delButton.setVisibility(View.GONE);
            x.setVisibility(View.GONE);
        }

        // if Save button clicked do next:
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.save){
                    if(nameBox.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "Имя не выбрано", Toast.LENGTH_SHORT).show();
                    }else if(textcolor.getText().toString().equals("Цвет не выбран")) {
                        Toast.makeText(getApplicationContext(), "Цвет не выбран", Toast.LENGTH_SHORT).show();
                    }else if(termidBox.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(), "Заполните уровень теплоты 0.1 - 10", Toast.LENGTH_SHORT).show();
                    }else {
                        ContentValues cv = new ContentValues();
                        cv.put(DBFields.NAME.toFieldName(), nameBox.getText().toString());
                        cv.put(DBFields.TERMID.toFieldName(), Double.parseDouble(termidBox.getText().toString()));
                        cv.put(DBFields.STYLE.toFieldName(), spinner.getSelectedItem().toString());
                        if (radGrpTop.getCheckedRadioButtonId() == R.id.top) {
                            cv.put(DBFields.ISTOP.toFieldName(), 1);
                        } else if(radGrpTop.getCheckedRadioButtonId() == R.id.bottom){
                            cv.put(DBFields.ISTOP.toFieldName(), 0);
                        }else{
                            Toast.makeText(getApplicationContext(), "Выберете на верх или низ", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        if (radGrpLayer.getCheckedRadioButtonId() == R.id.layer1) {
                            cv.put(DBFields.LAYER.toFieldName(), 1);
                        } else if (radGrpLayer.getCheckedRadioButtonId() == R.id.layer2) {
                            cv.put(DBFields.LAYER.toFieldName(), 2);
                        } else if(radGrpLayer.getCheckedRadioButtonId() == R.id.layer3){
                            cv.put(DBFields.LAYER.toFieldName(), 3);
                        }else{
                            if(radGrpTop.getCheckedRadioButtonId() == R.id.top) {
                                Toast.makeText(getApplicationContext(), "Выберете слой", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            cv.put(DBFields.LAYER.toFieldName(), 3);
                        }
                        if (newColor)
                            cv.put(DBFields.COLOR.toFieldName(), textcolor.getCurrentTextColor());

                        //save image if image was changed or new
                        if(imageItem.getDrawable() == null)
                        {
                            Toast.makeText(getApplicationContext(), "Добавьте фото", Toast.LENGTH_SHORT).show();
                            return false;
                        }

                        if (newImage) {
                            if (userId > 0) {
                                ImageManager.deleteImagesById(userId, db);
                            }
                                Bitmap bm = ((BitmapDrawable) imageItem.getDrawable()).getBitmap();
                                cv.put(DBFields.FOTO.toFieldName(), ImageManager.saveToInternalStorage(bm, getApplicationContext()));
                        }

                        // update or insert DB

                            if (userId > 0) {
                                db.update(DatabaseHelper.TABLE, cv, DBFields.ID.toFieldName() + "=" + userId, null);
                            } else {
                                db.insert(DatabaseHelper.TABLE, null, cv);
                            }
                            cv.clear();
                            goHome();

                    }
                }
                return true;
            }
        });

        imageItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //CropImage.startPickImageActivity(ConfigItem.this);
            }
        });
        btReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.startPickImageActivity(ConfigItem.this);
                //imageItem.setImageResource(0);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    public void onResume() {
        super.onResume();

        spinnerTemplate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                Item selectedTemplate = Templates.fillTemplate(spinnerTemplate.getSelectedItemPosition());
                if (spinnerTemplate.getSelectedItemPosition() != 0) {
                    if (selectedTemplate.getTop() == 0) {
                        nameBox.setText(selectedTemplate.getName());
                        termidBox.setText("" + selectedTemplate.getTermid());
                        spinner.setSelection(Styles.getOrdinalByString(selectedTemplate.getStyle()));
                        radGrpTop.check(R.id.top);
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
                    } else {
                        nameBox.setText(selectedTemplate.getName());
                        termidBox.setText("" + selectedTemplate.getTermid());
                        spinner.setSelection(Styles.getOrdinalByString(selectedTemplate.getStyle()));
                        radGrpTop.check(R.id.bottom);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        radGrpTop.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton bot = radioGroup.findViewById(R.id.bottom);
                boolean isChecked = bot.isChecked();
                if(isChecked){
                    layoutTop.setVisibility(View.GONE);
                } else {
                    layoutTop.setVisibility(View.VISIBLE);
                }
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

    private void createColorPickerDialog(int id) {
        ColorPickerDialog.newBuilder()
                .setColor(Color.RED)
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setAllowCustom(false)
                .setAllowPresets(true)
                .setColorShape(ColorShape.CIRCLE)
                .setDialogId(id)
                .show(this);
    }
    public void onClickColor(View view) {
                createColorPickerDialog(firstId);
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color,hsv);
        newColor = true;
        textcolor.setText("#" + (Integer.toHexString(color).substring(2).toUpperCase()) + " hue " + hsv[0]);
        textcolor.setTextColor(Color.HSVToColor(hsv));
        textcolor.setBackgroundColor(Color.HSVToColor(hsv));
    }

    @Override
    public void onDialogDismissed(int dialogId) {
        Toast.makeText(this, "Цвет выбран", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Image of Item was loaded success!", Toast.LENGTH_SHORT).show();
        }
    }
    private void startCrop(Uri imageuri) {
        CropImage.activity(imageuri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }


}
