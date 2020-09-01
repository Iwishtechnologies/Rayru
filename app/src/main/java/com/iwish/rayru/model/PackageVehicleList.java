package com.iwish.rayru.model;


public class PackageVehicleList {
    private String packid;
    private String package_type;
    private String vahicle_cat_id;
    private String amount;
    private String vahicle;
    private String packageAmt;


    public PackageVehicleList(String packid, String package_type, String vahicle_cat_id, String amount, String vahicle, String packageAmt) {
        this.packid = packid;
        this.package_type = package_type;
        this.vahicle_cat_id = vahicle_cat_id;
        this.amount = amount;
        this.vahicle = vahicle;
        this.packageAmt = packageAmt;
    }

    public String getPackid() {
        return packid;
    }

    public void setPackid(String packid) {
        this.packid = packid;
    }

    public String getPackage_type() {
        return package_type;
    }

    public void setPackage_type(String package_type) {
        this.package_type = package_type;
    }

    public String getVahicle_cat_id() {
        return vahicle_cat_id;
    }

    public void setVahicle_cat_id(String vahicle_cat_id) {
        this.vahicle_cat_id = vahicle_cat_id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getVahicle() {
        return vahicle;
    }

    public void setVahicle(String vahicle) {
        this.vahicle = vahicle;
    }

    public String getPackageAmt() {
        return packageAmt;
    }

    public void setPackageAmt(String packageAmt) {
        this.packageAmt = packageAmt;
    }
}
