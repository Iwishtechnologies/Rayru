package com.iwish.rayru.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iwish.rayru.R;
import com.iwish.rayru.activity.RentalPackageSelectActivity;
import com.iwish.rayru.model.PackageVehicleList;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.HashMap;
import java.util.List;

public class RentalPackageAdapter extends RecyclerView.Adapter<RentalPackageAdapter.Viewholder> {

    List<PackageVehicleList> packageVehicleLists;
    Context context;
    KProgressHUD kProgressHUD;

    public RentalPackageAdapter(RentalPackageSelectActivity rentalPackageSelectActivity, List<PackageVehicleList> packageVehicles) {
        this.context = rentalPackageSelectActivity;
        this.packageVehicleLists = packageVehicles;
        kProgressHUD = new KProgressHUD(context);
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_package_show, parent, false);
        Viewholder viewholder = new Viewholder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        PackageVehicleList list = packageVehicleLists.get(position);

        holder.packageshow.setText(list.getPackage_type());
        holder.vehicle_type.setText(list.getVahicle());


    }

    @Override
    public int getItemCount() {
        return packageVehicleLists.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        private TextView packageshow, vehicle_type;
        private LinearLayout mainLayout;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            packageshow = (TextView) itemView.findViewById(R.id.packageshow);
            vehicle_type = (TextView) itemView.findViewById(R.id.vehicle_type);
            mainLayout = (LinearLayout) itemView.findViewById(R.id.mainLayout);

        }
    }



}
