package com.app.grsonlineshopping.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.app.grsonlineshopping.R;
import com.app.grsonlineshopping.activity.ProductActivity;
import com.app.grsonlineshopping.helper.Constants;
import com.app.grsonlineshopping.helper.ImageCache;
import java.util.ArrayList;
import java.util.HashMap;
import spencerstudios.com.bungeelib.Bungee;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<HashMap<String,String>> brandList;
    ImageLoader imageLoader;

    public BrandAdapter(Context context, ArrayList<HashMap<String, String>> brandList) {
        this.context = context;
        this.brandList = brandList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.brand_adapter, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        final HashMap<String,String> map = brandList.get(i);

        myViewHolder.title.setText(map.get("brand"));

        imageLoader = ImageCache.getInstance(context).getImageLoader();
        imageLoader.get(Constants.IMAGE_URL + map.get("pro_image"), ImageLoader.getImageListener(myViewHolder.image, R.drawable.ic_image, android.R.drawable.ic_dialog_alert));
        myViewHolder.image.setImageUrl(Constants.IMAGE_URL + map.get("pro_image"), imageLoader);
        myViewHolder.image.setScaleType(NetworkImageView.ScaleType.FIT_CENTER);

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
        return brandList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        NetworkImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.brand_image);
            title = itemView.findViewById(R.id.brand_title);
        }
    }
}
