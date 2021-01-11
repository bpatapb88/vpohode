package com.simon.vpohode.Managers;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.simon.vpohode.database.DBFields;
import com.simon.vpohode.database.DatabaseHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageManager {

    public static String saveToInternalStorage(Bitmap bitmapImage, Context context){
        // getApplicationContext()
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        String filename = System.currentTimeMillis() + ".jpg";
        File mypath=new File(directory,filename);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath() + "/" + filename;
    }

    public static Bitmap loadImageFromStorage(String path) {
        Bitmap b = null;
        FileInputStream fis = null;
        try {
            File f=new File(path);
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return b;
    }

    public static boolean deleteImagesById(long id, SQLiteDatabase db){
        Cursor cursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " + DBFields.ID.toFieldName() + "=?", new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        File file = new File(cursor.getString(7));
        return file.delete();
    }

}
