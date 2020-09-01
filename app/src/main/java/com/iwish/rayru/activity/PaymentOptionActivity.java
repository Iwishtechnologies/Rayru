package com.iwish.rayru.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iwish.rayru.R;
import com.iwish.rayru.config.Constants;
import com.iwish.rayru.config.JsonHelper;
import com.iwish.rayru.other.Session;
import com.iwish.rayru.other.Wallet;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.iwish.rayru.config.Constants.USER_CONTACT;
import static com.iwish.rayru.config.Constants.WALLET_AMOUNT;

public class
PaymentOptionActivity extends AppCompatActivity implements PaymentResultListener {

    private RelativeLayout rayru_wallet, online_payment, cases;
    private RadioButton rayruWallet, onlinePayments, caseRadios;
    private Button paymentButton;
    String Checker, real_amoun;
    private Map data;
    Session session;
    LinearLayout paymentAvailable;
    TextView edit_amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_option);

        getSupportActionBar().hide();

        rayru_wallet = (RelativeLayout) findViewById(R.id.rayru_wallet);
        online_payment = (RelativeLayout) findViewById(R.id.online_payment);
        cases = (RelativeLayout) findViewById(R.id.cases);

        rayruWallet = (RadioButton) findViewById(R.id.rayruWallet);
        onlinePayments = (RadioButton) findViewById(R.id.onlinePayments);
        caseRadios = (RadioButton) findViewById(R.id.caseRadios);

        paymentButton = (Button) findViewById(R.id.paymentButton);
        paymentAvailable = (LinearLayout) findViewById(R.id.paymentAvailable);

        edit_amount = (TextView) findViewById(R.id.amount);

        Intent intent = getIntent();
        real_amoun = intent.getStringExtra("amount");

        String rating = intent.getStringExtra("ration");


        session = new Session(this);
        data = session.getShare();

        Object number = data.get(USER_CONTACT);


        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile", number.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request1 = new Request.Builder().url(Constants.WALEET_MONEY_GET).post(body).build();


        okHttpClient.newCall(request1).enqueue(new okhttp3.Callback() {
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
                        String responses = jsonHelper.GetResult("response");
                        if (responses.equals("TRUE")) {
                            JSONArray jsonArray = jsonHelper.setChildjsonArray(jsonHelper.getCurrentJsonObj(), "data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonHelper.setChildjsonObj(jsonArray, i);
                                String amounts = jsonHelper.GetResult("wallet");
                                PaymentOptionActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        session.walletAdd(amounts);
                                    }
                                });

                            }
                        }
                    }
                }
            }
        });


        Object walletamount = data.get(WALLET_AMOUNT);
        if (walletamount != null)

            paymentAvailable.setVisibility(View.GONE);

        paymentButton.setOnClickListener(view1 -> {
            if (Checker != null) {
                switch (Checker) {
                    case "rayru_wallet":
                        break;
                    case "online_payment":
                        paymentonline();
                        break;
                    case "cases":
                        cash();
                        break;
                }

            } else {
                Toast.makeText(this, "Select Payment mode", Toast.LENGTH_SHORT).show();
            }

        });


    }

    private void paymentonline() {
        Checkout.preload(PaymentOptionActivity.this);
        Wallet wallet = new Wallet(PaymentOptionActivity.this);
        wallet.PaymentDetails(real_amoun);
    }

    public void clickerCheckMethod(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.rayru_wallet:
                this.Checker = "rayru_wallet";
                rayruWallet.setChecked(true);
                onlinePayments.setChecked(false);
                caseRadios.setChecked(false);
                paymentAvailable.setVisibility(View.VISIBLE);
                caluclu_amount();
                break;
            case R.id.online_payment:
                this.Checker = "online_payment";
                onlinePayments.setChecked(true);
                rayruWallet.setChecked(false);
                caseRadios.setChecked(false);
                paymentAvailable.setVisibility(View.GONE);
                break;
            case R.id.cases:
                this.Checker = "cases";
                caseRadios.setChecked(true);
                rayruWallet.setChecked(false);
                onlinePayments.setChecked(false);
                paymentAvailable.setVisibility(View.GONE);
                break;
        }

    }

    private void cash() {
//        View view = LayoutInflater.from(this).inflate(R.layout.row_successfully_payment , null);
        Intent intent = new Intent(PaymentOptionActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void caluclu_amount() {
        if (data.get(WALLET_AMOUNT) != null) {


            String wallet_amount = data.get(WALLET_AMOUNT).toString();
            int wallet_amt = Integer.parseInt(wallet_amount);
            int amount = Integer.parseInt(real_amoun);
            int amt = wallet_amt - amount;
            if (amt > wallet_amt) {
//            amount add wallet
            } else if (amt < wallet_amt) {
//            amount wallet successfully
                String show_amt = String.valueOf(amt);
                edit_amount.setText(getResources().getString(R.string.rs_symbol) + show_amt);

            }

        }
    }


    @Override
    public void onPaymentSuccess(String s) {
        switch (Checker) {
            case "rayru_wallet":
                break;
            case "online_payment":
                break;
        }
    }

    @Override
    public void onPaymentError(int i, String s) {

    }


//    @Override
//    public void onBackPressed() {
////        super.onBackPressed();
//        Intent intent = new Intent(PaymentOptionActivity.this , MainActivity.class);
//        startActivity(intent);
//
//    }
}

























