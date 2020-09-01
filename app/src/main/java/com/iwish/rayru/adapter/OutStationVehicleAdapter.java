package com.iwish.rayru.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iwish.rayru.R;
import com.iwish.rayru.model.OutStationVehicleList;

import java.util.List;

public class OutStationVehicleAdapter extends RecyclerView.Adapter<OutStationVehicleAdapter.Viewholder> {

    List<OutStationVehicleList> outStationVehicleLists;

    public OutStationVehicleAdapter(List<OutStationVehicleList> outStationVehicleLists) {
        this.outStationVehicleLists = outStationVehicleLists;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_outstation_vehicle, parent, false);
        Viewholder viewholder = new Viewholder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        OutStationVehicleList list = outStationVehicleLists.get(position);

        holder.vehicleType.setText(list.getVahicle_cat());

    }

    @Override
    public int getItemCount() {
        return outStationVehicleLists.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        TextView vehicleType;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            vehicleType = itemView.findViewById(R.id.vehicleType);


        }
    }
}





















