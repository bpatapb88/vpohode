package com.simon.vpohode;

import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.ToDoubleFunction;

public class ColorManager {

    public static ArrayList<Integer> savePallitra(Cursor input){
        ArrayList<Integer> result = new ArrayList<>();
        if (input.moveToFirst()){
            do {
                int output = input.getInt(input.getColumnIndex("color"));
                result.add(output);
                Log.i("Colors of bottom items"," here " + output);
            }
            while (input.moveToNext());
        }
        return result;
    }
    public static ArrayList<Integer[]> colorLook(Integer[][] allColors){
        ArrayList<Integer[]> listOfLooks = new ArrayList<>();
        if (allColors.length == 4){
        for(int i = 0; i < allColors[0].length;i++){
            for(int j = 0; j < allColors[1].length;j++){
                for(int k = 0; k < allColors[2].length;k++){
                    for(int l = 0; l < allColors[3].length;l++){
                        listOfLooks.add(new Integer[]{allColors[0][i],allColors[1][j],allColors[2][k],allColors[3][l]});
                    }
                }
            }
        }
        }else if(allColors.length == 3){
            for(int i = 0; i < allColors[0].length;i++){
                for(int j = 0; j < allColors[1].length;j++){
                    for(int k = 0; k < allColors[2].length;k++){
                            listOfLooks.add(new Integer[]{allColors[0][i],allColors[1][j],allColors[2][k]});
                    }
                }
            }
        }else{
            for(int i = 0; i < allColors[0].length;i++){
                for(int j = 0; j < allColors[1].length;j++){
                        listOfLooks.add(new Integer[]{allColors[0][i],allColors[1][j]});
                }
            }
        }
        return listOfLooks;
    }
    public static ArrayList<Integer[]> bestLooks(ArrayList<Integer[]> listOfLooks){
        ArrayList<Integer[]> result = new ArrayList<>();

        for(Integer i = 0; i < listOfLooks.size(); i++){
            float[][] looksHSV = new float[listOfLooks.get(0).length][3];
            float[] hsv = new float[3];
            ArrayList<Integer> neutral = new ArrayList<>();
            ArrayList<Float> notNeutral = new ArrayList<>();

            for(Integer j = 0; j < looksHSV.length; j++) {
                Color.colorToHSV(listOfLooks.get(i)[j],hsv);
                looksHSV[j] = hsv;
                looksHSV[j][0] = (int)looksHSV[j][0]/10;
                if(looksHSV[j][1] < 0.2 || looksHSV[j][2] < 0.2){
                    neutral.add(j);
                }else{
                    notNeutral.add(looksHSV[j][0]);
                }
            }

            Collections.sort(notNeutral);
            if(notNeutral.size() < 2){
                result.add(listOfLooks.get(i));
            }else{
                switch (notNeutral.size()){
                    case 2:
                        if((notNeutral.get(1) - notNeutral.get(0)) == 18){
                            result.add(listOfLooks.get(i));
                        }
                        break;
                    case 3:
                        if((notNeutral.get(1) - notNeutral.get(0) == 3) && (notNeutral.get(2) - notNeutral.get(1) == 3)){
                            result.add(listOfLooks.get(i));
                        }else if((notNeutral.get(1) - notNeutral.get(0) == 12) && (notNeutral.get(2) - notNeutral.get(1) == 12)){
                            result.add(listOfLooks.get(i));
                        }
                        break;
                    }
                }
            }
        return result;

    }

}
