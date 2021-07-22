package com.simon.vpohode.managers;

import android.content.SharedPreferences;

import com.simon.vpohode.Item;
import com.simon.vpohode.R;

import java.util.ArrayList;

public class StyleManager {

    public static boolean isLookMatchStyle (Item[] finalLook, SharedPreferences prefs){
        Integer[] styles = new Integer[finalLook.length];
        int counter = 0;
        for(Item item: finalLook){
            styles[counter++] = item.getStyle();
        }

        ArrayList<Integer> notCheckedStyles = StyleManager.notCheckedStyles(prefs);
        for(Integer temp : styles){

            if(notCheckedStyles.contains(temp)){
                return false;
            }
        }
        return true;
    }

    private static ArrayList<Integer> notCheckedStyles(SharedPreferences pref){
        ArrayList<Integer> notCheckedStyles = new ArrayList<>();
        if(!pref.getBoolean("check_box_casual", true)){
            notCheckedStyles.add(R.string.casual);
        }
        if(!pref.getBoolean("check_box_business", true)){
            notCheckedStyles.add(R.string.business);
        }
        if(!pref.getBoolean("check_box_elegant", true)){
            notCheckedStyles.add(R.string.elegant);
        }
        if(!pref.getBoolean("check_box_sport", true)){
            notCheckedStyles.add(R.string.sport);
        }
        if(!pref.getBoolean("check_box_home", true)){
            notCheckedStyles.add(R.string.home);
        }
        return notCheckedStyles;
    }

    public static ArrayList<Item[]> filterStyle(ArrayList<Item[]> looks, SharedPreferences prefs){
        ArrayList<Item[]> result = new ArrayList<>();
        for(Item[] look: looks){
            if(isLookMatchStyle(look,prefs)){
                result.add(look);
            }
        }
        return result;
    }

}
