package com.iwish.rayru.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.ValueEventListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iwish.rayru.R;
import com.iwish.rayru.adapter.RentalPackageAdapter;
import com.iwish.rayru.config.Constants;
import com.iwish.rayru.config.JsonHelper;
import com.iwish.rayru.model.PackageVehicleList;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tech.iwish.taxi.RecyclerTouchListener;

public class RentalPackageSelectActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView PackageRecyclerView;
    KProgressHUD kProgressHUD;
    private List<PackageVehicleList> packageVehicles = new ArrayList<>();
    String PLat, PDrop, Area;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental_package_select);

        getSupportActionBar().setTitle("Select Package");

        PLat = getIntent().getStringExtra("PLat");
        PDrop = getIntent().getStringExtra("PDrop");
        Area = getIntent().getStringExtra("Area");

        PackageRecyclerView = (RecyclerView) findViewById(R.id.PackageRecyclerView);


        kProgressHUD = new KProgressHUD(this);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        PackageRecyclerView.setLayoutManager(linearLayoutManager);

        PackageRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, PackageRecyclerView, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                setProgressDialog("");
                RequestSend(packageVehicles.get(position).getVahicle(), packageVehicles.get(position).getPackage_type());
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));


        kProgressHUD = new KProgressHUD(this);

        packageSelect();
        AcceptRequest();

    }

    private void packageSelect() {


        setProgressDialog("Package Search");
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request1 = new Request.Builder().url(Constants.PACKAGEVEHICLE).post(body).build();

        okHttpClient.newCall(request1).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RentalPackageSelectActivity.this, "Connection Time out", Toast.LENGTH_SHORT).show();
                        remove_progress_Dialog();
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
                                packageVehicles.add(new PackageVehicleList(jsonHelper.GetResult("packid"), jsonHelper.GetResult("package_type"), jsonHelper.GetResult("vahicle_cat_id"), jsonHelper.GetResult("amount"), jsonHelper.GetResult("vahicle"), jsonHelper.GetResult("packageAmt")));
                            }

                            new Handler(getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    RentalPackageAdapter packageAdapter = new RentalPackageAdapter(RentalPackageSelectActivity.this, packageVehicles);
                                    PackageRecyclerView.setAdapter(packageAdapter);
                                    remove_progress_Dialog();
                                }
                            });

                        } else {

                            new Handler(getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RentalPackageSelectActivity.this, "No Packet", Toast.LENGTH_SHORT).show();
                                    remove_progress_Dialog();
                                }
                            });

                        }
                    }
                }
            }
        });

    }


    private void RequestSend(String VehicleType, String packageType) {


        DatabaseReference zonesRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference req = zonesRef.child("VehicleRequest").child("8871121949");
        req.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {

                HashMap<String, String> map = new HashMap<>();
                map.put("RequestType", "Rental");
                map.put("VehicleType", VehicleType);
                map.put("PickLat", PLat);
                map.put("PickLong", PDrop);
                map.put("Area", Area);
                map.put("PackageType", packageType);
                map.put("ClientId", "8871121949");
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference url = firebaseDatabase.getReference("VehicleRequest").child("8871121949").push();
                url.setValue(map);

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
            public void onChildAdded(@NonNull com.google.firebase.database.DataSnapshot snapshot, @Nullable String previousChildName) {

                Log.e("snapshot.getChildrenCou", String.valueOf(snapshot.getChildrenCount()));

                String DriverId = String.valueOf(snapshot.child("DriverID").getValue());
                String DriverLat = String.valueOf(snapshot.child("DLat").getValue());
                String DriverLong = String.valueOf(snapshot.child("DLong").getValue());
                String type = String.valueOf(snapshot.child("Type").getValue());
                if (type.equals("rental")) {
                    Intent intent = new Intent(RentalPackageSelectActivity.this, DriverInfoActivity.class);
                    intent.putExtra("DriverId", DriverId);
                    intent.putExtra("DriverLat", DriverLat);
                    intent.putExtra("DriverLong", DriverLong);
                    startActivity(intent);
                    remove_progress_Dialog();
                    acceptRequest.removeValue();
                }


            }

            @Override
            public void onChildChanged(@NonNull com.google.firebase.database.DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull com.google.firebase.database.DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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

    @Override
    public void onClick(View view) {
        int id = view.getId();

    }
}