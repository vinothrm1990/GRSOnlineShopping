package com.app.grsonlineshopping.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.app.grsonlineshopping.R;
import com.app.grsonlineshopping.helper.Constants;
import com.app.grsonlineshopping.helper.ImageCache;

import java.util.ArrayList;
import java.util.HashMap;

import thebat.lib.validutil.ValidUtils;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<HashMap<String,String>> orderList;
    ImageLoader imageLoader;
    ValidUtils validUtils;

    public OrderAdapter(Context context, ArrayList<HashMap<String, String>> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.order_adapter, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        final HashMap<String,String> map = orderList.get(i);

        myViewHolder.tvPName.setText(map.get("brand")+ "\t" +map.get("product"));
        myViewHolder.tvPPrice.setText( "₹"+map.get("price"));
        myViewHolder.tvPCPrice.setText( "₹"+map.get("crossprice"));
        myViewHolder.tvPCPrice.setPaintFlags(myViewHolder.tvPCPrice.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);
        myViewHolder.tvTotalPrice.setText("₹"+map.get("total"));
        myViewHolder.tvQuantity.setText(map.get("qty"));

        imageLoader = ImageCache.getInstance(context).getImageLoader();
        imageLoader.get(Constants.IMAGE_URL + map.get("proimage"), ImageLoader.getImageListener(myViewHolder.ivImage, R.drawable.ic_image, android.R.drawable.ic_dialog_alert));
        myViewHolder.ivImage.setImageUrl(Constants.IMAGE_URL + map.get("proimage"), imageLoader);
        myViewHolder.ivImage.setScaleType(NetworkImageView.ScaleType.FIT_CENTER);


    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvPName, tvPPrice, tvPCPrice, tvTotalPrice, tvQuantity;
        NetworkImageView ivImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPName = itemView.findViewById(R.id.order_pro_name);
            tvPPrice = itemView.findViewById(R.id.order_price);
            tvPCPrice = itemView.findViewById(R.id.order_cross_price);
            tvTotalPrice = itemView.findViewById(R.id.order_total_price);
            tvQuantity = itemView.findViewById(R.id.order_qty);
            ivImage = itemView.findViewById(R.id.order_image);

        }
    }
}
