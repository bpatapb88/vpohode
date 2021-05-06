package com.simon.vpohode.screens;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.preference.PreferenceManager;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.jaredrummler.android.colorpicker.ColorShape;
import com.simon.vpohode.Item;
import com.simon.vpohode.Managers.ColorManager;
import com.simon.vpohode.Managers.ImageManager;
import com.simon.vpohode.Managers.LayoutManager;
import com.simon.vpohode.Managers.TemplatesManager;
import com.simon.vpohode.R;
import com.simon.vpohode.Styles;
import com.simon.vpohode.database.DBFields;
import com.simon.vpohode.database.DatabaseHelper;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ConfigItem extends AppCompatActivity implements ColorPickerDialogListener {

    EditText nameBox, usedTime;
    TextView colorHex, warmText, brand;
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
    Switch topBot;
    boolean newImage = false;
    boolean newColor = false;
    long userId=0;
    private Uri uri;

    private Calendar calendar;
    private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private Context internContext = this;
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

        //hidden keyboard by default
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        seekBar.setProgress(1);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setWarmText(seekBar.getProgress());
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
            colorHex.setText("#" + ColorManager.convertIntToHex(userCursor.getInt(6)));
            usedTime.setText(String.valueOf(userCursor.getInt(8)));
            if(userCursor.getString(7) != null){
                imageItem.setImageBitmap(ImageManager.loadImageFromStorage(userCursor.getString(7)));
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
            created.setText("Создано " + userCursor.getString(9));

            if(userCursor.getInt(10) > 0){
                Toast.makeText(getApplicationContext(), "inWash - " + userCursor.getInt(10), Toast.LENGTH_SHORT).show();
            }

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
                CropImage.startPickImageActivity(ConfigItem.this);
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
                warmText.setText("Холодное");
                break;
            case 1:
                warmText.setText("Среднее");
                break;
            case 2:
                warmText.setText("Теплое");
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
        finish();
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
                            colorHex.setText("#" + ColorManager.convertIntToHex(selectedTemplate.getColor()));
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
                        }
                    }
                }
                String[] array = new String[arrayTemplate.size()];
                for(int i = 0; i < arrayTemplate.size(); i++){
                    array[i] = arrayTemplate.get(i);
                }
                spinnerTemplate.setAdapter(TemplatesManager.spinnerConfig(array, internContext));
                spinnerTemplate.setSelection(counter);
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
        colorHex.setText("#" + ColorManager.convertIntToHex(Color.HSVToColor(hsv)));
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
