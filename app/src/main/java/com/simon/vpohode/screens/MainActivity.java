package com.simon.vpohode.screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.simon.vpohode.Managers.LayoutManager;
import com.simon.vpohode.Managers.WeatherManager;
import com.simon.vpohode.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.jar.Attributes;

public class MainActivity extends AppCompatActivity {

    // private final String weatherURL = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=8e923e31bdf57632b77f12106cf7f3ee&lang=ru&units=metric";
    // try https://api.openweathermap.org/data/2.5/onecall?lat=33.441792&lon=-94.037689&exclude=daily,minutely&appid=8e923e31bdf57632b77f12106cf7f3ee
    private final static String weatherURL = "http://api.openweathermap.org/data/2.5/forecast?q=%s&appid=8e923e31bdf57632b77f12106cf7f3ee&lang=ru&units=metric";
    private TextView textViewWeather;
    private Double avgTempertureCel;
    public static String rain;
    private String city = "Brno";
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean("theme", true)){
            getTheme().applyStyle(R.style.AppTheme,true);
        }else{
            getTheme().applyStyle(R.style.OverlayThemeRose,true);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.welcome));
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.action_settings){
                    goToSettings(item.getActionView());
                }
                return true;
            }

        });

        textViewWeather = findViewById(R.id.textViewWeather);
        //show the weather

        //String city = editTextCity.getText().toString().trim();

       // task.onPostExecute(url,1);
        System.out.println("onCreate");
    }

    @Override
    public void onResume(){
        super.onResume();
        prefs= PreferenceManager.getDefaultSharedPreferences(this);
        city = prefs.getString("city", "Brno");
        DownloadTask task = new DownloadTask();
        String weatherURLWithCity = String.format(weatherURL, city);
        task.execute(weatherURLWithCity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        LayoutManager.invisible(R.id.save,menu);
        LayoutManager.invisible(R.id.search,menu);

        return true;
    }

    public void goToWardrobe(View view){                                        //TODO create new class view manager
        Intent intent = new Intent(this, Wardrobe.class);
        startActivity(intent);
    }
    public void goToSettings(View view){
        Intent intent = new Intent(this, SettingsActivity.class);
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
        private final static String NOW_WORD = "Сейчас в городе ";

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
                textViewWeather.setText(NOW_WORD + city + ": " + ifPlus + outputWeather);
        }
    }
}