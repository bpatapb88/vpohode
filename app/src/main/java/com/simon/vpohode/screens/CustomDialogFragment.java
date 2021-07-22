package com.simon.vpohode.screens;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        assert getArguments() != null;
        String name = getArguments().getString("name");
        String brand = getArguments().getString("brand");
        int style = getArguments().getInt("style");
        int color = getArguments().getInt("color");
        int termIndex = getArguments().getInt("termIndex");
        boolean isTop = getArguments().getBoolean("isTop");
        int layer = getArguments().getInt("layer");
        List<String> parameterNames = new ArrayList<>();

        if(!brand.equals("")){
            parameterNames.add(getResources().getString(R.string.brand));
        }
        if(style > 0){
            parameterNames.add(getResources().getString(R.string.style));
        }
        if(color != 0){
            parameterNames.add(getResources().getString(R.string.color));
        }
        parameterNames.add(getResources().getString(R.string.warmth));
        parameterNames.add(getResources().getString(R.string.typ_item));
        if(layer > 0){
            parameterNames.add(getResources().getString(R.string.layer));
        }

        String[] choices = parameterNames.toArray(new String[0]);
        boolean[] checked = new boolean[parameterNames.size()];
        Arrays.fill(checked, true);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.template_choose)+" \"" + name + "\"");
        builder.setMultiChoiceItems(choices, checked, (dialog, which, isChecked) -> {
            if(isChecked){
                parameterNames.add(choices[which]);
            }else parameterNames.remove(choices[which]);
        });

        builder.setPositiveButton(getResources().getString(R.string.save), (dialog, which) -> {
            if(parameterNames.size() == 0){
                return;
            }
            ContentValues cv = new ContentValues();
            cv.put(DBFields.NAME.toFieldName(), name);

            for(String paramName: parameterNames){
                if(paramName.equals(getResources().getString(R.string.style))){
                    cv.put(DBFields.STYLE.toFieldName(), Styles.values()[style].toInt());
                }else if(paramName.equals(getResources().getString(R.string.brand))){
                    cv.put(DBFields.BRAND.toFieldName(), brand);
                }else if(paramName.equals(getResources().getString(R.string.color))){
                    cv.put(DBFields.COLOR.toFieldName(), color);
                }else if(paramName.equals(getResources().getString(R.string.warmth))){
                    cv.put(DBFields.TERMID.toFieldName(), Double.valueOf(termIndex));
                }else if(paramName.equals(getResources().getString(R.string.typ_item))){
                    cv.put(DBFields.ISTOP.toFieldName(), isTop?0:1);
                }else if(paramName.equals(getResources().getString(R.string.layer))){
                    cv.put(DBFields.LAYER.toFieldName(), layer);
                }
            }
            DBHelperTemplate dbHelperTemplate = new DBHelperTemplate(getContext());
            SQLiteDatabase dbTemplate = dbHelperTemplate.getReadableDatabase();
            dbTemplate.insert(DBHelperTemplate.TABLE, null, cv);
            dbTemplate.close();
            dbHelperTemplate.close();

            Intent intent = new Intent(getContext(), ConfigItem.class);
            startActivity(intent);
        });

        builder.setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> {

        });

        return builder.create();
    }

}
