package com.iwish.rayru.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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

import android.widget.ImageView;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iwish.rayru.R;
import com.iwish.rayru.activity.BillActivity;
import com.iwish.rayru.activity.DriverInfoActivity;

import com.iwish.rayru.adapter.VehicleAdapter;
import com.iwish.rayru.config.Constants;
import com.iwish.rayru.config.JsonHelper;
import com.iwish.rayru.inter.DropInterface;
import com.iwish.rayru.inter.PickupInterface;
import com.iwish.rayru.inter.VehicleInterface;
import com.iwish.rayru.model.VehicleCategoryList;

import com.iwish.rayru.other.DirectionsJSONParser;
import com.iwish.rayru.other.Session;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.iwish.rayru.config.Constants.DRIVER_ID;
import static com.iwish.rayru.config.Constants.DRIVER_NAVIGATION;
import static com.iwish.rayru.config.Constants.USER_CONTACT;

public class DailyFragment extends Fragment implements OnMapReadyCallback
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
    Session session;
    ArrayList<LatLng> points;
    Map data;
    DriverInfoBottomFragment driverInfoBottomFragment = new DriverInfoBottomFragment();

    ImageView redPin;
    FirebaseDatabase firebaseDatabase;
    LatLng origin, dest;


    public DailyFragment(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_daily, null);

        InitializeActivity();
        ActivityAction();
//        Maps
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.dailyMap);
        if (supportMapFragment == null) {
            FragmentManager fm = getChildFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            supportMapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.dailyMap, supportMapFragment);
        }
        supportMapFragment.getMapAsync(this);

        currentlocationget();

        clickAbleSet();
        AcceptRequest();
