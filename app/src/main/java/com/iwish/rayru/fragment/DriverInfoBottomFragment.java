package com.iwish.rayru.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.solver.state.State;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.iwish.rayru.R;
import com.iwish.rayru.config.Constants;
import com.iwish.rayru.other.Session;

import java.util.Map;

import static com.iwish.rayru.config.Constants.Driver_Img;
import static com.iwish.rayru.config.Constants.Driver_Name;
import static com.iwish.rayru.config.Constants.Driver_Number;
import static com.iwish.rayru.config.Constants.Driver_VehicleNumber;
import static com.iwish.rayru.config.Constants.OTP;

public class DriverInfoBottomFragment extends BottomSheetDialogFragment {

    Session session;
    Map data;
    ImageView driverImg;
    TextView driverName , drivernumber , driverVehiclenumber ,otp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_info_bottom,container, false);

        session = new Session(getContext());
        data = session.getShare();

        driverImg = view.findViewById(R.id.driverImg);
        driverName = view.findViewById(R.id.driverName);
        driverVehiclenumber = view.findViewById(R.id.driverVehiclenumber);
        drivernumber = view.findViewById(R.id.drivernumber);
        otp = view.findViewById(R.id.otp);

        String ImgUrl = Constants.IMAGE_URL + data.get(Driver_Img).toString();

        Glide
                .with(getContext())
                .load(ImgUrl)
                .centerCrop()
                .placeholder(R.drawable.red_pin_icon)
                .into(driverImg);

        driverName.setText(data.get(Driver_Name).toString());
        driverVehiclenumber.setText(data.get(Driver_VehicleNumber).toString());
        drivernumber.setText(data.get(Driver_Number).toString());
        otp.setText(data.get(OTP).toString());

        return view;
    }
}