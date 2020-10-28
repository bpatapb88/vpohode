package com.simon.vpohode;
import android.database.Cursor;

public class CountBestTermIndex {
    private static final int MAX_TEMPER = 31;
    public static double getTopIndex(Cursor input, Double currentTemperature){
        double min = Integer.MAX_VALUE;
        double bestTopIndex = 0;
        int cooficent;

        if(currentTemperature >= 20){
            cooficent = 3;
        }else if(currentTemperature >= 9){
            cooficent = 6;
        }else{
            cooficent = 9;
        }

        if (input.moveToFirst()){
            do {
                double x = Math.abs((MAX_TEMPER - currentTemperature)/cooficent - input.getDouble(input.getColumnIndex("termindex")));
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
