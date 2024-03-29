package com.simon.vpohode.managers;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.preference.PreferenceManager;

import com.simon.vpohode.Item;
import com.simon.vpohode.Look;
import com.simon.vpohode.R;
import com.simon.vpohode.Rules;
import com.simon.vpohode.database.DBFields;
import com.simon.vpohode.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LookManager {

    public LookManager() {
    }

    public String message = "";

    public List<Item[]> getPotentialLooks(String lookId, Context context){
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_LOOKS + " WHERE _id = " + lookId,null);
        List<Integer> itemsId;
        Item[] items;
        List<Item[]> result = new ArrayList<>();
        if(cursor.moveToFirst()){
            itemsId = new Look(cursor,db).getItemsArray();
            items = new Item[itemsId.size()];
            for(int i = 0; i< items.length; i++){
                items[i] = new Item().getItemById(itemsId.get(i),db);
            }
            result.add(items);
        }
        cursor.close();
        db.close();
        databaseHelper.close();
        return result;
    }

    public int isLookUseable(Look look){
        ArrayList<Item> items = look.getItemsNew();
        for(Item item : items){
            if(item.isInwash()){
                return item.getId();
            }
        }
        return -1;
    }

    public List<Item[]> getPotentialLooks(double temp, Context context){
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

        Cursor[] cursorLayersBot = new Cursor[layersBot];
        for(int i = 0; i< cursorLayersBot.length; i++){
            cursorLayersBot[i] = DatabaseHelper.getCursoreByIsTop(db,0,2-i);
        }

        Cursor[] cursorLayersBoots = new Cursor[]{DatabaseHelper.getCursoreByIsTop(db,0,3)};


        Item[][] topLooks = cursorsToArray(cursorLayersTop);
        Item[][] botLooks = cursorsToArray(cursorLayersBot);
        Item[][] bootsLooks = cursorsToArray(cursorLayersBoots);

        closeCursors(cursorLayersBot);
        closeCursors(cursorLayersTop);
        closeCursors(cursorLayersBoots);
        db.close();
        databaseHelper.close();

        List<Item[]> readyTopLooks =  referedToTempTop(topLooks,temp);
        List<Item[]> readyBotLooks = referedToTempBot(botLooks,temp);
        List<Item[]> readyBootsLooks = referedToTempBoots(bootsLooks,temp);

        if(!prefs.getBoolean("weather",true)){
            readyTopLooks =  Arrays.asList(topLooks);
            readyBotLooks =  Arrays.asList(botLooks);
            readyBootsLooks =  Arrays.asList(bootsLooks);
        }

        List<Item[]> result = new ArrayList<>();
        if(readyTopLooks.isEmpty() || readyBotLooks.isEmpty() || readyBootsLooks.isEmpty()){
            if(readyTopLooks.isEmpty()){
                message += context.getResources().getString(R.string.no_top);
            }
            if(readyBotLooks.isEmpty()){
                message += context.getResources().getString(R.string.no_bot);
            }
            if(readyBootsLooks.isEmpty()){
                message += context.getResources().getString(R.string.no_foot);
            }
            return new ArrayList<>();
        }else{
            for(int i =0; i<readyTopLooks.size();i++){
                for(int j =0; j<readyBotLooks.size();j++){
                    for(int y = 0; y<readyBootsLooks.size();y++) {
                        Item[] finalLook = sumOfArray(readyTopLooks.get(i), readyBotLooks.get(j),readyBootsLooks.get(y));
                        boolean checkColor = prefs.getBoolean("sync",false);
                        if(!checkColor) {
                            result.add(finalLook);
                            int[] ids = new int[finalLook.length];
                            for (int z = 0 ; z < finalLook.length ; z++){
                                ids[z] = finalLook[z].getId();
                            }
                        }else{
                            if(ColorManager.isLookMatch(finalLook)){
                                result.add(finalLook);
                            }
                        }
                    }
                }
            }
        }
        if(result.isEmpty()){
            message = context.getResources().getString(R.string.no_color);
            return new ArrayList<>();
        }else{
            result = StyleManager.filterStyle(result,prefs);
            if(result.isEmpty()){
                message = context.getResources().getString(R.string.no_style);
                return new ArrayList<>();
            }else{
                return result;
            }
        }
    }

    private void closeCursors(Cursor[] cursors){
        for(Cursor cursor: cursors){
            cursor.close();
        }
    }

    private Item[] sumOfArray(Item[] items1, Item[] items2, Item[] items3){
        Item[] result = new Item[items1.length + items2.length + items3.length];
        int count = 0;
        for(int i = 0; i < items1.length;i++){
            result[i] = items1[i];
            count++;
        }
        for (Item item : items2) {
            result[count++] = item;
        }
        for (Item item : items3) {
            result[count++] = item;
        }
        return result;
    }

    private Item[][] cursorsToArray(Cursor[] input){
        int sizeOfOutput = 1;
        for(Cursor cursor: input){
            cursor.moveToFirst();
            sizeOfOutput = sizeOfOutput*cursor.getCount();
        }
        Item[][] result = new Item[sizeOfOutput][input.length];
        for(int i = 0; i<result.length;i++){
            for(int j = 0;j<input.length;j++){
                result[i][j]=new Item().cursorToItem(input[j]);
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

    private List<Item[]> referedToTempTop(Item[][] looks, Double temp){
        ArrayList<Item[]> matchedLooks = new ArrayList<>();
        for(Item[] look: looks){
            double merginalIndex=0;
            for(int i = 0; i < look.length;i++){
                merginalIndex += look[i].getTermid();
            }

            int layers = Rules.getLayersTop(temp);

            double neededTemp = 0;
            switch (layers){
                case 1:
                    neededTemp = ((int) (33 - temp))/4d + 1;
                    break;
                case 2:
                    neededTemp = ((int) (21 - temp))/3d + 2;
                    break;
                case 3:
                    neededTemp = ((int) (6 - temp))/3d + 3;
                    break;
                default:
            }

            if(merginalIndex == neededTemp && merginalIndex < 9 || merginalIndex == 9){
                matchedLooks.add(look);
            }
        }
        return matchedLooks;
    }
    private List<Item[]> referedToTempBot (Item[][] looks, Double temp){
        ArrayList<Item[]> matchedLooks = new ArrayList<>();
        for(Item[] look: looks){
            double merginalIndex=0;
            for(int i = 0; i < look.length;i++){
                merginalIndex += look[i].getTermid();
            }

            int layers = Rules.getLayersBot(temp);
            if(layers == 1){
                if(temp > 21){
                    if(merginalIndex==1) {
                        matchedLooks.add(look);
                    }
                }else{
                    if(merginalIndex==2){
                        matchedLooks.add(look);
                    }
                }

            }else{
                if(temp > (-3)){
                    if(look[1].getTermid() == 1 && look[0].getTermid() ==2){
                        matchedLooks.add(look);
                    }
                }else if(temp > (-10)){
                    if(look[1].getTermid() == 1 && look[0].getTermid() ==3){
                        matchedLooks.add(look);
                    }
                }else{
                    if(look[1].getTermid() > 1 && look[0].getTermid() == 3){
                        matchedLooks.add(look);
                    }

                }

            }

        }
        return matchedLooks;
    }
    private List<Item[]> referedToTempBoots (Item[][] looks, Double temp){
        ArrayList<Item[]> matchedLooks = new ArrayList<>();
        for(Item[] look: looks){
            double termIndex=look[0].getTermid();

            if(temp > 21){
                if(termIndex == 1){
                    matchedLooks.add(look);
                }
            }else if(temp > 6){
                if(termIndex == 2){
                    matchedLooks.add(look);
                }
            }else{
                if(termIndex == 3) {
                    matchedLooks.add(look);
                }
            }

        }
        return matchedLooks;
    }

    private void setAccurancy(SharedPreferences prefs) {
        String accuracy = prefs.getString("accuracy","0.5");
        if(!accuracy.equals("0.5")) {
            Rules.accuracy = Double.parseDouble(accuracy);
        }
    }

    public void useLook(Look look, Context context){
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        ContentValues cv = new ContentValues();
        ArrayList<Integer> itemsId = look.getItemsArray();
        for(int i = 0; i < itemsId.size(); i++){
            Item item = new Item().getItemById(itemsId.get(i),db);
            cv.put(DBFields.USED.toFieldName(), item.getUsed() + 1);
            db.update(DatabaseHelper.TABLE, cv, DBFields.ID.toFieldName() + "=" + item.getId(), null);
            cv.clear();
        }
        databaseHelper.close();
        db.close();
    }

}
