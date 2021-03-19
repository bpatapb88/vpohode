package com.simon.vpohode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LooksAdapter extends ArrayAdapter<Item> {

    private Context mContext;
    private List<Item> itemList = new ArrayList<>();

    public LooksAdapter(@NonNull Context context, @SuppressLint("SupportAnnotationUsage") @LayoutRes ArrayList<Item> list){
        super(context, 0 , list);
        mContext = context;
        itemList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false);

        Item currentMovie = itemList.get(position);

        ImageView image = listItem.findViewById(R.id.imageViewPhoto);
        image.setImageURI(Uri.parse(currentMovie.getFoto()));
        image.setBackgroundColor(currentMovie.getColor());

        ImageView image2 = listItem.findViewById(R.id.imageViewColor);
        image2.setBackgroundColor(currentMovie.getColor());

        TextView name = listItem.findViewById(R.id.nameItem);
        name.setText(currentMovie.getName());

        TextView style = listItem.findViewById(R.id.styleItem);
        style.setText(currentMovie.getStyle());

        TextView temp = listItem.findViewById(R.id.tempItem);
        temp.setText(getContext().getResources().getString(R.string.warm) + " " + currentMovie.getTermid());

        return listItem;
    }


}
