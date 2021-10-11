package com.simon.vpohode;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView nameOfLook;
    TextView tempRange;
    TextView description;
    TextView lookId;
    CardView[] cardViews;
    ImageView[] imageViews;

    ItemClickListener itemClickListener;

    public MyHolder(View itemView) {
        super(itemView);
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
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        this.itemClickListener.onItemClick(v,getLayoutPosition());
    }

    public void setItemClickListener(ItemClickListener ic)
    {
        this.itemClickListener=ic;
    }

}
