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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import thebat.lib.validutil.ValidUtils;
import static android.content.Context.MODE_PRIVATE;
import static com.app.grsonlineshopping.navigation.BagActivity.bagList;
import static com.app.grsonlineshopping.navigation.BagActivity.progress;

public class BagAdapter extends RecyclerView.Adapter<BagAdapter.MyViewHolder> {


    private Context context;
    private ArrayList<HashMap<String,String>> bagList;
    ImageLoader imageLoader;
    ValidUtils validUtils;
    String ADD_QTY_URL = Constants.BASE_URL + Constants.ADD_BAG_QUANTITY;
    String GET_PRODUCT_URL = Constants.BASE_URL + Constants.GET_SUBPRODUCT;
    String MOVE_CART_URL = Constants.BASE_URL + Constants.MOVE_CART;
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
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        validUtils = new ValidUtils();
        final HashMap<String,String> map = bagList.get(i);

        myViewHolder.tvPName.setText(map.get("brand")+ "\t" +map.get("product"));
        myViewHolder.tvPPrice.setText( "₹"+map.get("price"));
        myViewHolder.tvPCPrice.setText( "₹"+map.get("cross_price"));
        myViewHolder.tvPCPrice.setPaintFlags(myViewHolder.tvPCPrice.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);
        myViewHolder.tvTotalPrice.setText("₹"+map.get("total"));

        imageLoader = ImageCache.getInstance(context).getImageLoader();
        imageLoader.get(Constants.IMAGE_URL + map.get("pro_image"), ImageLoader.getImageListener(myViewHolder.ivImage, R.drawable.ic_image, android.R.drawable.ic_dialog_alert));
        myViewHolder.ivImage.setImageUrl(Constants.IMAGE_URL + map.get("pro_image"), imageLoader);
        myViewHolder.ivImage.setScaleType(NetworkImageView.ScaleType.FIT_CENTER);

        String quantity= map.get("qty");
        if (quantity != null){
            final String qty[] = new String[]{

                    "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"
            };

            List<String> stringList = new ArrayList<>(Arrays.asList(qty));
            ArrayAdapter<String > arrayAdapter =new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, stringList);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            myViewHolder.spQty.setAdapter(arrayAdapter);
            myViewHolder.spQty.setSelection(arrayAdapter.getPosition(quantity));
        }else {
            final String qty[] = new String[]{

                    "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"
            };

            List<String> stringList = new ArrayList<>(Arrays.asList(qty));
            ArrayAdapter<String > arrayAdapter =new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, stringList);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            myViewHolder.spQty.setAdapter(arrayAdapter);
        }

        myViewHolder.spQty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedItem = parent.getItemAtPosition(position).toString();
                String price = map.get("price");
                String totalprice = String.valueOf(Integer.valueOf(selectedItem) * Integer.valueOf(price));
                myViewHolder.tvTotalPrice.setText("₹"+totalprice);

                Constants.pref = context.getSharedPreferences("GRS",MODE_PRIVATE);
                String cusid = Constants.pref.getString("mobile", "");
                String proid = map.get("id");
                addQuantity(cusid, proid, selectedItem, totalprice);

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

                int flag = 0;
                int pos = myViewHolder.getAdapterPosition();
                removeBag(pos, cusid, proid, flag);

            }
        });

        myViewHolder.btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String proid = map.get("id");
                String qty = Constants.pref.getString("b_quantity", "");
                String total = Constants.pref.getString("b_total_price", "");
                int pos = myViewHolder.getAdapterPosition();
                getProduct(proid, pos, qty, total);

            }
        });
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
                                    Constants.editor.putString("b_quantity", selectedItem);
                                    Constants.editor.putString("b_total_price", totalprice);
                                    Constants.editor.apply();
                                    Constants.editor.commit();
                                    //validUtils.showToast(context, jsonObject.getString("data"));
                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    progress.hideProgressBar();
                                    validUtils.showToast(context, jsonObject.getString("data"));
                                }
                            }else {
                                CartActivity.progress.hideProgressBar();
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
                                    notifyItemRangeChanged(i, bagList.size());
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

    private void getProduct(final String proid, final int i, final String qty, final String total) {

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
                                    Random rand = new Random();
                                    int rand_int = rand.nextInt(1000);
                                    addCart(i, proid, cusid, flag, timestamp, bid, bname, bmobile, qty, total, rand_int);

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

    private void addCart(final int i, final String proid, final String cusid, final int flag, final String timestamp, final String bid, final String bname, final String bmobile, final String qty, final String total, final int rand_int) {

        progress.showProgressBar();
        StringRequest request = new StringRequest(Request.Method.POST, MOVE_CART_URL,
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
                params.put("bmobile", bmobile);
                params.put("cartid", String.valueOf(rand_int));
                params.put("qty", qty);
                params.put("total", total);
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
