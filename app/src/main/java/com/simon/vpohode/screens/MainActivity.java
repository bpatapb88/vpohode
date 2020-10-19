package com.simon.vpohode.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.simon.vpohode.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    // private final String weatherURL = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=8e923e31bdf57632b77f12106cf7f3ee&lang=ru&units=metric";
    private final static String weatherURL = "http://api.openweathermap.org/data/2.5/forecast?q=%s&appid=8e923e31bdf57632b77f12106cf7f3ee&lang=ru&units=metric";
    private TextView textViewWeather;
    private Double avgTempertureCel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textViewWeather = findViewById(R.id.textViewWeather);
        //show the weather
        //String city = editTextCity.getText().toString().trim();
        DownloadTask task = new DownloadTask();
        //String url = String.format(weatherURL, city);
        String weatherURLWithCity = String.format(weatherURL, "Brno");
        task.execute(weatherURLWithCity);
       // task.onPostExecute(url,1);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem search = menu.findItem(R.id.search);
        search.setVisible(false);
        return true;
    }

    public void goToWardrobe(View view){                                        //TODO create new class view manager
        Intent intent = new Intent(this, Wardrobe.class);
        startActivity(intent);
    }

    public void onClickShowItems (View view){
        Intent intent = new Intent(this, ShowItems.class);
        intent.putExtra("term", avgTempertureCel);
        startActivity(intent);
    }

    private class DownloadTask extends AsyncTask <String, Void, String> {
        private final static String CELSIUS_SYMBOL = "\u2103 ";
        private final static String NOW_WORD = "Сейчас: ";

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
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
            }
            return result.toString();
        }

        @Override

        protected void onPostExecute (String s){
            super.onPostExecute(s);
            String mainTem0 ="";
            String mainTem1 = "";
            String description = "";
            try {
                    final JSONObject jsonObject = new JSONObject(s);
                    final JSONArray jsonArray = jsonObject.getJSONArray("list");
                    // current weather
                    final JSONObject currentWeatherAll = jsonArray.getJSONObject(0);
                    // weather in 3 hours
                    JSONObject weatherIn3HoursAll = jsonArray.getJSONObject(1);
                    JSONArray forecast = weatherIn3HoursAll.getJSONArray("weather");
                    JSONObject weather = forecast.getJSONObject(0);
                    description = weather.getString("description");
                    JSONObject main0 = currentWeatherAll.getJSONObject("main");
                    mainTem0 = main0.getString("feels_like");
                    JSONObject main1 = weatherIn3HoursAll.getJSONObject("main");
                    mainTem1 = main1.getString("feels_like");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // save the temperature
                    avgTempertureCel = (Double.parseDouble(mainTem0) + Double.parseDouble(mainTem1))/2;

                String ifPlus = "";
                if (avgTempertureCel >= 0) {
                    ifPlus = "+";
                }
                final String outputWeather = (int)Double.parseDouble(mainTem0) + CELSIUS_SYMBOL + description;
                // TODO change variable names
                textViewWeather.setText(NOW_WORD + ifPlus + outputWeather);

        }

    }

}