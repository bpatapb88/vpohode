package com.simon.vpohode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        Look recyclerData = lookDataArrayList.get(position);
        holder.nameOfLook.setText(recyclerData.getName());
        String tempRangeString = prepareTemp(recyclerData.getMin()) +
                ".." +
                prepareTemp(recyclerData.getMax());
        holder.tempRange.setText(tempRangeString);
        holder.description.setText(recyclerData.getItems());
        holder.lookId.setText(recyclerData.getId() + "");
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

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            nameOfLook = itemView.findViewById(R.id.name);
            tempRange = itemView.findViewById(R.id.temp);
            description = itemView.findViewById(R.id.description);
            lookId = itemView.findViewById(R.id.look_id);
        }
    }

    public static String prepareTemp(Double temp){
        Integer tempInt = temp.intValue();
        return temp>=0 ? "+" + tempInt: tempInt.toString();
    }
}
