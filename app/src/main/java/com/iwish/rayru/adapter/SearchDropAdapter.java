package com.iwish.rayru.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iwish.rayru.R;
import com.iwish.rayru.model.DropLocationList;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SearchDropAdapter extends RecyclerView.Adapter<SearchDropAdapter.Viewholder> implements Filterable {


    private Context context;
    private List<DropLocationList> dropLocationListMap;


    public SearchDropAdapter(FragmentActivity activity, List<DropLocationList> dropLocationListMap) {
        this.context = activity;
        this.dropLocationListMap = dropLocationListMap;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_picklist, parent, false);
        Viewholder viewholder = new Viewholder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        holder.place.setText(dropLocationListMap.get(position).getDescription());

    }

    @Override
    public int getItemCount() {
        return dropLocationListMap.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                Toast.makeText(context, "" + charSequence.toString(), Toast.LENGTH_SHORT).show();
                return null;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                Toast.makeText(context, "" + charSequence.toString(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        TextView place, status_check;
        LinearLayout placeLayout, click_linerLayout_vehicle;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            place = (TextView) itemView.findViewById(R.id.place);
            status_check = (TextView) itemView.findViewById(R.id.status_check);
            placeLayout = (LinearLayout) itemView.findViewById(R.id.placeLayout);

        }
    }

}
