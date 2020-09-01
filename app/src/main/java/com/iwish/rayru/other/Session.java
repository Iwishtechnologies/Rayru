package com.iwish.rayru.other;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

import static com.iwish.rayru.config.Constants.DRIVER_ID;
import static com.iwish.rayru.config.Constants.DRIVER_NAVIGATION;
import static com.iwish.rayru.config.Constants.Driver_Img;
import static com.iwish.rayru.config.Constants.Driver_Name;
import static com.iwish.rayru.config.Constants.Driver_Number;
import static com.iwish.rayru.config.Constants.Driver_VehicleNumber;
import static com.iwish.rayru.config.Constants.FIRST_TIME_COME;
import static com.iwish.rayru.config.Constants.OTP;
import static com.iwish.rayru.config.Constants.REFER_CODE;
import static com.iwish.rayru.config.Constants.RIDE_START;
import static com.iwish.rayru.config.Constants.USER_CONTACT;
import static com.iwish.rayru.config.Constants.USER_EMAIL;
import static com.iwish.rayru.config.Constants.USER_NAME;
import static com.iwish.rayru.config.Constants.WALLET_AMOUNT;


public class Session {

    SharedPreferences Preferences;
    public static final String MyPREFERENCES = "TaxtSharepreferen";
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    public Context context;

    public Session(Context context) {
        this.context = context;
        Preferences = context.getSharedPreferences(MyPREFERENCES, PRIVATE_MODE);
        editor = Preferences.edit();
    }

    //     Client First Time Come Apllication
    public void FirstTimeCome(String val) {
        editor.putString(FIRST_TIME_COME, val).commit();
    }

    //     logout
    public void logout() {
        editor.clear().commit();
    }


    public Map getShare() {
        Preferences.getAll();
        return Preferences.getAll();
    }


    //    Client
    public void ClientDetails(String name, String number, String email, String token) {
        editor.putString(USER_NAME, name);
        editor.putString(USER_EMAIL, email);
        editor.putString(USER_CONTACT, number);
        editor.putString(REFER_CODE, token);
        editor.commit();
    }

    public void DriverInfo(String DriverName, String DriverNumber, String DriverImg, String DriverVehicleNumber, String otp, String DriverId) {

        editor.putString(Driver_Name, DriverName);
        editor.putString(Driver_Number, DriverNumber);
        editor.putString(Driver_Img, DriverImg);
        editor.putString(Driver_VehicleNumber, DriverVehicleNumber);
        editor.putString(OTP, otp);
        editor.putString(DRIVER_ID, DriverId);
        editor.commit();

    }


    public void DriverNavigationSet() {
        editor.putString(DRIVER_NAVIGATION, "save");
        editor.commit();
    }

    public void RemoveDriverNavigationSet() {
        editor.remove(DRIVER_NAVIGATION).commit();
    }


    public void RideStart() {
        editor.putString(RIDE_START, "start");
        editor.commit();
    }

    public void RemoveRideStart() {
        editor.remove(RIDE_START).commit();
    }

//  wallet
    public void walletAdd(String data) {
        editor.putString(WALLET_AMOUNT, data).commit();
    }

}
























