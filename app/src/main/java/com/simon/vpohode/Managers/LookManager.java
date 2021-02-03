package com.simon.vpohode.Managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.preference.PreferenceManager;

import com.simon.vpohode.Item;
import com.simon.vpohode.Rules;
import com.simon.vpohode.database.DatabaseHelper;

import java.util.ArrayList;

public class LookManager {

    public static ArrayList<Item[]> getLooks(double temp, Context context){
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(context);

        setAccurancy(prefs);

        int layersTop = Rules.getLayersTop(temp);
        int layersBot = Rules.getLayersBot(temp);

        Cursor[] cursorLayersTop = new Cursor[layersTop];
        for(int i = 0; i< cursorLayersTop.length; i++){
            cursorLayersTop[i] = DatabaseHelper.getCursoreByIsTop(db,1,i+1);
        }

        Cursor[] cursorLayersBot = new Cursor[layersBot-1];
        for(int i = 0; i< cursorLayersBot.length; i++){
            cursorLayersBot[i] = DatabaseHelper.getCursoreByIsTop(db,0,i+1);
        }

        Item[][] topLooks = cursorsToArray(cursorLayersTop);
        Item[][] botLooks = cursorsToArray(cursorLayersBot);

        closeCursors(cursorLayersBot);
        closeCursors(cursorLayersTop);

        ArrayList<Item[]> readyTopLooks = referedToTemp(topLooks,temp);
        ArrayList<Item[]> readyBotLooks = referedToTemp(botLooks,temp);

        ArrayList<Item[]> result = new ArrayList<>();
        if(readyTopLooks.size()==0 || readyBotLooks.size()==0){
            return null;
        }else{

            for(int i =0; i<readyTopLooks.size();i++){
                for(int j =0; j<readyBotLooks.size();j++){
                    result.add(sumOfArray(readyTopLooks.get(i),readyBotLooks.get(j)));
                }
            }
        }
        return result;
    }

    public static void closeCursors(Cursor[] cursors){
        for(Cursor cursor: cursors){
            cursor.close();
        }
    }

    public static Item[] sumOfArray(Item[] items1, Item[] items2){
        Item[] result = new Item[items1.length + items2.length];
        int count =0;
        for(int i = 0; i < items1.length;i++){
            result[i] = items1[i];
            count++;
        }
        for(int j = 0; j< items2.length; j++){
            result[count++]=items2[j];
        }
        return result;
    }

    public static Item[][] cursorsToArray(Cursor[] input){
        int sizeOfOutput = 1;
        for(Cursor cursor: input){
            cursor.moveToFirst();
            sizeOfOutput = sizeOfOutput*cursor.getCount();
        }
        Item[][] result = new Item[sizeOfOutput][input.length];
        for(int i = 0; i<result.length;i++){
            for(int j = 0;j<input.length;j++){
                result[i][j]=cursorToItem(input[j]);
            }
            for(int x = 0; x < input.length; x++){
                if(input[x].moveToNext()){
                    break;
                }else{
                    input[x].moveToFirst();
                }
            }
        }
        return result;
    }

    private static Item cursorToItem(Cursor cursors){
        Item item = new Item(cursors.getInt(cursors.getColumnIndex("_id")),
                cursors.getString(cursors.getColumnIndex("name")),
                cursors.getString(cursors.getColumnIndex("style")),
                cursors.getInt(cursors.getColumnIndex("istop")),
                cursors.getDouble(cursors.getColumnIndex("termindex")),
                cursors.getInt(cursors.getColumnIndex("layer")),
                cursors.getInt(cursors.getColumnIndex("color")),
                cursors.getString(cursors.getColumnIndex("foto")));

        return item;
    }

    public static ArrayList<Item[]> referedToTemp (Item[][] looks, Double temp){
        ArrayList<Item[]> matchedLooks = new ArrayList<>();
        for(Item[] look: looks){
            double merginalIndex=0;
            for(int i = 0; i < look.length;i++){
                merginalIndex += look[i].getTermid();
            }
            merginalIndex = merginalIndex/look.length;
            double min = getInterval(temp) - Rules.ACCURACY;
            double max = getInterval(temp) + Rules.ACCURACY;
            if(merginalIndex > min && merginalIndex < max){
                matchedLooks.add(look);
            }
        }
        return matchedLooks;
    }

    public static double getInterval(double temp){
        double result;
        if(temp >= 20){
            result = (Rules.MAX_TEMPER - temp)/(Rules.COEFFICIENT);
        }else if(temp >= 9){
            result = (Rules.MAX_TEMPER - temp)/(Rules.COEFFICIENT*2);
        }else{
            result = (Rules.MAX_TEMPER - temp)/(Rules.COEFFICIENT*3);
        }
        return result;
    }

    private static void setAccurancy(SharedPreferences prefs) {
        String accuracy = prefs.getString("accuracy","0.5");
        if(!accuracy.equals(0.5)){
            Rules.ACCURACY = Double.valueOf(accuracy);
        }
    }

}
