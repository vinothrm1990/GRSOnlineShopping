package com.app.grsonlineshopping.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.grsonlineshopping.R;
import com.app.grsonlineshopping.helper.Constants;
import com.app.grsonlineshopping.helper.GRS;
import com.libizo.CustomEditText;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ir.alirezabdn.wp7progress.WP10ProgressBar;
import ir.alirezabdn.wp7progress.WP7ProgressBar;
import spencerstudios.com.bungeelib.Bungee;
import thebat.lib.validutil.ValidUtils;

public class LoginActivity extends AppCompatActivity implements InternetConnectivityListener {

    InternetAvailabilityChecker availabilityChecker;
    ValidUtils validUtils;
    CustomEditText etLogPhone, etLogPass, etRegPhone, etRegPass, etRegCPass;
    Button btnLogin, btnRegister;
    TextView tvCreate, tvForgot, tvAlready;
    AlertDialog otpDialog, forgotDialog;
    LinearLayout loginlayout, registerLayout, getOtpLayout, verifyOtpLayout;
    WP10ProgressBar progress, otpProgress, forgotProgress;
    String GET_OTP_URL = Constants.BASE_URL + Constants.GET_OTP;
    String LOGIN_URL = Constants.BASE_URL + Constants.LOGIN;
    String REGISTER_URL = Constants.BASE_URL + Constants.REGISTER;
    String CHECK_URL = Constants.BASE_URL + Constants.CHECK;
    String VERIFY_OTP_URL = Constants.BASE_URL + Constants.VERIFY_OTP;
    String FORGOT_URL = Constants.BASE_URL + Constants.FORGOT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);
        validUtils = new ValidUtils();

        Constants.pref = getApplicationContext().getSharedPreferences("GRS", MODE_PRIVATE);
        Constants.editor = Constants.pref.edit();

        etLogPhone = findViewById(R.id.log_et_mobile);
        etLogPass = findViewById(R.id.log_et_password);
        etRegPhone = findViewById(R.id.reg_et_mobile);
        etRegPass = findViewById(R.id.reg_et_password);
        etRegCPass = findViewById(R.id.reg_et_confirmpassword);
        tvCreate = findViewById(R.id.log_tv_create);
        tvForgot = findViewById(R.id.log_tv_forgot);
        tvAlready = findViewById(R.id.reg_tv_already);
        btnLogin = findViewById(R.id.log_btn_login);
        btnRegister = findViewById(R.id.reg_btn_register);
        progress = findViewById(R.id.log_progress);
        loginlayout = findViewById(R.id.login_layout);
        registerLayout = findViewById(R.id.register_layout);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginlayout.setVisibility(View.VISIBLE);
                registerLayout.setVisibility(View.GONE);

                if (validUtils.validateEditTexts(etLogPhone, etLogPass)) {
                    String phone = etLogPhone.getText().toString().trim();
                    String pass = etLogPass.getText().toString().trim();
                    login(phone, pass);
                } else {
                    validUtils.showToast(LoginActivity.this, "Feilds are Empty");
                }
            }
        });

        tvCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginlayout.setVisibility(View.GONE);
                registerLayout.setVisibility(View.VISIBLE);

                btnRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (validUtils.validateEditTexts(etRegPhone, etRegPass, etRegCPass)) {
                            String phone = etRegPhone.getText().toString().trim();
                            checkRegistered(phone);

                        } else {
                            validUtils.showToast(LoginActivity.this, "Feilds are Empty");
                        }
                    }
                });
            }
        });

        tvAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerLayout.setVisibility(View.GONE);
                loginlayout.setVisibility(View.VISIBLE);
            }
        });

        tvForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                LayoutInflater inflater = LoginActivity.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.forgot_dialog, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setCancelable(false);

                final CustomEditText etPass = dialogView.findViewById(R.id.forgot_et_password);
                Button btnUpdate = dialogView.findViewById(R.id.forgot_btn_update);
                forgotProgress = dialogView.findViewById(R.id.forgot_progress);

                forgotDialog = dialogBuilder.create();

                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (validUtils.validateEditTexts(etPass)) {
                            String pass = etPass.getText().toString().trim();
                            String phone = Constants.pref.getString("mobile", "");
                            forgot(phone, pass);
                        } else {
                            validUtils.showToast(LoginActivity.this, "Password Feilds are Empty");
                        }
                    }
                });

                forgotDialog.show();
            }
        });

    }

    private void checkRegistered(final String phone) {

        progress.showProgressBar();
        StringRequest request = new StringRequest(Request.Method.POST, CHECK_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject != null){

                                if (jsonObject.getString("status")
                                        .equalsIgnoreCase("Already")){
                                    progress.hideProgressBar();
                                    registerLayout.setVisibility(View.GONE);
                                    loginlayout.setVisibility(View.VISIBLE);
                                    validUtils.showToast(LoginActivity.this, jsonObject.getString("message"));

                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("New User")){
                                    progress.hideProgressBar();
                                    String phone = etRegPhone.getText().toString().trim();
                                    String pass = etRegPass.getText().toString().trim();
                                    String cpass = etRegCPass.getText().toString().trim();
                                    if (pass.matches(cpass)){

                                        Constants.editor.putString("pass", cpass);
                                        Constants.editor.apply();
                                        Constants.editor.commit();

                                        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                                        LayoutInflater inflater = LoginActivity.this.getLayoutInflater();
                                        final View dialogView = inflater.inflate(R.layout.otp_dialog, null);
                                        dialogBuilder.setView(dialogView);
                                        dialogBuilder.setCancelable(false);

                                        Button btnGet = dialogView.findViewById(R.id.otp_btn_get);
                                        Button btnVerify = dialogView.findViewById(R.id.otp_btn_verify);
                                        final CustomEditText etPhone = dialogView.findViewById(R.id.otp_et_mobile);
                                        final CustomEditText etOtp = dialogView.findViewById(R.id.otp_et_otp);
                                        getOtpLayout = dialogView.findViewById(R.id.otp_get);
                                        verifyOtpLayout = dialogView.findViewById(R.id.otp_verify);
                                        otpProgress = dialogView.findViewById(R.id.otp_progress);

                                        otpDialog = dialogBuilder.create();

                                        etPhone.setText(phone);

                                        btnGet.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                String phone = etPhone.getText().toString().trim();
                                                Constants.editor.putString("mobile", phone);
                                                Constants.editor.apply();
                                                Constants.editor.commit();
                                                getOtp(phone);

                                            }
                                        });

                                        btnVerify.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                String phone = Constants.pref.getString("mobile", "");
                                                String otp = etOtp.getText().toString().trim();
                                                if (validUtils.validateEditTexts(etOtp)){
                                                    verifyOtp(phone, otp);
                                                }else {
                                                    validUtils.showToast(LoginActivity.this, "OTP Feilds are Empty");
                                                }

                                            }
                                        });

                                        otpDialog.show();
                                    }else {
                                        validUtils.showToast(LoginActivity.this, "Password didn't Match");
                                    }
                                }
                            }else {
                                progress.hideProgressBar();
                                validUtils.showToast(LoginActivity.this, "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.hideProgressBar();
                            validUtils.showToast(LoginActivity.this, e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.hideProgressBar();
                        validUtils.showToast(LoginActivity.this, error.getMessage());
                    }
                })
        {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("mobileno", phone);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(request);
    }

    private void login(final String phone, final String pass) {

        progress.showProgressBar();
        StringRequest request = new StringRequest(Request.Method.POST, LOGIN_URL,
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

                                    String data = jsonObject.getString("message");
                                    JSONArray array = new JSONArray(data);
                                    JSONObject object = array.getJSONObject(0);

                                    Constants.editor.putBoolean("isLogged", true);
                                    Constants.editor.putString("id", object.getString("cus_id"));
                                    Constants.editor.putString("name", object.getString("name"));
                                    Constants.editor.putString("mobile", object.getString("mobile_no"));
                                    Constants.editor.putString("email", object.getString("email"));
                                    Constants.editor.putString("address1", object.getString("address1"));
                                    Constants.editor.putString("state", object.getString("state"));
                                    Constants.editor.putString("city", object.getString("city"));
                                    Constants.editor.putString("pincode", object.getString("post_code"));
                                    Constants.editor.putString("profile", object.getString("userimage"));
                                    Constants.editor.apply();
                                    Constants.editor.commit();

                                    String name = object.getString("name");
                                    String phone = object.getString("mobile_no");
                                    String email = object.getString("email");
                                    String city = object.getString("city");
                                    String state = object.getString("state");
                                    String pincode = object.getString("post_code");
                                    String address = object.getString("address1");

                                    if (name.equals("") && phone.equals("")
                                            && email.equals("") && city.equals("")
                                            && state.equals("") && pincode.equals("")
                                            && address.equals("")){
                                        Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                                        startActivity(intent);
                                        Bungee.fade(LoginActivity.this);
                                    }else {
                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        Bungee.fade(LoginActivity.this);
                                        finish();
                                    }

                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    progress.hideProgressBar();
                                    validUtils.showToast(LoginActivity.this, jsonObject.getString("message"));
                                }
                            }else {
                                progress.hideProgressBar();
                                validUtils.showToast(LoginActivity.this, "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.hideProgressBar();
                            //validUtils.showToast(LoginActivity.this, e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.hideProgressBar();
                        //validUtils.showToast(LoginActivity.this, error.getMessage());
                    }
                })
        {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("mobileno", phone);
                params.put("password", pass);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(request);

    }

    private void getOtp(final String phone) {

        otpProgress.showProgressBar();
        StringRequest request = new StringRequest(Request.Method.POST, GET_OTP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject != null){

                                if (jsonObject.getString("status")
                                        .equalsIgnoreCase("success")){
                                    otpProgress.hideProgressBar();
                                    getOtpLayout.setVisibility(View.GONE);
                                    verifyOtpLayout.setVisibility(View.VISIBLE);
                                    validUtils.showToast(LoginActivity.this, jsonObject.getString("message"));

                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    otpProgress.hideProgressBar();
                                    otpDialog.dismiss();
                                    validUtils.showToast(LoginActivity.this, jsonObject.getString("message"));
                                }
                            }else {
                                otpProgress.hideProgressBar();
                                validUtils.showToast(LoginActivity.this, "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            otpProgress.hideProgressBar();
                            validUtils.showToast(LoginActivity.this, e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        otpProgress.hideProgressBar();
                        validUtils.showToast(LoginActivity.this, error.getMessage());
                    }
                })
        {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("mobile", phone);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(request);
    }

    private void verifyOtp(final String phone, final String otp) {

        otpProgress.showProgressBar();
        StringRequest request = new StringRequest(Request.Method.POST, VERIFY_OTP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject != null){

                                if (jsonObject.getString("status")
                                        .equalsIgnoreCase("success")){
                                    otpProgress.hideProgressBar();
                                    otpDialog.dismiss();
                                    registerLayout.setVisibility(View.GONE);
                                    loginlayout.setVisibility(View.VISIBLE);
                                    register();
                                    validUtils.showToast(LoginActivity.this, jsonObject.getString("message"));

                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    otpProgress.hideProgressBar();
                                    otpDialog.dismiss();
                                    validUtils.showToast(LoginActivity.this, jsonObject.getString("message"));
                                }
                            }else {
                                otpProgress.hideProgressBar();
                                validUtils.showToast(LoginActivity.this, "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            otpProgress.hideProgressBar();
                            validUtils.showToast(LoginActivity.this, e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        otpProgress.hideProgressBar();
                        validUtils.showToast(LoginActivity.this, error.getMessage());
                    }
                })
        {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("mobileno", phone);
                params.put("otp", otp);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(request);

    }

    private void register() {

        progress.showProgressBar();
        StringRequest request = new StringRequest(Request.Method.POST, REGISTER_URL,
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
                                    etLogPhone.setText(Constants.pref.getString("mobile", ""));
                                    etLogPass.setText(Constants.pref.getString("pass", ""));
                                    validUtils.showToast(LoginActivity.this, jsonObject.getString("message"));

                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    progress.hideProgressBar();
                                    validUtils.showToast(LoginActivity.this, jsonObject.getString("message"));
                                }
                            }else {
                                progress.hideProgressBar();
                                validUtils.showToast(LoginActivity.this, "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.hideProgressBar();
                            //validUtils.showToast(LoginActivity.this, e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.hideProgressBar();
                        //validUtils.showToast(LoginActivity.this, error.getMessage());
                    }
                })
        {

            @Override
            protected Map<String, String> getParams()
            {
                String phone = Constants.pref.getString("mobile", "");
                String pass = Constants.pref.getString("pass", "");
                Map<String, String>  params = new HashMap<String, String>();
                params.put("mobileno", phone);
                params.put("password", pass);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(request);
    }

    private void forgot(final String phone, final String pass) {

        forgotProgress.showProgressBar();
        StringRequest request = new StringRequest(Request.Method.POST, FORGOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);

                            if (jsonObject != null){

                                if (jsonObject.getString("status")
                                        .equalsIgnoreCase("success")){
                                    forgotProgress.hideProgressBar();
                                    forgotDialog.dismiss();
                                    validUtils.showToast(LoginActivity.this, jsonObject.getString("message"));

                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    forgotProgress.hideProgressBar();
                                    forgotDialog.dismiss();
                                    validUtils.showToast(LoginActivity.this, jsonObject.getString("message"));
                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("empty")){
                                    forgotProgress.hideProgressBar();
                                    validUtils.showToast(LoginActivity.this, jsonObject.getString("message"));
                                }
                            }else {
                                forgotProgress.hideProgressBar();
                                validUtils.showToast(LoginActivity.this, "Something went wrong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            forgotProgress.hideProgressBar();
                            //validUtils.showToast(LoginActivity.this, e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        forgotProgress.hideProgressBar();
                        //validUtils.showToast(LoginActivity.this, error.getMessage());
                    }
                })
        {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("mobileno", phone);
                params.put("newpassword", pass);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (validUtils.isNetworkAvailable(this)){
            if (Constants.pref.getBoolean("isLogged", false)){
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
                Bungee.fade(LoginActivity.this);
            }
        }else {
            validUtils.showToast(this, "No Internet Connection Available");
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
            validUtils.showToast(LoginActivity.this, "Check your Internet Connection!");
        }
    }
}
