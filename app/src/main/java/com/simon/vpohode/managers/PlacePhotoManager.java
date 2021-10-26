package com.simon.vpohode.managers;

import com.simon.vpohode.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PlacePhotoManager {
    private String photoURL;

    public PlacePhotoManager(String s) {
        try{
            final JSONObject jsonObject = new JSONObject(s);
            final JSONArray jsonArray = jsonObject.getJSONArray("candidates");
            final JSONObject jsonObject1 = jsonArray.getJSONObject(0);
            final JSONArray photos = jsonObject1.getJSONArray("photos");
            final JSONObject jsonObject2 = photos.getJSONObject(0);
            final String photoReference = jsonObject2.getString("photo_reference");
            String photoUrlTemplate = "https://maps.googleapis.com/maps/api/place/photo?photoreference=%s&key=%s&maxwidth=400&maxheight=400";
            photoURL = String.format(photoUrlTemplate,photoReference, "AIzaSyCIiHq1jStgXeV9JgfFtoXdyKs8ZHBdrzk");
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public String getPhotoURL() {
        return photoURL;
    }
}
