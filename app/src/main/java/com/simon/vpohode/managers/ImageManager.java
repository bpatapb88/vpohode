package com.simon.vpohode.managers;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import com.simon.vpohode.database.DBFields;
import com.simon.vpohode.database.DatabaseHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.NoSuchFileException;

public class ImageManager {

    private ImageManager() {
    }

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
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 20, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(fos != null){
                try {
                    fos.close();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return directory.getAbsolutePath() + "/" + filename;
    }

    public static void deleteImagesById(long id, SQLiteDatabase db) throws NoSuchFileException, DirectoryNotEmptyException, IOException{
        Cursor cursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " + DBFields.ID.toFieldName() + "=?", new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        File file = new File(cursor.getString(7));
        cursor.close();
        if(!file.delete()){
            //exception is threw
        }



    }

}
