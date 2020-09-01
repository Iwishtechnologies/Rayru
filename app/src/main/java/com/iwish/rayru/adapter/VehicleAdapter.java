package com.iwish.rayru.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iwish.rayru.R;
import com.iwish.rayru.config.Constants;
import com.iwish.rayru.inter.VehicleInterface;
import com.iwish.rayru.model.VehicleCategoryList;

import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.Viewholder> {

    List<VehicleCategoryList> vehicleCategoryLists;
    Context context;
    VehicleInterface vehicleInterface;

    public VehicleAdapter(List<VehicleCategoryList> vehicleCategoryLists , VehicleInterface vehicleInterface) {
        this.vehicleCategoryLists = vehicleCategoryLists;
        this.vehicleInterface = vehicleInterface;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_vehicle_category, parent, false);
        Viewholder viewholder = new Viewholder(view);
        context = parent.getContext();
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        final VehicleCategoryList list = vehicleCategoryLists.get(position);

        holder.vehicle_name.setText(list.getCatagory_name());

        String ImgUrl = Constants.IMAGE_URL + list.getImg();

        Glide
                .with(context)
                .load(ImgUrl)
                .centerCrop()
                .placeholder(R.drawable.red_pin_icon)
                .into(holder.vehicle_img);

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vehicleInterface.vehicleInterface(list.getCatagory_name());
            }
        });

    }

    @Override
    public int getItemCount() {
        return vehicleCategoryLists.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        ImageView vehicle_img;
        TextView vehicle_name;
        LinearLayout mainLayout;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            vehicle_img = itemView.findViewById(R.id.vehicle_img);
            vehicle_name = itemView.findViewById(R.id.vehicle_name);
            mainLayout = itemView.findViewById(R.id.mainLayout);

        }
    }
}
