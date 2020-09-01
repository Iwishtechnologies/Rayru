package com.iwish.rayru.model;

public class VehicleCategoryList {

    String catagory_id;
    String catagory_name;
    String MinRate;
    String Rate_Km;
    String waitingRate_m;
    String rtc_m;
    String img;


    public VehicleCategoryList(String catagory_id, String catagory_name, String minRate, String rate_Km, String waitingRate_m, String rtc_m, String img) {
        this.catagory_id = catagory_id;
        this.catagory_name = catagory_name;
        MinRate = minRate;
        Rate_Km = rate_Km;
        this.waitingRate_m = waitingRate_m;
        this.rtc_m = rtc_m;
        this.img = img;
    }

    public String getCatagory_id() {
        return catagory_id;
    }

    public void setCatagory_id(String catagory_id) {
        this.catagory_id = catagory_id;
    }

    public String getCatagory_name() {
        return catagory_name;
    }

    public void setCatagory_name(String catagory_name) {
        this.catagory_name = catagory_name;
    }

    public String getMinRate() {
        return MinRate;
    }

    public void setMinRate(String minRate) {
        MinRate = minRate;
    }

    public String getRate_Km() {
        return Rate_Km;
    }

    public void setRate_Km(String rate_Km) {
        Rate_Km = rate_Km;
    }

    public String getWaitingRate_m() {
        return waitingRate_m;
    }

    public void setWaitingRate_m(String waitingRate_m) {
        this.waitingRate_m = waitingRate_m;
    }

    public String getRtc_m() {
        return rtc_m;
    }

    public void setRtc_m(String rtc_m) {
        this.rtc_m = rtc_m;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
