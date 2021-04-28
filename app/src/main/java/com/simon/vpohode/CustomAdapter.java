package com.simon.vpohode;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.simon.vpohode.screens.ConfigItem;

import java.util.List;


public class CustomAdapter extends CursorAdapter {
    private LayoutInflater mLayoutInflater;
    private List<ListViewItemDTO> listViewItemDtoList = null;
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
        final Long id = c.getLong(c.getColumnIndexOrThrow("_id"));
        CardView cardItem = (CardView) v.findViewById(R.id.cardItem);
        String name = c.getString(c.getColumnIndexOrThrow("name"));
        Integer style = c.getInt(c.getColumnIndexOrThrow("style"));
        Integer istop = c.getInt(c.getColumnIndexOrThrow("istop"));
        Integer layer = c.getInt(c.getColumnIndexOrThrow("layer"));
        Integer color = c.getInt(c.getColumnIndexOrThrow("color"));
        boolean inWash = c.getInt(c.getColumnIndexOrThrow("inwash")) > 0;
        TextView name_text = (TextView) v.findViewById(R.id.text1);
        if (name_text != null) {
            name_text.setText(name);
        }
        ImageView color_field = (ImageView) v.findViewById(R.id.colorView);
        if (color_field != null) {
            color_field.setBackgroundColor(color);
        }

        if(inWash){
            CardView colorCard = v.findViewById(R.id.color_card);
            colorCard.setCardElevation(0);
            final CheckBox checkBox = v.findViewById(R.id.checkbox);
            checkBox.setVisibility(View.VISIBLE);
            color_field.setVisibility(View.GONE);

        }else{
            cardItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), ConfigItem.class);
                    System.out.println("Test id - " + id);
                    intent.putExtra("id", id);
                    context.startActivity(intent);
                }
            });
        }



        TextView style_text = (TextView) v.findViewById(R.id.text3);
        if (style_text != null) {
            style_text.setText(context.getResources().getString(style));
        }

        ImageView item_image = (ImageView) v.findViewById(R.id.imageView);
        setImageIcon(item_image,istop,layer);
    }

    @Override
    public View getView(int itemIndex, View convertView, ViewGroup viewGroup) {
        ListViewItemViewHolder view = null;
        if(convertView!=null){
            view = (ListViewItemViewHolder) convertView.getTag();
        }else{
            convertView = View.inflate(viewGroup.getContext(),R.layout.activity_list_view_with_checkbox_item,null);
            CheckBox listItemCheckbox = (CheckBox) convertView.findViewById(R.id.list_view_item_checkbox);

            TextView listItemText = (TextView) convertView.findViewById(R.id.list_view_item_text);

            view = new ListViewItemViewHolder(convertView);

            view.setItemCheckbox(listItemCheckbox);

            view.setItemTextView(listItemText);

            convertView.setTag(view);
        }
        ListViewItemDTO listViewItemDto = listViewItemDtoList.get(itemIndex);
        view.getItemCheckbox().setChecked(listViewItemDto.isChecked());
        view.getItemTextView().setText(listViewItemDto.getItemText());

        return convertView;
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
        if(!drawable.equals(null)){
            drawable.setTint(imageView.getContext().getColor(R.color.colorPrimaryDark));
            imageView.setImageDrawable(drawable);
        }
    }
}
