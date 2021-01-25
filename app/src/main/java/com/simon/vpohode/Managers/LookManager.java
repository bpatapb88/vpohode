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

    public static ArrayList<Item[]> getLooks(double temp, Context context){

        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(context);

        setAccurancy(prefs);

        ArrayList<Item[]> result = new ArrayList<>();
        Cursor botItems,topItems,topItems2,topItems3;

        int layers = Rules.getLayers(temp);
        double min = getInterval(temp) - Rules.ACCURACY;
        double max = getInterval(temp) + Rules.ACCURACY;
        double top;
        double bot;

        botItems = DatabaseHelper.getCursoreByIsTop(db,0);
        topItems = DatabaseHelper.getCursoreByIsTop(db,1,1);

        switch (layers){
            case 1:
                if(botItems.moveToFirst()){
                    do{
                        if(topItems.moveToFirst()){
                         do{
                             top = topItems.getDouble(topItems.getColumnIndex("termindex"));
                             bot = botItems.getDouble(botItems.getColumnIndex("termindex"));
                             if(top > min &&
                                     top < max &&
                                     bot > min &&
                                     bot < max) {
                                 result.add(itemCursorToArray(new Cursor[]{topItems, botItems},layers));
                             }

                         }while (topItems.moveToNext());
                        }
                    }while(botItems.moveToNext());
                }
                break;
            case 2:
                topItems2 = DatabaseHelper.getCursoreByIsTop(db,1,2);
                if(botItems.moveToFirst()){
                    do{
                        if(topItems.moveToFirst()){
                            do{
                                if(topItems2.moveToFirst()){
                                 do{
                                     top = (topItems.getDouble(4) +
                                             topItems2.getDouble(4))/2;
                                     bot = botItems.getDouble(4);
                                     if(bot > min & bot < max & top > min & top < max)
                                     {
                                         result.add(itemCursorToArray(new Cursor[]{topItems, topItems2, botItems},layers));
                                     }
                                 }while(topItems2.moveToNext());
                                }
                            }while (topItems.moveToNext());
                        }
                    }while(botItems.moveToNext());
                }
                topItems2.close();
                break;
            case 3:
                topItems2 = DatabaseHelper.getCursoreByIsTop(db,1,2);
                topItems3 = DatabaseHelper.getCursoreByIsTop(db,1,3);
                if(botItems.moveToFirst()){
                    do{
                        if(topItems.moveToFirst()){
                            do{
                                if(topItems2.moveToFirst()){
                                    do{
                                        if(topItems3.moveToFirst()){
                                            do{


                                                bot = botItems.getDouble(botItems.getColumnIndex("termindex"));
                                                top = (topItems.getDouble(topItems.getColumnIndex("termindex")) +
                                                        topItems2.getDouble(topItems2.getColumnIndex("termindex"))+
                                                        topItems3.getDouble(topItems3.getColumnIndex("termindex")))/3;

                                        if(bot > min &&
                                                bot < max &&
                                                top > min &&
                                                top < max){
                                            //check if styles match
                                            if(StyleManager.isLookMatchStyle(new String[]{botItems.getString(botItems.getColumnIndex("style")),
                                                    topItems.getString(topItems.getColumnIndex("style")),
                                                    topItems2.getString(topItems.getColumnIndex("style")),
                                                    topItems3.getString(topItems.getColumnIndex("style"))},prefs)){
                                                //check if color match
                                                if(prefs.getBoolean("sync", false)) {
                                                    if(ColorManager.isLookMatch(new Integer[]{
                                                            botItems.getInt(botItems.getColumnIndex("color")),
                                                            topItems.getInt(topItems.getColumnIndex("color")),
                                                            topItems2.getInt(topItems.getColumnIndex("color")),
                                                            topItems3.getInt(topItems.getColumnIndex("color"))})){

                                                        result.add(itemCursorToArray(new Cursor[]{topItems, topItems2, topItems3, botItems},layers));
                                                    }
                                                }else{
                                                        result.add(itemCursorToArray(new Cursor[]{topItems, topItems2, topItems3, botItems},layers));
                                                }
                                            }
                                        }
                                            }while (topItems3.moveToNext());

                                        }
                                    }while(topItems2.moveToNext());
                                }
                            }while (topItems.moveToNext());
                        }
                    }while(botItems.moveToNext());
                }
                topItems2.close();
                topItems3.close();
                break;
        }
        botItems.close();
        topItems.close();
        db.close();
        return result;
    }

    private static void setAccurancy(SharedPreferences prefs) {
        String accuracy = prefs.getString("accuracy","0.5");
        if(!accuracy.equals(0.5)){
            Rules.ACCURACY = Double.valueOf(accuracy);
        }
    }

    private static Item[] itemCursorToArray(Cursor[] cursors, Integer layers){
        Item[] look = new Item[layers + 1];
        for(int i = 0; i < layers+1; i++){
            look[i] = new Item(cursors[i].getInt(cursors[i].getColumnIndex("_id")),
                    cursors[i].getString(cursors[i].getColumnIndex("name")),
                    cursors[i].getString(cursors[i].getColumnIndex("style")),
                    cursors[i].getInt(cursors[i].getColumnIndex("istop")),
                    cursors[i].getDouble(cursors[i].getColumnIndex("termindex")),
                    cursors[i].getInt(cursors[i].getColumnIndex("layer")),
                    cursors[i].getInt(cursors[i].getColumnIndex("color")),
                    cursors[i].getString(cursors[i].getColumnIndex("foto")));
        }
        return look;
    }

}
