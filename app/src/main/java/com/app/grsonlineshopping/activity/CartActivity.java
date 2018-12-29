package com.app.grsonlineshopping.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.grsonlineshopping.R;
import com.app.grsonlineshopping.adapter.CartAdapter;
import com.app.grsonlineshopping.helper.Constants;
import com.app.grsonlineshopping.helper.GRS;
import com.app.grsonlineshopping.helper.OnDataChangeListener;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ir.alirezabdn.wp7progress.WP10ProgressBar;
import spencerstudios.com.bungeelib.Bungee;
import thebat.lib.validutil.ValidUtils;

public class CartActivity extends AppCompatActivity implements InternetConnectivityListener, OnDataChangeListener {

    InternetAvailabilityChecker availabilityChecker;
    RecyclerView rvCart;
    TextView tvAddress, tvTotalAmount;
    Button btnChange, btnProceed, btnContinue;
    HashMap<String, String> map;
    ValidUtils validUtils;
    static LinearLayout cartLayout, bottomLayout, emptyLayout;
    public static WP10ProgressBar progress;
    CartAdapter cartAdapter;
    RecyclerView.LayoutManager layoutManager;
    public static ArrayList<HashMap<String,String>> cartList;
    public static int total;
    String GET_CART_URL = Constants.BASE_URL + Constants.GET_CART;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("My Cart");
        title.setTextSize(20);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "sans_bold.otf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(title);

        Constants.pref = getSharedPreferences("GRS",Context.MODE_PRIVATE);
        Constants.editor = Constants.pref.edit();

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);
        validUtils = new ValidUtils();

        rvCart = findViewById(R.id.rv_cart);
        tvAddress = findViewById(R.id.cart_address);
        tvTotalAmount = findViewById(R.id.cart_total_amount);
        btnChange = findViewById(R.id.cart_btn_change);
        btnProceed = findViewById(R.id.cart_btn_proceed);
        btnContinue = findViewById(R.id.cart_btn_continue);
        cartLayout = findViewById(R.id.cart_layout);
        emptyLayout = findViewById(R.id.cart_no_layout);
        bottomLayout = findViewById(R.id.bottom_layout);
        progress = findViewById(R.id.cart_progress);

        cartList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        rvCart.setLayoutManager(layoutManager);
        String cus_id = Constants.pref.getString("mobile", "");
        getCart(cus_id);

        tvTotalAmount.setText("₹"+String.valueOf(grandTotal()));

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(CartActivity.this, HomeActivity.class));
                Bungee.fade(CartActivity.this);
            }
        });
    }

    private void getCart(final String cus_id) {

        progress.showProgressBar();
        StringRequest request = new StringRequest(Request.Method.POST, GET_CART_URL,
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
                                    cartLayout.setVisibility(View.VISIBLE);
                                    bottomLayout.setVisibility(View.VISIBLE);
                                    emptyLayout.setVisibility(View.GONE);
                                    String data = jsonObject.getString("data");
                                    JSONArray array = new JSONArray(data);
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject object = array.getJSONObject(i);
                                        map = new HashMap<String, String>();

                                        String id = object.getString("id");
                                        String brand = object.getString("sub_product");
                                        String product = object.getString("pname");
                                        String pro_image = object.getString("image");
                                        String price =  object.getString("price");
                                        String crossprice =  object.getString("cross_price");
                                        String branch_id =  object.getString("b_id");
                                        String branch_name =  object.getString("branchname");
                                        String branch_number =  object.getString("b_mobile");

                                        map.put("id", id);
                                        map.put("brand", brand);
                                        map.put("product", product);
                                        map.put("pro_image", pro_image);
                                        map.put("price", price);
                                        map.put("cross_price", crossprice);
                                        map.put("branch_id", branch_id);
                                        map.put("branch_name", branch_name);
                                        map.put("branch_number", branch_number);

                                        cartList.add(map);

                                    }

                                    cartAdapter = new CartAdapter(CartActivity.this, cartList);
                                    rvCart.setAdapter(cartAdapter);
                                    cartAdapter.setOnDataChangeListener(CartActivity.this,CartActivity.this);
                                    cartAdapter.notifyDataSetChanged();

                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("empty")){
                                    progress.hideProgressBar();
                                    cartLayout.setVisibility(View.GONE);
                                    bottomLayout.setVisibility(View.GONE);
                                    emptyLayout.setVisibility(View.VISIBLE);
                                    validUtils.showToast(CartActivity.this, jsonObject.getString("message"));
                                }
                            }else {
                                progress.hideProgressBar();
                                validUtils.showToast(CartActivity.this, "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.hideProgressBar();
                            validUtils.showToast(CartActivity.this, e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.hideProgressBar();
                        validUtils.showToast(CartActivity.this, error.getMessage());
                    }
                })
        {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("customer_id", cus_id);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(CartActivity.this);
        queue.add(request);
    }

    public static int grandTotal(){
        total = 0;
        for(int i = 0 ; i < cartList.size(); i++) {
            if (cartList.get(i).get("totalprice") != null) {
                total += Integer.parseInt(cartList.get(i).get("totalprice"));
            }
        }
        return total;
    }

    @Override
    public void onDataChanged(int total) {
        tvTotalAmount.setText("₹"+String.valueOf(total));
    }

    @Override
    protected void onResume() {
        super.onResume();
        onDataChanged(total);
    }

    public static void cartList(){
        if (cartList.size()==0){
            cartLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
            bottomLayout.setVisibility(View.GONE);
        }else {
            cartLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        availabilityChecker.removeInternetConnectivityChangeListener(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        GRS.freeMemory();
    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        if (!isConnected){
            validUtils.showToast(CartActivity.this, "Check your Internet Connection!");
        }
    }

}
