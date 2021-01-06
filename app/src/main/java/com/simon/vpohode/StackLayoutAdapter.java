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
        int res;
        switch (position % 6) {
            case 0:
                res = R.drawable.image1;
                break;
            case 1:
                res = R.drawable.image2;
                break;
            case 2:
                res = R.drawable.image3;
                break;
            case 3:
                res = R.drawable.image4;
                break;
            case 4:
                res = R.drawable.image5;
                break;
            default:
                res = R.drawable.image6;
                break;
        }


        stackHolder.imageView.setImageResource(res);
        Log.i("Test image resource"," res = " + res);
        String [] images = getImageResource(looks.get(position),db);
        ImageView[] imageViews = {stackHolder.imageView, stackHolder.imageView2, stackHolder.imageView3, stackHolder.imageView4};
        for(int j = 0 ; j < imageViews.length; j++){
            if(images.length > j){
                Log.i("Test image resource"," path of image " + images[j]);
                imageViews[j].setImageURI(Uri.parse(images[j]));

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
    public static String fillText(int position, int[] look, SQLiteDatabase db){
        String result = "";
        for(int id : look) {
            Cursor Item = DatabaseHelper.getItemByID(db, id);
            if(Item.moveToFirst()){
                do{
                    result += "Name: " + Item.getString(Item.getColumnIndex("name")) + " id "+Item.getString(Item.getColumnIndex("_id")) + " \n";
                }while (Item.moveToNext());
            }
        }
        return result;
    }

    public static String[] getImageResource (int[] look, SQLiteDatabase db){
        String[] result = new String[look.length];
        for(int i = 0; i<look.length; i++) {
            Cursor Item = DatabaseHelper.getItemByID(db, look[i]);
            if (Item.moveToFirst()) {
                do {
                    result[i] = Item.getString(Item.getColumnIndex("foto"));
                } while (Item.moveToNext());
            }
        }
        return result;
    }
}
