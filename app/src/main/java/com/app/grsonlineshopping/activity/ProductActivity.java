package com.app.grsonlineshopping.activity;

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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.grsonlineshopping.R;
import com.app.grsonlineshopping.adapter.BrandAdapter;
import com.app.grsonlineshopping.adapter.ProductAdapter;
import com.app.grsonlineshopping.helper.Constants;
import com.app.grsonlineshopping.helper.GRS;
import com.app.grsonlineshopping.helper.RecyclerViewClickListener;
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

public class ProductActivity extends AppCompatActivity implements InternetConnectivityListener {

    HashMap<String, String> map;
    InternetAvailabilityChecker availabilityChecker;
    ValidUtils validUtils;
    String product;
    RecyclerView rvProduct;
    WP10ProgressBar progress;
    ProductAdapter productAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<HashMap<String,String>> productList;
    String PRODUCT_URL = Constants.BASE_URL + Constants.GET_PRODUCT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        Constants.pref = getSharedPreferences("GRS",Context.MODE_PRIVATE);
        Constants.editor = Constants.pref.edit();

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);
        validUtils = new ValidUtils();

        map = (HashMap<String, String>)this.getIntent().getSerializableExtra("product");

        if (map != null && !map.isEmpty()){
            product = map.get("brand");
            Constants.editor.putString("product", product);
            Constants.editor.apply();
            Constants.editor.commit();
            TextView title = new TextView(getApplicationContext());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
            title.setLayoutParams(layoutParams);
            title.setText(product);
            title.setTextSize(20);
            title.setTextColor(Color.parseColor("#FFFFFF"));
            Typeface font = Typeface.createFromAsset(getAssets(), "sans_bold.otf");
            title.setTypeface(font);
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setCustomView(title);
        }else {
            product = Constants.pref.getString("product", "");
            TextView title = new TextView(getApplicationContext());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
            title.setLayoutParams(layoutParams);
            title.setText(product);
            title.setTextSize(20);
            title.setTextColor(Color.parseColor("#FFFFFF"));
            Typeface font = Typeface.createFromAsset(getAssets(), "sans_bold.otf");
            title.setTypeface(font);
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setCustomView(title);
        }

        rvProduct = findViewById(R.id.rv_product);
        progress = findViewById(R.id.product_progress);

        productList = new ArrayList<>();
        layoutManager = new GridLayoutManager(this, 2);
        rvProduct.setLayoutManager(layoutManager);
        getProduct();
    }

    private void getProduct() {

        progress.showProgressBar();
        StringRequest request = new StringRequest(Request.Method.POST, PRODUCT_URL,
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
                                    productList.clear();
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

                                        map.put("proid", id);
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

                                        productList.add(map);

                                    }

                                    productAdapter = new ProductAdapter(ProductActivity.this, productList);
                                    rvProduct.setAdapter(productAdapter);
                                    productAdapter.notifyDataSetChanged();

                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    progress.hideProgressBar();
                                    validUtils.showToast(ProductActivity.this, jsonObject.getString("data"));
                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("empty")){
                                    progress.hideProgressBar();
                                    validUtils.showToast(ProductActivity.this, jsonObject.getString("data"));
                                }
                            }else {
                                progress.hideProgressBar();
                                validUtils.showToast(ProductActivity.this, "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.hideProgressBar();
                            validUtils.showToast(ProductActivity.this, e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.hideProgressBar();
                        validUtils.showToast(ProductActivity.this, error.getMessage());
                    }
                })
        {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("subcatname", product);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(ProductActivity.this);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_cart) {
            startActivity(new Intent(ProductActivity.this, CartActivity.class));
            Bungee.fade(ProductActivity.this);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(ProductActivity.this);
            builder.setTitle("Network Error");
            builder.setMessage("Check your Internet Connection");
            builder.setCancelable(false);
            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //startActivity(new Intent(Settings.ACTION_SETTINGS));
                    finish();
                    startActivity(getIntent());
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
