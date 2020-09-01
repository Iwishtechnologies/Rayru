package com.iwish.rayru.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.iwish.rayru.R;
import com.iwish.rayru.config.Constants;
import com.iwish.rayru.config.JsonHelper;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginFragment_1 extends Fragment {

    private EditText mobile_number;
    private Button login_button;
    private ProgressBar login_progress;
    private KProgressHUD kProgressHUD;
    LinearLayout mainLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_1, null);


        mobile_number = (EditText) view.findViewById(R.id.mobile_number);
        login_button = (Button) view.findViewById(R.id.login_button);
        kProgressHUD = new KProgressHUD(getActivity());
        mainLayout = view.findViewById(R.id.mainLayout);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_button.setClickable(false);
                mobileverify();
                login_button.setClickable(true);
            }
        });


        return view;
    }

    private void mobileverify() {


        mainLayout.setAlpha((float) 0.5);
        setProgressDialog("Number Verify");

        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile_number", mobile_number.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request1 = new Request.Builder().url(Constants.USER_LOGIN).post(body).build();


        okHttpClient.newCall(request1).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainLayout.setAlpha(1);
                        Toast.makeText(getActivity(), "Connection Time out", Toast.LENGTH_SHORT).show();
                        remove_progress_Dialog();
                    }
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
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mainLayout.setAlpha(1);
                                    Login_Fragment_2 login_fragment_2 = new Login_Fragment_2();
                                    Bundle args = new Bundle();
                                    args.putString("number", mobile_number.getText().toString().trim());
                                    login_fragment_2.setArguments(args);
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                                    transaction.setCustomAnimations(R.anim.enter_fragment1, R.anim.exit_fragment1, R.anim.enter_fragment1, R.anim.exit_fragment1);
                                    transaction.addToBackStack(null);
                                    transaction.replace(R.id.Login_fragmentLayout, login_fragment_2, "BLANK_FRAGMENT").commit();
                                    remove_progress_Dialog();

                                }
                            });
                        } else {

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mainLayout.setAlpha(1);
                                    Toast.makeText(getActivity(), "Mobile Number Not Match", Toast.LENGTH_SHORT).show();
                                    remove_progress_Dialog();
                                }
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
