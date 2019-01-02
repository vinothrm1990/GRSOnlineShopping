package com.app.grsonlineshopping.activity;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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
import com.app.grsonlineshopping.helper.Constants;
import com.app.grsonlineshopping.helper.FilePath;
import com.app.grsonlineshopping.helper.GRS;
import com.bumptech.glide.Glide;
import com.libizo.CustomEditText;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import ir.alirezabdn.wp7progress.WP10ProgressBar;
import spencerstudios.com.bungeelib.Bungee;
import thebat.lib.validutil.ValidUtils;

public class ProfileActivity extends AppCompatActivity implements InternetConnectivityListener {

    InternetAvailabilityChecker availabilityChecker;
    TextView tvName, tvPhone, tvEmail, tvAddress;
    CustomEditText etName, etPhone, etEmail, etCity, etState, etPincode, etAddress;
    CircularImageView cvImage;
    public static final int PICK_REQUEST = 1;
    public static final int PERMISSION_CODE = 2;
    Button btnUpdate, btnSave, btnUpload;
    LinearLayout profileViewLayout, profileEditLayout, profileHeaderViewLayout, buttonLayout;
    WP10ProgressBar progress;
    ValidUtils validUtils;
    Uri filePath;
    String path, fname;
    Bitmap bitmap;
    String UPLOAD_PIC_URL = Constants.BASE_URL + Constants.POST_PIC;
    String UPLOAD_PROFILE_URL = Constants.BASE_URL + Constants.POST_PROFILE;
    String GET_PROFILE_URL = Constants.BASE_URL + Constants.GET_PROFILE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("My Profile");
        title.setTextSize(20);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "sans_bold.otf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(title);

