package com.simon.vpohode.AssyncTasks;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TakeImageFromStorage extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    ProgressDialog p;

    public TakeImageFromStorage(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        /*p = new ProgressDialog(bmImage.getContext());
        p.setMessage("Please wait...It is downloading");
        p.setIndeterminate(false);
        p.setCancelable(false);
        p.show();*/
    }


    @Override
    protected Bitmap doInBackground(String... path) {
        String pathCurrent = path[0];
        Bitmap b = null;
        FileInputStream fis = null;
        try {
            File f=new File(pathCurrent);
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
    @Override
    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
        //p.hide();
    }
}
