package com.simon.vpohode;

import android.database.Cursor;

public class CountSomething {

    public double getTopIndex(Cursor input, Double term){
        double min = Integer.MAX_VALUE;
        double bestTopIndex = 0;
        if (input.moveToFirst()){
            do {
                double x = Math.abs((30 - term)/3 - input.getDouble(4));
                if (min > x) {
                    min = x;
                    bestTopIndex = input.getDouble(4);
                }
            }
            while (input.moveToNext());
        }
        return bestTopIndex;
    }
    public double getBotIndex(Cursor input, Double term){
        double min = Integer.MAX_VALUE;
        double bestBottomIndex = 0;
        if (input.moveToFirst()){
            do {
                double x = Math.abs((31 - term)/3 - input.getDouble(4));
                if (min > x) {
                    min = x;
                    bestBottomIndex = input.getDouble(4);
                }
            }
            while (input.moveToNext());
        }
        return bestBottomIndex;

    }

}
