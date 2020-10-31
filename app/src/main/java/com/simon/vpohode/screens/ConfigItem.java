package com.simon.vpohode.screens;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.jaredrummler.android.colorpicker.ColorShape;
import com.simon.vpohode.Item;
import com.simon.vpohode.LayoutManager;
import com.simon.vpohode.R;
import com.simon.vpohode.Styles;
import com.simon.vpohode.Templates;
import com.simon.vpohode.database.DBFields;
import com.simon.vpohode.database.DatabaseHelper;

public class ConfigItem extends AppCompatActivity implements ColorPickerDialogListener {

    TextView textcolor;
    EditText nameBox,termidBox;
    Spinner spinner, spinnerTemplate;
    Button delButton,colorButton;
    RadioGroup radGrpTop, radGrpLayer;
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    long userId=0;
    private static final int firstId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setTitle(getString(R.string.title_item));

        textcolor = findViewById(R.id.textColor);
        nameBox = findViewById(R.id.name);
        termidBox = findViewById(R.id.termid);
        spinner = findViewById(R.id.Style);
        spinnerTemplate = findViewById(R.id.Template);
        radGrpTop = findViewById(R.id.radios);
        radGrpLayer = findViewById(R.id.radios2);
        delButton = findViewById(R.id.deleteButton);
        colorButton = findViewById(R.id.color);
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
            termidBox.setText(String.valueOf(userCursor.getInt(4)));
            spinner.setSelection(Styles.getOrdinalByString(userCursor.getString(2)));
            textcolor.setTextColor(userCursor.getInt(6));
            textcolor.setBackgroundColor(userCursor.getInt(6));
            Log.i("Test color"," color is " + userCursor.getInt(6));
            //textcolor.setText("#" + (Integer.toHexString(userCursor.getInt(6)).substring(2).toUpperCase()));

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
                radGrpLayer.setVisibility(View.GONE);
            }

            userCursor.close();
        } else {
            // hide button Delete, It will be new Item
            delButton.setVisibility(View.GONE);
        }

        // if Save button clicked do next:
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.save){
                    ContentValues cv = new ContentValues();
                    cv.put(DBFields.NAME.toFieldName(), nameBox.getText().toString());
                    cv.put(DBFields.TERMID.toFieldName(), Double.parseDouble(termidBox.getText().toString()));
                    cv.put(DBFields.STYLE.toFieldName(), spinner.getSelectedItem().toString());
                    if (radGrpTop.getCheckedRadioButtonId() == R.id.top) {
                        cv.put(DBFields.ISTOP.toFieldName(), 1);
                    } else {
                        cv.put(DBFields.ISTOP.toFieldName(), 0);
                    }
                    if(radGrpLayer.getCheckedRadioButtonId() == R.id.layer1){
                        cv.put(DBFields.LAYER.toFieldName(), 1);
                    }else if(radGrpLayer.getCheckedRadioButtonId() == R.id.layer2){
                        cv.put(DBFields.LAYER.toFieldName(), 2);
                    }else {
                        cv.put(DBFields.LAYER.toFieldName(), 3);
                    }
                    cv.put(DBFields.COLOR.toFieldName(),textcolor.getCurrentTextColor());

                    if (userId > 0) {
                        db.update(DatabaseHelper.TABLE, cv, DBFields.ID.toFieldName() + "=" + userId, null);
                    } else {
                        db.insert(DatabaseHelper.TABLE, null, cv);
                    }
                    goHome();
                }
                return true;
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
                        termidBox.setText(selectedTemplate.getTermid());
                        spinner.setSelection(selectedTemplate.getStyle());
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
                        termidBox.setText(selectedTemplate.getTermid());
                        spinner.setSelection(selectedTemplate.getStyle());
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
                    radGrpLayer.setVisibility(View.GONE);
                } else {
                    radGrpLayer.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    public void delete(View view){
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
// полный список атрибутов класса ColorPickerDialog смотрите ниже
    }
    public void onClickColor(View view) {
                createColorPickerDialog(firstId);
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color,hsv);
        textcolor.setText("#" + (Integer.toHexString(color).substring(2).toUpperCase()) + " hue " + hsv[0]);
        for(float x: hsv) {
            Log.i("Test color!!!", "- " + x);
        }
        textcolor.setTextColor(Color.HSVToColor(hsv));
        textcolor.setBackgroundColor(Color.HSVToColor(hsv));
    }

    @Override
    public void onDialogDismissed(int dialogId) {
        Toast.makeText(this, "Цвет выбран", Toast.LENGTH_SHORT).show();
    }


}
