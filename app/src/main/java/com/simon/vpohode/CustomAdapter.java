package com.simon.vpohode;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class CustomAdapter extends CursorAdapter {
    private LayoutInflater mLayoutInflater;
    public CustomAdapter(Context context, Cursor c) {
        super(context, c);
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = mLayoutInflater.inflate(R.layout.two_line_list_item, parent, false);
        return v;
    }

    /**
     * @param   v
     *          The view in which the elements we set up here will be displayed.
     *
     * @param   context
     *          The running context where this ListView adapter will be active.
     *
     * @param   c
     *          The Cursor containing the query results we will display.
     */

    @Override
    public void bindView(View v, Context context, Cursor c) {
        String name = c.getString(c.getColumnIndexOrThrow("name"));
        String style = c.getString(c.getColumnIndexOrThrow("style"));
        Integer istop = c.getInt(c.getColumnIndexOrThrow("istop"));
        Integer layer = c.getInt(c.getColumnIndexOrThrow("layer"));

        TextView name_text = (TextView) v.findViewById(R.id.text1);
        if (name_text != null) {
            name_text.setText(name);
        }

        TextView style_text = (TextView) v.findViewById(R.id.text3);
        if (style_text != null) {
            style_text.setText(style);
        }

        ImageView item_image = (ImageView) v.findViewById(R.id.imageView);
        if(istop == 0){
            if(layer == 1){
                item_image.setImageResource(R.drawable.ic_layer1_bot);
            }else if(layer == 2){
                item_image.setImageResource(R.drawable.ic_layer2_bot);
            }else{
                item_image.setImageResource(R.drawable.ic_layer_boots);
            }
        }else{
            if(layer == 1){
                item_image.setImageResource(R.drawable.ic_layer1);
            }else if(layer == 2){
                item_image.setImageResource(R.drawable.ic_layer2);
            }else{
                item_image.setImageResource(R.drawable.ic_layer3);
            }
        }

    }
}
