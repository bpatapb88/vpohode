package com.simon.vpohode.screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.simon.vpohode.BuildConfig;
import com.simon.vpohode.Managers.LayoutManager;
import com.simon.vpohode.Managers.PlacePhotoManager;
import com.simon.vpohode.Managers.WeatherManager;
import com.simon.vpohode.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    // private final String weatherURL = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=8e923e31bdf57632b77f12106cf7f3ee&lang=ru&units=metric";
    // try https://api.openweathermap.org/data/2.5/onecall?lat=33.441792&lon=-94.037689&exclude=daily,minutely&appid=8e923e31bdf57632b77f12106cf7f3ee
    private final static String weatherURL = "http://api.openweathermap.org/data/2.5/forecast?q=%s&appid=8e923e31bdf57632b77f12106cf7f3ee&lang=%s&units=metric";
    private final static String placeURL = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=%s&key=%s&inputtype=textquery&fields=name,photos";
    private TextView textViewWeather;
    private Double avgTempertureCel;
    private String photoReference;
    public static String rain = "";
    private String city = "Brno";
    private SharedPreferences preferences;
    private ImageView fotoCity;
    private StringBuilder stringBuilderPlace = new StringBuilder();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        LayoutManager.setTheme(preferences, getTheme());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fotoCity = findViewById(R.id.foto_city);
       /* FragmentManager fm = getSupportFragmentManager();
        CustomDialogFragment editNameDialogFragment = CustomDialogFragment.newInstance("Some Title");
        editNameDialogFragment.show(fm, "fragment_edit_name");*/
        textViewWeather = findViewById(R.id.textViewWeather);
    }

    @Override
    public void onResume(){
        super.onResume();
        city = preferences.getString("city", "Brno");
        DownloadTask task = new DownloadTask();
        String weatherURLWithCity = String.format(weatherURL, city, getResources().getConfiguration().locale.getCountry());
        String placeURLWithCity = String.format(placeURL,city, BuildConfig.GOOGLE_API);
        task.execute(weatherURLWithCity, placeURLWithCity);
    }

    public void goToWardrobe(View view){                                        //TODO create new class view manager
        Intent intent = new Intent(this, Wardrobe.class);
        startActivity(intent);
    }

    public void goToWash(View view){
        Intent intent = new Intent(this, WashActivity.class);
        startActivity(intent);
    }
    public void goToSettings(View view){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void logOff(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void scrollTest(View view){
        Double temp = 1000d;
        try {
            temp = Double.valueOf(preferences.getString("temp", "1000"));
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        if(temp != 1000){
            avgTempertureCel = temp;
        }
        Intent intent = new Intent(this, ScrollingLooksActivity.class);
        intent.putExtra("term", avgTempertureCel);
        startActivity(intent);
    }

    private class DownloadTask extends AsyncTask <String, Void, String> {
        private final static String CELSIUS_SYMBOL = "\u2103 ";

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder result = new StringBuilder();
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line = bufferedReader.readLine();
                while (line != null){
                    result.append(line);
                    line = bufferedReader.readLine();
                }
                // Now store output from Google Place Api
                url = new URL(strings[1]);
                urlConnection = (HttpURLConnection) url.openConnection();
                in = urlConnection.getInputStream();
                reader = new InputStreamReader(in);
                bufferedReader = new BufferedReader(reader);
                line = bufferedReader.readLine();
                while (line != null){
                    stringBuilderPlace.append(line);
                    line = bufferedReader.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "End";
            } finally {
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
            }

            return result.toString();
        }
        @Override
        protected void onPostExecute (String s){
            if (s.equals("End")){
                return;
            }
            super.onPostExecute(s);

            WeatherManager weatherManager = new WeatherManager();
            avgTempertureCel = weatherManager.getAverange(s);

            PlacePhotoManager placePhotoManager = new PlacePhotoManager(stringBuilderPlace.toString());
            DownloadImageTask downloadImageTask = new DownloadImageTask(fotoCity);
            downloadImageTask.execute(placePhotoManager.getPhotoURL());

        // save the temperature
                avgTempertureCel = (Double.parseDouble(weatherManager.getFeelTem0()) + Double.parseDouble(weatherManager.getFeelTem1()))/2;
                String ifPlus = "";
                if (avgTempertureCel >= 0) {
                    ifPlus = "+";
                }
                final String outputWeather = (int)Double.parseDouble(weatherManager.getFeelTem0()) + " " + CELSIUS_SYMBOL + weatherManager.getDescription();
                textViewWeather.setText(city + ": " + ifPlus + outputWeather);
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error Image", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}