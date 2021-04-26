package com.simon.vpohode.Managers;

import com.simon.vpohode.screens.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherManager {
    private String feelTem0 ="";
    private String feelTem1 = "";
    private String description = "";

    public String getFeelTem0() {
        return feelTem0;
    }

    public String getFeelTem1() {
        return feelTem1;
    }

    public String getDescription() {
        return description;
    }

    public double getAverange(String s){

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
            if(description.equals("дождь")){
                MainActivity.rain = description;
            }
            JSONObject main0 = currentWeatherAll.getJSONObject("main");
            feelTem0 = main0.getString("feels_like");
            JSONObject main1 = weatherIn3HoursAll.getJSONObject("main");
            feelTem1 = main1.getString("feels_like");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // save the temperature
        return (Double.parseDouble(feelTem0) + Double.parseDouble(feelTem1))/2;
    }

}
