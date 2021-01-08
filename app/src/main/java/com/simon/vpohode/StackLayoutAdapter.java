package com.simon.vpohode;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.littlemango.stacklayoutmanager.StackLayoutManager;
import com.simon.vpohode.database.DatabaseHelper;

import java.util.ArrayList;


public class StackLayoutAdapter extends RecyclerView.Adapter<StackLayoutAdapter.StackHolder> {
    private Toast mToast;
    private StackLayoutManager mStackLayoutManager;
    private RecyclerView mRecyclerView;
    private ArrayList<int[]> looks;
    private SQLiteDatabase db;
    public StackLayoutAdapter(Toast mToast, StackLayoutManager mStackLayoutManager, RecyclerView mRecyclerView, ArrayList<int[]> looks, SQLiteDatabase db) {
        this.mToast = mToast;
        this.mStackLayoutManager = mStackLayoutManager;
        this.mRecyclerView = mRecyclerView;
        this.looks = looks;
        this.db = db;
    }

    @NonNull
    @Override
    public StackHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_card, viewGroup, false);
        return new StackHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final StackHolder stackHolder, int position) {
        ItemFC images = getImageResource(looks.get(position),db);
        ImageView[] imageViews = {stackHolder.imageView, stackHolder.imageView2, stackHolder.imageView3, stackHolder.imageView4};
        for(int j = 0 ; j < imageViews.length; j++){
            if(images.colors.length > j){
                imageViews[j].setImageURI(Uri.parse(images.fotos[j]));
                imageViews[j].setBackgroundColor(images.colors[j]);
            }

        }
        //stackHolder.textView.setText("" + fillText(position,looks.get(position),db));

        stackHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stackHolder.getAdapterPosition() == mStackLayoutManager.getFirstVisibleItemPosition()) {
                    mToast.setText("position: " + stackHolder.getAdapterPosition() + " is click!");
                    mToast.show();
                } else {
                    mRecyclerView.smoothScrollToPosition(stackHolder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return looks.size();
    }

    class StackHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView imageView, imageView4, imageView2, imageView3;
        TextView textView;

        StackHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageView = itemView.findViewById(R.id.imageView);
            imageView2 = itemView.findViewById(R.id.imageView2);
            imageView3 = itemView.findViewById(R.id.imageView3);
            imageView4 = itemView.findViewById(R.id.imageView4);
            textView = itemView.findViewById(R.id.textView);
        }
    }

    public static ItemFC getImageResource (int[] look, SQLiteDatabase db){
        String[] imagesPath = new String[look.length];
        Integer[] colors = new Integer[look.length];
            for (int i = 0; i < look.length; i++) {
                    Cursor Item = DatabaseHelper.getItemByID(db, look[i]);
                    Item.moveToFirst();
                    Log.i("Test420"," Test420 before set" + Item.getString(Item.getColumnIndex("foto")));
                    imagesPath[i] = Item.getString(Item.getColumnIndex("foto"));
                    colors[i] = Item.getInt(Item.getColumnIndex("color"));
            }
        ItemFC result = new ItemFC(imagesPath,colors);
        return result;
    }


}
