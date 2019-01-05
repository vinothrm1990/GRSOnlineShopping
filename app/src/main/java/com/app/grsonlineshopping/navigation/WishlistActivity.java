package com.app.grsonlineshopping.navigation;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.grsonlineshopping.R;
import com.app.grsonlineshopping.activity.HomeActivity;
import com.app.grsonlineshopping.activity.RateActivity;
import com.app.grsonlineshopping.adapter.BagAdapter;
import com.app.grsonlineshopping.adapter.WishlistAdapter;
import com.app.grsonlineshopping.helper.Constants;
import com.app.grsonlineshopping.helper.GRS;
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

public class WishlistActivity extends AppCompatActivity implements InternetConnectivityListener {

    HashMap<String, String> map;
    InternetAvailabilityChecker availabilityChecker;
    ValidUtils validUtils;
    RecyclerView rvWish;
    public static WP10ProgressBar progress;
    WishlistAdapter wishlistAdapter;
    static LinearLayout bagLayout, emptyLayout;
    RecyclerView.LayoutManager layoutManager;
    Button btnAdd;
    public static ArrayList<HashMap<String,String>> wishList;
    String WISH_URL = Constants.BASE_URL + Constants.GET_WISHLIST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        Constants.pref = getSharedPreferences("GRS",Context.MODE_PRIVATE);
        Constants.editor = Constants.pref.edit();

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);
        validUtils = new ValidUtils();

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("My Wishlist");
        title.setTextSize(20);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "sans_bold.otf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(title);

        rvWish = findViewById(R.id.rv_wish);
        progress = findViewById(R.id.wish_progress);
        bagLayout = findViewById(R.id.wish_layout);
        emptyLayout = findViewById(R.id.wish_empty_layout);
        btnAdd = findViewById(R.id.wish_btn_add);

        wishList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        rvWish.setLayoutManager(layoutManager);
        String cusid = Constants.pref.getString("mobile", "");
        getWishlist(cusid);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(WishlistActivity.this, HomeActivity.class));
                Bungee.fade(WishlistActivity.this);
            }
        });

    }

    private void getWishlist(final String cusid) {

        progress.showProgressBar();
        StringRequest request = new StringRequest(Request.Method.POST, WISH_URL,
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
                                    bagLayout.setVisibility(View.VISIBLE);
                                    emptyLayout.setVisibility(View.GONE);
                                    String data = jsonObject.getString("data");
                                    JSONArray array = new JSONArray(data);
                                    wishList.clear();
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject object = array.getJSONObject(i);
                                        map = new HashMap<String, String>();

                                        String id = object.getString("id");
                                        String brand = object.getString("sub_product");
                                        String product = object.getString("product");
                                        String pro_image = object.getString("image");
                                        String slider_image = object.getString("image1");
                                        String size =  object.getString("size");
                                        String color =  object.getString("color");
                                        String desc =  object.getString("mobile_app");
                                        String price =  object.getString("price");
                                        String crossprice =  object.getString("cross_price");
                                        String branch_id =  object.getString("b_id");
                                        String branch_name =  object.getString("branchname");
                                        String branch_number =  object.getString("b_mobile");
                                        String prate =  object.getString("prate");
                                        String trate =  object.getString("trate");

                                        map.put("id", id);
                                        map.put("brand", brand);
                                        map.put("product", product);
                                        map.put("pro_image", pro_image);
                                        map.put("slider_image", slider_image);
                                        map.put("size", size);
                                        map.put("color", color);
                                        map.put("desc", desc);
                                        map.put("price", price);
                                        map.put("cross_price", crossprice);
                                        map.put("branch_id", branch_id);
                                        map.put("branch_name", branch_name);
                                        map.put("branch_number", branch_number);
                                        map.put("rate", prate);
                                        map.put("trate", trate);

                                        wishList.add(map);

                                    }
                                    wishlistAdapter = new WishlistAdapter(WishlistActivity.this, wishList);
                                    rvWish.setAdapter(wishlistAdapter);
                                    wishlistAdapter.notifyDataSetChanged();

                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("empty")){
                                    progress.hideProgressBar();
                                    bagLayout.setVisibility(View.GONE);
                                    emptyLayout.setVisibility(View.VISIBLE);
                                    validUtils.showToast(WishlistActivity.this, jsonObject.getString("message"));
                                }
                            }else {
                                progress.hideProgressBar();
                                validUtils.showToast(WishlistActivity.this, "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.hideProgressBar();
                            validUtils.showToast(WishlistActivity.this, e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.hideProgressBar();
                        validUtils.showToast(WishlistActivity.this, error.getMessage());
                    }
                })
        {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("customer_id", cusid);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(WishlistActivity.this);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);
    }

    public static void wishList(){
        if (wishList.size()==0){
            bagLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }else {
            bagLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(WishlistActivity.this);
            builder.setTitle("Network Error");
            builder.setMessage("Check your Internet Connection");
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                }
            });
            builder.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        GRS.activityResumed();
        availabilityChecker.addInternetConnectivityListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        GRS.activityPaused();
        availabilityChecker.removeInternetConnectivityChangeListener(this);
    }
}
