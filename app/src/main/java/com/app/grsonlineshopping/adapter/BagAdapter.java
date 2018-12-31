package com.app.grsonlineshopping.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
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
import com.app.grsonlineshopping.helper.Constants;
import com.app.grsonlineshopping.helper.ImageCache;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import thebat.lib.validutil.ValidUtils;
import static android.content.Context.MODE_PRIVATE;
import static com.app.grsonlineshopping.navigation.BagActivity.bagList;
import static com.app.grsonlineshopping.navigation.BagActivity.progress;

public class BagAdapter extends RecyclerView.Adapter<BagAdapter.MyViewHolder> {


    private Context context;
    private ArrayList<HashMap<String,String>> bagList;
    ImageLoader imageLoader;
    ValidUtils validUtils;
    String GET_PRODUCT_URL = Constants.BASE_URL + Constants.GET_SUBPRODUCT;
    String ADD_REMOVE_URL = Constants.BASE_URL + Constants.ADD_REMOVE_CART;
    String ADD_REMOVE_BAG_URL = Constants.BASE_URL + Constants.ADD_REMOVE_BAG;


    public BagAdapter(Context context, ArrayList<HashMap<String, String>> bagList) {
        this.context = context;
        this.bagList = bagList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.bag_adapter, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {

        validUtils = new ValidUtils();
        final HashMap<String,String> map = bagList.get(i);

        myViewHolder.tvPName.setText(map.get("brand")+ "\t" +map.get("product"));
        myViewHolder.tvPPrice.setText( "₹"+map.get("price"));
        myViewHolder.tvPCPrice.setText( "₹"+map.get("cross_price"));
        myViewHolder.tvPCPrice.setPaintFlags(myViewHolder.tvPCPrice.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);

        imageLoader = ImageCache.getInstance(context).getImageLoader();
        imageLoader.get(Constants.IMAGE_URL + map.get("pro_image"), ImageLoader.getImageListener(myViewHolder.ivImage, R.drawable.ic_image, android.R.drawable.ic_dialog_alert));
        myViewHolder.ivImage.setImageUrl(Constants.IMAGE_URL + map.get("pro_image"), imageLoader);
        myViewHolder.ivImage.setScaleType(NetworkImageView.ScaleType.FIT_CENTER);

        myViewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Constants.pref = context.getSharedPreferences("GRS",MODE_PRIVATE);
                String cusid = Constants.pref.getString("mobile", "");
                String proid = map.get("id");

                int flag = 0;
                removeBag(i, cusid, proid, flag);

            }
        });

        myViewHolder.btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String proid = map.get("id");
                getProduct(proid, i);

            }
        });
    }

    private void removeBag(final int i, final String cusid, final String proid, final int flag) {

        progress.showProgressBar();
        StringRequest request = new StringRequest(Request.Method.POST, ADD_REMOVE_BAG_URL,
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
                                    bagList.remove(i);
                                    notifyItemRemoved(i);
                                    notifyDataSetChanged();
                                    bagList();
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

    private void getProduct(final String proid, final int i) {

        progress.showProgressBar();
        StringRequest request = new StringRequest(Request.Method.POST, GET_PRODUCT_URL,
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

                                    String proid = object.getString("id");
                                    String bid = object.getString("b_id");
                                    String bname = object.getString("branchname");
                                    String bmobile = object.getString("b_mobile");

                                    Constants.pref = context.getSharedPreferences("GRS",MODE_PRIVATE);
                                    String cusid = Constants.pref.getString("mobile", "");
                                    Date now = new Date();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    String timestamp = sdf.format(now);
                                    int flag = 1;
                                    addCart(i, proid, cusid, flag, timestamp, bid, bname, bmobile);

                                    //validUtils.showToast(context, jsonObject.getString("message"));
                                }else  if (jsonObject.getString("status")
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

    private void addCart(final int i, final String proid, final String cusid, final int flag, final String timestamp, final String bid, final String bname, final String bmobile) {

        progress.showProgressBar();
        StringRequest request = new StringRequest(Request.Method.POST, ADD_REMOVE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject != null){

                                if (jsonObject.getString("status")
                                        .equalsIgnoreCase("Inserted")){
                                    progress.hideProgressBar();
                                    /*bagList.remove(i);
                                    notifyItemRemoved(i);
                                    notifyDataSetChanged();
                                    bagList();*/
                                    int flag=0;
                                    removeBag(i, cusid, proid, flag);
                                    validUtils.showToast(context, jsonObject.getString("message"));
                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("Already")){
                                    progress.hideProgressBar();
                                    validUtils.showToast(context, jsonObject.getString("message"));
                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("Deleted")){
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
                params.put("customer_id", cusid);
                params.put("product_id", proid);
                params.put("flag", String.valueOf(flag));
                params.put("date", timestamp);
                params.put("bid", bid);
                params.put("bname", bname);
                params.put("bmobile", bmobile);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    @Override
    public int getItemCount() {
        return bagList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvPName, tvPPrice, tvPCPrice, tvTotalPrice;
        Spinner spQty;
        NetworkImageView ivImage;
        Button btnCart, btnRemove;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPName = itemView.findViewById(R.id.bag_pro_name);
            tvPPrice = itemView.findViewById(R.id.bag_price);
            tvPCPrice = itemView.findViewById(R.id.bag_cross_price);
            tvTotalPrice = itemView.findViewById(R.id.bag_total_price);
            spQty = itemView.findViewById(R.id.bag_qty);
            ivImage = itemView.findViewById(R.id.bag_image);
            btnCart = itemView.findViewById(R.id.bag_btn_cart);
            btnRemove = itemView.findViewById(R.id.bag_btn_remove);
        }
    }
}
