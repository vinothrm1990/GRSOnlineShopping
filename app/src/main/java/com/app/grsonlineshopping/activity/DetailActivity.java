package com.app.grsonlineshopping.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.app.grsonlineshopping.adapter.DetailAdapter;
import com.app.grsonlineshopping.adapter.RateAdapter;
import com.app.grsonlineshopping.helper.Constants;
import com.app.grsonlineshopping.helper.GRS;
import com.codesgood.views.JustifiedTextView;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;
import com.viewpagerindicator.CirclePageIndicator;

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

import ir.alirezabdn.wp7progress.WP10ProgressBar;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import spencerstudios.com.bungeelib.Bungee;
import thebat.lib.validutil.ValidUtils;

public class DetailActivity extends AppCompatActivity implements InternetConnectivityListener {

    InternetAvailabilityChecker availabilityChecker;
    HashMap<String, String> map;
    ValidUtils validUtils;
    String cus_id, pro_id, product, brand, image, size, color, desc, b_id, b_name, b_mobile, price, c_price, rate, trate;
    DetailAdapter detailAdapter;
    private static ViewPager viewPager;
    private static int NUM_PAGES = 0;
    private static int currentPage = 0;
    CirclePageIndicator pageIndicator;
    LinearLayout descLayout, descDetailLayout, sizeLayout, rateLayout;
    TextView tvName, tvCrossPrice, tvPrice, tvColor, tvSize, tvTotalRate;
    ImageView ivShow, ivHide;
    JustifiedTextView tvDesc;
    int count = 0;
    String flag;
    MaterialRatingBar ratingBar;
    WP10ProgressBar progress;
    Button btnBuy, btnWish, btnCart;
    String ADD_REMOVE_URL = Constants.BASE_URL + Constants.ADD_REMOVE_CART;
    String GET_FLAG_URL = Constants.BASE_URL + Constants.GET_CART_FLAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Constants.pref = getSharedPreferences("GRS",Context.MODE_PRIVATE);
        Constants.editor = Constants.pref.edit();

