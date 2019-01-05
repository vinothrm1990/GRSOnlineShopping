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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.grsonlineshopping.R;
import com.app.grsonlineshopping.adapter.ProductAdapter;
import com.app.grsonlineshopping.adapter.RateAdapter;
import com.app.grsonlineshopping.helper.Constants;
import com.app.grsonlineshopping.helper.GRS;
import com.app.grsonlineshopping.helper.ImageCache;
import com.libizo.CustomEditText;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import ir.alirezabdn.wp7progress.WP10ProgressBar;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import thebat.lib.validutil.ValidUtils;

public class RateActivity extends AppCompatActivity implements InternetConnectivityListener {

    NetworkImageView imageView;
    TextView tvBrand, tvProduct, tvPrice, tvCrossPrice, tvWrite, tvTotalRate;
    InternetAvailabilityChecker availabilityChecker;
    HashMap<String, String> map;
    ValidUtils validUtils;
    String pro_id, product, brand, image, price, c_price;
    ImageLoader imageLoader;
    RecyclerView rvRate;
    String rate, review, crate, trate;
    MaterialRatingBar ratingBar, totalRatingBar;
    CustomEditText etReview;
    Button btnCancel, btnSubmit;
    LinearLayout reviewlayout, rateLayout;
    WP10ProgressBar progress;
    RateAdapter rateAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<HashMap<String,String>> rateList;
    String RATE_URL = Constants.BASE_URL + Constants.RATING;
    String GET_RATE_URL = Constants.BASE_URL + Constants.GET_RATING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        Constants.pref = getSharedPreferences("GRS",Context.MODE_PRIVATE);
        Constants.editor = Constants.pref.edit();

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);
        validUtils = new ValidUtils();

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("Ratings & Reviews");
        title.setTextSize(20);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "sans_bold.otf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(title);

        map = (HashMap<String, String>)this.getIntent().getSerializableExtra("rating");

        if (map != null && !map.isEmpty()){
            pro_id = map.get("id");
            product = map.get("product");
            brand = map.get("brand");
            image = map.get("pro_image");
            price = map.get("price");
            c_price = map.get("cross_price");
            crate = map.get("rate");
            trate = map.get("trate");
        }

        imageView = findViewById(R.id.rate_iv_image);
        tvBrand = findViewById(R.id.rate_brand);
        tvProduct = findViewById(R.id.rate_product);
        tvPrice = findViewById(R.id.rate_price);
        tvCrossPrice = findViewById(R.id.rate_cross_price);
        rvRate = findViewById(R.id.rv_rate);
        tvWrite = findViewById(R.id.rate_write);
        reviewlayout = findViewById(R.id.rate_write_layout);
        rateLayout = findViewById(R.id.review_layout);
        btnCancel = findViewById(R.id.rate_btn_cancel);
        btnSubmit = findViewById(R.id.rate_btn_submit);
        progress = findViewById(R.id.rate_progress);
        ratingBar = findViewById(R.id.rate_rating_bar);
        etReview = findViewById(R.id.rate_et_review);
        totalRatingBar = findViewById(R.id.rate_total_rating_bar);
        tvTotalRate = findViewById(R.id.rate_total_rate);

        tvBrand.setText(brand);
        tvProduct.setText(product);
        tvPrice.setText("₹"+price);
        tvCrossPrice.setText("₹"+c_price);
        tvCrossPrice.setPaintFlags(tvCrossPrice.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);

        if (crate!=null && !trate.isEmpty()){
            totalRatingBar.setRating(Float.parseFloat(crate));
            tvTotalRate.setText("("+trate+")");
        }else {
            totalRatingBar.setRating(0);
            tvTotalRate.setText("(" + 0 + ")");
        }

        imageLoader = ImageCache.getInstance(RateActivity.this).getImageLoader();
        imageLoader.get(Constants.IMAGE_URL + image, ImageLoader.getImageListener(imageView, R.drawable.ic_image, android.R.drawable.ic_dialog_alert));
        imageView.setImageUrl(Constants.IMAGE_URL + image, imageLoader);
        imageView.setScaleType(NetworkImageView.ScaleType.FIT_CENTER);

        tvWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reviewlayout.setVisibility(View.VISIBLE);
                tvWrite.setVisibility(View.GONE);

                if (validUtils.validateEditTexts(etReview)){

                    btnSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Date now = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                            String timestamp = sdf.format(now);

                            if (reviewlayout.getVisibility() == View.VISIBLE){
                                Float star = ratingBar.getRating();
                                rate = String.valueOf(Math.round(star));
                                review = etReview.getText().toString().trim();
                            }

                            String cus_id = Constants.pref.getString("mobile", "");
                            String cus_name = Constants.pref.getString("name", "");
                            rating(pro_id, cus_id, cus_name, rate, review, timestamp);
                        }
                    });

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            reviewlayout.setVisibility(View.GONE);
                            tvWrite.setVisibility(View.VISIBLE);
                        }
                    });
                }else {
                    validUtils.showToast(RateActivity.this, "Empty Feilds");
                }

            }
        });

        rateList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        rvRate.setLayoutManager(layoutManager);
        getRating(pro_id);
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
                                    rateLayout.setVisibility(View.VISIBLE);
                                    tvWrite.setVisibility(View.VISIBLE);
                                    String data = jsonObject.getString("data");
                                    JSONArray array = new JSONArray(data);
                                    rateList.clear();
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject object = array.getJSONObject(i);
                                        map = new HashMap<String, String>();

                                        String name = object.getString("cus_name");
                                        String rating = object.getString("rating");
                                        String review = object.getString("review");
                                        String date = object.getString("rdate");
                                        String prate = object.getString("prate");
                                        String trate = object.getString("trate");


                                        if (prate!=null && !trate.isEmpty()){
                                            totalRatingBar.setRating(Float.parseFloat(prate));
                                            tvTotalRate.setText("("+trate+")");
                                        }else {
                                            totalRatingBar.setRating(0);
                                            tvTotalRate.setText("(" + 0 + ")");
                                        }

                                        map.put("name", name);
                                        map.put("rating", rating);
                                        map.put("review", review);
                                        map.put("date", date);

                                        rateList.add(map);
                                    }
                                    rateAdapter = new RateAdapter(RateActivity.this, rateList);
                                    rvRate.setAdapter(rateAdapter);
                                    rateAdapter.notifyDataSetChanged();


                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    progress.hideProgressBar();
                                    rateLayout.setVisibility(View.GONE);
                                    validUtils.showToast(RateActivity.this, jsonObject.getString("message"));
                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("empty")){
                                    progress.hideProgressBar();
                                    rateLayout.setVisibility(View.GONE);
                                    validUtils.showToast(RateActivity.this, jsonObject.getString("message"));
                                }
                            }else {
                                progress.hideProgressBar();
                                validUtils.showToast(RateActivity.this, "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.hideProgressBar();
                            validUtils.showToast(RateActivity.this, e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.hideProgressBar();
                        validUtils.showToast(RateActivity.this, error.getMessage());
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
        RequestQueue queue = Volley.newRequestQueue(RateActivity.this);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);

    }

    private void rating(final String pro_id, final String cus_id, final String cus_name, final String rate, final String review, final String timestamp) {

        progress.showProgressBar();
        StringRequest request = new StringRequest(Request.Method.POST, RATE_URL,
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
                                    reviewlayout.setVisibility(View.GONE);
                                    tvWrite.setVisibility(View.VISIBLE);
                                    getRating(pro_id);
                                    validUtils.showToast(RateActivity.this, jsonObject.getString("message"));

                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    progress.hideProgressBar();
                                    validUtils.showToast(RateActivity.this, jsonObject.getString("message"));
                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("Already")){
                                    progress.hideProgressBar();
                                    validUtils.showToast(RateActivity.this, jsonObject.getString("message"));
                                }
                            }else {
                                progress.hideProgressBar();
                                validUtils.showToast(RateActivity.this, "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.hideProgressBar();
                            validUtils.showToast(RateActivity.this, e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.hideProgressBar();
                        validUtils.showToast(RateActivity.this, error.getMessage());
                    }
                })
        {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("customer_id", cus_id);
                params.put("customer_name", cus_name);
                params.put("product_id", pro_id);
                params.put("rate", rate);
                params.put("review", review);
                params.put("timestamp", timestamp);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(RateActivity.this);
        queue.add(request);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(RateActivity.this);
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
