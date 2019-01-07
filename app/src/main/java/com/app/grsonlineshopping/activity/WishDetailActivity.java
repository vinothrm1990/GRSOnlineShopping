package com.app.grsonlineshopping.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.provider.Settings;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.grsonlineshopping.R;
import com.app.grsonlineshopping.adapter.DetailAdapter;
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
import java.util.Random;

import ir.alirezabdn.wp7progress.WP10ProgressBar;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import spencerstudios.com.bungeelib.Bungee;
import thebat.lib.validutil.ValidUtils;

public class WishDetailActivity extends AppCompatActivity implements InternetConnectivityListener {

    InternetAvailabilityChecker availabilityChecker;
    HashMap<String, String> map, wmap;
    ValidUtils validUtils;
    String cus_id, pro_id, product, brand, image, size, color, desc, b_id, b_name, b_mobile, price, c_price, rate, trate;
    DetailAdapter detailAdapter;
    private static ViewPager viewPager;
    private static int NUM_PAGES = 0;
    private static int currentPage = 0;
    CirclePageIndicator pageIndicator;
    LinearLayout descLayout, descDetailLayout, sizeLayout, rateLayout, colorLayout, detailLayout;
    TextView tvName, tvCrossPrice, tvPrice, tvColor, tvSize, tvTotalRate;
    ImageView ivShow, ivHide;
    JustifiedTextView tvDesc;
    int count = 0;
    String flag, wflag;
    MaterialRatingBar ratingBar;
    WP10ProgressBar progress;
    Button btnBuy, btnWish, btnCart;
    String ADD_REMOVE_URL = Constants.BASE_URL + Constants.ADD_REMOVE_CART;
    String GET_WISH_FLAG_URL = Constants.BASE_URL + Constants.GET_WISH_FLAG;
    String GET_FLAG_URL = Constants.BASE_URL + Constants.GET_CART_FLAG;
    String ADD_REMOVE_WISH_URL = Constants.BASE_URL + Constants.ADD_REMOVE_WISHLIST;
    String GET_RATE_URL = Constants.BASE_URL + Constants.GET_RATING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_detail);

        Constants.pref = getSharedPreferences("GRS",Context.MODE_PRIVATE);
        Constants.editor = Constants.pref.edit();

        cus_id = Constants.pref.getString("mobile", "");

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);
        validUtils = new ValidUtils();
       // map = (HashMap<String, String>)this.getIntent().getSerializableExtra("subproduct");
        wmap = (HashMap<String, String>)this.getIntent().getSerializableExtra("wishproduct");
        flag = getIntent().getStringExtra("flag");
        wflag = getIntent().getStringExtra("wflag");


        if (wmap != null && !wmap.isEmpty()){
            pro_id = wmap.get("id");
            product = wmap.get("product");
            brand = wmap.get("brand");
            image = wmap.get("slider_image");
            size = wmap.get("size");
            color = wmap.get("color");
            desc = wmap.get("desc");
            price = wmap.get("price");
            c_price = wmap.get("cross_price");
            b_id = wmap.get("branch_id");
            b_name = wmap.get("branch_name");
            b_mobile = wmap.get("branch_number");
            rate = wmap.get("rate");
            trate = wmap.get("trate");

            Constants.editor.putString("id", pro_id);
            Constants.editor.putString("detail", product);
            Constants.editor.putString("brand", brand);
            Constants.editor.putString("size", size);
            Constants.editor.putString("color", color);
            Constants.editor.putString("image", image);
            Constants.editor.putString("desc", desc);
            Constants.editor.putString("price", price);
            Constants.editor.putString("cprice", c_price);
            /*Constants.editor.putString("rate", rate);
            Constants.editor.putString("trate", trate);*/
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
           /* rate = Constants.pref.getString("rate", "");
            trate = Constants.pref.getString("trate", "");*/
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
        detailLayout = findViewById(R.id.det_detail_layout);
        descDetailLayout = findViewById(R.id.det_desc_detail);
        ivShow = findViewById(R.id.det_iv_add);
        ivHide = findViewById(R.id.det_iv_remove);
        tvColor = findViewById(R.id.det_color);
        tvSize = findViewById(R.id.det_size);
        sizeLayout = findViewById(R.id.det_size_layout);
        colorLayout = findViewById(R.id.det_color_layout);
        rateLayout = findViewById(R.id.det_rate_layout);
        progress = findViewById(R.id.wish_detail_progress);
        ratingBar = findViewById(R.id.det_rating_bar);
        tvTotalRate = findViewById(R.id.det_trate);
        btnBuy = findViewById(R.id.det_btn_buy);
        btnWish = findViewById(R.id.detail_btn_wishlist);
        btnCart = findViewById(R.id.detail_btn_cart);

        if (flag==null){
            getFlag(cus_id, pro_id);
        }else if (flag.equalsIgnoreCase("0")) {
            getFlag(cus_id, pro_id);
        }else if (flag.equalsIgnoreCase("1")){
            btnCart.setText("REMOVE FROM CART");
            btnBuy.setVisibility(View.GONE);
        }

        if (wflag==null){
            getWishFlag(cus_id, pro_id);
        }else if (wflag.equalsIgnoreCase("0")) {
            getWishFlag(cus_id, pro_id);
        }else if (wflag.equalsIgnoreCase("1")){
            btnWish.setText("REMOVE FROM WISHLIST");
        }

        getRating(pro_id);

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

        if (color!=null && !color.isEmpty()){
            tvColor.setText(color);
            tvColor.setSelected(true);
        }else {
            colorLayout.setVisibility(View.GONE);
        }

        if (size == null && color == null){
            detailLayout.setVisibility(View.GONE);
        }else if (size.equals("") && color.equals("")){
            detailLayout.setVisibility(View.GONE);
        }else {
            detailLayout.setVisibility(View.VISIBLE);
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

                if (map!=null && !map.isEmpty()){
                    Intent intent = new Intent(WishDetailActivity.this, RateActivity.class);
                    intent.putExtra("rating", map);
                    startActivity(intent);
                    Bungee.fade(WishDetailActivity.this);
                }else {
                    Intent intent = new Intent(WishDetailActivity.this, RateActivity.class);
                    intent.putExtra("rating", wmap);
                    startActivity(intent);
                    Bungee.fade(WishDetailActivity.this);
                }

            }
        });

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date now = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String timestamp = sdf.format(now);
                int flag = 1;
                Random rand = new Random();
                int rand_int = rand.nextInt(1000);
                addCart(pro_id, cus_id, flag, timestamp, b_id, b_name, b_mobile, rand_int);

            }
        });

        btnWish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Constants.wish.equals("0")){
                    int flag = 1;
                    Constants.wish="1";
                    btnWish.setText("REMOVE FROM WISHLIST");
                    addRemoveWish(pro_id, cus_id, flag);
                }else if (Constants.wish.equals("1")){
                    int flag = 0;
                    Constants.wish="0";
                    btnWish.setText("ADD TO WISHLIST");
                    addRemoveWish(pro_id, cus_id, flag);
                }
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
                    Random rand = new Random();
                    int rand_int = rand.nextInt(1000);
                    addRemoveCart(pro_id, cus_id, flag, timestamp, b_id, b_name, b_mobile, rand_int);
                }else if (Constants.cart.equals("1")){
                    int flag = 0;
                    Constants.cart="0";
                    btnCart.setText("ADD TO CART");
                    btnBuy.setVisibility(View.VISIBLE);
                    Random rand = new Random();
                    int rand_int = rand.nextInt(1000);
                    addRemoveCart(pro_id, cus_id, flag, timestamp, b_id, b_name, b_mobile, rand_int);
                }

            }
        });
    }

    private void getRating(final String pro_id) {

        progress.showProgressBar();
        StringRequest request = new StringRequest(Request.Method.POST, GET_RATE_URL,
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

                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject object = array.getJSONObject(i);
                                        map = new HashMap<String, String>();

                                        String id = object.getString("pro_id");
                                        String image = object.getString("image");
                                        String product = object.getString("product");
                                        String brand = object.getString("sub_product");
                                        String price = object.getString("price");
                                        String cprice = object.getString("cross_price");
                                        String prate = object.getString("prate");
                                        String trate = object.getString("trate");

                                        if (prate!=null && !trate.isEmpty()){
                                            ratingBar.setRating(Float.parseFloat(prate));
                                            tvTotalRate.setText("("+trate+")"+"\tReview for this Product");
                                        }else {
                                            ratingBar.setRating(0);
                                            tvTotalRate.setText("(" + 0 + ")"+"\tReview for this Product");
                                        }

                                        map.put("id", id);
                                        map.put("product", product);
                                        map.put("brand", brand);
                                        map.put("price", price);
                                        map.put("cross_price", cprice);
                                        map.put("rate", prate);
                                        map.put("trate", trate);
                                        map.put("pro_image", image);
                                    }

                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    progress.hideProgressBar();
                                    // validUtils.showToast(DetailActivity.this, jsonObject.getString("message"));
                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("empty")){
                                    progress.hideProgressBar();


                                    // validUtils.showToast(DetailActivity.this, jsonObject.getString("message"));
                                }
                            }else {
                                progress.hideProgressBar();
                                //validUtils.showToast(DetailActivity.this, "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.hideProgressBar();
                            // validUtils.showToast(DetailActivity.this, e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.hideProgressBar();
                        validUtils.showToast(WishDetailActivity.this, error.getMessage());
                    }
                })
        {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("product_id", pro_id);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(WishDetailActivity.this);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);
    }

    private void getWishFlag(final String cus_id, final String pro_id) {

        progress.showProgressBar();
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
                                    progress.hideProgressBar();
                                    String flag = jsonObject.getString("cart_flag");
                                    if (flag.equalsIgnoreCase("1")) {
                                        btnWish.setText("REMOVE FROM WISHLIST");
                                        Constants.wish="1";
                                    }
                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("empty")){
                                    progress.hideProgressBar();
                                    btnWish.setText("ADD TO WISHLIST");
                                    Constants.wish="0";
                                }
                            }else {
                                progress.hideProgressBar();
                                validUtils.showToast(WishDetailActivity.this, "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.hideProgressBar();
                            validUtils.showToast(WishDetailActivity.this, e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.hideProgressBar();
                        validUtils.showToast(WishDetailActivity.this, error.getMessage());
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
        RequestQueue queue = Volley.newRequestQueue(WishDetailActivity.this);
        queue.add(request);

    }

    private void addRemoveWish(final String pro_id, final String cus_id, final int flag) {

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
                                        .equalsIgnoreCase("Inserted")){
                                    progress.hideProgressBar();
                                    validUtils.showToast(WishDetailActivity.this, jsonObject.getString("message"));
                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("Already")){
                                    progress.hideProgressBar();
                                    validUtils.showToast(WishDetailActivity.this, jsonObject.getString("message"));
                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("Deleted")){
                                    progress.hideProgressBar();
                                    validUtils.showToast(WishDetailActivity.this, jsonObject.getString("message"));
                                }
                            }else {
                                progress.hideProgressBar();
                                validUtils.showToast(WishDetailActivity.this, "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.hideProgressBar();
                            validUtils.showToast(WishDetailActivity.this, e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.hideProgressBar();
                        validUtils.showToast(WishDetailActivity.this, error.getMessage());
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
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(WishDetailActivity.this);
        queue.add(request);
    }

    private void addCart(final String pro_id, final String cus_id, final int flag, final String timestamp, final String b_id, final String b_name, final String b_mobile, final int rand_int) {

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
                                    Intent intent = new Intent(WishDetailActivity.this, CartActivity.class);
                                    startActivity(intent);
                                    Bungee.fade(WishDetailActivity.this);
                                    //validUtils.showToast(DetailActivity.this, jsonObject.getString("message"));
                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("Already")){
                                    progress.hideProgressBar();
                                    validUtils.showToast(WishDetailActivity.this, jsonObject.getString("message"));
                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("Deleted")){
                                    progress.hideProgressBar();
                                    validUtils.showToast(WishDetailActivity.this, jsonObject.getString("message"));
                                }
                            }else {
                                progress.hideProgressBar();
                                validUtils.showToast(WishDetailActivity.this, "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.hideProgressBar();
                            validUtils.showToast(WishDetailActivity.this, e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.hideProgressBar();
                        validUtils.showToast(WishDetailActivity.this, error.getMessage());
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
                params.put("cartid", String.valueOf(rand_int));
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(WishDetailActivity.this);
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
                                        Constants.cart="1";
                                    }
                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("empty")){
                                    progress.hideProgressBar();
                                    btnCart.setText("ADD TO CART");
                                    btnBuy.setVisibility(View.VISIBLE);
                                    Constants.cart="0";
                                }
                            }else {
                                progress.hideProgressBar();
                                validUtils.showToast(WishDetailActivity.this, "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.hideProgressBar();
                            validUtils.showToast(WishDetailActivity.this, e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.hideProgressBar();
                        validUtils.showToast(WishDetailActivity.this, error.getMessage());
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
        RequestQueue queue = Volley.newRequestQueue(WishDetailActivity.this);
        queue.add(request);
    }

    private void addRemoveCart(final String pro_id, final String cus_id, final int flag, final String timestamp, final String b_id, final String b_name, final String b_mobile,  final int rand_int) {

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
                                    validUtils.showToast(WishDetailActivity.this, jsonObject.getString("message"));
                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("Already")){
                                    progress.hideProgressBar();
                                    validUtils.showToast(WishDetailActivity.this, jsonObject.getString("message"));
                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("Deleted")){
                                    progress.hideProgressBar();
                                    validUtils.showToast(WishDetailActivity.this, jsonObject.getString("message"));
                                }
                            }else {
                                progress.hideProgressBar();
                                validUtils.showToast(WishDetailActivity.this, "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.hideProgressBar();
                            validUtils.showToast(WishDetailActivity.this, e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.hideProgressBar();
                        validUtils.showToast(WishDetailActivity.this, error.getMessage());
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
                params.put("cartid", String.valueOf(rand_int));
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(WishDetailActivity.this);
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
            startActivity(new Intent(WishDetailActivity.this, CartActivity.class));
            Bungee.fade(WishDetailActivity.this);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(WishDetailActivity.this);
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
