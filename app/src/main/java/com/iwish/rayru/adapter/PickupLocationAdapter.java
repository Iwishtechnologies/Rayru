package com.iwish.rayru.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.iwish.rayru.R;

import java.util.List;

import tech.iwish.taxi.other.PickupLocationList;

public class PickupLocationAdapter extends RecyclerView.Adapter<PickupLocationAdapter.Viewholder> {

    private List<PickupLocationList> pickupLocationLists;
    private Context context;
    private onPickupListner onpickupListner;


    public PickupLocationAdapter(FragmentActivity activity, List<PickupLocationList> pickupLocationLists) {
        this.pickupLocationLists = pickupLocationLists;
        this.context = activity;
    }


    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_picklist, parent, false);
        Viewholder viewholder = new Viewholder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, final int position) {

        if (!pickupLocationLists.get(position).getDescription().isEmpty())
            holder.place.setText(pickupLocationLists.get(position).getDescription());
/*
        holder.placeLayout.setOnClickListener(view -> {
//                Toast.makeText(context, "" + pickupLocationLists.get(position).getDescription(), Toast.LENGTH_SHORT).show();
            InputMethodManager input = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            input.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            onpickupListner.onListen(pickupLocationLists.get(position).getDescription());

        });*/
    }

    @Override
    public int getItemCount() {

        return pickupLocationLists.size();

    }

    public class Viewholder extends RecyclerView.ViewHolder {

        private TextView place;
        private LinearLayout placeLayout, click_linerLayout_vehicle;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            place = (TextView) itemView.findViewById(R.id.place);
            placeLayout = (LinearLayout) itemView.findViewById(R.id.placeLayout);


        }
    }


    public void setOnPickupListner(onPickupListner onpickupListner) {
        this.onpickupListner = onpickupListner;
    }


    public interface onPickupListner {

        void onListen(String location);

    }


}
