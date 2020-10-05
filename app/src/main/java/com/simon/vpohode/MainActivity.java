package com.simon.vpohode;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
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
    private final String weatherURL2 = "http://api.openweathermap.org/data/2.5/forecast?q=%s&appid=8e923e31bdf57632b77f12106cf7f3ee&lang=ru&units=metric";
    private TextView textViewWeather;
    Double term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewWeather = findViewById(R.id.textViewWeather);

        //show the weather
        //String city = editTextCity.getText().toString().trim();
        DownloadTask task = new DownloadTask();
        //String url = String.format(weatherURL, city);
        String url2 = String.format(weatherURL2, "Brno");
        task.execute(url2);
       // task.onPostExecute(url,1);
    }

    public void wardrobe(View view){
        Intent intent = new Intent(this, Wardrobe.class);
        startActivity(intent);
    }

    public void onClickShowItems (View view){
        Intent intent = new Intent(this, ShowItems.class);
        intent.putExtra("term", term);
        startActivity(intent);
    }

    private class DownloadTask extends AsyncTask <String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder result = new StringBuilder();
            URL url = null;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(strings[0]);
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
            try {
                JSONObject jsonObject = new JSONObject(s);

                    JSONArray jsonArray = jsonObject.getJSONArray("list");
                    JSONObject list0 = jsonArray.getJSONObject(0);
                    JSONObject list1 = jsonArray.getJSONObject(1);
                    JSONArray forcast = list1.getJSONArray("weather");
                    JSONObject weather = forcast.getJSONObject(0);
                    String description = weather.getString("description");

                    JSONObject main0 = list0.getJSONObject("main");
                    String mainTem0 = main0.getString("feels_like");
                    JSONObject main1 = list1.getJSONObject("main");
                    String mainTem1 = main1.getString("feels_like");

                    // save the temperature
                    term = (Double.parseDouble(mainTem0) + Double.parseDouble(mainTem1))/2;

                    if (term >= 0 ) {
                        textViewWeather.setText("Сейчас: +" + (int)Double.parseDouble(mainTem0) + "\u2103 " + description);
                    } else {
                        textViewWeather.setText("Сейчас: " + (int)Double.parseDouble(mainTem0) + "\u2103 " + description);
                    }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

}