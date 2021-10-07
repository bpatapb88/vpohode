package com.simon.vpohode;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.simon.vpohode.database.DatabaseHelper;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    private ArrayList<Look> lookDataArrayList;
    private Context mcontext;

    public RecyclerViewAdapter(ArrayList<Look> recyclerDataArrayList, Context mcontext) {
        this.lookDataArrayList = recyclerDataArrayList;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.look_form, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.RecyclerViewHolder holder, int position) {
// Set the data to textview from our modal class.
        Look look = lookDataArrayList.get(position);
        holder.nameOfLook.setText(look.getName());
        String tempRangeString = prepareTemp(look.getMin()) +
                ".." +
                prepareTemp(look.getMax());
        holder.tempRange.setText(tempRangeString);
        holder.lookId.setText(look.getId() + "");
        fillColorsAndTitle(look.getItems(),holder);
    }

    private void fillColorsAndTitle(String items, RecyclerViewHolder holder) {
        DatabaseHelper databaseHelper = new DatabaseHelper(mcontext);
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

    @Override
    public int getItemCount() {
        return lookDataArrayList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        // creating a variable for our text view.
        private TextView nameOfLook;
        private TextView tempRange;
        private TextView description;
        private TextView lookId;
        private CardView[] cardViews;
        private ImageView[] imageViews;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            nameOfLook = itemView.findViewById(R.id.name);
            tempRange = itemView.findViewById(R.id.temp);
            description = itemView.findViewById(R.id.description);
            lookId = itemView.findViewById(R.id.look_id);
            cardViews = new CardView[]{itemView.findViewById(R.id.colorCard1),
                    itemView.findViewById(R.id.colorCard2),
                    itemView.findViewById(R.id.colorCard3),
                    itemView.findViewById(R.id.colorCard4),
                    itemView.findViewById(R.id.colorCard5),
                    itemView.findViewById(R.id.colorCard6)};
            imageViews = new ImageView[]{itemView.findViewById(R.id.colorView1),
                    itemView.findViewById(R.id.colorView2),
                    itemView.findViewById(R.id.colorView3),
                    itemView.findViewById(R.id.colorView4),
                    itemView.findViewById(R.id.colorView5),
                    itemView.findViewById(R.id.colorView6)};
        }
    }

    public static String prepareTemp(Double temp){
        Integer tempInt = temp.intValue();
        return temp>=0 ? "+" + tempInt: tempInt.toString();
    }
}
