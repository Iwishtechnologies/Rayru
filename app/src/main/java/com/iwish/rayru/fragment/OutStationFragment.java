package com.iwish.rayru.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iwish.rayru.R;
import com.iwish.rayru.activity.DriverInfoActivity;
import com.iwish.rayru.adapter.VehicleAdapter;
import com.iwish.rayru.config.Constants;
import com.iwish.rayru.config.JsonHelper;
import com.iwish.rayru.inter.DropInterface;
import com.iwish.rayru.inter.PickupInterface;
import com.iwish.rayru.inter.VehicleInterface;
import com.iwish.rayru.model.VehicleCategoryList;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class OutStationFragment extends Fragment implements OnMapReadyCallback
        , View.OnClickListener
        , LocationListener
        , GoogleMap.OnCameraIdleListener
        , PickupInterface
        , DropInterface {

    View view;
    SupportMapFragment supportMapFragment;
    TextView pickupEditText, dropTextView;
    RelativeLayout mainRelativeLayout, bottom_vehile;
    Toolbar toolbar;
    FusedLocationProviderClient fusedLocationProviderClient;
    GoogleMap googleMaps;
    List<Address> currentLocationAll;
    List<Address> dropLocationAll;
    String clickCheck = "", vehicleName;
    RecyclerView vehicleCategoryRecyclerView;
    List<VehicleCategoryList> vehicleCategoryLists = new ArrayList<>();
    VehicleInterface vehicleInterface;
    Button RideNowBtn;
    KProgressHUD kProgressHUD;
    public  static String FragmentTag = "OutStaionFrgment";
    FirebaseDatabase firebaseDatabase;


    public OutStationFragment(Toolbar upperToolbar) {
        this.toolbar = upperToolbar;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_out_station, null);


        InitializeActivity();
        ActivityAction();
//        Maps
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.outstationMap);
        if (supportMapFragment == null) {
            FragmentManager fm = getChildFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            supportMapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.outstationMap, supportMapFragment);
        }
        supportMapFragment.getMapAsync(this);

        driverOnlinecheck();
        currentlocationget();
        clickAbleSet();
        AcceptRequest();


        return view;
    }


    private void driverOnlinecheck() {

        //        Driver online check
        DatabaseReference acceptRequest = FirebaseDatabase.getInstance().getReference("DriverOnline");
        acceptRequest.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                        String area = String.valueOf(dataSnapshot2.child("Area").getValue());
                        if (area.equals(currentLocationAll.get(0).getLocality())) {
                            RideNowBtn.setEnabled(true);
                            break;
                        } else {
//                            RideNowBtn.setEnabled(false);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void AcceptRequest() {

        DatabaseReference acceptRequest = FirebaseDatabase.getInstance().getReference("AcceptRequest").child("8871121949");

        acceptRequest.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Log.e("snapshot.getChildrenCou", String.valueOf(snapshot.getChildrenCount()));

                String DriverId = String.valueOf(snapshot.child("DriverID").getValue());
                String DriverLat = String.valueOf(snapshot.child("DLat").getValue());
                String DriverLong = String.valueOf(snapshot.child("DLong").getValue());
                String type = String.valueOf(snapshot.child("Type").getValue());
                if (type.equals("daily")) {
                    Intent intent = new Intent(getActivity(), DriverInfoActivity.class);
                    intent.putExtra("DriverId", DriverId);
                    intent.putExtra("DriverLat", DriverLat);
                    intent.putExtra("DriverLong", DriverLong);
                    startActivity(intent);
                    remove_progress_Dialog();
                    acceptRequest.removeValue();
                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void currentlocationget() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLoction();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

    }

    private void getLoction() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                    try {
                        currentLocationAll = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        pickupEditText.setText(currentLocationAll.get(0).getAddressLine(0));
                        LatLng currentLocation = new LatLng(currentLocationAll.get(0).getLatitude(), currentLocationAll.get(0).getLongitude());
//                        default current location set
                        MarkerOptions markerOptions = new MarkerOptions().position(currentLocation).title("i m here").visible(false);
                        googleMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16), 100, null);
                        googleMaps.addMarker(markerOptions);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }
        });

    }

    private void ActivityAction() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        vehicleCategoryRecyclerView.setLayoutManager(linearLayoutManager);

        VehicleSet();

        vehicleInterface = nameVehicle -> vehicleName = nameVehicle;


    }

    private void InitializeActivity() {
        pickupEditText = (TextView) view.findViewById(R.id.pickupEditText);
        dropTextView = (TextView) view.findViewById(R.id.dropTextView);
        mainRelativeLayout = view.findViewById(R.id.mainRelativeLayout);
        bottom_vehile = view.findViewById(R.id.bottom_vehile);

        RideNowBtn = view.findViewById(R.id.RideNowBtn);

        vehicleCategoryRecyclerView = view.findViewById(R.id.vehicleCategoryRecyclerView);


        kProgressHUD = new KProgressHUD(Objects.requireNonNull(getActivity()));
        RideNowBtn.setOnClickListener(this);

    }

    private void VehicleSet() {

        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("PickLatitude", "");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request1 = new Request.Builder().url(Constants.VEHICLE_CATEGORY).post(body).build();


        okHttpClient.newCall(request1).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "Connection Time Out", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    Log.e("result", result);
                    JsonHelper jsonHelper = new JsonHelper(result);
                    if (jsonHelper.isValidJson()) {
                        String responses = jsonHelper.GetResult("response");
                        if (responses.equals("TRUE")) {

                            JSONArray jsonArray = jsonHelper.setChildjsonArray(jsonHelper.getCurrentJsonObj(), "data");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonHelper.setChildjsonObj(jsonArray, i);
                                vehicleCategoryLists.add(new VehicleCategoryList(
                                        jsonHelper.GetResult("catagory_id")
                                        , jsonHelper.GetResult("catagory_name")
                                        , jsonHelper.GetResult("MinRate")
                                        , jsonHelper.GetResult("Rate_Km")
                                        , jsonHelper.GetResult("waitingRate_m")
                                        , jsonHelper.GetResult("rtc_m")
                                        , jsonHelper.GetResult("img")
                                ));
                            }

                            if (getActivity() != null) {

                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        vehicleCategoryRecyclerView.setAdapter(new VehicleAdapter(vehicleCategoryLists, vehicleInterface));
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });


    }

    private void clickAbleSet() {

        pickupEditText.setOnClickListener(this);
        dropTextView.setOnClickListener(this);

    }

