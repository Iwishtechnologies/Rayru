package com.iwish.rayru.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.iwish.rayru.R;
import com.iwish.rayru.fragment.DailyFragment;
import com.iwish.rayru.fragment.MainFragment;
import com.iwish.rayru.fragment.OutStationFragment;
import com.iwish.rayru.fragment.OutStationVehicleFragment;
import com.iwish.rayru.fragment.PickupDialogFragment;
import com.iwish.rayru.fragment.RateCardFragment;
import com.iwish.rayru.fragment.ReferEarnFragment;
import com.iwish.rayru.fragment.RentalFragment;
import com.iwish.rayru.fragment.RideHistoryFragment;
import com.iwish.rayru.fragment.SupportFragmnet;
import com.iwish.rayru.fragment.WalletFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView drawerLayoutIcon;
    boolean doubleBackToExitPressedOnce = false;
    public static final String MAIN_FRAGMENT_BACK_STACK = "mainFragmentback";


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide();

        InitializeActivity();


        MainFragment mainFragment = new MainFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.mainLayoutLoad, mainFragment).commit();

        getSupportFragmentManager().addOnBackStackChangedListener(() -> Toast.makeText(MainActivity.this, "addOnBackStackChangedListener", Toast.LENGTH_SHORT).show());

    }


    private void InitializeActivity() {

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigationview);
        drawerLayoutIcon = findViewById(R.id.drawerLayoutIcon);


        navigation();


        drawerLayoutIcon.setOnClickListener(this);


    }

    private void navigation() {


        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        Fragment fragment = null;
        switch (id) {
            case R.id.nav_home:
                fragment = new MainFragment();
                break;
            case R.id.ride_history:
                fragment = new RideHistoryFragment();
                break;
            case R.id.rate_card:
                fragment = new RateCardFragment();
                break;
            case R.id.refer_earn:
                fragment = new ReferEarnFragment();
                break;
            case R.id.wallet:
                fragment = new WalletFragment();
                break;
            case R.id.support:
                fragment = new SupportFragmnet();
                break;
            case R.id.logout:

                break;
        }
        loadfragment(fragment);
        return true;
    }

    private boolean loadfragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.mainLayoutLoad, fragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        Fragment fragment = null;

        switch (id) {
            case R.id.drawerLayoutIcon:
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {

//            **********************************************************************************

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction bg = fm.beginTransaction();

            Toast.makeText(this, "" + fm.getBackStackEntryCount(), Toast.LENGTH_SHORT).show();



//            **********************************************************************************
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                finish();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }

    }


}
