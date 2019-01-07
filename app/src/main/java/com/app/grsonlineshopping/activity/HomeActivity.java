package com.app.grsonlineshopping.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.grsonlineshopping.R;
import com.app.grsonlineshopping.fragment.HomeFragment;
import com.app.grsonlineshopping.helper.CircularNetworkImageView;
import com.app.grsonlineshopping.helper.Constants;
import com.app.grsonlineshopping.helper.GRS;
import com.app.grsonlineshopping.helper.PlayStoreUpdate;
import com.app.grsonlineshopping.helper.VersionListener;
import com.app.grsonlineshopping.navigation.AboutActivity;
import com.app.grsonlineshopping.navigation.BagActivity;
import com.app.grsonlineshopping.navigation.ContactActivity;
import com.app.grsonlineshopping.navigation.OrderActivity;
import com.app.grsonlineshopping.navigation.TermActivity;
import com.app.grsonlineshopping.navigation.WishlistActivity;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import spencerstudios.com.bungeelib.Bungee;
import thebat.lib.validutil.ValidUtils;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, VersionListener, InternetConnectivityListener {

    ValidUtils validUtils;
    InternetAvailabilityChecker availabilityChecker;
    android.app.AlertDialog logoutDialog;
    public static CircularNetworkImageView circularImageView;
    public static TextView tvName, tvPhone;
    boolean isForceUpdate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Constants.pref = getSharedPreferences("GRS", Context.MODE_PRIVATE);
        Constants.editor = Constants.pref.edit();

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("GRS Online Shopping");
        title.setTextSize(20);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "sans_bold.otf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(title);

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);
        validUtils = new ValidUtils();

        new PlayStoreUpdate(this, this).execute();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, new HomeFragment());
        fragmentTransaction.addToBackStack(null).commit();

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);
        circularImageView = headerLayout.findViewById(R.id.nav_iv);
        tvName = headerLayout.findViewById(R.id.nav_name);
        tvPhone = headerLayout.findViewById(R.id.nav_phone);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (getSupportFragmentManager().getBackStackEntryCount() == 1){

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
            alertDialog.setTitle("Exit GRS :");
            alertDialog.setMessage("Are you sure you want to Exit?");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    System.exit(0);
                }
            });
            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(@NonNull DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cart) {
            startActivity(new Intent(HomeActivity.this, CartActivity.class));
            Bungee.fade(HomeActivity.this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, new HomeFragment());
            fragmentTransaction.addToBackStack(null).commit();
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            Bungee.slideRight(HomeActivity.this);
        }else if (id == R.id.nav_wishlist) {
            startActivity(new Intent(HomeActivity.this, WishlistActivity.class));
            Bungee.slideRight(HomeActivity.this);
        } else if (id == R.id.nav_order) {
            startActivity(new Intent(HomeActivity.this, OrderActivity.class));
            Bungee.slideRight(HomeActivity.this);
        } else if (id == R.id.nav_cart) {
            startActivity(new Intent(HomeActivity.this, CartActivity.class));
            Bungee.slideRight(HomeActivity.this);
        } else if (id == R.id.nav_bag) {
            startActivity(new Intent(HomeActivity.this, BagActivity.class));
            Bungee.slideRight(HomeActivity.this);
        } else if (id == R.id.nav_contact) {
            startActivity(new Intent(HomeActivity.this, ContactActivity.class));
            Bungee.slideRight(HomeActivity.this);
        }else if (id == R.id.nav_term) {
            startActivity(new Intent(HomeActivity.this, TermActivity.class));
            Bungee.slideRight(HomeActivity.this);
        }else if (id == R.id.nav_about) {
            startActivity(new Intent(HomeActivity.this, AboutActivity.class));
            Bungee.slideRight(HomeActivity.this);
        }else if (id == R.id.nav_logout) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {

        final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.logout_dialog, null);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle("Logout :");
        Button btnyes = dialogView.findViewById(R.id.btn_yes_logout);
        Button btnno = dialogView.findViewById(R.id.btn_no_logout);

        logoutDialog = dialogBuilder.create();

        btnyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.editor.clear();
                Constants.editor.apply();
                Constants.editor.commit();
                finish();
                Intent p = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(p);
            }
        });
        btnno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                logoutDialog.dismiss();
            }
        });

        logoutDialog.show();
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

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        if (!isConnected){
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setTitle("Network Error");
            builder.setMessage("Can't Retrieve Data, due to Network Connection");
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
    public void onGetResponse(boolean isUpdateAvailable) {
        Log.e("ResultAPPMAIN", String.valueOf(isUpdateAvailable));
        if (isUpdateAvailable) {
            showUpdateDialog();
        }
    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle(HomeActivity.this.getString(R.string.app_name));
        builder.setMessage("Update for GRS is available in Play Store, Please Update it..");
        builder.setCancelable(false);
        builder.setPositiveButton("Update Now", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                HomeActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isForceUpdate) {
                    finish();

                }
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
