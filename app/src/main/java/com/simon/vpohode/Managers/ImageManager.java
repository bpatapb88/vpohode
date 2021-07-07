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
        System.out.println("image manager " + directory.toString());
        System.out.println(directory.getAbsolutePath());
        // Create imageDir
        String filename = System.currentTimeMillis() + ".jpg";
        File mypath=new File(directory,filename);
        FileOutputStream fos = null;
        System.out.println("image manager mypath- " + mypath.getAbsolutePath());
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 20, fos);
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

    public static boolean deleteImagesById(long id, SQLiteDatabase db){
        Cursor cursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " + DBFields.ID.toFieldName() + "=?", new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        File file = new File(cursor.getString(7));
        return file.delete();
    }

}
