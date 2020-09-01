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
import com.iwish.rayru.R;
import com.iwish.rayru.activity.DriverInfoActivity;
import com.iwish.rayru.activity.RentalPackageSelectActivity;
import com.iwish.rayru.inter.PickupInterface;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class RentalFragment extends Fragment implements OnMapReadyCallback
        , View.OnClickListener
        , LocationListener
        , PickupInterface, GoogleMap.OnCameraIdleListener {


    FusedLocationProviderClient fusedLocationProviderClient;
    SupportMapFragment supportMapFragment;
    List<Address> currentLocationAll;
    TextView pickupEditText;
    View view;
    GoogleMap googleMaps;
    Toolbar toolbar;
    RelativeLayout mainRelativeLayout;
    Button RentalContious;


    public RentalFragment(Toolbar toolbar) {
        this.toolbar = toolbar;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_rental, null);

        InitializeActivity();
        ActivityAction();

        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.rentalMap);
        if (supportMapFragment == null) {
            FragmentManager fm = getChildFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            supportMapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.rentalMap, supportMapFragment);
        }
        supportMapFragment.getMapAsync(this);

        currentlocationget();

        return view;
    }

    private void ActivityAction() {

        pickupEditText.setOnClickListener(this);
        RentalContious.setOnClickListener(this);

    }

    private void InitializeActivity() {

        pickupEditText = (TextView) view.findViewById(R.id.pickupEditText);
        mainRelativeLayout = (RelativeLayout) view.findViewById(R.id.mainRelativeLayout);
        RentalContious = view.findViewById(R.id.RentalContious);
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


    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.pickupEditText:
                loadPickupdialog();
                break;
            case R.id.RentalContious:
                Intent intent = new Intent(getContext() , RentalPackageSelectActivity.class);
                intent.putExtra("PLat" , String.valueOf(currentLocationAll.get(0).getLatitude()));
                intent.putExtra("PDrop" , String.valueOf(currentLocationAll.get(0).getLongitude()));
                intent.putExtra("Area" , String.valueOf(currentLocationAll.get(0).getLocality()));
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMaps = googleMap;
        googleMap.setOnCameraIdleListener(this);
    }

    private void loadPickupdialog() {
        toolbar.setVisibility(View.GONE);
        mainRelativeLayout.setVisibility(View.GONE);
        getChildFragmentManager().beginTransaction().replace(R.id.dialogLoad, new PickupDialogFragment(currentLocationAll, this)).commit();
    }





    @Override
    public void pickupinterface(String add) {
        toolbar.setVisibility(View.VISIBLE);
        mainRelativeLayout.setVisibility(View.VISIBLE);
        Intent intent = new Intent(getContext() , RentalPackageSelectActivity.class);
        intent.putExtra("PLat" , String.valueOf(currentLocationAll.get(0).getLatitude()));
        intent.putExtra("PDrop" , String.valueOf(currentLocationAll.get(0).getLongitude()));
        intent.putExtra("Area" , String.valueOf(currentLocationAll.get(0).getLocality()));
        startActivity(intent);

    }

    @Override
    public void onCameraIdle() {
        centerLocation();
        pickupEditText.setText(currentLocationAll.get(0).getAddressLine(0));
    }

    private void centerLocation() {

        LatLng center = googleMaps.getCameraPosition().target;
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try {
            currentLocationAll = geocoder.getFromLocation(center.latitude, center.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}















