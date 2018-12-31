package com.app.grsonlineshopping.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.app.grsonlineshopping.helper.OnDataChangeListener;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import thebat.lib.validutil.ValidUtils;
import static android.content.Context.MODE_PRIVATE;
import static com.app.grsonlineshopping.activity.CartActivity.cartList;
import static com.app.grsonlineshopping.activity.CartActivity.grandTotal;
import static com.app.grsonlineshopping.activity.CartActivity.progress;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<HashMap<String,String>> cartList;
    ImageLoader imageLoader;
    HashMap<String,String> map;
    HashMap<String, String> dataMap;
    OnDataChangeListener mOnDataChangeListener;
    ValidUtils validUtils;
    String ADD_QTY_URL = Constants.BASE_URL + Constants.ADD_QUANTITY;
    String ADD_REMOVE_URL = Constants.BASE_URL + Constants.ADD_REMOVE_CART;
    String ADD_REMOVE_BAG_URL = Constants.BASE_URL + Constants.ADD_REMOVE_BAG;

    public CartAdapter(Context context, ArrayList<HashMap<String, String>> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    public void setOnDataChangeListener(Context mcontext,OnDataChangeListener onDataChangeListener){
        mOnDataChangeListener = onDataChangeListener;
        context=mcontext;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cart_adapter, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        validUtils = new ValidUtils();
        map = cartList.get(i);

        myViewHolder.tvPName.setText(map.get("brand")+ "\t" +map.get("product"));
        myViewHolder.tvPPrice.setText( "₹"+map.get("price"));
        myViewHolder.tvPCPrice.setText( "₹"+map.get("cross_price"));
        myViewHolder.tvPCPrice.setPaintFlags(myViewHolder.tvPCPrice.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);

        imageLoader = ImageCache.getInstance(context).getImageLoader();
        imageLoader.get(Constants.IMAGE_URL + map.get("pro_image"), ImageLoader.getImageListener(myViewHolder.ivImage, R.drawable.ic_image, android.R.drawable.ic_dialog_alert));
        myViewHolder.ivImage.setImageUrl(Constants.IMAGE_URL + map.get("pro_image"), imageLoader);
        myViewHolder.ivImage.setScaleType(NetworkImageView.ScaleType.FIT_CENTER);

        final String qty[] = new String[]{

                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"
        };

        String quantity= map.get("quantity");
        if (!quantity.isEmpty()){
            List<String> qtyList = new ArrayList<>(Arrays.asList(quantity));
            ArrayAdapter<String > arrayAdapter =new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, qtyList);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            myViewHolder.spQty.setAdapter(arrayAdapter);
        }else {
            List<String> stringList = new ArrayList<>(Arrays.asList(qty));
            ArrayAdapter<String > arrayAdapter =new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, stringList);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            myViewHolder.spQty.setPrompt("Qty");
            myViewHolder.spQty.setAdapter(arrayAdapter);
        }


        myViewHolder.spQty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedItem = parent.getItemAtPosition(position).toString();
                String price = map.get("price");
                String totalprice = String.valueOf(Integer.valueOf(selectedItem) * Integer.valueOf(price));
                myViewHolder.tvTotalPrice.setText("₹"+totalprice);

                dataMap = new HashMap<String, String>();
                map.put("qty", selectedItem);
                map.put("totalprice", totalprice);
                dataMap = map;
                cartList.get(i).putAll(dataMap);

                if(mOnDataChangeListener != null){
                    mOnDataChangeListener.onDataChanged(grandTotal());
                    Constants.pref = context.getSharedPreferences("GRS",MODE_PRIVATE);
                    String cusid = Constants.pref.getString("mobile", "");
                    String proid = map.get("id");
                    addQuantity(cusid, proid, selectedItem, totalprice);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        myViewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Constants.pref = context.getSharedPreferences("GRS",MODE_PRIVATE);
                String cusid = Constants.pref.getString("mobile", "");
                String proid = map.get("id");

                cartList.remove(i);
                notifyItemRemoved(i);
                notifyDataSetChanged();

                int flag = 0;
                removeCart(cusid, proid, flag);

            }
        });

        myViewHolder.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cartList.remove(i);
                notifyItemRemoved(i);
                notifyDataSetChanged();

                Constants.pref = context.getSharedPreferences("GRS",MODE_PRIVATE);
                String cusid = Constants.pref.getString("mobile", "");
                String proid = map.get("id");
                int flag =1;
                String qty = Constants.pref.getString("b_qty", "");
                String total = Constants.pref.getString("b_total", "");
                addBag(cusid, proid, flag, qty, total);
            }
        });
    }

    private void addBag(final String cusid, final String proid, final int flag, final String qty, final String total) {

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
                                        .equalsIgnoreCase("Added")){
                                    progress.hideProgressBar();
                                    mOnDataChangeListener.onDataChanged(grandTotal());
                                    cartList();
                                    int flag = 0;
                                    removeCart(cusid, proid, flag);
                                    validUtils.showToast(context, jsonObject.getString("message"));
                                }else  if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
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
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    private void removeCart(final String cusid, final String proid, final int flag) {

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
                                        .equalsIgnoreCase("Deleted")){
                                    progress.hideProgressBar();
                                    mOnDataChangeListener.onDataChanged(grandTotal());
                                    cartList();
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

    private void addQuantity(final String cusid, final String proid, final String selectedItem, final String totalprice) {

        progress.showProgressBar();
        StringRequest request = new StringRequest(Request.Method.POST, ADD_QTY_URL,
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
                                    Constants.editor.putString("b_qty", selectedItem);
                                    Constants.editor.putString("b_total", totalprice);
                                    Constants.editor.apply();
                                    Constants.editor.commit();
                                    //validUtils.showToast(context, jsonObject.getString("data"));
                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    progress.hideProgressBar();
                                    validUtils.showToast(context, jsonObject.getString("data"));
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
                params.put("quantity", selectedItem);
                params.put("total", totalprice);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvPName, tvPPrice, tvPCPrice, tvTotalPrice;
        Spinner spQty;
        NetworkImageView ivImage;
        Button btnSave, btnRemove;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPName = itemView.findViewById(R.id.cart_pro_name);
            tvPPrice = itemView.findViewById(R.id.cart_price);
            tvPCPrice = itemView.findViewById(R.id.cart_cross_price);
            tvTotalPrice = itemView.findViewById(R.id.cart_total_price);
            spQty = itemView.findViewById(R.id.cart_qty);
            ivImage = itemView.findViewById(R.id.cart_image);
            btnSave = itemView.findViewById(R.id.cart_btn_save);
            btnRemove = itemView.findViewById(R.id.cart_btn_remove);
        }
    }
}
