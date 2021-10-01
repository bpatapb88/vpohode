package com.simon.vpohode.screens;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.simon.vpohode.BuildConfig;
import com.simon.vpohode.managers.LayoutManager;
import com.simon.vpohode.managers.PlacePhotoManager;
import com.simon.vpohode.managers.WeatherManager;
import com.simon.vpohode.R;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    // another Weather URL "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=8e923e31bdf57632b77f12106cf7f3ee&lang=ru&units=metric"
    // try https://api.openweathermap.org/data/2.5/onecall?lat=33.441792&lon=-94.037689&exclude=daily,minutely&appid=8e923e31bdf57632b77f12106cf7f3ee
    private static final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/forecast?q=%s&appid=8e923e31bdf57632b77f12106cf7f3ee&lang=%s&units=metric";
    private static final String PLACE_URL = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=%s&key=%s&inputtype=textquery&fields=name,photos";
    private TextView textViewWeather;
    private Double avgTemperatureCel;

    public static String getPop() {
        return pop;
    }

    public static void setPop(String pop) {
        MainActivity.pop = pop;
    }

    private static String pop = "";
    private String city = "Brno";
    private SharedPreferences preferences;
    private ImageView fotoCity;
    private final StringBuilder stringBuilderPlace = new StringBuilder();
    private FusedLocationProviderClient mFusedLocationClient;
    static final int PERMISSION_ID = 44;
    double latitudeTextView;
    double longitTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        LayoutManager.setTheme(preferences, getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fotoCity = findViewById(R.id.foto_city);
        textViewWeather = findViewById(R.id.textViewWeather);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }



    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if (location == null) {
                        requestNewLocationData();
                    } else {
                        latitudeTextView = location.getLatitude();
                        longitTextView = location.getLongitude();
                        city = getLocationName(latitudeTextView, longitTextView);
                        setWeatherAndPicture();
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private void setWeatherAndPicture(){
        DownloadTask task2 = new DownloadTask();
        String weatherURLWithCity = String.format(WEATHER_URL, city, getResources().getConfiguration().locale.getCountry());
        String placeURLWithCity = String.format(PLACE_URL, city, BuildConfig.GOOGLE_API);
        task2.execute(weatherURLWithCity, placeURLWithCity);
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitudeTextView = mLastLocation.getLatitude();
            longitTextView = mLastLocation.getLongitude();
            city = getLocationName(latitudeTextView, longitTextView);
            setWeatherAndPicture();
        }
    };

    private boolean checkPermissions() {
        return androidx.core.content.ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && androidx.core.content.ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == PERMISSION_ID && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (preferences.getBoolean("firstrun", true)) {
            requestPermissions();
            preferences.edit().putBoolean("firstrun", false).commit();
        }

        city = preferences.getString("city", "Unknown");
        if(city.equals("Unknown") || city.equals("")){
            if (checkPermissions()) {
                getLastLocation();
            }else{
                Toast.makeText(this, "Please turn on your location or write city in Settings", Toast.LENGTH_LONG).show();
            }

        }else{
            setWeatherAndPicture();
        }
    }

    public void goToWardrobe(View view){
        Intent intent = new Intent(this, Wardrobe.class);
        startActivity(intent);
    }

    public void goToLookActivity(View view){
        double temp = 1000d;
        try {
            temp = Double.parseDouble(preferences.getString("temp", "1000"));
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        if(temp != 1000){
            avgTemperatureCel = temp;
        }
        Intent intent = new Intent(this, LooksActivity.class);
        intent.putExtra("term", avgTemperatureCel);
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

    public void scrollTest(View view){
        double temp = 1000d;
        try {
            temp = Double.parseDouble(preferences.getString("temp", "1000"));
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        if(temp != 1000){
            avgTemperatureCel = temp;
        }
        Intent intent = new Intent(this, SelectLookActivity.class);
        intent.putExtra("term", avgTemperatureCel);
        startActivity(intent);
    }

    private class DownloadTask extends AsyncTask <String, Void, String> {
        private static final String CELSIUS_SYMBOL = "\u2103 ";

        @Override
        protected String doInBackground(String... strings) {

            StringBuilder result = new StringBuilder();
            HttpURLConnection urlConnection = null;
            URL url;
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
            avgTemperatureCel = weatherManager.getAverange(s);

            PlacePhotoManager placePhotoManager = new PlacePhotoManager(stringBuilderPlace.toString());
            Picasso.get().load(placePhotoManager.getPhotoURL()).into(fotoCity);

        // save the temperature
                avgTemperatureCel = (Double.parseDouble(weatherManager.getFeelTem0()) + Double.parseDouble(weatherManager.getFeelTem1()))/2;
                StringBuilder textWeather = new StringBuilder();
                textWeather.append(city).append(": ");
                if (avgTemperatureCel >= 0) {
                    textWeather.append("+");
                }
                final String outputWeather = (int)Double.parseDouble(weatherManager.getFeelTem0()) + " " + CELSIUS_SYMBOL + weatherManager.getDescription();
                textWeather.append(outputWeather);
                textViewWeather.setText(textWeather.toString());
        }
    }

    public String getLocationName(double lattitude, double longitude) {

        String cityName = "Not Found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {

            List<Address> addresses = gcd.getFromLocation(lattitude, longitude,
                    10);

            for (Address adrs : addresses) {
                if (adrs != null) {

                    String locality = adrs.getLocality();
                    if (locality != null && !locality.equals("")) {
                        cityName = locality;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;

    }

}