//        driverOnlinecheck();
        if (data.get(DRIVER_NAVIGATION) != null) {
            DriverConfirem();
            getChildFragmentManager().beginTransaction().replace(R.id.dialogLoad, driverInfoBottomFragment).commit();
            toolbar.setVisibility(View.GONE);
            bottom_vehile.setVisibility(View.GONE);
            pickupEditText.setVisibility(View.GONE);
            dropTextView.setVisibility(View.GONE);
//            redPin.setVisibility(View.GONE);

        }

        if (data.get(DRIVER_ID) != null) otpVerfication();
        if (data.get(DRIVER_ID) != null) EndTrip();


        return view;
    }

    private void EndTrip() {

        DatabaseReference endTrip = FirebaseDatabase.getInstance().getReference("ENDTrip").child(data.get(DRIVER_ID).toString());
        endTrip.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                startActivity(new Intent(getActivity(), BillActivity.class));

                endTrip.removeValue();
                googleMaps.clear();
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

    private void otpVerfication() {

        DatabaseReference otpVerification = FirebaseDatabase.getInstance().getReference("OTPVarification").child(data.get(DRIVER_ID).toString());
        otpVerification.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                session.RemoveDriverNavigationSet();
                getChildFragmentManager().beginTransaction().remove(driverInfoBottomFragment).commitAllowingStateLoss();
                session.RideStart();
                otpVerification.removeValue();
                googleMaps.clear();


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

    private void DriverConfirem() {

        DatabaseReference DriverLocation = FirebaseDatabase.getInstance().getReference("DriverOnline").child(data.get(DRIVER_ID).toString());

        DriverLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() != 0) {
                    LatLng pick = new LatLng(currentLocationAll.get(0).getLatitude(), currentLocationAll.get(0).getLongitude());
                    LatLng drop = new LatLng(Double.parseDouble(snapshot.child("Lat").getValue().toString()), Double.parseDouble(snapshot.child("Long").getValue().toString()));
                    drawRoute(pick, drop);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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
                        if (currentLocationAll.get(0).getLocality() != null) {
                            Log.e("area", currentLocationAll.get(0).getLocality());
                            if (area.equals(currentLocationAll.get(0).getLocality())) {
                                RideNowBtn.setEnabled(true);
                                break;
                            } else {
//                            RideNowBtn.setEnabled(false);
                            }
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

        DatabaseReference acceptRequest = FirebaseDatabase.getInstance().getReference("AcceptRequest").child(data.get(USER_CONTACT).toString());

        acceptRequest.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Log.e("snapshot.getChildrenCou", String.valueOf(snapshot.getChildrenCount()));

                String DriverId = String.valueOf(snapshot.child("DriverID").getValue());
                String DriverLat = String.valueOf(snapshot.child("DriverLat").getValue());
                String DriverLong = String.valueOf(snapshot.child("DriverLong").getValue());

                String Plat = String.valueOf(snapshot.child("Plat").getValue());
                String Plong = String.valueOf(snapshot.child("Plong").getValue());
                String Dlat = String.valueOf(snapshot.child("Dlat").getValue());
                String Dlong = String.valueOf(snapshot.child("Dlong").getValue());
                String VehicleType = String.valueOf(snapshot.child("VehicleType").getValue());
                String TrackID = String.valueOf(snapshot.child("TrackId").getValue());
                String bookingType = String.valueOf(snapshot.child("bookingType").getValue());


                String type = String.valueOf(snapshot.child("Type").getValue());
                if (type.equals("daily")) {
                    Intent intent = new Intent(getActivity(), DriverInfoActivity.class);
                    intent.putExtra("DriverId", DriverId);
                    intent.putExtra("DriverLat", DriverLat);
                    intent.putExtra("DriverLong", DriverLong);
                    intent.putExtra("Plat", Plat);
                    intent.putExtra("Plong", Plong);
                    intent.putExtra("Dlat", Dlat);
                    intent.putExtra("Dlong", Dlong);
                    intent.putExtra("TrackID", TrackID);
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
//        redPin = (ImageView) view.findViewById(R.id.redPin);

        RideNowBtn = view.findViewById(R.id.RideNowBtn);

        vehicleCategoryRecyclerView = view.findViewById(R.id.vehicleCategoryRecyclerView);


        kProgressHUD = new KProgressHUD(Objects.requireNonNull(getActivity()));
        session = new Session(getActivity());
        data = session.getShare();

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
                if (getActivity() != null)
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(getContext(), "Connection Time Out", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = Objects.requireNonNull(response.body()).string();
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
                    setProgressDialog("");
                    RequestSend();
                }
                break;
        }
    }

    private void RequestSend() {


        DatabaseReference zonesRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference req = zonesRef.child("VehicleRequest").child(Objects.requireNonNull(data.get(USER_CONTACT)).toString());
        req.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();
                if (count == 0) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("RequestType", "Daily");
                    map.put("VehicleType", "MINI");
                    map.put("PickLat", String.valueOf(currentLocationAll.get(0).getLatitude()));
                    map.put("PickLong", String.valueOf(currentLocationAll.get(0).getLongitude()));
                    map.put("DropLat", String.valueOf(dropLocationAll.get(0).getLatitude()));
                    map.put("DropLong", String.valueOf(dropLocationAll.get(0).getLongitude()));
                    map.put("Area", currentLocationAll.get(0).getLocality());
                    map.put("ClientId", data.get(USER_CONTACT).toString());
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference url = firebaseDatabase.getReference("VehicleRequest").child(data.get(USER_CONTACT).toString()).push();
                    url.setValue(map);

//                    other method

//                    Firebase.setAndroidContext(getActivity());
//                    Firebase urls = new Firebase("https://rayru-907f7.firebaseio.com/VehicleRequest");
//                    urls.child("RequestType").push().setValue("ss");
//                    urls.child("VehicleType").push().setValue("ss");
//                    urls.child("PickLat").push().setValue("ss");
//                    urls.child("PickLong").push().setValue("ss");
//                    urls.child("DropLat").push().setValue("ss");
//                    urls.child("DropLong").push().setValue("ss");
//                    urls.child("Area").push().setValue("ss");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.e("onDataChange: ", "onCancelled");
            }
        });


    }

    private void loadPickupdialog() {
        clickCheck = "PickupClick";
        toolbar.setVisibility(View.GONE);
        mainRelativeLayout.setVisibility(View.GONE);
        bottom_vehile.setVisibility(View.GONE);
        getChildFragmentManager().beginTransaction().add(R.id.dialogLoad, new PickupDialogFragment(currentLocationAll, this)).addToBackStack(null).commit();
    }

    private void loadDropdialog() {
        clickCheck = "DropClick";
        toolbar.setVisibility(View.GONE);
        mainRelativeLayout.setVisibility(View.GONE);
        bottom_vehile.setVisibility(View.GONE);
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.dialogLoad, new DropDialogFragment(currentLocationAll, this))
                .addToBackStack(null)
                .commit();
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


