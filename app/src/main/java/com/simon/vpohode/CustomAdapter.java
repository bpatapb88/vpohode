package com.simon.vpohode;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;

import com.simon.vpohode.Managers.LookManager;
import com.simon.vpohode.screens.ConfigItem;

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
    public void bindView(final View v, final Context context, final Cursor c) {

        Item item = LookManager.cursorToItem(c);
        final Long id = Long.valueOf(item.getId());

        CardView cardItem = (CardView) v.findViewById(R.id.cardItem);
        cardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ConfigItem.class);
                intent.putExtra("id", id);
                context.startActivity(intent);
            }
        });

        TextView name_text = (TextView) v.findViewById(R.id.text1);
        if (name_text != null) {
            name_text.setText(item.getName());
        }
        ImageView color_field = (ImageView) v.findViewById(R.id.colorView);
        if (color_field != null) {
            color_field.setBackgroundColor(item.getColor());
        }

        TextView style_text = (TextView) v.findViewById(R.id.text3);
        if (style_text != null) {
            style_text.setText(item.getBrand());
        }

        ImageView item_image = (ImageView) v.findViewById(R.id.imageView);
        setImageIcon(item_image,item.getTop(),item.getLayer());
    }

    private void setImageIcon(ImageView imageView, Integer isTop, Integer layer){
        Drawable drawable;
        Context context = imageView.getContext();
        if(isTop == 0 && layer == 1){
            drawable = context.getDrawable(R.drawable.ic_layer1_bot);
        }else if(isTop == 0 && layer == 2){
            drawable = context.getDrawable(R.drawable.ic_layer2_bot);
        }else if(isTop == 0 && layer == 3){
            drawable = context.getDrawable(R.drawable.ic_layer_boots);
        }else if(isTop == 1 && layer == 1){
            drawable = context.getDrawable(R.drawable.ic_layer1);
        }else if(isTop == 1 && layer == 2){
            drawable = context.getDrawable(R.drawable.ic_layer2);
        }else if(isTop == 1 && layer == 3){
            drawable = context.getDrawable(R.drawable.ic_layer3);
        }else{
            System.out.println("Error - Layer or isTop is not correct!");
            return;
        }

        drawable.setTint(imageView.getContext().getColor(R.color.colorPrimaryDark));
        imageView.setImageDrawable(drawable);

    }
}
