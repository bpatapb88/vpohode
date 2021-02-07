package com.simon.vpohode.Managers;

import android.content.SharedPreferences;

import com.simon.vpohode.Item;

import java.util.ArrayList;

public class StyleManager {

    public static boolean isLookMatchStyle (Item[] finalLook, SharedPreferences prefs){
        String[] styles = new String[finalLook.length];
        int counter = 0;
        for(Item item: finalLook){
            styles[counter++] = item.getStyle();
        }

        ArrayList<String> notCheckedStyles = StyleManager.notCheckedStyles(prefs);
        for(String temp : styles){
            if(notCheckedStyles.contains(temp)){
                return false;
            }
        }
        return true;
    }

    private static ArrayList<String> notCheckedStyles(SharedPreferences pref){
        ArrayList<String> notCheckedStyles = new ArrayList<>();
        if(!pref.getBoolean("check_box_casual", true)){
            notCheckedStyles.add("Кэжуал");
        }
        if(!pref.getBoolean("check_box_business", true)){
            notCheckedStyles.add("Бизнес");
        }
        if(!pref.getBoolean("check_box_elegant", true)){
            notCheckedStyles.add("Элегантный");
        }
        if(!pref.getBoolean("check_box_sport", true)){
            notCheckedStyles.add("Спорт");
        }
        if(!pref.getBoolean("check_box_home", true)){
            notCheckedStyles.add("Домашнее");
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
