package com.iwish.rayru.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.iwish.rayru.R;
import com.iwish.rayru.config.Constants;
import com.iwish.rayru.config.JsonHelper;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignupActivity extends AppCompatActivity {

    private EditText signup_name, signup_email, signup_contact, signup_password;
    private Button signup_button;
    private LinearLayout login_activity_go, refer_code_layout, mainLayout;
    boolean doubleBackToExitPressedOnce = false;
    private Spinner spinner;
    private String spinner_value;
    private KProgressHUD kProgressHUD;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        getSupportActionBar().hide();

        signup_name = (EditText) findViewById(R.id.signup_name);
        signup_email = (EditText) findViewById(R.id.signup_email);
        signup_contact = (EditText) findViewById(R.id.signup_contact);
        signup_password = (EditText) findViewById(R.id.signup_password);
        signup_button = (Button) findViewById(R.id.signup_button);
        login_activity_go = (LinearLayout) findViewById(R.id.login_activity_go);
        spinner = (Spinner) findViewById(R.id.spinner);
        refer_code_layout = (LinearLayout) findViewById(R.id.refer_code_layout);
        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);

        final List<String> categories = new ArrayList<String>();
        kProgressHUD = new KProgressHUD(this);

        categories.add("Select");
        categories.add("Delhi");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        spinner.setAdapter(dataAdapter);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(249, 249, 249));
//                ((TextView)view).setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(SignupActivity.this, R.drawable.user_icon_login), null, null, null);
//                ((TextView) view).setCompoundDrawablePadding(20);
                spinner_value = categories.get(position);
//                Toast.makeText(SignupActivity.this, "Selected : "+ categories.get(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        login_activity_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                SignupActivity.this.startActivity(intent);
            }
        });


        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Validation()) {
//                    Object a = data.get(REFER_CODE_LINK_CHECK);
//                    if (a != null) {
//                        if (data.get(REFER_CODE_LINK_CHECK).toString() != null) {
//                            refercodechec();
//                        }
//                    } else {
                        signup_button.setClickable(false);
                        datainsert();
                        signup_button.setClickable(true);
//                    }
                }
            }

        });

    }



    private Boolean Validation() {
        if (signup_name.getText().toString().trim().isEmpty()) {
            signup_name.setError("Please Fill Your Name");
            return false;
        }
        if (signup_email.getText().toString().trim().isEmpty()) {
            signup_email.setError("empty");
            return false;
        }
        if (signup_contact.getText().toString().trim().isEmpty()) {
            signup_email.setError("Please Fill Your Contact");
            return false;
        }
        if (signup_contact.length() < 10) {
            signup_contact.setError("Please Enter Valid Number ");
            return false;
        }
        if (spinner_value.equals("Select")) {
            Toast.makeText(SignupActivity.this, "Select City", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void datainsert() {

        mainLayout.setAlpha((float) 0.5);
        setProgressDialog("");

        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", signup_name.getText().toString());
            jsonObject.put("email", signup_email.getText().toString());
            jsonObject.put("contact", signup_contact.getText().toString());
            jsonObject.put("password", signup_password.getText().toString());
            jsonObject.put("city", spinner_value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request1 = new Request.Builder().url(Constants.USER_SIGUP).post(body).build();


        okHttpClient.newCall(request1).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                SignupActivity.this.runOnUiThread(() -> {
                    mainLayout.setAlpha(1);
                    Toast.makeText(SignupActivity.this, "Connection Time out", Toast.LENGTH_SHORT).show();
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
                            SignupActivity.this.runOnUiThread(() -> {
                                mainLayout.setAlpha(1);
                                remove_progress_Dialog();
                                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                startActivity(intent);
                            });
                        } else {

                            SignupActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mainLayout.setAlpha(1);
                                    Toast.makeText(SignupActivity.this, "Number already exists", Toast.LENGTH_SHORT).show();
                                    remove_progress_Dialog();
                                }
                            });
                        }
                    }
                }
            }
        });

    }


/*
    private void refercodechec() {
        setProgressDialog("");

        ConnectionServer connectionServer = new ConnectionServer();
        connectionServer.set_url(Constants.REFER_CODE_CHECK);
        connectionServer.requestedMethod("POST");
        connectionServer.buildParameter("name", signup_name.getText().toString());
        connectionServer.buildParameter("email", signup_email.getText().toString());
        connectionServer.buildParameter("contact", signup_contact.getText().toString());
        connectionServer.buildParameter("password", signup_password.getText().toString());
        connectionServer.buildParameter("city", spinner_value);
        connectionServer.execute(output -> {
            Log.e("output", output);
            JsonHelper jsonHelper = new JsonHelper(output);
            if (jsonHelper.isValidJson()) {
                String response = jsonHelper.GetResult("response");
                if (response.equals("TRUE")) {
                    remove_progress_Dialog();
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
//                                 login fail
                }
            }
        });
    }
*/

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
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