package com.app.grsonlineshopping.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.grsonlineshopping.R;
import com.app.grsonlineshopping.activity.DetailActivity;
import com.app.grsonlineshopping.activity.ProductActivity;
import com.app.grsonlineshopping.helper.Constants;
import com.app.grsonlineshopping.helper.ImageCache;
import com.app.grsonlineshopping.helper.RecyclerViewClickListener;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import spencerstudios.com.bungeelib.Bungee;
import thebat.lib.validutil.ValidUtils;

import static android.content.Context.MODE_PRIVATE;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<HashMap<String,String>> productList;
    ImageLoader imageLoader;
    ValidUtils validUtils;
    String GET_RATE_URL = Constants.BASE_URL + Constants.GET_RATING;

    public ProductAdapter(Context context, ArrayList<HashMap<String, String>> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.product_adapter, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {

        validUtils = new ValidUtils();
        final HashMap<String,String> map = productList.get(i);

        myViewHolder.title.setText(map.get("product"));
        myViewHolder.price.setText("₹"+map.get("price"));
        myViewHolder.cprice.setText("₹"+map.get("cross_price"));
        myViewHolder.cprice.setPaintFlags(myViewHolder.cprice.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);

        imageLoader = ImageCache.getInstance(context).getImageLoader();
        imageLoader.get(Constants.IMAGE_URL + map.get("pro_image"), ImageLoader.getImageListener(myViewHolder.image, R.drawable.ic_image, android.R.drawable.ic_dialog_alert));
        myViewHolder.image.setImageUrl(Constants.IMAGE_URL + map.get("pro_image"), imageLoader);
        myViewHolder.image.setScaleType(NetworkImageView.ScaleType.FIT_CENTER);

        String proid = map.get("proid");
        getRating(proid, myViewHolder.totalrate, myViewHolder.rate);

        myViewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getItemId(i);
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("subproduct", map);
                context.startActivity(intent);
            }
        });

      /*  myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("subproduct", map);
                context.startActivity(intent);


            }
        });*/

      /* myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("subproduct", map);
                context.startActivity(intent);

            }
        });*/
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    private void getRating(final String proid, final TextView totalrate, final TextView rate) {

        StringRequest request = new StringRequest(Request.Method.POST, GET_RATE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject != null){

                                if (jsonObject.getString("status")
                                        .equalsIgnoreCase("success")){
                                    String data = jsonObject.getString("data");
                                    JSONArray array = new JSONArray(data);

                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject object = array.getJSONObject(i);

                                        rate.setText(object.getString("prate"));
                                        totalrate.setText("("+object.getString("trate")+")");
                                    }
                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    // validUtils.showToast(DetailActivity.this, jsonObject.getString("message"));
                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("empty")){
                                    rate.setText("0");
                                    totalrate.setText("("+"0"+")");

                                    // validUtils.showToast(DetailActivity.this, jsonObject.getString("message"));
                                }
                            }else {
                                //validUtils.showToast(DetailActivity.this, "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            // validUtils.showToast(DetailActivity.this, e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       // validUtils.showToast(context, error.getMessage());
                    }
                })
        {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("product_id", proid);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView title, price, cprice,rate, totalrate;
        NetworkImageView image;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            image = itemView.findViewById(R.id.product_image);
            title = itemView.findViewById(R.id.product_title);
            price = itemView.findViewById(R.id.product_price);
            cprice = itemView.findViewById(R.id.product_cprice);
            rate = itemView.findViewById(R.id.product_rate);
            totalrate = itemView.findViewById(R.id.product_total_rate);
            cardView = itemView.findViewById(R.id.cv_product);

        }

        @Override
        public void onClick(View v) {

            //Toast.makeText(context, String.valueOf(getAdapterPosition() +"\t"+map.get("proid")), Toast.LENGTH_SHORT).show();
        }
    }
}
