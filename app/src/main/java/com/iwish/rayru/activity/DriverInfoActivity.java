package com.iwish.rayru.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iwish.rayru.R;
import com.iwish.rayru.config.Constants;
import com.iwish.rayru.config.JsonHelper;
import com.iwish.rayru.other.Session;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Time;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DriverInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout confirmOtp, trip_dis;
    private Button info_driver;
    private TextView trip_distance, trip_duration, trip_rate, call_driver, amts, timeset;
    String DriverID, DriverLat, DriverLong, PLat, PLong, DLat, DLong, TrackId, otp, DriverName, DriverNumber, DriverImg, DriverVehicleNumber;
    private AlertDialog.Builder builder;
    private View dialogView;
    Session session;
    Map data;
    int j = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_info);

        getSupportActionBar().hide();

        info_driver = (Button) findViewById(R.id.info_driver);
        trip_distance = (TextView) findViewById(R.id.trip_distance);
        trip_duration = (TextView) findViewById(R.id.trip_duration);
        trip_rate = (TextView) findViewById(R.id.trip_rate);
        amts = (TextView) findViewById(R.id.amts);
        timeset = (TextView) findViewById(R.id.timeset);

        trip_dis = (LinearLayout) findViewById(R.id.trip_dis);

        DriverID = getIntent().getStringExtra("DriverId");
        DriverLat = getIntent().getStringExtra("DriverLat");
        DriverLong = getIntent().getStringExtra("DriverLong");
        PLat = getIntent().getStringExtra("Plat");
        PLong = getIntent().getStringExtra("Plong");
        DLat = getIntent().getStringExtra("Dlat");
        DLong = getIntent().getStringExtra("Dlong");
        TrackId = getIntent().getStringExtra("TrackID");

        session = new Session(this);
        data = session.getShare();

        info_driver.setOnClickListener(this);

        dataset();

    }

    private void dataset() {

        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("driverid", DriverID);
            jsonObject.put("PLat", PLat);
            jsonObject.put("PLong", PLong);
            jsonObject.put("DLat", DLat);
            jsonObject.put("DLong", DLong);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request1 = new Request.Builder().url(Constants.RIDE_SET).post(body).build();


        okHttpClient.newCall(request1).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
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
                                new Handler(getMainLooper()).post(() -> {

                                    trip_distance.setText(jsonHelper.GetResult("Distance"));
                                    trip_duration.setText(jsonHelper.GetResult("Time"));
                                    amts.setText(jsonHelper.GetResult("Price"));
                                    otp = jsonHelper.GetResult("Otp");
                                    DriverName = jsonHelper.GetResult("DriverName");
                                    DriverNumber = jsonHelper.GetResult("Mobile");
                                    DriverImg = jsonHelper.GetResult("Image");
                                    DriverVehicleNumber = jsonHelper.GetResult("VehicleNumber");

                                });
                                session.DriverNavigationSet();
                                session.DriverInfo(DriverName, DriverNumber, DriverImg, DriverVehicleNumber, otp, DriverID);
                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        j--;
                                        new Handler(getMainLooper()).post(() -> {
                                            timeset.setText(String.valueOf(j));
                                            if (j == 0) {
                                                startActivity(new Intent(DriverInfoActivity.this, MainActivity.class));
                                                finish();
                                            }
                                        });

                                    }
                                }, 0, 1000);

                            }
                        }
                    }
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.info_driver) {
            driverInfo();
        }
    }

    private void driverInfo() {

        builder = new AlertDialog.Builder(this);
        dialogView = LayoutInflater.from(this).inflate(R.layout.row_driver_info_layout, null);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


        call_driver = dialogView.findViewById(R.id.call_driver);
        TextView name_drivers = dialogView.findViewById(R.id.name_drivers);
        TextView vehicleNumber = dialogView.findViewById(R.id.vehicleNumber);
        TextView otp_1 = dialogView.findViewById(R.id.otp_1);
//        TextView otp_2 = dialogView.findViewById(R.id.otp_2);
//        TextView otp_3 = dialogView.findViewById(R.id.otp_3);
//        TextView otp_4 = dialogView.findViewById(R.id.otp_4);

        ImageView driverImg = dialogView.findViewById(R.id.driverImg);

        Glide
                .with(this)
                .load(Constants.IMAGE_URL + DriverImg)
                .centerCrop()
                .placeholder(R.drawable.red_pin_icon)
                .into(driverImg);

        name_drivers.setText(DriverName);
        vehicleNumber.setText(DriverVehicleNumber);

        otp_1.setText(otp);
        call_driver.setOnClickListener((view1) -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + DriverNumber));
            startActivity(intent);
        });

    }
}





























