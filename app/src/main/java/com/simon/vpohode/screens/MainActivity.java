package com.simon.vpohode.screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

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
    private TextView textViewWeather;
    private Double avgTempertureCel;
    public static String rain;
    private String city = "Brno";
    private SharedPreferences preferences;
    private ImageView fotoCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if(preferences.getBoolean("theme", true)){
            getTheme().applyStyle(R.style.AppTheme,true);
        }else{
            getTheme().applyStyle(R.style.OverlayThemeRose,true);
        }


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
        task.execute(weatherURLWithCity);

        DownloadImageTask downloadImageTask = new DownloadImageTask(fotoCity);
        switch (city){
            case "Brno":
                downloadImageTask.execute("http://utazasok.org/wp-content/uploads/2019/03/Brno2.jpg");
                break;
            case "Moscow":
                downloadImageTask.execute("https://www.moscow-driver.com/photos/var/albums/Personalized_Tours/2018-01-25_Moscow_Winter_Photo_Tour/ALP-2018-0125-268-Old-Kremlin-Against-Modern-Moskva-City-in-Winter.jpg");
                break;
            case "Екатеринбург":
                downloadImageTask.execute("https://nesiditsa.ru/wp-content/uploads/2012/10/Ekaterinburg.jpg");
                break;
            default:
                downloadImageTask.execute("https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.healthwire.co%2Fwp-content%2Fuploads%2F2020%2F07%2Fweather.png");
        }
    }

    public void goToWardrobe(View view){                                        //TODO create new class view manager
        Intent intent = new Intent(this, Wardrobe.class);
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

    public void onClickShowItems (View view){
        //Intent intent = new Intent(this, ShowItems.class);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        Intent intent = new Intent(this, LooksActivity.class);

        Double temp = 1000d;
        try {
            temp = Double.valueOf(prefs.getString("temp", "1000"));
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        if(temp != 1000){
            avgTempertureCel = temp;
        }
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