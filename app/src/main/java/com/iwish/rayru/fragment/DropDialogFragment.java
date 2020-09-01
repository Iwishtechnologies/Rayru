package com.iwish.rayru.fragment;

import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Toast;

import com.iwish.rayru.R;
import com.iwish.rayru.adapter.SearchDropAdapter;
import com.iwish.rayru.config.JsonHelper;
import com.iwish.rayru.inter.DropInterface;
import com.iwish.rayru.model.DropLocationList;

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


public class DropDialogFragment extends DialogFragment {

    private SearchView searchview;
    private RecyclerView search_drop_recycle;
    private List<DropLocationList> dropLocationListMap = new ArrayList<>();
    public Map<String, Double> latitude_logitude;
    List<Address> currentLocationAll;
    DropInterface dropInterface;


    public DropDialogFragment(List<Address> currentLocationAll, DropInterface dropInterface) {
        this.currentLocationAll = currentLocationAll;
        this.dropInterface = dropInterface;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drop_dialog, container, false);

        search_drop_recycle = (RecyclerView) view.findViewById(R.id.search_drop_recycle);
        searchview = (SearchView) view.findViewById(R.id.searchview);


        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
//                SearchTime(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                SearchTimes(s);
                return true;
            }
        });


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        search_drop_recycle.setLayoutManager(linearLayoutManager);


        search_drop_recycle.addOnItemTouchListener(new tech.iwish.taxi.RecyclerTouchListener(getContext(), search_drop_recycle, new tech.iwish.taxi.RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                InputMethodManager input = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                input.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                dropInterface.dropInterface(dropLocationListMap.get(position).getDescription());
                dismiss();


            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));


        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        return new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                Toast.makeText(getContext(), "Drop back", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void SearchTimes(String s) {


        dropLocationListMap.clear();
        String URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input='india'" + s + "&location=" + String.valueOf(currentLocationAll.get(0).getLatitude()) + "," + String.valueOf(currentLocationAll.get(0).getLongitude()) + " &radius=5000&key=AIzaSyApe8T3JMiqj9OgEbqd--zTBfl3fibPeEs";

        Log.e("a", URL);

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
                                dropLocationListMap.add(new DropLocationList(jsonHelper.GetResult("description"), jsonHelper.GetResult("id"), jsonHelper.GetResult("matched_substrings"), jsonHelper.GetResult("place_id"), jsonHelper.GetResult("reference"), jsonHelper.GetResult("structured_formatting"), jsonHelper.GetResult("terms")));

                            }

                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        SearchDropAdapter searchDropAdapter = new SearchDropAdapter(getActivity(), dropLocationListMap);
                                        search_drop_recycle.setAdapter(searchDropAdapter);
                                        searchDropAdapter.notifyDataSetChanged();

                                    }
                                });
                            }

                        }
                    }

                }
            }
        });


    }

}