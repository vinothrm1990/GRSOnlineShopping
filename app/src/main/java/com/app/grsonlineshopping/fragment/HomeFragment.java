package com.app.grsonlineshopping.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.grsonlineshopping.R;
import com.app.grsonlineshopping.activity.LoginActivity;
import com.app.grsonlineshopping.adapter.DiscoverAdapter;
import com.app.grsonlineshopping.helper.Constants;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ir.alirezabdn.wp7progress.WP10ProgressBar;
import thebat.lib.validutil.ValidUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    SliderLayout sliderLayout;
    ValidUtils validUtils;
    SliderView sliderView;
    RecyclerView rvDiscover;
    ArrayList<HashMap<String,String>> discoverList;
    DiscoverAdapter discoverAdapter;
    RecyclerView.LayoutManager layoutManager;
    WP10ProgressBar progress;
    HashMap<String, String> map;
    String BANNER_URL = Constants.BASE_URL + Constants.GET_BANNER;
    String DISCOVER_URL = Constants.BASE_URL + Constants.GET_DISCOVER;

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
        sliderLayout = view.findViewById(R.id.banner_slider);
        sliderLayout.setIndicatorAnimation(SliderLayout.Animations.DROP);
        sliderLayout.setScrollTimeInSec(2);

        setSliderViews();

        discoverList = new ArrayList<>();
        discoverAdapter = new DiscoverAdapter(getActivity(), discoverList);
        layoutManager = new GridLayoutManager(getActivity(), 2);
        rvDiscover.setLayoutManager(layoutManager);
        rvDiscover.setAdapter(discoverAdapter);
        getDiscover();
        return view;
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
