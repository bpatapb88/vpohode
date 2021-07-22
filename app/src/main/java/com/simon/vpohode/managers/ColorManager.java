package com.simon.vpohode.managers;


import android.graphics.Color;

import com.simon.vpohode.Item;
import com.simon.vpohode.Rules;

import java.util.ArrayList;
import java.util.Collections;

public class ColorManager {

    private ColorManager() {
    }

    public static boolean isLookMatch(Item[] finalLook){
        Integer[] listOfColors = new Integer[finalLook.length];
        int counter = 0;
        for (Item item : finalLook) {
            listOfColors[counter++] = item.getColor();
        }

        float[][] looksHSV = new float[listOfColors.length][3];
        float[] hsv = new float[3];
        ArrayList<Float> notNeutral = new ArrayList<>();

        for(int j = 0; j < looksHSV.length; j++) {
            Color.colorToHSV(listOfColors[j],hsv);
            looksHSV[j] = hsv;
            looksHSV[j][0] = (int)looksHSV[j][0]/10.f;
            if(looksHSV[j][1] >= 0.2 && looksHSV[j][2] >= 0.2){
                notNeutral.add(looksHSV[j][0]);
            }
        }
        Collections.sort(notNeutral);
        switch (notNeutral.size()){
                case 0: case 1:
                    return true;
                case 2:
                    if(Math.abs((notNeutral.get(1) - notNeutral.get(0)) - 18) < Rules.accuracy){
                        return true;
                    }
                    break;
                case 3:
                    if(Math.abs((notNeutral.get(1) - notNeutral.get(0) - 3))<Rules.accuracy && Math.abs((notNeutral.get(2) - notNeutral.get(1) - 3))<Rules.accuracy ||
                            Math.abs((notNeutral.get(1) - notNeutral.get(0) - 12))<Rules.accuracy && Math.abs((notNeutral.get(2) - notNeutral.get(1) - 12))<Rules.accuracy){
                        return true;
                    }
                    break;
                default:
            }
        return false;
    }

    public static int getHSVColors(int color2){

        int counter = Integer.MAX_VALUE;
        int resourceId = 0;
        ColorList[] colorLists = ColorList.values();

        int colorFromPallet;
        for (ColorList colorList : colorLists){
            colorFromPallet = Color.parseColor(colorList.getValue());

            int diff = Math.abs(Color.red(color2) - Color.red(colorFromPallet)) +
                    Math.abs(Color.green(color2) - Color.green(colorFromPallet)) +
                    Math.abs(Color.blue(color2) - Color.blue(colorFromPallet));

            if(diff < counter){
                counter = diff;
                resourceId = colorList.getResource();
            }
        }
        return resourceId;
    }

}
