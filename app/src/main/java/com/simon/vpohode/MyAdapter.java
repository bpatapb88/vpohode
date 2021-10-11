package com.simon.vpohode;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.simon.vpohode.database.DatabaseHelper;
import com.simon.vpohode.screens.SelectLookActivity;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyHolder> implements Filterable {

    private ArrayList<Look> lookDataArrayList;
    private ArrayList<Look> lookDataArrayListFiltered;
    private Context c;
    private CustomFilter filter;

    public MyAdapter(Context ctx,ArrayList<Look> recyclerDataArrayList)
    {
        this.c=ctx;
        this.lookDataArrayList=recyclerDataArrayList;
        this.lookDataArrayListFiltered=lookDataArrayList;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.look_form, parent, false);
        return new MyHolder(v);
    }
    @Override
    public void onBindViewHolder(MyHolder holder, final int position) {
        Look look = lookDataArrayList.get(position);
        holder.nameOfLook.setText(look.getName());
        String tempString = prepareTemp(look.getMin()) + ".." + prepareTemp(look.getMax());
        holder.tempRange.setText(tempString);
        holder.lookId.setText(String.valueOf(look.getId()));
        fillColorsAndTitle(look.getItems(),holder);

        holder.setItemClickListener((v, pos) -> {
            Intent intent = new Intent(v.getContext(), SelectLookActivity.class);
            intent.putExtra("look_id", lookDataArrayList.get(pos).getId());
            v.getContext().startActivity(intent);
        });
    }

    public static String prepareTemp(double v) {
        return v<0? "" + (int)v: "+" + (int)v;
    }

    @Override
    public int getItemCount() {
        return lookDataArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null)
        {
            filter=new CustomFilter();
        }
        return filter;
    }

    private void fillColorsAndTitle(String items, MyHolder holder) {
        DatabaseHelper databaseHelper = new DatabaseHelper(c);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor itemsCursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE + " WHERE _id IN (" + items + ")", null);
        StringBuilder title = new StringBuilder();
        String prefix = "";
        int counter = 0;
        if(itemsCursor.moveToFirst()){
            do{
                Item item = new Item().cursorToItem(itemsCursor);
                holder.cardViews[counter].setAlpha(1);
                holder.cardViews[counter].setCardElevation(5);
                holder.imageViews[counter].setBackgroundColor(item.getColor());
                title.append(prefix).append(item.getName());
                prefix = ", ";
                counter++;
            }while (itemsCursor.moveToNext());
        }
        holder.description.setText(title.toString());
        itemsCursor.close();
        db.close();
        databaseHelper.close();
    }



    class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if(constraint != null && constraint.length()>0)
            {
                constraint=constraint.toString().toUpperCase();
                ArrayList<Look> filters=new ArrayList<>();

                for(int i=0;i<lookDataArrayListFiltered.size();i++)
                {
                    if(lookDataArrayListFiltered.get(i).getName().toUpperCase().contains(constraint))
                    {
                        filters.add(lookDataArrayListFiltered.get(i));
                    }
                }

                results.count=filters.size();
                results.values=filters;
            }else
            {
                results.count=lookDataArrayListFiltered.size();
                results.values=lookDataArrayListFiltered;
            }
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            lookDataArrayList= (ArrayList<Look>) results.values;
            notifyDataSetChanged();
        }
    }

}
