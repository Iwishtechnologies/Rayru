package com.iwish.rayru.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import com.iwish.rayru.R;
import com.iwish.rayru.other.Session;

import org.json.JSONException;
import org.json.JSONObject;

public class BillActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private TextView buttonOption, picAddress, dropAddressss, amounts;
    private String pickAddress, dropAddress, amount;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        getSupportActionBar().hide();


        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        buttonOption = (TextView) findViewById(R.id.buttonOption);
        picAddress = (TextView) findViewById(R.id.picAddress);
        dropAddressss = (TextView) findViewById(R.id.dropAddressss);
        amounts = (TextView) findViewById(R.id.amounts);

        Intent intent1 = getIntent();
        String booking_confirm = intent1.getStringExtra("booking_confirm");
        if (booking_confirm == null) {
            String datas = intent1.getStringExtra("confirmRide");

            try {
                JSONObject jsonObject = new JSONObject(datas);
                JSONObject data = jsonObject.getJSONObject("data");
                pickAddress = data.getString("pic");
                dropAddress = data.getString("drop");
                amount = data.getString("amt");
                picAddress.setText(pickAddress);
                dropAddressss.setText(dropAddress);
                amounts.setText(amount);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            buttonOption.setOnClickListener(view -> {
                Intent intent = new Intent(BillActivity.this, PaymentOptionActivity.class);
                intent.putExtra("amount", amount);
                intent.putExtra("ration", ratingBar.getRating());
                startActivity(intent);
            });
        } else {

            picAddress.setText(intent1.getStringExtra("pickaddress"));
            dropAddressss.setText(intent1.getStringExtra("dropaddress"));
            amounts.setText(intent1.getStringExtra("amount"));

        }

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(BillActivity.this, MainActivity.class);
        startActivity(intent);

    }
}