//     current location get

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMaps = googleMap;
        googleMap.setOnCameraIdleListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.pickupEditText:
                if (clickCheck.isEmpty() || !clickCheck.equals("DropClick")) loadPickupdialog();
                else {
                    clickCheck = "PickupClick";
                    zoomlocation(new LatLng(currentLocationAll.get(0).getLatitude(), currentLocationAll.get(0).getLongitude()));
                }
                break;
            case R.id.dropTextView:
                if (clickCheck.isEmpty()) loadDropdialog();
                else if (clickCheck.equals("PickupClick")) {
                    clickCheck = "DropClick";
                    if (dropTextView.getText().toString().equals("Enter drop location"))
                        loadDropdialog();
                    zoomlocation(new LatLng(dropLocationAll.get(0).getLatitude(), dropLocationAll.get(0).getLongitude()));
                } else {
                    loadDropdialog();
                }

                break;
            case R.id.RideNowBtn:
                if (dropLocationAll == null) loadDropdialog();
                else {
                    String backStateName = new OutStationVehicleFragment(currentLocationAll , dropLocationAll).getClass().getName();
                    toolbar.setVisibility(View.GONE);
                    getFragmentManager().beginTransaction().add(R.id.dialogLoad , new OutStationVehicleFragment(currentLocationAll , dropLocationAll) , FragmentTag).addToBackStack(backStateName).commit();
                    RideNowBtn.setVisibility(View.GONE);
                }
                break;
        }
    }



    private void loadPickupdialog() {
        clickCheck = "PickupClick";
        toolbar.setVisibility(View.GONE);
        mainRelativeLayout.setVisibility(View.GONE);
        bottom_vehile.setVisibility(View.GONE);
        getChildFragmentManager().beginTransaction().replace(R.id.dialogLoad, new PickupDialogFragment(currentLocationAll, this)).commit();
    }

    private void loadDropdialog() {
        clickCheck = "DropClick";
        toolbar.setVisibility(View.GONE);
        mainRelativeLayout.setVisibility(View.GONE);
        bottom_vehile.setVisibility(View.GONE);
        getChildFragmentManager().beginTransaction().replace(R.id.dialogLoad, new DropDialogFragment(currentLocationAll, this)).commit();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onCameraIdle() {
        switch (clickCheck) {
            case "PickupClick":
                PickupClick();
                break;
            case "DropClick":
                DropClick();
                break;
            default:
                centerLocation();
                pickupEditText.setText(currentLocationAll.get(0).getAddressLine(0));
                break;
        }

    }

    private void DropClick() {
//        Toast.makeText(getActivity(), "DropClick", Toast.LENGTH_SHORT).show();
        centerLocation();
        dropTextView.setText(dropLocationAll.get(0).getAddressLine(0));
    }

    private void PickupClick() {
//        Toast.makeText(getActivity(), "PickupClick", Toast.LENGTH_SHORT).show();
        centerLocation();
        pickupEditText.setText(currentLocationAll.get(0).getAddressLine(0));
    }

    private void centerLocation() {

        LatLng center = googleMaps.getCameraPosition().target;
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

        switch (clickCheck) {
            case "PickupClick":
                try {
                    currentLocationAll = geocoder.getFromLocation(center.latitude, center.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "DropClick":
                try {
                    dropLocationAll = geocoder.getFromLocation(center.latitude, center.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void pickupinterface(String add) {

        mainRelativeLayout.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);
        bottom_vehile.setVisibility(View.VISIBLE);
        pickupEditText.setText(add);
        giveAddress(add);

    }

    @Override
    public void dropInterface(String add) {
        mainRelativeLayout.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);
        bottom_vehile.setVisibility(View.VISIBLE);
        dropTextView.setText(add);
        giveAddress(add);
    }

    private void giveAddress(String des) {

        Geocoder geocoder = new Geocoder(getActivity());
        switch (clickCheck) {
            case "PickupClick":
                try {
                    List<Address> addresses = geocoder.getFromLocationName(des, 1);
                    currentLocationAll = addresses;
                    zoomlocation(new LatLng(currentLocationAll.get(0).getLatitude(), currentLocationAll.get(0).getLongitude()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "DropClick":
                try {
                    List<Address> addresses = geocoder.getFromLocationName(des, 1);
                    dropLocationAll = addresses;
                    zoomlocation(new LatLng(dropLocationAll.get(0).getLatitude(), dropLocationAll.get(0).getLongitude()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }

    }

    private void zoomlocation(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("i m here").visible(false);
        googleMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16), 100, null);
        googleMaps.addMarker(markerOptions);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setProgressDialog(String msg) {
        kProgressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(msg)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

    }

    public void remove_progress_Dialog() {
        kProgressHUD.dismiss();
    }


}























