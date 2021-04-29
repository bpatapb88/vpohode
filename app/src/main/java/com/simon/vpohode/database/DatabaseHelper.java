package com.simon.vpohode.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "vpohode.db"; //name of DB
    private static final int SCHEMA = 1;  // Version of DB
    public static final String TABLE = "items";// Name of Table
    public static final String rawQueryPart = "SELECT * FROM " + TABLE + " WHERE ";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {

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
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public static Cursor getCursorWardrobe(SQLiteDatabase db){
        return db.rawQuery(rawQueryPart + DBFields.INWASH.toFieldName() + " = 0", null);
    }

    public static Cursor getCursorInWash(SQLiteDatabase db){
        return db.rawQuery(rawQueryPart + DBFields.INWASH.toFieldName() + " = 1", null);
    }

    public static Cursor getCursoreByIsTop (SQLiteDatabase db, final int istop, int layer){
        return db.rawQuery(rawQueryPart + DBFields.ISTOP.toFieldName() + " = " + istop + " AND " + DBFields.LAYER.toFieldName() + "=" + layer + " AND " + DBFields.INWASH.toFieldName() + " = 0", null);
    }

}
