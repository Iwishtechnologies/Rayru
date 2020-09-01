package com.iwish.rayru.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Toolbar;


import com.google.android.material.navigation.NavigationView;
import com.iwish.rayru.R;
import com.iwish.rayru.adapter.VehicleAdapter;
import com.iwish.rayru.config.Constants;
import com.iwish.rayru.config.JsonHelper;
import com.iwish.rayru.inter.VehicleInterface;
import com.iwish.rayru.model.VehicleCategoryList;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainFragment extends Fragment implements View.OnClickListener {

    LinearLayout dailyLinearLayout, rentalLinearLayout, outstationLinearLayout;
    View view;
    Toolbar upperToolbar;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container,false);

        InitializeActivity();




        Fragment fragment = new DailyFragment(upperToolbar);
        fragmentload(fragment);


        return view;
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void InitializeActivity() {



        dailyLinearLayout = view.findViewById(R.id.dailyLinearLayout);
        rentalLinearLayout = view.findViewById(R.id.rentalLinearLayout);
        outstationLinearLayout = view.findViewById(R.id.outstationLinearLayout);

        upperToolbar = (Toolbar) view.findViewById(R.id.upperToolbar);





        dailyLinearLayout.setOnClickListener(this);
        rentalLinearLayout.setOnClickListener(this);
        outstationLinearLayout.setOnClickListener(this);


    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        Fragment fragment;

        switch (id) {
            case R.id.dailyLinearLayout:
                fragment = new DailyFragment(upperToolbar);
                fragmentload(fragment);
                dailyLinearLayout.setClickable(false);
                rentalLinearLayout.setClickable(true);
                outstationLinearLayout.setClickable(true);
                break;
            case R.id.rentalLinearLayout:
                fragment = new RentalFragment(upperToolbar);
                fragmentload(fragment);
                rentalLinearLayout.setClickable(false);
                dailyLinearLayout.setClickable(true);
                outstationLinearLayout.setClickable(true);
                break;
            case R.id.outstationLinearLayout:
                fragment = new OutStationFragment(upperToolbar);
                fragmentload(fragment);
                outstationLinearLayout.setClickable(false);
                dailyLinearLayout.setClickable(true);
                rentalLinearLayout.setClickable(true);
                break;
        }
    }


    private void fragmentload(Fragment fragment) {
        if (fragment != null)
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragmentLayout, fragment)
                    .addToBackStack(null)
                    .commit();
    }

}