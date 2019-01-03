package com.app.grsonlineshopping.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.grsonlineshopping.R;
import com.app.grsonlineshopping.activity.CartActivity;
import com.app.grsonlineshopping.activity.DetailActivity;
import com.app.grsonlineshopping.activity.WishDetailActivity;
import com.app.grsonlineshopping.helper.Constants;
import com.app.grsonlineshopping.helper.ImageCache;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import spencerstudios.com.bungeelib.Bungee;
import thebat.lib.validutil.ValidUtils;
import static android.content.Context.MODE_PRIVATE;
import static com.app.grsonlineshopping.navigation.WishlistActivity.progress;
import static com.app.grsonlineshopping.navigation.WishlistActivity.wishList;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<HashMap<String,String>> wishList;
    ImageLoader imageLoader;
    HashMap<String,String> map, wmap;
    ValidUtils validUtils;
    String DETAIL_URL = Constants.BASE_URL + Constants.GET_SUBPRODUCT;
    String ADD_REMOVE_WISH_URL = Constants.BASE_URL + Constants.ADD_REMOVE_WISHLIST;
    String GET_FLAG_URL = Constants.BASE_URL + Constants.GET_CART_FLAG;
    String GET_WISH_FLAG_URL = Constants.BASE_URL + Constants.GET_WISH_FLAG;

    public WishlistAdapter(Context context, ArrayList<HashMap<String, String>> wishList) {
        this.context = context;
        this.wishList = wishList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.wishlist_adapter, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {

        validUtils = new ValidUtils();
        map = wishList.get(i);

        myViewHolder.tvPName.setText(map.get("brand")+ "\t" +map.get("product")+map.get("id"));
        myViewHolder.tvPPrice.setText( "₹"+map.get("price"));
        myViewHolder.tvPCPrice.setText( "₹"+map.get("cross_price"));
        myViewHolder.tvPCPrice.setPaintFlags(myViewHolder.tvPCPrice.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);

        imageLoader = ImageCache.getInstance(context).getImageLoader();
        imageLoader.get(Constants.IMAGE_URL + map.get("pro_image"), ImageLoader.getImageListener(myViewHolder.ivImage, R.drawable.ic_image, android.R.drawable.ic_dialog_alert));
        myViewHolder.ivImage.setImageUrl(Constants.IMAGE_URL + map.get("pro_image"), imageLoader);
        myViewHolder.ivImage.setScaleType(NetworkImageView.ScaleType.FIT_CENTER);

        final String proid = map.get("id");
        wmap = map;
        myViewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Constants.pref = context.getSharedPreferences("GRS",MODE_PRIVATE);
                String cusid = Constants.pref.getString("mobile", "");
                int flag = 0;
                int pos = myViewHolder.getAdapterPosition();
                //validUtils.showToast(context, proid);
                removeWish(pos, cusid, proid, flag);

            }
        });

       myViewHolder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Constants.pref = context.getSharedPreferences("GRS",MODE_PRIVATE);
                String cusid = Constants.pref.getString("mobile", "");
                //validUtils.showToast(context, proid);
                getFlag(cusid, proid);
            }
        });
    }

    private void getFlag(final String cusid, final String proid) {

        StringRequest request = new StringRequest(Request.Method.POST, GET_FLAG_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject != null){

                                if (jsonObject.getString("status")
                                        .equalsIgnoreCase("success")){
                                    String flag = jsonObject.getString("cart_flag");
                                    getWishFlag(cusid, proid, flag);
                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("empty")){
                                    getWishFlag(cusid, proid, "0");
                                }
                            }else {
                                validUtils.showToast(context, "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            validUtils.showToast(context, e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        validUtils.showToast(context, error.getMessage());
                    }
                })
        {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("customer_id", cusid);
                params.put("product_id", proid);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);

    }

    private void getWishFlag(final String cusid, final String proid, final String flag) {

        StringRequest request = new StringRequest(Request.Method.POST, GET_WISH_FLAG_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject != null){

                                if (jsonObject.getString("status")
                                        .equalsIgnoreCase("success")){
                                    String wflag = jsonObject.getString("wish_flag");
                                    viewDetail(proid, flag, wflag);
                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("empty")){
                                    viewDetail(proid, flag, "0");
                                }
                            }else {
                                validUtils.showToast(context, "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            validUtils.showToast(context, e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        validUtils.showToast(context, error.getMessage());
                    }
                })
        {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("customer_id", cusid);
                params.put("product_id", proid);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    private void viewDetail(final String proid, final String flag, final String wflag) {

        progress.showProgressBar();
        StringRequest request = new StringRequest(Request.Method.POST, DETAIL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject != null){

                                if (jsonObject.getString("status")
                                        .equalsIgnoreCase("success")){
                                    progress.hideProgressBar();

                                    String data = jsonObject.getString("data");
                                    JSONArray array = new JSONArray(data);
                                    JSONObject object = array.getJSONObject(0);

                                    HashMap<String, String> wmap = new HashMap<>();
                                    wmap.put("id", object.getString("id"));
                                    wmap.put("brand", object.getString("sub_product"));
                                    wmap.put("product", object.getString("product"));
                                    wmap.put("desc", object.getString("mobile_app"));
                                    wmap.put("slider_image", object.getString("image1"));
                                    wmap.put("size", object.getString("size"));
                                    wmap.put("color", object.getString("color"));
                                    wmap.put("price", object.getString("price"));
                                    wmap.put("cross_price", object.getString("cross_price"));
                                    wmap.put("branch_id", object.getString("b_id"));
                                    wmap.put("branch_name", object.getString("branchname"));
                                    wmap.put("branch_number", object.getString("b_mobile"));
                                    wmap.put("rate", object.getString("prate"));
                                    wmap.put("trate", object.getString("trate"));
                                    wmap.put("pro_image", object.getString("image"));

                                    Intent intent = new Intent(context, WishDetailActivity.class);
                                    intent.putExtra("flag", flag);
                                    intent.putExtra("wflag", wflag);
                                   intent.putExtra("wishproduct", wmap);
                                    context.startActivity(intent);
                                    Bungee.fade(context);
                                    //validUtils.showToast(DetailActivity.this, jsonObject.getString("message"));
                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("empty")){
                                    progress.hideProgressBar();
                                    validUtils.showToast(context, jsonObject.getString("message"));
                                }
                            }else {
                                progress.hideProgressBar();
                                validUtils.showToast(context, "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.hideProgressBar();
                            validUtils.showToast(context, e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.hideProgressBar();
                        validUtils.showToast(context, error.getMessage());
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
        queue.add(request);
    }

    private void removeWish(final int pos, final String cusid, final String proid, final int flag) {

        progress.showProgressBar();
        StringRequest request = new StringRequest(Request.Method.POST, ADD_REMOVE_WISH_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject != null){

                                if (jsonObject.getString("status")
                                        .equalsIgnoreCase("Deleted")){
                                    progress.hideProgressBar();
                                    wishList.remove(pos);
                                    notifyItemRemoved(pos);
                                    notifyItemRangeChanged(pos, wishList.size());
                                    wishList();
                                    validUtils.showToast(context, jsonObject.getString("message"));
                                }
                            }else {
                                progress.hideProgressBar();
                                validUtils.showToast(context, "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.hideProgressBar();
                            validUtils.showToast(context, e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.hideProgressBar();
                        validUtils.showToast(context, error.getMessage());
                    }
                })
        {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("customer_id", cusid);
                params.put("product_id", proid);
                params.put("flag", String.valueOf(flag));
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    @Override
    public int getItemCount() {
        return wishList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvPName, tvPPrice, tvPCPrice;
        NetworkImageView ivImage;
        Button btnDetail, btnRemove;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPName = itemView.findViewById(R.id.wish_pro_name);
            tvPPrice = itemView.findViewById(R.id.wish_price);
            tvPCPrice = itemView.findViewById(R.id.wish_cross_price);
            ivImage = itemView.findViewById(R.id.wish_image);
            btnDetail = itemView.findViewById(R.id.wish_btn_detail);
            btnRemove = itemView.findViewById(R.id.wish_btn_remove);
        }
    }
}