//    ***************************************************************************************************

    private String getUrl(LatLng origin, LatLng dest) {


        //  String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters + "&key=" + MY_API_KEY
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web SocketService
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web SocketService
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters
                + "&key=" + "AIzaSyApe8T3JMiqj9OgEbqd--zTBfl3fibPeEs";


        return url;
    }

    public void drawRoute(LatLng pickup, LatLng drop) {
        googleMaps.clear();
        double Lati;
        double Logi;


  /*      if (userSession.getLocationStatus()) {
            Lati = Double.valueOf(userSession.getdroplat());
            Logi = Double.valueOf(userSession.getdroplong());
            origin = new LatLng(userSession.getLatitute(), userSession.getLogitute());
            dest = new LatLng(Lati, Logi);
            Source(Lati, Logi, "drop");
        } else {
            switch (userSession.getBookingtype()) {
                case "daily":
                    Lati = Double.valueOf(userSession.getpiclat());
                    Logi = Double.valueOf(userSession.getpiclong());
                    origin = new LatLng(userSession.getLatitute(), userSession.getLogitute());
                    dest = new LatLng(Lati, Logi);
                    Source(Lati, Logi, "drop");
                    break;

                case "rental":
                    Lati = Double.valueOf(userSession.getRentaldetail().get("rentalpiclat"));
                    Logi = Double.valueOf(userSession.getRentaldetail().get("rentalpiclong"));
                    origin = new LatLng(userSession.getLatitute(), userSession.getLogitute());
                    dest = new LatLng(Lati, Logi);
                    Source(Lati, Logi, "drop");
                    break;

                case "OutStation":
                    Lati = Double.valueOf(userSession.getpiclat());
                    Logi = Double.valueOf(userSession.getpiclong());
                    origin = new LatLng(userSession.getLatitute(), userSession.getLogitute());
                    dest = new LatLng(Lati, Logi);
                    Source(Lati, Logi, "drop");
                    break;


            }

        }

        if (!(userSession.getBookingtype() == "rental")) {
            // Getting URL to the Google Directions API
            String url = getUrl(origin, dest);
            Log.d("onMapClick", url.toString());
            FetchUrl FetchUrl = new FetchUrl();

            // Start downloading json data from Google Directions API
            FetchUrl.execute(url);
            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            startTrack = true;
        }
*/

//        DistanceTimeCalculate distanceTimeCalculate =new DistanceTimeCalculate(Lati,Logi,mLastLocation.getLatitude(),mLastLocation.getLongitude());


//        origin = new LatLng(26.236285, 78.179939);
//        dest = new LatLng(26.236285, 79.179939);
        origin = pickup;
        dest = drop;
        String url = getUrl(origin, dest);
        FetchUrl FetchUrl = new FetchUrl();
        FetchUrl.execute(url);

    }


    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web SocketService
            String data = "";

            try {
                // Fetching the data from web SocketService
                data = downloadUrl(url[0]);
                Log.e("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);


        }
    }

    private static String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DirectionsJSONParser parser = new DirectionsJSONParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            //  ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.BLUE);
                Log.d("onPostExecute", "onPostExecute lineoptions decoded");

            }
            Log.d("lineoption", String.valueOf(lineOptions));
            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                googleMaps.addPolyline(lineOptions).remove();
                googleMaps.addPolyline(lineOptions);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }


//    ***************************************************************************************************

}
























