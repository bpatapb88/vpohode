package com.simon.vpohode.assync_tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class TakeImageFromStorage extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public TakeImageFromStorage(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    @Override
    protected Bitmap doInBackground(String... path) {
        String pathCurrent = path[0];
        Bitmap b = null;
        File file2 = new File(pathCurrent);
        try(FileInputStream fis2 = new FileInputStream(file2)){
            b = BitmapFactory.decodeStream(fis2);
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return b;
    }
    @Override
    protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
    }
}
