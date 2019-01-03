package com.app.grsonlineshopping.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.app.grsonlineshopping.adapter.BrandAdapter;
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

public class BrandActivity extends AppCompatActivity implements InternetConnectivityListener {

    InternetAvailabilityChecker availabilityChecker;
    HashMap<String, String> map;
    ValidUtils validUtils;
    RecyclerView rvBrand;
    WP10ProgressBar progress;
    BrandAdapter brandAdapter;
    String product;
    LinearLayout brandLayout, emptyLayout;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<HashMap<String,String>> brandList;
    String BRAND_URL = Constants.BASE_URL + Constants.GET_BRAND;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand);

        Constants.pref = getSharedPreferences("GRS",Context.MODE_PRIVATE);
        Constants.editor = Constants.pref.edit();

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);
        validUtils = new ValidUtils();
        map = (HashMap<String, String>)this.getIntent().getSerializableExtra("product");

        if (map!= null && !map.isEmpty()){
            product = map.get("cat");
            Constants.editor.putString("category", product);
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
            product = Constants.pref.getString("category", "");
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

        rvBrand = findViewById(R.id.rv_brand);
        progress = findViewById(R.id.brand_progress);
        brandLayout = findViewById(R.id.brand_layout);
        emptyLayout = findViewById(R.id.brand_empty_layout);

        brandList = new ArrayList<>();
        layoutManager = new GridLayoutManager(this, 2);
        rvBrand.setLayoutManager(layoutManager);
        getBrand();

    }

    private void getBrand() {

        progress.showProgressBar();
        StringRequest request = new StringRequest(Request.Method.POST, BRAND_URL,
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
                                    brandLayout.setVisibility(View.VISIBLE);
                                    emptyLayout.setVisibility(View.GONE);
                                    String data = jsonObject.getString("data");
                                    JSONArray array = new JSONArray(data);
                                    brandList.clear();
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject object = array.getJSONObject(i);
                                        map = new HashMap<String, String>();

                                        String brand = object.getString("sub_product");
                                        String pro_image = object.getString("image");

                                        map.put("brand", brand);
                                        map.put("pro_image", pro_image);

                                        brandList.add(map);

                                    }
                                    brandAdapter = new BrandAdapter(BrandActivity.this, brandList);
                                    rvBrand.setAdapter(brandAdapter);
                                    brandAdapter.notifyDataSetChanged();

                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    progress.hideProgressBar();
                                    brandLayout.setVisibility(View.GONE);
                                    emptyLayout.setVisibility(View.VISIBLE);
                                    validUtils.showToast(BrandActivity.this, jsonObject.getString("data"));
                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("empty")){
                                    progress.hideProgressBar();
                                    brandLayout.setVisibility(View.GONE);
                                    emptyLayout.setVisibility(View.VISIBLE);
                                    validUtils.showToast(BrandActivity.this, jsonObject.getString("data"));
                                }
                            }else {
                                progress.hideProgressBar();
                                validUtils.showToast(BrandActivity.this, "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.hideProgressBar();
                            validUtils.showToast(BrandActivity.this, e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.hideProgressBar();
                        validUtils.showToast(BrandActivity.this, error.getMessage());
                    }
                })
        {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("catname", product);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(BrandActivity.this);
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
            startActivity(new Intent(BrandActivity.this, CartActivity.class));
            Bungee.fade(BrandActivity.this);
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
            validUtils.showToast(BrandActivity.this, "Check your Internet Connection!");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        GRS.activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GRS.activityResumed();
    }

}
