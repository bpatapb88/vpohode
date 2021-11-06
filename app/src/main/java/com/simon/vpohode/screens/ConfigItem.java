package com.simon.vpohode.screens;

import static com.simon.vpohode.managers.ImageManager.deleteImagesById;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.palette.graphics.Palette;
import androidx.preference.PreferenceManager;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.jaredrummler.android.colorpicker.ColorShape;
import com.simon.vpohode.Look;
import com.simon.vpohode.cut_out_background.CutOut;
import com.simon.vpohode.Item;
import com.simon.vpohode.database.DBLooksFields;
import com.simon.vpohode.managers.ColorManager;
import com.simon.vpohode.managers.ImageManager;
import com.simon.vpohode.managers.LayoutManager;
import com.simon.vpohode.managers.TemplatesManager;
import com.simon.vpohode.R;
import com.simon.vpohode.Styles;
import com.simon.vpohode.database.DBFields;
import com.simon.vpohode.database.DBHelperTemplate;
import com.simon.vpohode.database.DatabaseHelper;
import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageView;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ConfigItem extends AppCompatActivity implements ColorPickerDialogListener {

    EditText nameBox;
    EditText usedTime;
    EditText brand;
    TextView colorHex;
    TextView warmText;
    ImageView colorView;
    ImageView imageLayer1;
    ImageView imageLayer2;
    ImageView imageLayer3;
    ImageView minus;
    ImageView plus;
    ImageView washItemImg;
    ImageView[] imagesOfLayers;
    ImageButton imageItem;
    ImageButton photoItem;
    Spinner spinnerStyle;
    Spinner spinnerTemplate;
    CardView delButton;
    CardView saveButton;
    DatabaseHelper sqlHelper;
    DBHelperTemplate dbHelperTemplate;
    TemplatesManager templatesManager;
    AlertDialog.Builder builder;

    SeekBar seekBar;
    ColorDrawable colorInicator;
    SQLiteDatabase db;
    SQLiteDatabase dbTemplate;
    Cursor userCursor;
    Cursor templateCursor;
    Space x;

    private static final String SELECT = "select * from ";

    SwitchCompat topBot;
    boolean newImage = false;
    boolean newColor = false;
    long itemId =0;
    int chekedLayer = 0;

    private final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        LayoutManager.setTheme(prefs, getTheme());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        washItemImg = findViewById(R.id.wash_item);
        colorHex = findViewById(R.id.colorHex);
        colorView = findViewById(R.id.colorView);
        imageItem = findViewById(R.id.image_of_item);
        photoItem = findViewById(R.id.photo_button);
        nameBox = findViewById(R.id.name);
        spinnerStyle = findViewById(R.id.Style);
        spinnerTemplate = findViewById(R.id.Template);
        delButton = findViewById(R.id.deleteButton);
        saveButton = findViewById(R.id.btnsave);
        x = findViewById(R.id.spacer);
        imageLayer1 = findViewById(R.id.imageLayer1);
        imageLayer2 = findViewById(R.id.imageLayer2);
        imageLayer3 = findViewById(R.id.imageLayer3);
        seekBar = findViewById(R.id.seekBar);
        warmText = findViewById(R.id.warmText);
        topBot = findViewById(R.id.top_bot);
        brand = findViewById(R.id.brand);

        usedTime = findViewById(R.id.usedTimes);
        minus = findViewById(R.id.minus);
        plus = findViewById(R.id.plus);

        builder = new AlertDialog.Builder(this);

        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.getWritableDatabase();

        dbHelperTemplate = new DBHelperTemplate(this);
        dbTemplate = dbHelperTemplate.getReadableDatabase();
        updateSpiner();

        imagesOfLayers = new ImageView[]{imageLayer1, imageLayer2, imageLayer3};
        imageLayer1.setOnClickListener(setListenerToLayer(imagesOfLayers));
        imageLayer2.setOnClickListener(setListenerToLayer(imagesOfLayers));
        imageLayer3.setOnClickListener(setListenerToLayer(imagesOfLayers));
        brand.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string = s.toString();
                int widthText = getWidthOfEditText(brand,string);
                setBrand(widthText, string);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });
        nameBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string = s.toString();
                int widthText = getWidthOfEditText(nameBox,string);
                setName(widthText,string);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Do nothing
            }
        });

        //hidden keyboard by default
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        seekBar.setProgress(1);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setWarmText(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Do nothing
            }
        });
        // configure spinner
        spinnerStyle.setAdapter(LayoutManager.spinnerConfig(Styles.values(),this));

        Bundle extras = getIntent().getExtras();

        Uri imageUri;

        if (extras != null) {
            itemId = extras.getLong("id");
            imageUri = (Uri) extras.get(CutOut.CUTOUT_EXTRA_RESULT);
            System.out.println("Item id is " + itemId);

            if(imageUri != null){
                imageItem.setImageURI(imageUri);
                imageItem.setBackgroundColor(getResources().getColor(R.color.white));
                newImage = true;
            }

            LinearLayout templateLayout = findViewById(R.id.template_layout);
            templateLayout.setVisibility(View.GONE);
            // get item by id from db
            userCursor = db.rawQuery(SELECT + DatabaseHelper.TABLE + " where " + DBFields.ID.toFieldName() + "=?", new String[]{String.valueOf(itemId)});
            userCursor.moveToFirst();
            nameBox.setText(userCursor.getString(1));
            brand.setText(userCursor.getString(11));
            seekBar.setProgress((int) userCursor.getDouble(4) - 1);
            setWarmText((int) userCursor.getDouble(4) - 1);
            spinnerStyle.setSelection(Styles.getOrdinalByString(userCursor.getInt(2)));
            colorView.setBackgroundColor(userCursor.getInt(6));

            int resourceID = ColorManager.getHSVColors(userCursor.getInt(6));
            colorHex.setText(getResources().getString(resourceID));


            usedTime.setText(String.valueOf(userCursor.getInt(8)));
            if(userCursor.getString(7) != null){
                imageUri = Uri.parse(userCursor.getString(7));
                imageItem.setImageURI(imageUri);
            }
            reDrawImage(imagesOfLayers[userCursor.getInt(5)-1], true);
            if (userCursor.getInt(3) == 1){
                topBot.setChecked(false);
                reBuildIcons(false);
            }else{
                topBot.setChecked(true);
                reBuildIcons(true);
            }
            TextView created = findViewById(R.id.created);
            String createdText = getResources().getString(R.string.created) + " " + userCursor.getString(9);
            created.setText(createdText);
            userCursor.close();
        }else{
            LinearLayout usedLayout = findViewById(R.id.usedLayout);
            usedLayout.setVisibility(View.GONE);
            washItemImg.setVisibility(View.GONE);
            TextView delButtonText = findViewById(R.id.deleteButtonText);
            delButtonText.setText(getResources().getString(R.string.save_template));
        }

        saveButton.setOnClickListener(view -> {
            try {
                saveItem();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // if Save edit_text clicked do next:

        photoItem.setOnClickListener(view -> {
            System.out.println("Photo Item clicked");
            CropImage.activity().start(ConfigItem.this);
        });

        minus.setOnClickListener(v -> {
            String currentString = usedTime.getText().toString();
            int currentInt = Integer.parseInt(currentString);
            if(currentInt > 0 ){
                currentInt--;
                usedTime.setText(String.valueOf(currentInt));
            }
        });

        plus.setOnClickListener(v -> {
            String currentString = usedTime.getText().toString();
            int currentInt = Integer.parseInt(currentString);
            if(currentInt < Integer.MAX_VALUE){
                currentInt++;
                usedTime.setText(String.valueOf(currentInt));
            }
        });
    }

    private void setBrand(int widthText, String string){
        if(widthText > 380){
            while (widthText > 380){
                string = string.substring(0,string.length() - 1);
                widthText = getWidthOfEditText(brand,string);
            }
            brand.setText(string);
            brand.setSelection(string.length());
        }
    }

    private void setName(int widthText, String string){
        if(widthText > 783){
            while (widthText > 7){
                string = string.substring(0,string.length() - 1);
                widthText = getWidthOfEditText(nameBox,string);
            }
            nameBox.setText(string);
            nameBox.setSelection(string.length());
        }
    }

    private void setWarmText(int progress){
        switch (progress){
            case 0:
                warmText.setText(getResources().getString(R.string.cold_temp));
                break;
            case 1:
                warmText.setText(getResources().getString(R.string.mid_temp));
                break;
            case 2:
                warmText.setText(getResources().getString(R.string.hot_temp));
                break;
            default:
        }
    }

    private View.OnClickListener setListenerToLayer(final ImageView[] imagesOfLayers){
        return v -> {
            for(ImageView imageView : imagesOfLayers){
                reDrawImage(imageView,v.getId() == imageView.getId());
            }
        };
    }

    private void reDrawImage(ImageView imageView, boolean isCheked){
        Drawable drawable = imageView.getDrawable();
        if(isCheked){
            drawable.setTint(getColor(R.color.colorAccent));
            imageView.setBackground(AppCompatResources.getDrawable(this,R.drawable.gradient));
        }else{
            drawable.setTint(getColor(R.color.colorPrimaryDark));
            imageView.setBackground(AppCompatResources.getDrawable(this,R.color.colorAccent));
        }
        imageView.setImageDrawable(drawable);
    }

    private boolean isImageChecked(ImageView imageView){
        String temp = imageView.getBackground().toString();
        temp = temp.substring(26,31);
        return !temp.equals("Color");
    }

    @Override
    public void onResume() {
        super.onResume();

        spinnerTemplate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Item selectedTemplate = templatesManager.getItemFromTemplate(spinnerTemplate.getSelectedItem().toString());
                if (position!=0) {
                    setTemplateParameters(selectedTemplate);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        topBot.setOnCheckedChangeListener((compoundButton, b) -> reBuildIcons(b));
    }

    private void setTemplateParameters(Item selectedTemplate){
        nameBox.setText(selectedTemplate.getName());
        double termIdDouble = selectedTemplate.getTermid() - 1;
        seekBar.setProgress((int) termIdDouble);
        setWarmText((int) termIdDouble);
        spinnerStyle.setSelection(Styles.getOrdinalByString(selectedTemplate.getStyle()));
        topBot.setChecked(selectedTemplate.getTop() != 0);

        for(int i = 0 ; i < imagesOfLayers.length; i++){
            reDrawImage(imagesOfLayers[i],(i+1) == selectedTemplate.getLayer());
        }
        if(selectedTemplate.getColor() != 0){
            colorView.setBackgroundColor(selectedTemplate.getColor());
            int resourceID = ColorManager.getHSVColors(selectedTemplate.getColor());
            colorHex.setText(getResources().getString(resourceID));
            newColor = true;
            colorInicator = (ColorDrawable) colorView.getBackground();
        }
        if(selectedTemplate.getBrand() != null && !selectedTemplate.getBrand().equals("")){
            brand.setText(selectedTemplate.getBrand());
        }
        if(selectedTemplate.getFoto() != null && !selectedTemplate.getFoto().equals("")){
            File fileFoto = new File(selectedTemplate.getFoto());
            Picasso.get().load(fileFoto).into(imageItem);
            newImage = true;
        }
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

            if(id>0 || newColor){
                colorInicator = (ColorDrawable) colorView.getBackground();
                color=colorInicator.getColor();
            }
        ColorPickerDialog.newBuilder()
                .setColor(color)
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
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

    @Override
    public void onColorSelected(int dialogId, int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color,hsv);
        newColor = true;
        colorView.setBackgroundColor(Color.HSVToColor(hsv));
        int resourceID = ColorManager.getHSVColors(color);
        colorHex.setText(getResources().getString(resourceID));
    }

    @Override
    public void onDialogDismissed(int dialogId) {
        //Do nothing
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {
            startCrop(CropImage.getPickImageResultUriContent(this,data));
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(result != null){
                //get color from photo start ->
                Bitmap bitmap = null;
                try {
                    if(result.getUriContent() != null){
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result.getUriContent());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(bitmap != null){
                    int paletteColor = getColorFromBitmap(bitmap);
                    newColor = true;
                    colorView.setBackgroundColor(paletteColor);
                    int resourceID = ColorManager.getHSVColors(paletteColor);
                    colorHex.setText(getResources().getString(resourceID));
                }
                //color from photo was set

                imageItem.setImageURI(result.getUriContent());
                newImage = true;
            }
        }
    }

    private void startCrop(Uri imageuri) {
        CropImage.activity(imageuri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    private void reBuildIcons(boolean isBotChecked){
        Drawable[] drawables;
        Drawable layerDr1;
        Drawable layerDr2;
        Drawable layerDr3;
        if(isBotChecked){
            layerDr1 = AppCompatResources.getDrawable(this,R.drawable.ic_layer1_bot);
            layerDr2 = AppCompatResources.getDrawable(this,R.drawable.ic_layer2_bot);
            layerDr3 = AppCompatResources.getDrawable(this,R.drawable.ic_layer_boots);
        } else {
            layerDr1 = AppCompatResources.getDrawable(this,R.drawable.ic_layer1);
            layerDr2 = AppCompatResources.getDrawable(this,R.drawable.ic_layer2);
            layerDr3 = AppCompatResources.getDrawable(this,R.drawable.ic_layer3);
        }
        drawables = new Drawable[]{layerDr1,layerDr2,layerDr3};

        for (int i = 0; i < drawables.length; i++){
            if(isImageChecked(imagesOfLayers[i])){
                drawables[i].setTint(getColor(R.color.colorAccent));
            }else{
                drawables[i].setTint(getColor(R.color.colorPrimaryDark));
            }
            imagesOfLayers[i].setImageDrawable(drawables[i]);
        }

    }

    private int getWidthOfEditText(EditText editText, String str){
        Paint paint = new Paint();
        paint.setTextSize(editText.getTextSize());
        Typeface typeface = editText.getTypeface();
        paint.setTypeface(typeface);
        Rect result = new Rect();
        paint.getTextBounds(str, 0, str.length(), result);
        return result.width();
    }

    private int getColorFromBitmap(Bitmap bitmap){
        Palette.Builder paletteBuilder = new Palette.Builder(bitmap);
        Palette palette = paletteBuilder.generate();
        int accentColor = getResources().getColor(R.color.colorPrimary);
        return palette.getDominantColor(accentColor);
    }

    public void deleteTemplate(View view) {
        String selected = spinnerTemplate.getSelectedItem().toString();
        dbTemplate.delete(DBHelperTemplate.TABLE, "name = ?", new String[]{selected});
        updateSpiner();
    }

    private boolean checkParameters(){
        for (int i = 0; i < imagesOfLayers.length; i++) {
            if (isImageChecked(imagesOfLayers[i])) {
                chekedLayer = i + 1;
            }
        }
        if (nameBox.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.enter_name), Toast.LENGTH_SHORT).show();
        } else if (!newColor && itemId == 0) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.color_not_set), Toast.LENGTH_SHORT).show();
        } else if(chekedLayer == 0) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.choose_layer), Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
    }

    private void saveItem() throws IOException {
        if (checkParameters()) {
            ContentValues cv = new ContentValues();
            cv.put(DBFields.LAYER.toFieldName(), chekedLayer);
            cv.put(DBFields.NAME.toFieldName(), nameBox.getText().toString());
            cv.put(DBFields.BRAND.toFieldName(), brand.getText().toString());
            cv.put(DBFields.INWASH.toFieldName(), false);
            cv.put(DBFields.TERMID.toFieldName(), 1 + (double) seekBar.getProgress());
            cv.put(DBFields.STYLE.toFieldName(), Styles.stringToResource(getResources(), spinnerStyle.getSelectedItem().toString()));
            if (!topBot.isChecked()) {
                cv.put(DBFields.ISTOP.toFieldName(), 1);
            } else {
                cv.put(DBFields.ISTOP.toFieldName(), 0);
            }

            if (newColor) {
                colorInicator = (ColorDrawable) colorView.getBackground();
                cv.put(DBFields.COLOR.toFieldName(), colorInicator.getColor());
            }
            //save image if image was changed or new
            if (newImage) {
                if (itemId > 0) {
                    deleteImagesById(itemId, db);
                }
                Bitmap bm = ((BitmapDrawable) imageItem.getDrawable()).getBitmap();
                cv.put(DBFields.FOTO.toFieldName(), ImageManager.saveToInternalStorage(bm, getApplicationContext()));
            }
            // update or insert DB
                if (itemId > 0) {
                    cv.put(DBFields.USED.toFieldName(), Integer.parseInt(usedTime.getText().toString()));
                    db.update(DatabaseHelper.TABLE, cv, DBFields.ID.toFieldName() + "=" + itemId, null);
                } else {
                    cv.put(DBFields.USED.toFieldName(), 0);
                    Calendar calendar = Calendar.getInstance();
                    String currentTime = dateFormat.format(calendar.getTime());
                    cv.put(DBFields.CREATED.toFieldName(), currentTime);
                    db.insert(DatabaseHelper.TABLE, null, cv);
                }
                cv.clear();
                db.close();
                goHome();
        }

    }

    public void onClickColor(View view) {
        createColorPickerDialog(itemId);
    }

    public void delete(View view) throws IOException {
        if(itemId > 0){
            deleteImagesById(itemId, db);
            Toast.makeText(this, getResources().getString(R.string.deleted) + " " + itemId, Toast.LENGTH_SHORT).show();
            deleteAllLooks(db);
            db.delete(DatabaseHelper.TABLE, "_id = ?", new String[]{String.valueOf(itemId)});
            goHome();
        }else{
            if (nameBox.getText().toString().equals("")){
                Toast.makeText(view.getContext(),"Name of template is missing", Toast.LENGTH_SHORT).show();
                return;
            }else if(isNameAlreadyExistInTemplates(nameBox.getText().toString())){
                Toast.makeText(view.getContext(),"Change Name of Template", Toast.LENGTH_SHORT).show();
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putString("name", nameBox.getText().toString());
            bundle.putInt("style", spinnerStyle.getSelectedItemPosition());
            bundle.putString("brand", brand.getText().toString());
            bundle.putInt("color",colorInicator == null ? 0 : colorInicator.getColor());
            bundle.putInt("termIndex",seekBar.getProgress() + 1);
            bundle.putBoolean("isTop",!topBot.isChecked());
            int chekedLayer2 = 0;
            for(int i = 0 ; i < imagesOfLayers.length ; i++){
                if(isImageChecked(imagesOfLayers[i])){
                    chekedLayer2 = i + 1;
                }
            }
            bundle.putInt("layer", chekedLayer2);

            CustomDialogFragment customDialogFragment = new CustomDialogFragment();
            customDialogFragment.setArguments(bundle);
            customDialogFragment.show(getSupportFragmentManager(),"missingFragment");
        }

    }

    private void deleteAllLooks(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_LOOKS, null);
        if(cursor.moveToFirst()){
            do{
                Look look = new Look(cursor,db);
                ArrayList<Integer> itemsId = look.getItemsArray();
                if(itemsId.contains((int)itemId)){
                    db.delete(DatabaseHelper.TABLE_LOOKS, "_id = ?", new String[]{String.valueOf(look.getId())});
                }
            }while (cursor.moveToNext());
        }
    }

    public void goHome(View view){
        Intent intent = new Intent(this, Wardrobe.class);
        startActivity(intent);
    }

    public void washItem(View view){
            ContentValues cv = new ContentValues();
            cv.put(DBFields.INWASH.toFieldName(),true);
            db.update(DatabaseHelper.TABLE, cv, DBFields.ID.toFieldName() + "=" + itemId, null);
            cv.clear();
            // move to main activity
            goHome();
    }

    private boolean isNameAlreadyExistInTemplates(String name){
        Cursor cursor = dbTemplate.rawQuery(SELECT + DBHelperTemplate.TABLE + " where name=\'" + name +"\'", null);
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    public void updateSpiner(){
        templateCursor = dbTemplate.rawQuery(SELECT + DBHelperTemplate.TABLE, null);
        templatesManager = new TemplatesManager(this, templateCursor);
        spinnerTemplate.setAdapter(templatesManager.spinnerConfig());
    }
}
