package com.simon.vpohode.Managers;

import android.content.SharedPreferences;

import java.util.ArrayList;

public class StyleManager {

    public static boolean isLookMatchStyle (String[] styles, SharedPreferences prefs){
        ArrayList<String> notCheckedStyles = StyleManager.notCheckedStyles(prefs);
        for(String temp : styles){
            if(notCheckedStyles.contains(temp)){
                return false;
            }
        }
        return true;
    }

    public static ArrayList<String> notCheckedStyles(SharedPreferences pref){
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

}
