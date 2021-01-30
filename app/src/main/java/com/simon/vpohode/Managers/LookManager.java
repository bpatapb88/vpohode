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


    public static ArrayList<Item[]> getLooksNew(double temp, Context context){
        ArrayList<Item[]> result = new ArrayList<>();
        ArrayList<Item[]> topItems = new ArrayList<>();
        ArrayList<Item> botItems = new ArrayList<>();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(context);
        setAccurancy(prefs);

        double min = getInterval(temp) - Rules.ACCURACY;
        double max = getInterval(temp) + Rules.ACCURACY;
        double top;
        double bot;
        int topLayers = Rules.getLayersTop(temp); // 1-3
        int botLayers = Rules.getLayersTop(temp); // 2-3

        Cursor[] topCursors = new Cursor[3];

        for(int i=0; i < topLayers; i++){
            topCursors[i] = DatabaseHelper.getCursoreByIsTop(db,1,i+1);
        }

        if(topCursors[0].moveToFirst()){
            do{
                if(topCursors[1].moveToFirst()){
                    do{
                        if(topCursors[2].moveToFirst()){
                           do{
                               top = (topCursors[0].getDouble(topCursors[0].getColumnIndex("termindex")) +
                                       topCursors[1].getDouble(topCursors[1].getColumnIndex("termindex"))+
                                       topCursors[2].getDouble(topCursors[2].getColumnIndex("termindex")))/3;
                               if(top > min &&
                                       top < max){
                                   topItems.add(new Item[]{cursorToItem(topCursors[0]),cursorToItem(topCursors[1]),cursorToItem(topCursors[2])});
                               }
                           }while (topCursors[2].moveToNext());
                        }else{
                                top = (topCursors[0].getDouble(topCursors[0].getColumnIndex("termindex")) +
                                    topCursors[1].getDouble(topCursors[1].getColumnIndex("termindex")))/2;
                            if(top > min &&
                                    top < max){
                                topItems.add(new Item[]{cursorToItem(topCursors[0]),cursorToItem(topCursors[1])});
                            }
                        }
                    }while(topCursors[1].moveToNext());
                }else{
                    top = topCursors[0].getDouble(topCursors[0].getColumnIndex("termindex"));
                    if(top > min &&
                            top < max){
                        topItems.add(new Item[]{cursorToItem(topCursors[0])});
                    }
                }
            }while (topCursors[0].moveToNext());
        }


        for(Cursor cursor: topCursors){
            cursor.close();
        }

        return topItems;
    }

    public static ArrayList<Item[]> getLooks(double temp, Context context){

        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(context);

        setAccurancy(prefs);

        ArrayList<Item[]> result = new ArrayList<>();
        Cursor botItems,topItems,topItems2,topItems3;

        int layers = Rules.getLayersTop(temp);
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

}
