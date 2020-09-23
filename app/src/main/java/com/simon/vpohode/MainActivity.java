package com.simon.vpohode;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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
    private final String weatherURL = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=8e923e31bdf57632b77f12106cf7f3ee&lang=en&units=metric";
    private TextView textViewWeather;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;
    ListView userList,userList2;
    Double term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewWeather = findViewById(R.id.textViewWeather);
        userList = (ListView)findViewById(R.id.list);
        userList2 = (ListView)findViewById(R.id.list2);
        databaseHelper = new DatabaseHelper(getApplicationContext());

        //show the weather
        //String city = editTextCity.getText().toString().trim();
        String city = "Brno";
        DownloadTask task = new DownloadTask();
        String url = String.format(weatherURL, city);
        task.execute(url);
    }
    @Override
    public void onResume() {
        super.onResume();
        // open connection

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        // close connection
        db.close();
        userCursor.close();
    }

    public void wardrobe(View view){
        Intent intent = new Intent(this, Wardrobe.class);
        startActivity(intent);
    }

    public void onClickShowItems(View view) {

        int result = 0;
        int result2 = 0;
        // connection to DB
        db = databaseHelper.getReadableDatabase();
        //get cursor from db to have list of termindexes
        userCursor =  db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE, null);

        if (userCursor.moveToFirst()){
            double min = Integer.MAX_VALUE;
            double min2 = Integer.MAX_VALUE;
                do {
                    int termindex = userCursor.getInt(4);
                        if (userCursor.getInt(3) == 1) {
                            if (min > Math.abs(36 - term - termindex)) {
                                min = Math.abs(36 - term - termindex);
                                result = termindex;
                            }
                        } else {
                            if (min2 > Math.abs(36 - term - termindex)) {
                                min2 = Math.abs(36 - term - termindex);
                                result2 = termindex;
                            }
                        }
                }
                while (userCursor.moveToNext());
        }


        String[] headers = new String[] {DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_STYLE, DatabaseHelper.COLUMN_TOP,};
        // again get cursor but only items with needed termindex
        userCursor =  db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE + " WHERE " + DatabaseHelper.COLUMN_TERMID + " = " + result + " AND " + DatabaseHelper.COLUMN_TOP + " = 1", null);
        // create adapter, send cursor
        userAdapter = new SimpleCursorAdapter(this, R.layout.two_line_list_item, userCursor, headers, new int[]{R.id.text1, R.id.text2, R.id.text3}, 0);
        //db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " + DatabaseHelper.COLUMN_NAME + " like ?", new String[]{"%" + constraint.toString() + "%"});
        userList.setAdapter(userAdapter);
        userCursor =  db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE + " WHERE " + DatabaseHelper.COLUMN_TERMID + " = " + result2 + " AND " + DatabaseHelper.COLUMN_TOP + " = 0", null);
        userAdapter = new SimpleCursorAdapter(this, R.layout.two_line_list_item, userCursor, headers, new int[]{R.id.text1, R.id.text2, R.id.text3}, 0);
        userList2.setAdapter(userAdapter);
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
                JSONArray jsonArray = jsonObject.getJSONArray("weather");
                JSONObject weather = jsonArray.getJSONObject(0);
                JSONObject main = jsonObject.getJSONObject("main");
                String mainTem = main.getString("temp");

                // save the temperature
                term = Double.parseDouble(mainTem);

                String description = weather.getString("description");
                String result = mainTem + " " + description;
                Double Temp = Double.parseDouble(mainTem);
                if (Temp >= 0 ) {
                    textViewWeather.setText("Погода сейчас в Брно: +" + result);
                } else {
                    textViewWeather.setText("Погода сейчас в Брно: " + result);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

}