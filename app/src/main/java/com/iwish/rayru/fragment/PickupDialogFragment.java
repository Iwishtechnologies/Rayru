package com.iwish.rayru.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import com.iwish.rayru.R;
import com.iwish.rayru.adapter.PickupLocationAdapter;
import com.iwish.rayru.config.JsonHelper;
import com.iwish.rayru.inter.PickupInterface;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class PickupDialogFragment extends DialogFragment {


    RecyclerView pickuprecycle;
    List<tech.iwish.taxi.other.PickupLocationList> pickupLocationLists = new ArrayList<>();
    private SearchView placeSearchview;
    List<Address> currentLocationAll;
    PickupInterface pickupInterface;

    public PickupDialogFragment(List<Address> currentLocationAll , PickupInterface pickupInterface) {
        this.currentLocationAll = currentLocationAll;
        this.pickupInterface = pickupInterface;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pickup_dialog, container, false);


        placeSearchview = (SearchView) view.findViewById(R.id.placeSearchview);
        pickuprecycle = (RecyclerView) view.findViewById(R.id.pickupRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        pickuprecycle.setLayoutManager(linearLayoutManager);


        pickuprecycle.addOnItemTouchListener(new tech.iwish.taxi.RecyclerTouchListener(getActivity(), pickuprecycle, new tech.iwish.taxi.RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                InputMethodManager input = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                input.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                pickupInterface.pickupinterface(pickupLocationLists.get(position).getDescription());
                dismiss();

            }

            @Override
            public void onLongItemClick(View view, int position) {


            }
        }));


        placeSearchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                setPickupLocation(s);


                return false;
            }
        });


        return view;
    }

    private void setPickupLocation(String s) {
        pickupLocationLists.clear();
        String URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input='india'" + s + "&location=" + String.valueOf(currentLocationAll.get(0).getLatitude()) + "," + String.valueOf(currentLocationAll.get(0).getLongitude()) + " &radius=5000&key=AIzaSyApe8T3JMiqj9OgEbqd--zTBfl3fibPeEs";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }


            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();

                    Log.e("result", result);

                    JsonHelper jsonHelper = new JsonHelper(result);
                    if (jsonHelper.isValidJson()) {
                        String responses = jsonHelper.GetResult("status");
                        if (responses.equals("OK")) {
//                            pickupLocationLists.clear();
                            JSONArray jsonArray = jsonHelper.setChildjsonArray(jsonHelper.getCurrentJsonObj(), "predictions");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonHelper.setChildjsonObj(jsonArray, i);
                                pickupLocationLists.add(new tech.iwish.taxi.other.PickupLocationList(jsonHelper.GetResult("description"), jsonHelper.GetResult("id"), jsonHelper.GetResult("matched_substrings"), jsonHelper.GetResult("place_id"), jsonHelper.GetResult("reference"), jsonHelper.GetResult("structured_formatting"), jsonHelper.GetResult("terms")));

                            }

                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        PickupLocationAdapter pickupLocationAdapter = new PickupLocationAdapter(getActivity(), pickupLocationLists);
                                        pickuprecycle.setAdapter(pickupLocationAdapter);


                                    }
                                });
                            }

                        }
                    }

                }
            }
        });

    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }



}