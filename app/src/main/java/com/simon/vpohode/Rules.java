package com.simon.vpohode;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import androidx.preference.PreferenceManager;

import com.simon.vpohode.screens.ConfigItem;
import com.simon.vpohode.screens.LooksActivity;

public class Rules {

    public static final int MAX_TEMPER = 33;

    //step
    public static final double COEFFICIENT = 4;

    //точность
    public static double ACCURACY = 0.5;

    public static int getLayersTop(double temp){
        int layers = 0;
        if(temp >= 21){
            layers = 1;
        }else if(temp >= 6){
            layers = 2;
        }else{
            layers = 3;
        }
        return layers;
    }
    public static int getLayersBot(double temp){
        int layers = 0;
        if(temp >= 5){
            layers = 1;
        }else{
            layers = 2;
        }
        return layers;
    }
}