        cus_id = Constants.pref.getString("mobile", "");

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);
        validUtils = new ValidUtils();
        map = (HashMap<String, String>)this.getIntent().getSerializableExtra("subproduct");
        flag = getIntent().getStringExtra("flag");

        if (map != null && !map.isEmpty()){
            pro_id = map.get("id");
            product = map.get("product");
            brand = map.get("brand");
            image = map.get("slider_image");
            size = map.get("size");
            color = map.get("color");
            desc = map.get("desc");
            price = map.get("price");
            c_price = map.get("cross_price");
            b_id = map.get("branch_id");
            b_name = map.get("branch_name");
            b_mobile = map.get("branch_number");
            rate = map.get("rate");
            trate = map.get("trate");

            Constants.editor.putString("id", pro_id);
            Constants.editor.putString("detail", product);
            Constants.editor.putString("brand", brand);
            Constants.editor.putString("size", size);
            Constants.editor.putString("color", color);
            Constants.editor.putString("image", image);
            Constants.editor.putString("desc", desc);
            Constants.editor.putString("price", price);
            Constants.editor.putString("cprice", c_price);
            Constants.editor.putString("rate", rate);
            Constants.editor.putString("trate", trate);
            Constants.editor.putString("bid", b_id);
            Constants.editor.putString("bname", b_name);
            Constants.editor.putString("bmobile", b_mobile);
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
            pro_id = Constants.pref.getString("id", "");
            product = Constants.pref.getString("detail", "");
            brand = Constants.pref.getString("brand", "");
            size = Constants.pref.getString("size", "");
            color = Constants.pref.getString("color", "");
            desc = Constants.pref.getString("desc", "");
            price = Constants.pref.getString("price", "");
            c_price = Constants.pref.getString("cprice", "");
            rate = Constants.pref.getString("rate", "");
            trate = Constants.pref.getString("trate", "");
            b_id = Constants.pref.getString("bid", "");
            b_name = Constants.pref.getString("bname", "");
            b_mobile = Constants.pref.getString("bmobile", "");
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

        viewPager = findViewById(R.id.slide_pager);
        pageIndicator = findViewById(R.id.slide_indicator);
        tvCrossPrice = findViewById(R.id.det_cross_price);
        tvPrice = findViewById(R.id.det_price);
        tvName = findViewById(R.id.det_name);
        tvDesc = findViewById(R.id.det_desc);
        descLayout = findViewById(R.id.det_desc_layout);
        descDetailLayout = findViewById(R.id.det_desc_detail);
        ivShow = findViewById(R.id.det_iv_add);
        ivHide = findViewById(R.id.det_iv_remove);
        tvColor = findViewById(R.id.det_color);
        tvSize = findViewById(R.id.det_size);
        sizeLayout = findViewById(R.id.det_size_layout);
        rateLayout = findViewById(R.id.det_rate_layout);
        progress = findViewById(R.id.detail_progress);
        ratingBar = findViewById(R.id.det_rating_bar);
        tvTotalRate = findViewById(R.id.det_trate);
        btnBuy = findViewById(R.id.det_btn_buy);
        btnWish = findViewById(R.id.detail_btn_wishlist);
        btnCart = findViewById(R.id.detail_btn_cart);

        if (flag.equalsIgnoreCase("")) {
            getFlag(cus_id, pro_id);
        }else {
            btnCart.setText("REMOVE FROM CART");
            btnBuy.setVisibility(View.GONE);
        }
        if (image!=null && !image.isEmpty()){
            String [] list = image.split(",");
            List<String> sepList = Arrays.asList(list);
            ArrayList<String> proList = new ArrayList<String>(sepList);
            NUM_PAGES = proList.size();
            detailAdapter= new DetailAdapter(this,proList);
            viewPager.setAdapter(detailAdapter);
            viewPager.setOffscreenPageLimit(NUM_PAGES);
            pageIndicator.setViewPager(viewPager);
        }else {
            image = Constants.pref.getString("image", "");
            String [] list = image.split(",");
            List<String> sepList = Arrays.asList(list);
            ArrayList<String> proList = new ArrayList<String>(sepList);
            NUM_PAGES = proList.size();
            detailAdapter= new DetailAdapter(this,proList);
            viewPager.setAdapter(detailAdapter);
            viewPager.setOffscreenPageLimit(NUM_PAGES);
            pageIndicator.setViewPager(viewPager);
        }
        final float density = getResources().getDisplayMetrics().density;
        pageIndicator.setRadius(5 * density);

        pageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                currentPage = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        tvName.setText(brand +"\t"+ product);
        tvName.setSelected(true);
        tvCrossPrice.setText("₹"+c_price);
        tvCrossPrice.setPaintFlags(tvCrossPrice.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);
        tvPrice.setText("₹"+price);
        tvColor.setText(color);
        tvColor.setSelected(true);
        if (!rate.isEmpty() && !trate.isEmpty()){
            ratingBar.setRating(Float.parseFloat(rate));
            tvTotalRate.setText(trate+"\t Reviews for this Product");
        }else {
            ratingBar.setRating(0);
            tvTotalRate.setText("\t Reviews for this Product");
        }

        if (size!=null && !size.isEmpty()){
            tvSize.setText(size);
            tvSize.setSelected(true);
        }else {
            sizeLayout.setVisibility(View.GONE);
        }

        descLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                count = count + 1;
                if (count == 1){
                    ivShow.setVisibility(View.GONE);
                    ivHide.setVisibility(View.VISIBLE);
                    descDetailLayout.setVisibility(View.VISIBLE);
                    tvDesc.setText(desc);
                }else {
                    count = 0;
                    ivShow.setVisibility(View.VISIBLE);
                    ivHide.setVisibility(View.GONE);
                    descDetailLayout.setVisibility(View.GONE);
                }

            }
        });

        rateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DetailActivity.this, RateActivity.class);
                intent.putExtra("rating", map);
                startActivity(intent);
                Bungee.fade(DetailActivity.this);
            }
        });

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date now = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String timestamp = sdf.format(now);
                int flag = 1;
                addCart(pro_id, cus_id, flag, timestamp, b_id, b_name, b_mobile);

            }
        });

        btnWish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date now = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String timestamp = sdf.format(now);

                if (Constants.cart.equals("0")){
                    int flag = 1;
                    Constants.cart="1";
                    btnCart.setText("REMOVE FROM CART");
                    btnBuy.setVisibility(View.GONE);
                    addRemoveCart(pro_id, cus_id, flag, timestamp, b_id, b_name, b_mobile);
                }else if (Constants.cart.equals("1")){
                    int flag = 0;
                    Constants.cart="0";
                    btnCart.setText("ADD TO CART");
                    btnBuy.setVisibility(View.VISIBLE);
                    addRemoveCart(pro_id, cus_id, flag, timestamp, b_id, b_name, b_mobile);
                }

            }
        });
    }

    private void addCart(final String pro_id, final String cus_id, final int flag, final String timestamp, final String b_id, final String b_name, final String b_mobile) {

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
                                    Intent intent = new Intent(DetailActivity.this, CartActivity.class);
                                    startActivity(intent);
                                    Bungee.fade(DetailActivity.this);
                                    //validUtils.showToast(DetailActivity.this, jsonObject.getString("message"));
                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("Already")){
                                    progress.hideProgressBar();
                                    validUtils.showToast(DetailActivity.this, jsonObject.getString("message"));
                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("Deleted")){
                                    progress.hideProgressBar();
                                    validUtils.showToast(DetailActivity.this, jsonObject.getString("message"));
                                }
                            }else {
                                progress.hideProgressBar();
                                validUtils.showToast(DetailActivity.this, "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.hideProgressBar();
                            validUtils.showToast(DetailActivity.this, e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.hideProgressBar();
                        validUtils.showToast(DetailActivity.this, error.getMessage());
                    }
                })
        {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("customer_id", cus_id);
                params.put("product_id", pro_id);
                params.put("flag", String.valueOf(flag));
                params.put("date", timestamp);
                params.put("bid", b_id);
                params.put("bname", b_name);
                params.put("bmobile", b_mobile);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(DetailActivity.this);
        queue.add(request);
    }

    private void getFlag(final String cus_id, final String pro_id) {

        progress.showProgressBar();
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
                                    progress.hideProgressBar();
                                    String flag = jsonObject.getString("cart_flag");
                                    if (flag.equalsIgnoreCase("1")) {
                                        btnCart.setText("REMOVE FROM CART");
                                        btnBuy.setVisibility(View.GONE);
                                    }
                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("empty")){
                                    progress.hideProgressBar();
                                    btnCart.setText("ADD TO CART");
                                    btnBuy.setVisibility(View.VISIBLE);
                                }
                            }else {
                                progress.hideProgressBar();
                                validUtils.showToast(DetailActivity.this, "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.hideProgressBar();
                            validUtils.showToast(DetailActivity.this, e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.hideProgressBar();
                        validUtils.showToast(DetailActivity.this, error.getMessage());
                    }
                })
        {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("customer_id", cus_id);
                params.put("product_id", pro_id);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(DetailActivity.this);
        queue.add(request);
    }

    private void addRemoveCart(final String pro_id, final String cus_id, final int flag, final String timestamp, final String b_id, final String b_name, final String b_mobile) {

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
                                    validUtils.showToast(DetailActivity.this, jsonObject.getString("message"));
                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("Already")){
                                    progress.hideProgressBar();
                                    validUtils.showToast(DetailActivity.this, jsonObject.getString("message"));
                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("Deleted")){
                                    progress.hideProgressBar();
                                    validUtils.showToast(DetailActivity.this, jsonObject.getString("message"));
                                }
                            }else {
                                progress.hideProgressBar();
                                validUtils.showToast(DetailActivity.this, "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.hideProgressBar();
                            validUtils.showToast(DetailActivity.this, e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.hideProgressBar();
                        validUtils.showToast(DetailActivity.this, error.getMessage());
                    }
                })
        {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("customer_id", cus_id);
                params.put("product_id", pro_id);
                params.put("flag", String.valueOf(flag));
                params.put("date", timestamp);
                params.put("bid", b_id);
                params.put("bname", b_name);
                params.put("bmobile", b_mobile);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(DetailActivity.this);
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
            startActivity(new Intent(DetailActivity.this, CartActivity.class));
            Bungee.fade(DetailActivity.this);
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
            validUtils.showToast(DetailActivity.this, "Check your Internet Connection!");
        }
    }

}