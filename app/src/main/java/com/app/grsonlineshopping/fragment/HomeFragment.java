package com.app.grsonlineshopping.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.grsonlineshopping.R;
import com.app.grsonlineshopping.activity.HomeActivity;
import com.app.grsonlineshopping.activity.LoginActivity;
import com.app.grsonlineshopping.activity.ProfileActivity;
import com.app.grsonlineshopping.adapter.DiscoverAdapter;
import com.app.grsonlineshopping.adapter.MobileAdapter;
import com.app.grsonlineshopping.data.CategoryMenu;
import com.app.grsonlineshopping.helper.CircularNetworkImageView;
import com.app.grsonlineshopping.helper.Constants;
import com.app.grsonlineshopping.helper.GRS;
import com.app.grsonlineshopping.helper.ImageCache;
import com.app.grsonlineshopping.helper.PlayStoreUpdate;
import com.app.grsonlineshopping.helper.VersionListener;
import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ir.alirezabdn.wp7progress.WP10ProgressBar;
import thebat.lib.validutil.ValidUtils;

import static com.app.grsonlineshopping.activity.HomeActivity.circularImageView;
import static com.app.grsonlineshopping.activity.HomeActivity.tvName;
import static com.app.grsonlineshopping.activity.HomeActivity.tvPhone;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    SliderLayout sliderLayout;
    ValidUtils validUtils;
    SliderView sliderView;
    RecyclerView rvDiscover, rvMobile;
    ArrayList<HashMap<String,String>> discoverList;
    ArrayList<HashMap<String,String>> mobileList;
    DiscoverAdapter discoverAdapter;
    MobileAdapter mobileAdapter;
    RecyclerView.LayoutManager layoutManager;
    WP10ProgressBar progress;
    List<CategoryMenu> catList;
    HashMap<String, String> map;
    ImageLoader imageLoader;
    String BANNER_URL = Constants.BASE_URL + Constants.GET_BANNER;
    String DISCOVER_URL = Constants.BASE_URL + Constants.GET_DISCOVER;
    String MOBILE_URL = Constants.BASE_URL + Constants.GET_MOBILE;
    String GET_PROFILE_URL = Constants.BASE_URL + Constants.GET_PROFILE;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        validUtils = new ValidUtils();
        progress = view.findViewById(R.id.home_progress);
        rvDiscover = view.findViewById(R.id.rv_discover);
        rvMobile = view.findViewById(R.id.rv_mobile);
        sliderLayout = view.findViewById(R.id.banner_slider);

        sliderLayout.setIndicatorAnimation(SliderLayout.Animations.DROP);
        sliderLayout.setScrollTimeInSec(2);

        setSliderViews();

        catList = new ArrayList<>();
        discoverList = new ArrayList<>();
        discoverAdapter = new DiscoverAdapter(getActivity(), discoverList, catList);
        layoutManager = new GridLayoutManager(getActivity(), 3);
        rvDiscover.setLayoutManager(layoutManager);
        rvDiscover.setAdapter(discoverAdapter);
        getDiscover();
        getMenu();

        mobileList = new ArrayList<>();
        mobileAdapter = new MobileAdapter(getActivity(), mobileList);
        layoutManager = new GridLayoutManager(getActivity(), 3);
        rvMobile.setLayoutManager(layoutManager);
        rvMobile.setAdapter(mobileAdapter);
        getMobile();

        String  cusid = Constants.pref.getString("mobile", "");
        getProfile(cusid);


        return view;
    }

    private void getProfile(final String cusid) {

        StringRequest request = new StringRequest(Request.Method.POST, GET_PROFILE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject != null){

                                if (jsonObject.getString("status")
                                        .equalsIgnoreCase("success")){

                                    String data = jsonObject.getString("message");
                                    JSONArray array = new JSONArray(data);
                                    JSONObject object = array.getJSONObject(0);

                                    String name = object.getString("name");
                                    String phone = object.getString("mobile_no");
                                    String image = object.getString("userimage");

                                    if (image.equals("")){
                                        Glide.with(getActivity()).load(R.drawable.grs_logo).into(circularImageView);
                                    }else {
                                        imageLoader = ImageCache.getInstance(getActivity()).getImageLoader();
                                        imageLoader.get(Constants.PROFILE_IMAGE_URL + image, ImageLoader.getImageListener(circularImageView, R.drawable.ic_image, android.R.drawable.ic_dialog_alert));
                                        circularImageView.setImageUrl(Constants.PROFILE_IMAGE_URL + image, imageLoader);
                                        circularImageView.setScaleType(CircularNetworkImageView.ScaleType.FIT_CENTER);
                                    }

                                    if (name.equals("")){
                                        tvName.setText("Username");
                                        tvPhone.setText("Mobile No");
                                    }else {
                                        tvName.setText(name);
                                        tvPhone.setText(phone);
                                    }

                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    validUtils.showToast(getActivity(), jsonObject.getString("message"));
                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("empty")){
                                    validUtils.showToast(getActivity(), jsonObject.getString("message"));
                                }
                            }else {
                                validUtils.showToast(getActivity(), "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            validUtils.showToast(getActivity(), e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        validUtils.showToast(getActivity(), error.getMessage());
                    }
                })
        {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("mobileno", cusid);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);
    }

    private void getMobile() {

        progress.showProgressBar();
        StringRequest request = new StringRequest(Request.Method.GET, MOBILE_URL,
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
                                    mobileList.clear();
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject object = array.getJSONObject(i);
                                        map = new HashMap<String, String>();

                                        String brand = object.getString("sub_product");

                                        map.put("brand", brand);

                                        mobileList.add(map);

                                    }
                                    mobileAdapter.notifyDataSetChanged();

                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    progress.hideProgressBar();
                                    validUtils.showToast(getActivity(), jsonObject.getString("data"));
                                }
                            }else {
                                progress.hideProgressBar();
                                validUtils.showToast(getActivity(), "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.hideProgressBar();
                            validUtils.showToast(getActivity(), e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.hideProgressBar();
                        validUtils.showToast(getActivity(), error.getMessage());
                    }
                });
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);
    }

    private void getMenu() {

        int [] icons = new int[]{

                R.drawable.mobile,
                R.drawable.mobileaccessories,
                R.drawable.mens,
                R.drawable.womens,
                R.drawable.shoes,
                R.drawable.electronics,
                R.drawable.watches,
                R.drawable.other

        };

        CategoryMenu menu = new CategoryMenu(icons[0]);
        catList.add(menu);
        menu = new CategoryMenu(icons[1]);
        catList.add(menu);
        menu = new CategoryMenu(icons[2]);
        catList.add(menu);
        menu = new CategoryMenu(icons[3]);
        catList.add(menu);
        menu = new CategoryMenu(icons[4]);
        catList.add(menu);
        menu = new CategoryMenu(icons[5]);
        catList.add(menu);
        menu = new CategoryMenu(icons[6]);
        catList.add(menu);
        menu = new CategoryMenu(icons[7]);
        catList.add(menu);
        discoverAdapter.notifyDataSetChanged();

    }

    private void getDiscover() {

        progress.showProgressBar();
        StringRequest request = new StringRequest(Request.Method.GET, DISCOVER_URL,
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
                                    discoverList.clear();
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject object = array.getJSONObject(i);
                                        map = new HashMap<String, String>();

                                        String name = object.getString("cat");
                                        String image_url = object.getString("cat_img_url");
                                        String image_name = object.getString("cat_img_name");

                                        map.put("cat", name);
                                        map.put("cat_img_url", image_url);
                                        map.put("cat_img_name", image_name);

                                        discoverList.add(map);

                                    }
                                    discoverAdapter.notifyDataSetChanged();

                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    progress.hideProgressBar();
                                    validUtils.showToast(getActivity(), jsonObject.getString("data"));
                                }
                            }else {
                                progress.hideProgressBar();
                                validUtils.showToast(getActivity(), "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.hideProgressBar();
                            validUtils.showToast(getActivity(), e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.hideProgressBar();
                        validUtils.showToast(getActivity(), error.getMessage());
                    }
                });
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
    }

    private void setSliderViews() {

        progress.showProgressBar();
        StringRequest request = new StringRequest(Request.Method.GET, BANNER_URL,
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

                                        String image = object.getString("image_url");

                                        for (int j = 0; j <= 4; j++) {
                                            sliderView = new SliderView(getActivity());
                                            sliderView.setImageUrl(image);
                                        }

                                        sliderView.setImageScaleType(ImageView.ScaleType.FIT_XY);
                                        sliderLayout.addSliderView(sliderView);
                                    }


                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    progress.hideProgressBar();
                                    validUtils.showToast(getActivity(), jsonObject.getString("data"));
                                }
                            }else {
                                progress.hideProgressBar();
                                validUtils.showToast(getActivity(), "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.hideProgressBar();
                            validUtils.showToast(getActivity(), e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.hideProgressBar();
                        //validUtils.showToast(LoginActivity.this, error.getMessage());
                    }
                });
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
    }

}
