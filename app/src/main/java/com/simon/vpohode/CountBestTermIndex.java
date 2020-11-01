package com.simon.vpohode;
import android.database.Cursor;

public class CountBestTermIndex {
    public static final int MAX_TEMPER = 33;
    public static final double coefficient = 4;

    public static double getTopIndex(Cursor input, Double currentTemperature){
        double min = Integer.MAX_VALUE;
        double bestTopIndex = 0;
        int layers;

        if(currentTemperature >= 20){
            layers = 1;
        }else if(currentTemperature >= 9){
            layers = 2;
        }else{
            layers = 3;
        }

        if (input.moveToFirst()){
            do {
                double x = Math.abs((MAX_TEMPER - currentTemperature)/(CountBestTermIndex.coefficient*layers) - input.getDouble(input.getColumnIndex("termindex")));
                if (min > x) {
                    min = x;
                    bestTopIndex = input.getDouble(input.getColumnIndex("termindex"));
                }
            }
            while (input.moveToNext());
        }
        return bestTopIndex;
    }

    public static double getBotIndex(Cursor input, Double currentTemperature){
        double min = Integer.MAX_VALUE;
        double bestBottomIndex = 0;
        if (input.moveToFirst()){
            do {
                double x = Math.abs((MAX_TEMPER - currentTemperature)/3 - input.getDouble(input.getColumnIndex("termindex")));
                if (min > x) {
                    min = x;
                    bestBottomIndex = input.getDouble(input.getColumnIndex("termindex"));
                }
            }
            while (input.moveToNext());
        }
        return bestBottomIndex;
    }

}
