package com.app.grsonlineshopping.navigation;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import com.app.grsonlineshopping.activity.ProductActivity;
import com.app.grsonlineshopping.adapter.BagAdapter;
import com.app.grsonlineshopping.adapter.ProductAdapter;
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
import thebat.lib.validutil.ValidUtils;

public class BagActivity extends AppCompatActivity implements InternetConnectivityListener {

    HashMap<String, String> map;
    InternetAvailabilityChecker availabilityChecker;
    ValidUtils validUtils;
    RecyclerView rvBag;
    public static WP10ProgressBar progress;
    BagAdapter bagAdapter;
    static LinearLayout bagLayout, emptyLayout;
    RecyclerView.LayoutManager layoutManager;
    public static ArrayList<HashMap<String,String>> bagList;
    String BAG_URL = Constants.BASE_URL + Constants.GET_BAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bag);

        Constants.pref = getSharedPreferences("GRS",Context.MODE_PRIVATE);
        Constants.editor = Constants.pref.edit();

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);
        validUtils = new ValidUtils();

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("My Bag");
        title.setTextSize(20);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "sans_bold.otf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(title);

        rvBag = findViewById(R.id.rv_bag);
        progress = findViewById(R.id.bag_progress);
        bagLayout = findViewById(R.id.bag_layout);
        emptyLayout = findViewById(R.id.bag_empty_layout);

        bagList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        rvBag.setLayoutManager(layoutManager);
        String cusid = Constants.pref.getString("mobile", "");
        getBag(cusid);

    }

    private void getBag(final String cusid) {

        progress.showProgressBar();
        StringRequest request = new StringRequest(Request.Method.POST, BAG_URL,
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
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject object = array.getJSONObject(i);
                                        map = new HashMap<String, String>();

                                        String id = object.getString("proid");
                                        String brand = object.getString("brand");
                                        String product = object.getString("product");
                                        String pro_image = object.getString("image");
                                        String price =  object.getString("price");
                                        String crossprice =  object.getString("crossprice");

                                        map.put("id", id);
                                        map.put("brand", brand);
                                        map.put("product", product);
                                        map.put("pro_image", pro_image);
                                        map.put("price", price);
                                        map.put("cross_price", crossprice);

                                        bagList.add(map);

                                    }
                                    bagAdapter = new BagAdapter(BagActivity.this, bagList);
                                    rvBag.setAdapter(bagAdapter);
                                    bagAdapter.notifyDataSetChanged();

                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("empty")){
                                    progress.hideProgressBar();
                                    bagLayout.setVisibility(View.GONE);
                                    emptyLayout.setVisibility(View.VISIBLE);
                                    validUtils.showToast(BagActivity.this, jsonObject.getString("message"));
                                }
                            }else {
                                progress.hideProgressBar();
                                validUtils.showToast(BagActivity.this, "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.hideProgressBar();
                            validUtils.showToast(BagActivity.this, e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.hideProgressBar();
                        validUtils.showToast(BagActivity.this, error.getMessage());
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
        RequestQueue queue = Volley.newRequestQueue(BagActivity.this);
        queue.add(request);

    }

    public static void bagList(){
        if (bagList.size()==0){
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
            validUtils.showToast(BagActivity.this, "Check your Internet Connection!");
        }
    }
}
