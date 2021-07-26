package com.simon.vpohode.cut_out_background;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Pair;

import com.simon.vpohode.screens.ConfigItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class SaveDrawingTask extends AsyncTask<Bitmap, Void, Pair<File, Exception>> {

    private static final String SAVED_IMAGE_FORMAT = "png";
    private static final String SAVED_IMAGE_NAME = "cutout_tmp";

    private final WeakReference<CutBGActivity> activityWeakReference;

    SaveDrawingTask(CutBGActivity activity) {
        this.activityWeakReference = new WeakReference<>(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        activityWeakReference.get().loadingModal.setVisibility(VISIBLE);
    }

    @Override
    protected Pair<File, Exception> doInBackground(Bitmap... bitmaps) {

        try {
            File file = File.createTempFile(SAVED_IMAGE_NAME, SAVED_IMAGE_FORMAT, activityWeakReference.get().getApplicationContext().getCacheDir());

            try (FileOutputStream out = new FileOutputStream(file)) {
                bitmaps[0].compress(Bitmap.CompressFormat.PNG, 95, out);
                return new Pair<>(file, null);
            }
        } catch (IOException e) {
            return new Pair<>(null, e);
        }
    }
    @Override
    protected void onPostExecute(Pair<File, Exception> result) {
        super.onPostExecute(result);

        Intent resultIntent = new Intent();

        if (result.first != null) {
            resultIntent.setClass(activityWeakReference.get(),ConfigItem.class);
            Uri uri = Uri.fromFile(result.first);
            resultIntent.putExtra(CutOut.CUTOUT_EXTRA_RESULT, uri);
            activityWeakReference.get().startActivity(resultIntent);
        } else {
            activityWeakReference.get().exitWithError(result.second);
        }
    }
}