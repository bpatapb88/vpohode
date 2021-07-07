package com.simon.vpohode.screens;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.palette.graphics.Palette;
import androidx.preference.PreferenceManager;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.jaredrummler.android.colorpicker.ColorShape;
import com.simon.vpohode.CutOutBackground.CutBGActivity;
import com.simon.vpohode.CutOutBackground.CutOut;
import com.simon.vpohode.CutOutBackground.SaveDrawingTask;
import com.simon.vpohode.Item;
import com.simon.vpohode.Managers.ColorManager;
import com.simon.vpohode.Managers.ImageManager;
import com.simon.vpohode.Managers.LayoutManager;
import com.simon.vpohode.Managers.TemplatesManager;
import com.simon.vpohode.R;
import com.simon.vpohode.Styles;
import com.simon.vpohode.database.DBFields;
import com.simon.vpohode.database.DatabaseHelper;
import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageView;


import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ConfigItem extends AppCompatActivity implements ColorPickerDialogListener {

    EditText nameBox, usedTime,brand;
    TextView colorHex, warmText;
    ImageView colorView,imageLayer1,imageLayer2,imageLayer3, minus, plus, washItemImg;
    ImageView[] imagesOfLayers;
    ImageButton imageItem;
    Spinner spinnerStyle, spinnerTemplate;
    CardView delButton, saveButton;
    DatabaseHelper sqlHelper;
    SeekBar seekBar;
    ColorDrawable colorInicator;
    SQLiteDatabase db;
    Cursor userCursor;
    Space x;

    private Uri imageUri = null;

    SwitchCompat topBot;
    boolean newImage = false;
    boolean newColor = false;
    long userId=0;

    private Calendar calendar;
    private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //stash Test2
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        LayoutManager.setTheme(prefs, getTheme());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        washItemImg = findViewById(R.id.wash_item);
        colorHex = findViewById(R.id.colorHex);
        colorView = findViewById(R.id.colorView);
        imageItem = findViewById(R.id.image_of_item);
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

        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.getWritableDatabase();

        imagesOfLayers = new ImageView[]{imageLayer1, imageLayer2, imageLayer3};
        imageLayer1.setOnClickListener(setListenerToLayer(imagesOfLayers));
        imageLayer2.setOnClickListener(setListenerToLayer(imagesOfLayers));
        imageLayer3.setOnClickListener(setListenerToLayer(imagesOfLayers));
        brand.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string = s.toString();
                int widthText = getWidthOfEditText(brand,string);
                if(widthText > 380){
                while (widthText > 380){
                    string = string.substring(0,string.length() - 1);
                    widthText = getWidthOfEditText(brand,string);
                }
                brand.setText(string);
                brand.setSelection(string.length());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        nameBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string = s.toString();
                int widthText = getWidthOfEditText(nameBox,string);
                if(widthText > 783){
                    while (widthText > 7){
                        string = string.substring(0,string.length() - 1);
                        widthText = getWidthOfEditText(nameBox,string);
                    }
                    nameBox.setText(string);
                    nameBox.setSelection(string.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        // configure spinner
        spinnerStyle.setAdapter(LayoutManager.spinnerConfig(Styles.values(),this));

        String[] namesTemplates =  getResources().getStringArray(R.array.templates);
        spinnerTemplate.setAdapter(TemplatesManager.spinnerConfig(namesTemplates,this));
        //spinnerTemplate.setAdapter(LayoutManager.spinnerConfig(getResources().getStringArray(R.array.templates),this));

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            userId = extras.getLong("id");
            imageUri = (Uri) extras.get(CutOut.CUTOUT_EXTRA_RESULT);
            System.out.println("Uri is here? " + imageUri);
            //System.out.println("Uri is here " + extras.get("CUTOUT_EXTRA_RESULT").toString());
            if(imageUri != null){
                imageItem.setImageURI(imageUri);
                imageItem.setBackgroundColor(getResources().getColor(R.color.white));
                newImage = true;
            }
        }
        // if 0, add
        if (userId > 0) {
            LinearLayout templateLayout = findViewById(R.id.template_layout);
            templateLayout.setVisibility(View.GONE);
            // get item by id from db
            userCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " + DBFields.ID.toFieldName() + "=?", new String[]{String.valueOf(userId)});
            userCursor.moveToFirst();
            nameBox.setText(userCursor.getString(1));
            brand.setText(userCursor.getString(11));
            //termidBox.setText(String.valueOf(userCursor.getDouble(4)));
            seekBar.setProgress((int) userCursor.getDouble(4) - 1);
            setWarmText((int) userCursor.getDouble(4) - 1);
            spinnerStyle.setSelection(Styles.getOrdinalByString(userCursor.getInt(2)));
            colorView.setBackgroundColor(userCursor.getInt(6));

            int resourceID = ColorManager.getHSVColors(userCursor.getInt(6));
            colorHex.setText(getResources().getString(resourceID));


            usedTime.setText(String.valueOf(userCursor.getInt(8)));
            if(userCursor.getString(7) != null){
                System.out.println("in DB uri is " + userCursor.getString(7));
                imageUri = Uri.parse(userCursor.getString(7));
                System.out.println("uri after " + imageUri);
                imageItem.setImageURI(imageUri);
                //imageItem.setImageBitmap(ImageManager.loadImageFromStorage(userCursor.getString(7)));
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
            created.setText(getResources().getString(R.string.created) + " " + userCursor.getString(9));
            userCursor.close();
        } else {
            LinearLayout usedLayout = findViewById(R.id.usedLayout);
            usedLayout.setVisibility(View.GONE);
            // hide edit_text Delete, It will be new Item
            washItemImg.setVisibility(View.GONE);
            delButton.setVisibility(View.GONE);
            x.setVisibility(View.GONE);
        }

        // if Save edit_text clicked do next:

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean allIsOk = true;
                if(nameBox.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.enter_name), Toast.LENGTH_SHORT).show();
                    allIsOk=false;
                }else if(!newColor && userId==0) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.color_not_set), Toast.LENGTH_SHORT).show();
                    allIsOk=false;
                }else {
                    ContentValues cv = new ContentValues();
                    cv.put(DBFields.NAME.toFieldName(), nameBox.getText().toString());
                    cv.put(DBFields.BRAND.toFieldName(), brand.getText().toString());
                    cv.put(DBFields.INWASH.toFieldName(),false);
                    cv.put(DBFields.TERMID.toFieldName(), Double.valueOf(seekBar.getProgress() + 1));
                    cv.put(DBFields.STYLE.toFieldName(), Styles.stringToResource(getResources(), spinnerStyle.getSelectedItem().toString()));
                    if (!topBot.isChecked()) {
                        cv.put(DBFields.ISTOP.toFieldName(), 1);
                    }else {
                        cv.put(DBFields.ISTOP.toFieldName(), 0);
                    }

                    int chekedLayer = 0;
                    for(int i = 0 ; i < imagesOfLayers.length ; i++){
                        if(isImageChecked(imagesOfLayers[i])){
                            chekedLayer = i + 1;
                        }
                    }
                    if(chekedLayer == 0 ){
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.choose_layer), Toast.LENGTH_SHORT).show();
                        allIsOk=false;
                    }else{
                        cv.put(DBFields.LAYER.toFieldName(), chekedLayer);
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
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.choose_foto), Toast.LENGTH_SHORT).show();
                            allIsOk=false;
                        }
                    }

                    // update or insert DB
                    if(allIsOk) {
                        if (userId > 0) {
                            cv.put(DBFields.USED.toFieldName(), Integer.parseInt(usedTime.getText().toString()));
                            db.update(DatabaseHelper.TABLE, cv, DBFields.ID.toFieldName() + "=" + userId, null);
                        } else {
                            cv.put(DBFields.USED.toFieldName(), 0);
                            calendar = Calendar.getInstance();
                            String currentTime = dateFormat.format(calendar.getTime());
                            cv.put(DBFields.CREATED.toFieldName(), currentTime);
                            db.insert(DatabaseHelper.TABLE, null, cv);
                        }
                        cv.clear();
                        db.close();
                        goHome();
                    }
                }

            }
        });

        imageItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageUri != null && imageUri.toString().substring(1,5).equals("data")){
                    System.out.println("image Item #1");
                    CropImage.activity(imageUri)
                            .start(ConfigItem.this);
                }else if(imageUri != null){
                    System.out.println("image Item #2");
                    CropImage.activity(imageUri).start(ConfigItem.this);
                }else{
                    System.out.println("image Item #3");
                    CropImage.activity().start(ConfigItem.this);
                }
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentString = usedTime.getText().toString();
                int currentInt = Integer.parseInt(currentString);
                if(currentInt > 0 ){
                    currentInt--;
                    usedTime.setText(String.valueOf(currentInt));
                }
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentString = usedTime.getText().toString();
                int currentInt = Integer.parseInt(currentString);
                if(currentInt < Integer.MAX_VALUE){
                    currentInt++;
                    usedTime.setText(String.valueOf(currentInt));
                }
            }
        });
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
        }
    }

    private View.OnClickListener setListenerToLayer(final ImageView[] imagesOfLayers){
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(ImageView imageView : imagesOfLayers){
                    reDrawImage(imageView,v.getId() == imageView.getId());
                    System.out.println(isImageChecked(imageView));
                }
            }
        };
        return onClickListener;
    }

    private void reDrawImage(ImageView imageView, boolean isCheked){
        Drawable drawable = imageView.getDrawable();
        if(isCheked){
            drawable.setTint(getColor(R.color.colorAccent));
            imageView.setBackground(getDrawable(R.drawable.gradient));
        }else{
            drawable.setTint(getColor(R.color.colorPrimaryDark));
            imageView.setBackground(getDrawable(R.color.colorAccent));
        }
        imageView.setImageDrawable(drawable);
    }

    private boolean isImageChecked(ImageView imageView){
        String temp = imageView.getBackground().toString();
        temp = temp.substring(26,31);
        if(temp.equals("Color")){
            return false;
        }
        return true;
    }

    public void goHome(View view){
        Intent intent = new Intent(this, Wardrobe.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        spinnerTemplate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                Item selectedTemplate = TemplatesManager.getItemFromTemplate(spinnerTemplate.getSelectedItem().toString(), getResources());

                if (!spinnerTemplate.getSelectedItem().toString().equals(getResources().getStringArray(R.array.templates)[0])) {
                        nameBox.setText(selectedTemplate.getName());
                        double x = selectedTemplate.getTermid() - 1;
                        seekBar.setProgress((int) x);
                        setWarmText((int) x);
                        spinnerStyle.setSelection(Styles.getOrdinalByString(selectedTemplate.getStyle()));
                        if(selectedTemplate.getTop() == 0){
                            topBot.setChecked(false);
                        }else{
                            topBot.setChecked(true);
                        }

                        for(int i = 0 ; i < imagesOfLayers.length; i++){
                            reDrawImage(imagesOfLayers[i],(i+1) == selectedTemplate.getLayer());
                        }

                        if(selectedTemplate.getColor() != 0 && !selectedTemplate.getFoto().equals("")){
                            colorView.setBackgroundColor(selectedTemplate.getColor());

                            int resourceID = ColorManager.getHSVColors(selectedTemplate.getColor());
                            colorHex.setText(getResources().getString(resourceID));
                            new DownloadImageTask((ImageView) imageItem).execute(selectedTemplate.getFoto());
                            brand.setText(selectedTemplate.getBrand());
                            newColor = true;
                            newImage = true;
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
                ArrayList<String> arrayTemplate = new ArrayList<>();
                String currentTemplate = spinnerTemplate.getSelectedItem().toString();
                int counter = 0;
                String[] arrayTemplatesFromResource = getResources().getStringArray(R.array.templates);

                for(int i = 0; i < arrayTemplatesFromResource.length; i++){
                    Item template = TemplatesManager.getItemFromTemplate(arrayTemplatesFromResource[i], getResources());
                    if(template != null && (template.getTop() == 0) != b ) {
                        arrayTemplate.add(arrayTemplatesFromResource[i]);
                        if(arrayTemplatesFromResource[i].equals(currentTemplate)){
                            counter =  arrayTemplate.size() - 1;
                            System.out.println("test3210"+spinnerTemplate.getSelectedItem().toString());
                        }
                    }
                }
                String[] array = new String[arrayTemplate.size()];
                for(int i = 0; i < arrayTemplate.size(); i++){
                    array[i] = arrayTemplate.get(i);
                }
                /*spinnerTemplate.setAdapter(TemplatesManager.spinnerConfig(array, internContext));
                spinnerTemplate.setSelection(counter);*/
            }
        });
    }

    public void delete(View view){
        boolean deleted = ImageManager.deleteImagesById(userId, db);
        Toast.makeText(this, getResources().getString(R.string.deleted) + " " + deleted, Toast.LENGTH_SHORT).show();
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
        int resourceID = ColorManager.getHSVColors(color);
        colorHex.setText(getResources().getString(resourceID));
    }

    @Override
    public void onDialogDismissed(int dialogId) {
       /* if(newColor)
        Toast.makeText(this, "Цвет выбран", Toast.LENGTH_SHORT).show();*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("request code is - " + requestCode);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {
            //Uri imageuri = CropImage.getPickImageResultUri(this, data);
            Uri imageuri = CropImage.getPickImageResultUriContent(this,data);
            startCrop(imageuri);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(result != null){
                //get color from photo start ->
                Bitmap bitmap = null;
                try {
                    System.out.println("result.getUriContent() " + result.getUriContent());
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result.getUriContent());
                } catch (IOException e) {
                    System.out.println("ERRROR!!! " + e.toString());
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

    public void washItem(View view){
        ContentValues cv = new ContentValues();
        cv.put(DBFields.INWASH.toFieldName(),true);
        db.update(DatabaseHelper.TABLE, cv, DBFields.ID.toFieldName() + "=" + userId, null);
        cv.clear();
        // move to main activity
        goHome();
    }
    public void cutBG(View view){
        Intent intent = new Intent(this, CutBGActivity.class);
        if(imageUri != null){
            intent.putExtra(CutOut.CUTOUT_EXTRA_SOURCE, imageUri);
        }
        startActivity(intent);
    }

    private void reBuildIcons(boolean isBotChecked){
        Drawable[] drawables;
        Drawable layerDr1,layerDr2,layerDr3;
        if(isBotChecked){
            layerDr1 = getDrawable(R.drawable.ic_layer1_bot);
            layerDr2 = getDrawable(R.drawable.ic_layer2_bot);
            layerDr3 = getDrawable(R.drawable.ic_layer_boots);
        } else {
            layerDr1 = getDrawable(R.drawable.ic_layer1);
            layerDr2 = getDrawable(R.drawable.ic_layer2);
            layerDr3 = getDrawable(R.drawable.ic_layer3);
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
        Palette.Builder builder = new Palette.Builder(bitmap);
        Palette palette = builder.generate();
        int accentColor = getResources().getColor(R.color.colorPrimary);
        return palette.getDominantColor(accentColor);
    }



    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
                in.close();
            } catch (Exception e) {
                Log.e("Error Image", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
