package com.iwish.rayru.other;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkCheckOnclick {

    Context context;
    public NetworkCheckOnclick(Context context){
        this.context = context;
    }


    public boolean onClickNetworkCheck() {

        ConnectivityManager cm = (ConnectivityManager) ((Activity)context).getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null;

    }

}
