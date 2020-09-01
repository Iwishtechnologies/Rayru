package com.iwish.rayru.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iwish.rayru.R;
import com.iwish.rayru.activity.DriverInfoActivity;
import com.iwish.rayru.adapter.OutStationVehicleAdapter;
import com.iwish.rayru.adapter.VehicleAdapter;
import com.iwish.rayru.config.Constants;
import com.iwish.rayru.config.JsonHelper;
import com.iwish.rayru.model.OutStationVehicleList;
import com.iwish.rayru.model.VehicleCategoryList;
import com.iwish.rayru.other.Session;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.iwish.rayru.config.Constants.USER_CONTACT;

public class OutStationVehicleFragment extends Fragment {


    private TextView leaveDateTime, returnTimeDate, pick, drop;


    RecyclerView vehicleOutstationshe;
    private DatePickerDialog.OnDateSetListener mleave, dropcal;
    private KProgressHUD kProgressHUD;
    String dropCalt;
    List<OutStationVehicleList> outStationVehicleLists = new ArrayList<>();
    List<Address> currentLocationAll;
    List<Address> dropLocationAll;
    Button continues;
    Session session;
    Map data;

    public OutStationVehicleFragment(List<Address> currentLocationAll, List<Address> dropLocationAll) {
        this.currentLocationAll = currentLocationAll;
        this.dropLocationAll = dropLocationAll;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_out_station_vehicle, null);


        getActivity().setTitle("Book Your Outstation ride");

        leaveDateTime = (TextView) view.findViewById(R.id.leaveDateTime);
        returnTimeDate = (TextView) view.findViewById(R.id.returnTimeDate);
        pick = (TextView) view.findViewById(R.id.pick);
        drop = (TextView) view.findViewById(R.id.drop);
        vehicleOutstationshe = (RecyclerView) view.findViewById(R.id.vehicleOutstationshe);
        continues = view.findViewById(R.id.continues);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        vehicleOutstationshe.setLayoutManager(linearLayoutManager);

        session = new Session(getContext());
        data = session.getShare();

        kProgressHUD = new KProgressHUD(getActivity());

        Date currentTime = Calendar.getInstance().getTime();
        Log.e("onCreateView: ", currentTime.toString());

        Calendar c = Calendar.getInstance();
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM" + " ," + "HH:mm a");
        String formattedDate = df.format(c.getTime());

        leaveDateTime.setText(formattedDate);

        pick.setText(currentLocationAll.get(0).getAddressLine(0));
        drop.setText(dropLocationAll.get(0).getAddressLine(0));

        AcceptRequest();
        returnTimeDate.setOnClickListener(view1 -> {

            Calendar cal = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Dialog_MinWidth, mleave, year, month, day);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mleave = (datePicker, ye, i1, dy) -> {
            Calendar calss = Calendar.getInstance();
            calss.set(Calendar.YEAR, ye);
            calss.set(Calendar.MONTH, i1);
            calss.set(Calendar.DAY_OF_MONTH, dy);
            dropCalt = DateFormat.getDateInstance(DateFormat.FULL).format(calss.getTime());
            returnTimeDate.setText(dropCalt);
        };


        leaveDateTime.setOnClickListener(View -> {

            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Dialog_MinWidth, dropcal, year, month, day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        dropcal = (datePicker, ye, i1, dy) -> {
            Calendar calss = Calendar.getInstance();
            calss.set(Calendar.YEAR, ye);
            calss.set(Calendar.MONTH, i1);
            calss.set(Calendar.DAY_OF_MONTH, dy);
            String da = DateFormat.getDateInstance(DateFormat.FULL).format(calss.getTime());
            leaveDateTime.setText(da);
        };
        vehicleOutstation();

        continues.setOnClickListener(view12 -> RequestSend());

        return view;
    }

    public void vehicleOutstation() {


        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("PickLatitude", "");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request1 = new Request.Builder().url(Constants.OUTSTATIONVEHICLE).post(body).build();


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
                                outStationVehicleLists.add(new OutStationVehicleList(jsonHelper.GetResult("catagory_id"), jsonHelper.GetResult("catagory_name"), jsonHelper.GetResult("MinRate"), jsonHelper.GetResult("Rate_Km"), jsonHelper.GetResult("waitingRate_m"), jsonHelper.GetResult("rtc_m"), jsonHelper.GetResult("img"), jsonHelper.GetResult("vahicle_cat")));
                            }

                            if (getActivity() != null) {

                                new Handler(Looper.getMainLooper()).post(() -> vehicleOutstationshe.setAdapter(new OutStationVehicleAdapter(outStationVehicleLists)));
                            }
                        }
                    }
                }
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
                if (type.equals("OutStation")) {
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

    private void RequestSend() {

        setProgressDialog("");

        DatabaseReference zonesRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference req = zonesRef.child("VehicleRequest").child(data.get(USER_CONTACT).toString());
        req.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();
                if (count == 0) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("RequestType", "OutStation");
                    map.put("VehicleType", "MINI");
                    map.put("PickLat", String.valueOf(currentLocationAll.get(0).getLatitude()));
                    map.put("PickLong", String.valueOf(currentLocationAll.get(0).getLongitude()));
                    map.put("DropLat", String.valueOf(dropLocationAll.get(0).getLatitude()));
                    map.put("DropLong", String.valueOf(dropLocationAll.get(0).getLongitude()));
                    map.put("Area", currentLocationAll.get(0).getLocality());
                    map.put("LeaveDate", leaveDateTime.getText().toString());
                    map.put("ClientId", data.get(USER_CONTACT).toString());
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
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