package com.app.grsonlineshopping.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.grsonlineshopping.R;

import java.util.ArrayList;
import java.util.HashMap;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class RateAdapter extends RecyclerView.Adapter<RateAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<HashMap<String,String>> rateList;

    public RateAdapter(Context context, ArrayList<HashMap<String, String>> rateList) {
        this.context = context;
        this.rateList = rateList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.rate_adapter, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        HashMap<String,String> map = rateList.get(i);

        myViewHolder.tvReview.setText(map.get("review"));
        myViewHolder.tvName.setText(map.get("name"));
        myViewHolder.tvDate.setText(map.get("date"));
        myViewHolder.ratingBar.setRating(Float.parseFloat(map.get("rating")));
    }

    @Override
    public int getItemCount() {
        return rateList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvReview, tvName, tvDate;
        MaterialRatingBar ratingBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvReview = itemView.findViewById(R.id.rate_adapter_review);
            tvName = itemView.findViewById(R.id.rate_adapter_name);
            tvDate = itemView.findViewById(R.id.rate_adapter_timestamp);
            ratingBar = itemView.findViewById(R.id.rate_adapter_rating_bar);
        }
    }
}
