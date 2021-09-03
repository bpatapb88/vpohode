package com.simon.vpohode.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.simon.vpohode.Styles;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "vpohode.db"; //name of DB
    private static final int SCHEMA = 1;  // Version of DB
    public static final String TABLE = "items";// Name of Table
    public static final String TABLE_LOOKS = "looks";// Name of Table Looks
    private static final String RAW_QUERY_PART = "SELECT * FROM " + TABLE + " WHERE ";
    private final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
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

        db.execSQL("CREATE TABLE " + TABLE_LOOKS + " ("
                + DBLooksFields.ID.toFieldName() + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DBLooksFields.NAME.toFieldName() + " " + DBLooksFields.NAME.toType() + ", "
                + DBLooksFields.TERMMAX.toFieldName() + " " + DBLooksFields.TERMMAX.toType() + ", "
                + DBLooksFields.TERMMIN.toFieldName() + " " + DBLooksFields.TERMMIN.toType() + ", "
                + DBLooksFields.ITEMS.toFieldName() + " " + DBLooksFields.ITEMS.toType() + ");"
        );

        addPrefilledLooks(db);
    }

    private void addPrefilledLooks(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(DBLooksFields.NAME.toFieldName(), "First look");
        cv.put(DBLooksFields.TERMMAX.toFieldName(), 30d);
        cv.put(DBLooksFields.TERMMIN.toFieldName(), 25d);
        cv.put(DBLooksFields.ITEMS.toFieldName(), "1,2,3,4");
        db.insert(TABLE_LOOKS,null,cv);
        cv.clear();

        cv.put(DBLooksFields.NAME.toFieldName(), "Second look");
        cv.put(DBLooksFields.TERMMAX.toFieldName(), 25d);
        cv.put(DBLooksFields.TERMMIN.toFieldName(), 20d);
        cv.put(DBLooksFields.ITEMS.toFieldName(), "1,3,4,5,7");
        db.insert(TABLE_LOOKS,null,cv);


        Calendar calendar = Calendar.getInstance();
        String currentTime = dateFormat.format(calendar.getTime());

        cv.clear();
        //shirt
        cv.put(DBFields.NAME.toFieldName(),"Shirt");
        cv.put(DBFields.COLOR.toFieldName(),-787987);
        cv.put(DBFields.STYLE.toFieldName(),2);
        cv.put(DBFields.ISTOP.toFieldName(),1);
        cv.put(DBFields.LAYER.toFieldName(),1);
        cv.put(DBFields.TERMID.toFieldName(),2);
        cv.put(DBFields.INWASH.toFieldName(), false);
        cv.put(DBFields.USED.toFieldName(), 0);
        cv.put(DBFields.CREATED.toFieldName(),currentTime);
        db.insert(TABLE,null,cv);

        cv.clear();
        //pants
        cv.put(DBFields.NAME.toFieldName(),"Pants");
        cv.put(DBFields.COLOR.toFieldName(),-14013910);
        cv.put(DBFields.STYLE.toFieldName(),1);
        cv.put(DBFields.ISTOP.toFieldName(),0);
        cv.put(DBFields.LAYER.toFieldName(),2);
        cv.put(DBFields.TERMID.toFieldName(),2);
        cv.put(DBFields.INWASH.toFieldName(), false);
        cv.put(DBFields.USED.toFieldName(), 0);
        cv.put(DBFields.CREATED.toFieldName(),currentTime);
        db.insert(TABLE,null,cv);

        cv.clear();
        //Shoes
        cv.put(DBFields.NAME.toFieldName(),"Shoes");
        cv.put(DBFields.COLOR.toFieldName(),-10797002);
        cv.put(DBFields.STYLE.toFieldName(),3);
        cv.put(DBFields.ISTOP.toFieldName(),0);
        cv.put(DBFields.LAYER.toFieldName(),3);
        cv.put(DBFields.TERMID.toFieldName(),2);
        cv.put(DBFields.INWASH.toFieldName(), false);
        cv.put(DBFields.USED.toFieldName(), 0);
        cv.put(DBFields.CREATED.toFieldName(),currentTime);
        db.insert(TABLE,null,cv);

        cv.clear();
        //Snickers
        cv.put(DBFields.NAME.toFieldName(),"Snickers");
        cv.put(DBFields.COLOR.toFieldName(),-6381922);
        cv.put(DBFields.STYLE.toFieldName(),4);
        cv.put(DBFields.ISTOP.toFieldName(),0);
        cv.put(DBFields.LAYER.toFieldName(),3);
        cv.put(DBFields.TERMID.toFieldName(),1);
        cv.put(DBFields.INWASH.toFieldName(), false);
        cv.put(DBFields.USED.toFieldName(), 0);
        cv.put(DBFields.CREATED.toFieldName(),currentTime);
        db.insert(TABLE,null,cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOOKS);
        onCreate(db);
    }

    public static Cursor getCursorWardrobe(SQLiteDatabase db, int sortBy){
        return db.rawQuery(RAW_QUERY_PART + DBFields.INWASH.toFieldName() + " = 0 " + getOrderString(sortBy), null);
    }

    public static Cursor getCursorInWash(SQLiteDatabase db){
        return db.rawQuery(RAW_QUERY_PART + DBFields.INWASH.toFieldName() + " = 1", null);
    }

    public static Cursor getCursoreByIsTop (SQLiteDatabase db, final int istop, int layer){
        return db.rawQuery(RAW_QUERY_PART + DBFields.ISTOP.toFieldName() + " = " + istop + " AND " + DBFields.LAYER.toFieldName() + "=" + layer + " AND " + DBFields.INWASH.toFieldName() + " = 0", null);
    }

    public static String getOrderString(int sortBy){
        String result = "ORDER BY ";
        switch (sortBy){
            case 1:
                result += "name";
                break;
            case 2:
                result += "name DESC";
                break;
            case 3:
                result += "brand";
                break;
            case 4:
                result += "termindex";
                break;
            default:
                result = "";
                break;
        }
        return result;
    }

}
