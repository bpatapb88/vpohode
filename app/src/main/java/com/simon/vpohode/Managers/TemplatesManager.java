package com.simon.vpohode.Managers;

import android.content.Context;
import android.content.res.Resources;
import android.widget.ArrayAdapter;

import com.simon.vpohode.Item;
import com.simon.vpohode.R;
import com.simon.vpohode.Styles;

public class TemplatesManager {

    public static ArrayAdapter<String> spinnerConfig(String[] array, Context context){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    public static Item getItemFromTemplate(String templateName, Resources resources){
        String[] arrayTemplate = resources.getStringArray(R.array.templates);
        int templateID = 0;
        for(int i = 0 ; i < arrayTemplate.length;i++){
            if(arrayTemplate[i].equals(templateName)){
                templateID = i;
            }
        }

        switch (templateID){
            case 1:
                return new Item(0,arrayTemplate[templateID], Styles.CASUAL.toInt(),0,1d,1,0,"",0,"",false,"");
            case 2:
                return new Item(0,arrayTemplate[templateID],Styles.BUSINESS.toInt(),0,2d,1,0,"",0,"",false,"");
            case 3:
                return new Item(0,arrayTemplate[templateID],Styles.CASUAL.toInt(),0,2d,2,0,"",0,"",false,"");
            case 4:
                return new Item(0,arrayTemplate[templateID],Styles.CASUAL.toInt(),1,3d,2,0,"",0,"",false,"");
            case 5:
                return new Item(0,arrayTemplate[templateID],Styles.CASUAL.toInt(),0,3d,3,0,"",0,"",false,"");
            case 6:
                return new Item(0,arrayTemplate[templateID],Styles.ELEGANT.toInt(),0,2d,3,0,"",0,"",false,"");
            case 7:
                return new Item(0,arrayTemplate[templateID],Styles.HOME.toInt(),1,1d,1,0,"",0,"",false,"");
            case 8:
                return new Item(0,arrayTemplate[templateID],Styles.SPORT.toInt(),1,2d,3,0,"",0,"",false,"");
            case 9:
                return new Item(0,arrayTemplate[templateID],Styles.CASUAL.toInt(),1,2d,2,-12961222,"https://thumb.tildacdn.com/tild6137-3566-4031-a636-663934313261/-/format/webp/2330_grey_5.jpg",0,"",false,"BLCV");
            // Шаблоны можно добавить тут + добавить имя в spinnerTemplate - --- - - - - --
        }
        return null;
    }
}
