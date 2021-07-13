package com.simon.vpohode.screens;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.simon.vpohode.R;
import com.simon.vpohode.Styles;
import com.simon.vpohode.database.DBFields;
import com.simon.vpohode.database.DBHelperTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CustomDialogFragment extends DialogFragment {

    private List<String> parameters;
    private String name = "";
    private String brand = "";
    private int style = 0;
    private int color = 0;
    private int termIndex = 0;
    private boolean isTop;
    private int layer;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        parameters=new ArrayList<>();
        name = getArguments().getString("name");
        brand = getArguments().getString("brand");
        style = getArguments().getInt("style");
        color = getArguments().getInt("color");
        termIndex = getArguments().getInt("termIndex");
        isTop = getArguments().getBoolean("isTop");
        layer = getArguments().getInt("layer");
        List<String> parameterNames = new ArrayList<>();

        if(!brand.equals("")){
            parameterNames.add("Brand");
        }
        if(style > 0){
            parameterNames.add("Style");
        }
        if(color != 0){
            parameterNames.add("Color");
        }
        parameterNames.add("TermIndex");
        parameterNames.add("isTop");
        if(layer > 0){
            parameterNames.add("Layer");
        }

        String[] choices = parameterNames.toArray(new String[parameterNames.size()]);
        boolean[] checked = new boolean[parameterNames.size()];
        Arrays.fill(checked, true);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose parameters to create template \"" + name + "\"");
        builder.setMultiChoiceItems(choices, checked, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if(isChecked){
                    parameterNames.add(choices[which]);
                }else if(parameterNames.contains(choices[which])){
                    parameterNames.remove(choices[which]);
                }
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(parameterNames.size() == 0){
                    return;
                }
                ContentValues cv = new ContentValues();
                cv.put(DBFields.NAME.toFieldName(), name);
                for(String paramName: parameterNames){
                    switch (paramName){
                        case "Style":
                            cv.put(DBFields.STYLE.toFieldName(), Styles.values()[style].toInt());
                            break;
                        case "Brand":
                            cv.put(DBFields.BRAND.toFieldName(), brand);
                            break;
                        case "Color":
                            cv.put(DBFields.COLOR.toFieldName(), color);
                            break;
                        case "TermIndex":
                            cv.put(DBFields.TERMID.toFieldName(), Double.valueOf(termIndex));
                            break;
                        case "isTop":
                            cv.put(DBFields.ISTOP.toFieldName(), isTop?1:0);
                            break;
                        case "Layer":
                            cv.put(DBFields.LAYER.toFieldName(), layer);
                            break;
                    }
                }
                DBHelperTemplate dbHelperTemplate = new DBHelperTemplate(getContext());
                SQLiteDatabase dbTemplate = dbHelperTemplate.getReadableDatabase();
                dbTemplate.insert(DBHelperTemplate.TABLE, null, cv);
                dbTemplate.close();
                dbHelperTemplate.close();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }

    public List<String> getParameters(){
        return parameters;
    }


}
