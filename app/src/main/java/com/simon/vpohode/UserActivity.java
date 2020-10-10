package com.simon.vpohode;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;


public class UserActivity extends AppCompatActivity {

    EditText nameBox;
    EditText termidBox;
    Spinner spinner, spinnerTemplate;
    Styles Style = Styles.NONE;
    Templates templates = Templates.NONE;
    Button delButton;
    Button saveButton;
    RadioGroup radGrpTop, radGrpLayer;
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    long userId=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        nameBox = findViewById(R.id.name);
        termidBox = findViewById(R.id.termid);
        spinner = findViewById(R.id.Style);
        spinnerTemplate = findViewById(R.id.Template);
        radGrpTop = findViewById(R.id.radios);
        radGrpLayer = findViewById(R.id.radios2);
        delButton = findViewById(R.id.deleteButton);
        saveButton = findViewById(R.id.saveButton);
        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.getWritableDatabase();
        // configure spinner
        ArrayAdapter<Styles> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Style.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        ArrayAdapter<Templates> adapterTemplate = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, templates.values());
        adapterTemplate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTemplate.setAdapter(adapterTemplate);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            userId = extras.getLong("id");
        }
        // if 0, add
        if (userId > 0) {
            Log.i("Test item edit"," Success! ");
            // get item by id from db
            userCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                    DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(userId)});
            userCursor.moveToFirst();
            nameBox.setText(userCursor.getString(1));
            termidBox.setText(String.valueOf(userCursor.getInt(4)));
            spinner.setSelection(Style.getOrdinalByString(userCursor.getString(2)));

            if (userCursor.getInt(3) == 1){
                radGrpTop.check(R.id.top);
            }else{
                radGrpTop.check(R.id.bottom);}
            userCursor.close();
        } else {
            // hide button Delete
            delButton.setVisibility(View.GONE);
        }

        // working with Radio buttons
        Log.i("Test item edit2"," Success! 2 - " + nameBox.getText());
    }
    @Override
    public void onResume() {
        super.onResume();

        spinnerTemplate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                Item selectedTemplate = templates.fillTemplate(spinnerTemplate.getSelectedItemPosition());
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
                RadioButton bot = (RadioButton)radioGroup.findViewById(R.id.bottom);
                boolean isChecked = bot.isChecked();
                if(isChecked){
                    radGrpLayer.setVisibility(View.GONE);
                } else {
                    radGrpLayer.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    public void save(View view){
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_NAME, nameBox.getText().toString());
        cv.put(DatabaseHelper.COLUMN_TERMID, Double.parseDouble(termidBox.getText().toString()));
        cv.put(DatabaseHelper.COLUMN_STYLE, spinner.getSelectedItem().toString());
        if (radGrpTop.getCheckedRadioButtonId() == R.id.top) {
            cv.put(DatabaseHelper.COLUMN_TOP, 1);
        } else {
            cv.put(DatabaseHelper.COLUMN_TOP, 0);
        }
        if (userId > 0) {
            db.update(DatabaseHelper.TABLE, cv, DatabaseHelper.COLUMN_ID + "=" + userId, null);
        } else {
            db.insert(DatabaseHelper.TABLE, null, cv);
        }
        goHome();
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

    enum Style{

    }
}
