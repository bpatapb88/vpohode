package com.simon.vpohode;
import android.database.Cursor;

public class CountBestTermIndex {
    private static int maxTemper = 31;
    public double getTopIndex(Cursor input, Double currentTemperature){
        double min = Integer.MAX_VALUE;
        double bestTopIndex = 0;
        if (input.moveToFirst()){
            do {
                double x = Math.abs((maxTemper - currentTemperature)/3 - input.getDouble(input.getColumnIndex("termindex")));
                if (min > x) {
                    min = x;
                    bestTopIndex = input.getDouble(input.getColumnIndex("termindex"));
                }
            }
            while (input.moveToNext());
        }
        return bestTopIndex;
    }
    public double getBotIndex(Cursor input, Double currentTemperature){
        double min = Integer.MAX_VALUE;
        double bestBottomIndex = 0;
        if (input.moveToFirst()){
            do {
                double x = Math.abs((maxTemper - currentTemperature)/3 - input.getDouble(input.getColumnIndex("termindex")));
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
