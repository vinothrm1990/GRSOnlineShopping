package com.app.grsonlineshopping.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.app.grsonlineshopping.R;
import com.app.grsonlineshopping.activity.BrandActivity;
import com.app.grsonlineshopping.data.CategoryMenu;
import com.app.grsonlineshopping.helper.CircularNetworkImageView;
import com.app.grsonlineshopping.helper.ImageCache;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import spencerstudios.com.bungeelib.Bungee;

public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<HashMap<String,String>> discoverList;
    List<CategoryMenu> catList;

    public DiscoverAdapter(Context context, ArrayList<HashMap<String, String>> discoverList, List<CategoryMenu> catList) {
        this.context = context;
        this.discoverList = discoverList;
        this.catList = catList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.discover_adapter, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        final HashMap<String,String> map = discoverList.get(i);

        CategoryMenu menu = catList.get(i);
        myViewHolder.title.setText(map.get("cat"));
        Glide.with(context).load(menu.getImage()).into(myViewHolder.image);

        myViewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, BrandActivity.class);
                intent.putExtra("product", map);
                context.startActivity(intent);
                Bungee.fade(context);
            }
        });
    }

    @Override
    public int getItemCount() {
        return discoverList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.home_image);
            title = itemView.findViewById(R.id.home_title);
        }
    }
}
