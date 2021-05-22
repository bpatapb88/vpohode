package com.simon.vpohode.Managers;

import com.simon.vpohode.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PlacePhotoManager {
    private String photoURL;
    private final static String photoURLTemplate = "https://maps.googleapis.com/maps/api/place/photo?photoreference=%s&key=%s&maxwidth=400&maxheight=400";

    public PlacePhotoManager(String s) {
        try{
            final JSONObject jsonObject = new JSONObject(s);
            final JSONArray jsonArray = jsonObject.getJSONArray("candidates");
            final JSONObject jsonObject1 = jsonArray.getJSONObject(0);
            final JSONArray photos = jsonObject1.getJSONArray("photos");
            final JSONObject jsonObject2 = photos.getJSONObject(0);
            final String photoReference = jsonObject2.getString("photo_reference");
            photoURL = String.format(photoURLTemplate,photoReference, BuildConfig.GOOGLE_API);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public String getPhotoURL() {
        return photoURL;
    }
}
