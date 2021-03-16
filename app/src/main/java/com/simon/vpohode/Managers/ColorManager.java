package com.simon.vpohode.Managers;


import android.graphics.Color;

import com.simon.vpohode.Item;
import com.simon.vpohode.Rules;

import java.util.ArrayList;
import java.util.Collections;

public class ColorManager {

    public static boolean isLookMatch(Item[] finalLook){
        Integer[] listOfColors = new Integer[finalLook.length];
        int counter = 0;
        for(Item item: finalLook){
            listOfColors[counter++] = item.getColor();
        }

        float[][] looksHSV = new float[listOfColors.length][3];
        float[] hsv = new float[3];
        ArrayList<Integer> neutral = new ArrayList<>();
        ArrayList<Float> notNeutral = new ArrayList<>();

        for(Integer j = 0; j < looksHSV.length; j++) {
            Color.colorToHSV(listOfColors[j],hsv);
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
            return true;
        }else{
            switch (notNeutral.size()){
                case 2:
                    if(Math.abs((notNeutral.get(1) - notNeutral.get(0)) - 18) < Rules.ACCURACY){
                        return true;
                    }
                    break;
                case 3:
                    if(Math.abs((notNeutral.get(1) - notNeutral.get(0) - 3))<Rules.ACCURACY && Math.abs((notNeutral.get(2) - notNeutral.get(1) - 3))<Rules.ACCURACY){
                        return true;
                    }else if(Math.abs((notNeutral.get(1) - notNeutral.get(0) - 12))<Rules.ACCURACY && Math.abs((notNeutral.get(2) - notNeutral.get(1) - 12))<Rules.ACCURACY){
                        return true;
                    }
                    break;
            }
        }
        return false;
    }

}
