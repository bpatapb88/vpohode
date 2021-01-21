package com.simon.vpohode.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.simon.vpohode.Item;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "vpohode.db"; //name of DB
    private static final int SCHEMA = 1;  // Version of DB
    public static final String TABLE = "items"; // Name of Table
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {

        db.execSQL ("CREATE TABLE items ("
                + DBFields.ID.toFieldName()
                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DBFields.NAME.toFieldName() + " " + DBFields.NAME.toType() + ", "
                + DBFields.STYLE.toFieldName() + " " + DBFields.STYLE.toType() + ", "
                + DBFields.ISTOP.toFieldName() + " " + DBFields.ISTOP.toType() + ", "
                + DBFields.TERMID.toFieldName() + " " + DBFields.TERMID.toType() + ", "
                + DBFields.LAYER.toFieldName() + " " + DBFields.LAYER.toType() + ", "
                + DBFields.COLOR.toFieldName() + " " + DBFields.COLOR.toType() + ", "
                + DBFields.FOTO.toFieldName() + " " + DBFields.FOTO.toType() + ");");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public static Cursor getCursoreByIsTop (SQLiteDatabase db, final int istop){

        return db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE + " WHERE " + DBFields.ISTOP.toFieldName() + " = " + istop, null);
    }
    public static Cursor getCursoreByIsTop (SQLiteDatabase db, final int istop, int layer){
        return db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE + " WHERE " + DBFields.ISTOP.toFieldName() + " = " + istop + " AND " + DBFields.LAYER.toFieldName() + "=" + layer, null);
    }
    public static Cursor getCursoreByIsTop (SQLiteDatabase db, final int istop, double index){
        return db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE + " WHERE " + DBFields.ISTOP.toFieldName() + " =" + istop + " AND " + DBFields.TERMID.toFieldName() + " = " + index, null);
    }
    public static Cursor getCursoreByIsTop (SQLiteDatabase db, final int istop, double index, ArrayList<Integer[]> colors){
        return db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE + " WHERE " + DBFields.ISTOP.toFieldName() + " =" + istop + " AND " + DBFields.TERMID.toFieldName() + " = " + index + " AND " + DBFields.COLOR.toFieldName() + " = " + colors.get(0)[0], null);
    }
    public static Cursor getItemByID (SQLiteDatabase db, int id){
        return db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE + " WHERE " + DBFields.ID.toFieldName() + " = " + id, null);
    }

    public static Cursor getItemByID (SQLiteDatabase db, Item[] id){
        String string = "";
        for(int i = 0; i < id.length; i++){
            if(i == (id.length-1)){
                string += DBFields.ID.toFieldName() + " = " + id[i].getId();
            }else{
                string += DBFields.ID.toFieldName() + " = " + id[i].getId() + " OR ";
            }
        }
        return db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE + " WHERE " + string, null);
    }

}
