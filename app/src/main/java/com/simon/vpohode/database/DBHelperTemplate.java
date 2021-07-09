package com.simon.vpohode.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.simon.vpohode.R;
import com.simon.vpohode.Styles;

public class DBHelperTemplate extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "template.db";
    private static final int SCHEMA = 1;
    public static final String TABLE = "templates";
    public static final String rawQueryPart = "SELECT * FROM " + TABLE;
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

        String[] arrayTemplate = context.getResources().getStringArray(R.array.templates);
        ContentValues cv = new ContentValues();
        cv.put(DBFields.NAME.toFieldName(), arrayTemplate[1]);
        cv.put(DBFields.STYLE.toFieldName(), Styles.CASUAL.toInt());
        cv.put(DBFields.ISTOP.toFieldName(), 0);
        cv.put(DBFields.TERMID.toFieldName(),1d);
        cv.put(DBFields.LAYER.toFieldName(),1);
        db.insert(DBHelperTemplate.TABLE, null, cv);
        cv.clear();
        cv.put(DBFields.NAME.toFieldName(), arrayTemplate[2]);
        cv.put(DBFields.STYLE.toFieldName(), Styles.BUSINESS.toInt());
        cv.put(DBFields.ISTOP.toFieldName(), 0);
        cv.put(DBFields.TERMID.toFieldName(),2d);
        cv.put(DBFields.LAYER.toFieldName(),1);
        db.insert(DBHelperTemplate.TABLE, null, cv);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public static Cursor getTemplates(SQLiteDatabase db){
        return db.rawQuery(rawQueryPart, null);
    }
}
