package com.simon.vpohode;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.littlemango.stacklayoutmanager.StackLayoutManager;

import java.util.ArrayList;


public class StackLayoutAdapter extends RecyclerView.Adapter<StackLayoutAdapter.StackHolder> {
    private Toast mToast;
    private StackLayoutManager mStackLayoutManager;
    private RecyclerView mRecyclerView;
    private ArrayList<String> text;
    public StackLayoutAdapter(Toast mToast, StackLayoutManager mStackLayoutManager, RecyclerView mRecyclerView, ArrayList<String> text) {
        this.mToast = mToast;
        this.mStackLayoutManager = mStackLayoutManager;
        this.mRecyclerView = mRecyclerView;
        this.text = text;
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

        stackHolder.textView.setText("" + text.get(position));

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
        return text.size();
    }

    class StackHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView imageView;
        TextView textView;

        StackHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
        }
    }
}
