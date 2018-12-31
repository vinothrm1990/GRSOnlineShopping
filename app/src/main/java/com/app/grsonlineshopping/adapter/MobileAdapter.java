package com.app.grsonlineshopping.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.app.grsonlineshopping.R;
import com.app.grsonlineshopping.activity.ProductActivity;
import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import java.util.ArrayList;
import java.util.HashMap;

import spencerstudios.com.bungeelib.Bungee;


public class MobileAdapter extends RecyclerView.Adapter<MobileAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<HashMap<String,String>> mobileList;

    public MobileAdapter(Context context, ArrayList<HashMap<String, String>> mobileList) {
        this.context = context;
        this.mobileList = mobileList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.mobile_adapter, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        final HashMap<String,String> map = mobileList.get(i);

        if (map.get("brand").equalsIgnoreCase("vivo")){
            Glide.with(context).load(R.drawable.vivo).into(myViewHolder.image);
        }else if (map.get("brand").equalsIgnoreCase("asus")){
            Glide.with(context).load(R.drawable.asus).into(myViewHolder.image);
        }else if (map.get("brand").equalsIgnoreCase("blackberry")){
            Glide.with(context).load(R.drawable.blackberry).into(myViewHolder.image);
        }else if (map.get("brand").equalsIgnoreCase("google")){
            Glide.with(context).load(R.drawable.google).into(myViewHolder.image);
        }else if (map.get("brand").equalsIgnoreCase("htc")){
            Glide.with(context).load(R.drawable.htc).into(myViewHolder.image);
        }else if (map.get("brand").equalsIgnoreCase("huawei")){
            Glide.with(context).load(R.drawable.huawei).into(myViewHolder.image);
        }else if (map.get("brand").equalsIgnoreCase("iphone")){
            Glide.with(context).load(R.drawable.iphone).into(myViewHolder.image);
        }else if (map.get("brand").equalsIgnoreCase("lenovo")){
            Glide.with(context).load(R.drawable.lenevo).into(myViewHolder.image);
        }else if (map.get("brand").equalsIgnoreCase("lg")){
            Glide.with(context).load(R.drawable.lg).into(myViewHolder.image);
        }else if (map.get("brand").equalsIgnoreCase("mi") || map.get("brand").equalsIgnoreCase("xiaomi")){
            Glide.with(context).load(R.drawable.mi).into(myViewHolder.image);
        }else if (map.get("brand").equalsIgnoreCase("motorola")){
            Glide.with(context).load(R.drawable.moto).into(myViewHolder.image);
        }else if (map.get("brand").equalsIgnoreCase("nokia")){
            Glide.with(context).load(R.drawable.nokia).into(myViewHolder.image);
        }else if (map.get("brand").equalsIgnoreCase("oneplus")){
            Glide.with(context).load(R.drawable.oneplus).into(myViewHolder.image);
        }else if (map.get("brand").equalsIgnoreCase("oppo")){
            Glide.with(context).load(R.drawable.oppo).into(myViewHolder.image);
        }else if (map.get("brand").equalsIgnoreCase("realme")){
            Glide.with(context).load(R.drawable.realme).into(myViewHolder.image);
        }else if (map.get("brand").equalsIgnoreCase("samsung")){
            Glide.with(context).load(R.drawable.samsung).into(myViewHolder.image);
        }else if (map.get("brand").equalsIgnoreCase("sony")){
            Glide.with(context).load(R.drawable.sony).into(myViewHolder.image);
        }else {
            Glide.with(context).load(R.drawable.ic_image).into(myViewHolder.image);
        }

        myViewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ProductActivity.class);
                intent.putExtra("product", map);
                context.startActivity(intent);
                Bungee.fade(context);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mobileList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircularImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.mobile_iv);
        }
    }
}