        Constants.pref = getSharedPreferences("GRS",Context.MODE_PRIVATE);
        Constants.editor = Constants.pref.edit();

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);
        validUtils = new ValidUtils();

        btnUpdate = findViewById(R.id.profile_btn_update);
        profileEditLayout = findViewById(R.id.profile_edit_layout);
        profileViewLayout = findViewById(R.id.profile_view_layout);
        profileHeaderViewLayout = findViewById(R.id.profile_headerview_layout);
        buttonLayout = findViewById(R.id.button_layout);
        btnSave = findViewById(R.id.profile_btn_save);
        btnUpload = findViewById(R.id.profile_btn_upload);
        progress = findViewById(R.id.profile_progress);
        tvName = findViewById(R.id.profile_name);
        tvEmail = findViewById(R.id.profile_email);
        tvPhone = findViewById(R.id.profile_phone);
        tvAddress = findViewById(R.id.profile_address);
        cvImage = findViewById(R.id.profile_image);
        etName = findViewById(R.id.profile_et_name);
        etPhone = findViewById(R.id.profile_et_phone);
        etEmail = findViewById(R.id.profile_et_email);
        etCity = findViewById(R.id.profile_et_city);
        etState = findViewById(R.id.profile_et_state);
        etPincode = findViewById(R.id.profile_et_pincode);
        etAddress = findViewById(R.id.profile_et_address);

        getProfile();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                profileViewLayout.setVisibility(View.GONE);
                profileHeaderViewLayout.setVisibility(View.GONE);
                buttonLayout.setVisibility(View.GONE);
                profileEditLayout.setVisibility(View.VISIBLE);
                requestPermission();

            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_REQUEST);

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validUtils.validateEditTexts(etName, etPhone, etEmail, etCity, etState, etAddress, etPincode)){
                    saveProfile();
                }else {
                    validUtils.showToast(ProfileActivity.this, "Feilds are Empty");
                }

            }
        });
    }

    private void getProfile() {

        validUtils.showProgressDialog(ProfileActivity.this, ProfileActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, GET_PROFILE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status")
                                    .equalsIgnoreCase("success")){
                                validUtils.hideProgressDialog();

                                String data = jsonObject.getString("message");
                                JSONArray array = new JSONArray(data);
                                JSONObject object = array.getJSONObject(0);

                                String name = object.getString("name");
                                String phone = object.getString("mobile_no");
                                String email = object.getString("email");
                                String city = object.getString("city");
                                String state = object.getString("state");
                                String pincode = object.getString("post_code");
                                String address = object.getString("address1");
                                String image = object.getString("userimage");

                                if (name.equals("") && phone.equals("")
                                        && email.equals("") && city.equals("")
                                        && state.equals("") && pincode.equals("")
                                        && address.equals("")){
                                    tvName.setText("");
                                    tvPhone.setText("");
                                    tvEmail.setText("");
                                    tvAddress.setText("");
                                    etPhone.setText("");
                                    etName.setText("");
                                    etEmail.setText("");
                                    etCity.setText("");
                                    etState.setText("");
                                    etPincode.setText("");
                                    etAddress.setText("");
                                    profileEditLayout.setVisibility(View.VISIBLE);
                                    profileViewLayout.setVisibility(View.GONE);
                                    profileHeaderViewLayout.setVisibility(View.GONE);
                                    buttonLayout.setVisibility(View.GONE);
                                }else {
                                    tvName.setText(name);
                                    tvPhone.setText(phone);
                                    tvEmail.setText(email);
                                    tvAddress.setText(address+","+city+","+state+","+pincode);
                                    etPhone.setText(phone);
                                    etName.setText(name);
                                    etEmail.setText(email);
                                    etCity.setText(city);
                                    etState.setText(state);
                                    etPincode.setText(pincode);
                                    etAddress.setText(address);
                                    profileEditLayout.setVisibility(View.GONE);
                                    profileViewLayout.setVisibility(View.VISIBLE);
                                    profileHeaderViewLayout.setVisibility(View.VISIBLE);
                                    buttonLayout.setVisibility(View.VISIBLE);
                                    Constants.editor.putString("name", object.getString("name"));
                                    Constants.editor.putString("email", object.getString("email"));
                                    Constants.editor.putString("address1", object.getString("address1"));
                                    Constants.editor.putString("state", object.getString("state"));
                                    Constants.editor.putString("city", object.getString("city"));
                                    Constants.editor.putString("pincode", object.getString("post_code"));
                                    Constants.editor.apply();
                                    Constants.editor.commit();
                                }

                                if (image.equals("")){
                                    Glide.with(ProfileActivity.this).load(R.drawable.grs_logo).into(cvImage);
                                }else {
                                    Glide.with(ProfileActivity.this).load(Constants.PROFILE_IMAGE_URL+image).into(cvImage);
                                }

                                //validUtils.showToast(ProfileActivity.this,jsonObject.getString("message"));

                            }else if (jsonObject.getString("status")
                                    .equalsIgnoreCase("failed")){
                                validUtils.hideProgressDialog();
                                validUtils.showToast(ProfileActivity.this, jsonObject.getString("message"));
                            }else if (jsonObject.getString("status")
                                    .equalsIgnoreCase("empty")){
                                validUtils.hideProgressDialog();
                                validUtils.showToast(ProfileActivity.this, jsonObject.getString("message"));
                            }
                            else {
                                validUtils.hideProgressDialog();
                                validUtils.showToast(ProfileActivity.this,"Something Went Wrong!");

                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                            validUtils.hideProgressDialog();
                            validUtils.showToast(ProfileActivity.this, e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        validUtils.hideProgressDialog();
                        validUtils.showToast(ProfileActivity.this, error.getMessage());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                String phone = Constants.pref.getString("mobile", "");
                Map<String, String>  params = new HashMap<String, String>();
                params.put("mobileno", phone);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
        queue.add(request);
    }

    private void saveProfile() {

        validUtils.showProgressDialog(ProfileActivity.this, ProfileActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, UPLOAD_PROFILE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status")
                                    .equalsIgnoreCase("success")){
                                validUtils.hideProgressDialog();
                                profileViewLayout.setVisibility(View.VISIBLE);
                                profileHeaderViewLayout.setVisibility(View.VISIBLE);
                                buttonLayout.setVisibility(View.VISIBLE);
                                profileEditLayout.setVisibility(View.GONE);
                                getProfile();
                                validUtils.showToast(ProfileActivity.this,jsonObject.getString("message"));

                            }else  if (jsonObject.getString("status")
                                    .equalsIgnoreCase("failed")){
                                validUtils.hideProgressDialog();
                                validUtils.showToast(ProfileActivity.this, jsonObject.getString("message"));
                            }
                            else {
                                validUtils.hideProgressDialog();
                                validUtils.showToast(ProfileActivity.this,"Something Went Wrong!");

                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                            validUtils.hideProgressDialog();
                            validUtils.showToast(ProfileActivity.this, e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        validUtils.hideProgressDialog();
                        validUtils.showToast(ProfileActivity.this, error.getMessage());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                String name = etName.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String city = etCity.getText().toString().trim();
                String state = etState.getText().toString().trim();
                String pincode = etPincode.getText().toString().trim();
                String address = etAddress.getText().toString().trim();

                Map<String, String>  params = new HashMap<String, String>();
                params.put("name", name);
                params.put("email", email);
                params.put("city", city);
                params.put("state", state);
                params.put("pincode", pincode);
                params.put("address1", address);
                params.put("mobileno", phone);
                if (path != null && !path.equals("")){
                    fname = path.substring(path.lastIndexOf("/")+1);
                    params.put("userpic", fname);
                }else {
                    params.put("userpic", "");
                }
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
        queue.add(request);
    }

    private void requestPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking the request code of our request
        if (requestCode == PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                validUtils.showToast(ProfileActivity.this, "Permission Granted");
            } else {
                //Displaying another toast if permission is not granted
                validUtils.showToast(ProfileActivity.this, "Permission Denied");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            path = FilePath.getPath(this, filePath);
            if (path != null && !path.equals("")){
                fname = path.substring(path.lastIndexOf("/")+1);
            }
            try{
                InputStream inputStream = getContentResolver().openInputStream(filePath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                cvImage.setImageBitmap(bitmap);
                uploadProfile();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadProfile() {

        progress.showProgressBar();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //creating new thread to handle Http Operations
                uploadImage(path);
            }
        }).start();
    }

    private int uploadImage(final String path) {

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";


        int bytesRead,bytesAvailable,bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(path);


        String[] parts = path.split("/");
        final String fileName = parts[parts.length-1];

        if (!selectedFile.isFile()){
            progress.hideProgressBar();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    validUtils.showToast(ProfileActivity.this, "Source File Doesn't Exist :"+ path);

                }
            });
            return 0;
        }else{
            try{
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(UPLOAD_PIC_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file",path);

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + path + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable,maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer,0,bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0){
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer,0,bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable,maxBufferSize);
                    bytesRead = fileInputStream.read(buffer,0,bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                Log.i("TAG", "Server Response is:" + serverResponseMessage + ":" + serverResponseCode);

                //response code of 200 indicates the server status OK
                if(serverResponseCode == 200){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            validUtils.showToast(ProfileActivity.this, "Profile Image Uploaded :"+ fileName);

                        }
                    });
                }
                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        validUtils.showToast(ProfileActivity.this, "File not Found"+ fileName);

                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();

                validUtils.showToast(ProfileActivity.this, "URL Error"+ fileName);


            } catch (IOException e) {
                e.printStackTrace();

                validUtils.showToast(ProfileActivity.this, "Cannot Read/Write File"+ fileName);

            }
            progress.hideProgressBar();
            return serverResponseCode;
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
            validUtils.showToast(ProfileActivity.this, "Check your Internet Connection!");
        }
    }
}
