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

import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;

import com.simon.vpohode.managers.LookManager;
import com.simon.vpohode.screens.ConfigItem;

public class CustomItemsAdapter extends CursorAdapter {
    private final LayoutInflater mLayoutInflater;
    public CustomItemsAdapter(Context context, Cursor c) {
        super(context, c);
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mLayoutInflater.inflate(R.layout.item_form, parent, false);
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

        Item item = new Item().cursorToItem(c);
        final Long id = (long) item.getId();

        CardView cardItem = v.findViewById(R.id.cardItem);
        cardItem.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ConfigItem.class);
            intent.putExtra("id", id);
            context.startActivity(intent);
        });

        TextView nameText = v.findViewById(R.id.text1);
        if (nameText != null) {
            nameText.setText(item.getName());
        }
        ImageView colorField = v.findViewById(R.id.colorView);
        if (colorField != null) {
            colorField.setBackgroundColor(item.getColor());
        }

        TextView styleText = v.findViewById(R.id.text3);
        if (styleText != null) {
            styleText.setText(item.getBrand());
        }

        ImageView itemImage = v.findViewById(R.id.imageView);
        setImageIcon(itemImage,item.getTop(),item.getLayer());
    }

    private void setImageIcon(ImageView imageView, Integer isTop, Integer layer){
        Drawable drawable;
        Context context = imageView.getContext();
        if(isTop == 0 && layer == 1){
            drawable = AppCompatResources.getDrawable(context,R.drawable.ic_layer1_bot);
        }else if(isTop == 0 && layer == 2){
            drawable = AppCompatResources.getDrawable(context,R.drawable.ic_layer2_bot);
        }else if(isTop == 0 && layer == 3){
            drawable = AppCompatResources.getDrawable(context,R.drawable.ic_layer_boots);
        }else if(isTop == 1 && layer == 1){
            drawable = AppCompatResources.getDrawable(context,R.drawable.ic_layer1);
        }else if(isTop == 1 && layer == 2){
            drawable = AppCompatResources.getDrawable(context,R.drawable.ic_layer2);
        }else if(isTop == 1 && layer == 3){
            drawable = AppCompatResources.getDrawable(context,R.drawable.ic_layer3);
        }else{
            return;
        }

        if (drawable != null) {
            drawable.setTint(imageView.getContext().getColor(R.color.colorPrimaryDark));
        }
        imageView.setImageDrawable(drawable);
    }
}
