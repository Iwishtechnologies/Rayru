package com.iwish.rayru.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.iwish.rayru.R;
import com.iwish.rayru.activity.MainActivity;
import com.iwish.rayru.config.Constants;
import com.iwish.rayru.config.JsonHelper;
import com.iwish.rayru.other.Session;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login_Fragment_2 extends Fragment {

    private Button otp_button;
    private EditText otp_check;
    private Pinview pinview;
    private TextView mobNumber;
    private KProgressHUD kProgressHUD;
    String mobile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login__2, null);

        otp_button = (Button) view.findViewById(R.id.otp_button);
        pinview = (Pinview) view.findViewById(R.id.pinview);
        mobNumber = (TextView) view.findViewById(R.id.mobNumber);

        kProgressHUD = new KProgressHUD(getActivity());


        pinview.setPinHeight(100);
        pinview.setPinWidth(100);
        pinview.setPinBackgroundRes(R.drawable.pin_design);

        mobile = getArguments().getString("number");
        mobNumber.setText(mobile);

        otp_button.setOnClickListener(view1 -> {
            otpMatch();
        });

        return view;
    }


    private void otpMatch() {

        setProgressDialog("Verify Otp");
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile_number", mobile);
            jsonObject.put("otp", pinview.getValue());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request1 = new Request.Builder().url(Constants.USER_OTP).post(body).build();


        okHttpClient.newCall(request1).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "Connection Time out", Toast.LENGTH_SHORT).show();
                    remove_progress_Dialog();
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    Log.e("result", result);
                    JsonHelper jsonHelper = new JsonHelper(result);
                    if (jsonHelper.isValidJson()) {
                        String responses = jsonHelper.GetResult("response");
                        if (responses.equals("TRUE")) {

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    JSONArray jsonArray = jsonHelper.setChildjsonArray(jsonHelper.getCurrentJsonObj(), "data");

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        jsonHelper.setChildjsonObj(jsonArray, i);

                                        String name = jsonHelper.GetResult("name");
                                        String email = jsonHelper.GetResult("email");
                                        String contact = jsonHelper.GetResult("contact");
                                        String refer_code = jsonHelper.GetResult("refer_code");

                                        Session session = new Session(getContext());
                                        session.ClientDetails(name, contact, email, refer_code);
                                        session.FirstTimeCome("open");
                                    }


                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    startActivity(intent);
                                    remove_progress_Dialog();

                                }
                            });


                        } else {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getActivity(), "Otp not Match", Toast.LENGTH_SHORT).show();
                                remove_progress_Dialog();
                            });
                        }
                    }
                }
            }
        });


    }

    public void setProgressDialog(String msg) {
        kProgressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(msg)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

    }

    public void remove_progress_Dialog() {
        kProgressHUD.dismiss();
    }


}
















