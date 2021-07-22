package com.simon.vpohode.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.simon.vpohode.R;
import com.simon.vpohode.Styles;

public class DBHelperTemplate extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "template.db";
    private static final int SCHEMA = 1;
    public static final String TABLE = "templates";
    public static final String RAW_QUERY_PART = "SELECT * FROM " + TABLE;
    private Context context;

    public DBHelperTemplate(Context context){
        super(context, DATABASE_NAME, null, SCHEMA);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) { //Running only first time and when update
        db.execSQL ("CREATE TABLE " + TABLE + " ("
                + DBFields.ID.toFieldName()
                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DBFields.NAME.toFieldName() + " " + DBFields.NAME.toType() + ", "
                + DBFields.STYLE.toFieldName() + " " + DBFields.STYLE.toType() + ", "
                + DBFields.ISTOP.toFieldName() + " " + DBFields.ISTOP.toType() + ", "
                + DBFields.TERMID.toFieldName() + " " + DBFields.TERMID.toType() + ", "
                + DBFields.LAYER.toFieldName() + " " + DBFields.LAYER.toType() + ", "
                + DBFields.COLOR.toFieldName() + " " + DBFields.COLOR.toType() + ", "
                + DBFields.FOTO.toFieldName() + " " + DBFields.FOTO.toType() + ", "
                + DBFields.USED.toFieldName() + " " + DBFields.USED.toType() + ", "
                + DBFields.CREATED.toFieldName() + " " + DBFields.CREATED.toType() + ", "
                + DBFields.INWASH.toFieldName() + " " + DBFields.INWASH.toType() + ", "
                + DBFields.BRAND.toFieldName() + " " + DBFields.BRAND.toType() + ");");

        addPrefilledTemplates(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public static Cursor getTemplates(SQLiteDatabase db){
        return db.rawQuery(RAW_QUERY_PART, null);
    }

    private void addPrefilledTemplates(SQLiteDatabase db){
        String[] arrayTemplate = context.getResources().getStringArray(R.array.templates);
        int[] arrayStyles = context.getResources().getIntArray(R.array.templateStyles);
        int[] arrayIsTop = context.getResources().getIntArray(R.array.templateIsTop);
        int[] arrayTermIndex = context.getResources().getIntArray(R.array.templateTermIndex);
        int[] arrayLayer = context.getResources().getIntArray(R.array.templateLayer);
        int length = arrayTemplate.length;

        if(length != arrayStyles.length ||
                length != arrayIsTop.length ||
                length != arrayTermIndex.length ||
                length != arrayLayer.length){
            Toast.makeText(context,"Count of parameters of prepared templates not match",Toast.LENGTH_LONG).show();
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put(DBFields.NAME.toFieldName(), arrayTemplate[0]);
        db.insert(DBHelperTemplate.TABLE, null, cv);

        for(int i = 1; i < length; i++){
            cv.clear();
            cv.put(DBFields.NAME.toFieldName(), arrayTemplate[i]);
            cv.put(DBFields.STYLE.toFieldName(), Styles.values()[arrayStyles[i]].toInt());
            cv.put(DBFields.ISTOP.toFieldName(), arrayIsTop[i]);
            cv.put(DBFields.TERMID.toFieldName(),Double.valueOf(arrayTermIndex[i]));
            cv.put(DBFields.LAYER.toFieldName(),arrayLayer[i]);
            db.insert(DBHelperTemplate.TABLE, null, cv);
        }

    }
}
