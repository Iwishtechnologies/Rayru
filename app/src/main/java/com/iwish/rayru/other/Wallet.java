package com.iwish.rayru.other;

import android.app.Activity;

import com.razorpay.Checkout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static com.iwish.rayru.config.Constants.USER_CONTACT;
import static com.iwish.rayru.config.Constants.USER_EMAIL;
import static com.iwish.rayru.config.Constants.USER_NAME;

public class Wallet {

    private Activity context;
    private Session session;
    private Map data;

    public Wallet(Activity activity) {

        this.context = activity;
        session = new Session(context);

    }

    public void PaymentDetails(String amount) {


//        this.editAmount = money ;

        data = session.getShare();
        String name = data.get(USER_NAME).toString();
        String mob = data.get(USER_CONTACT).toString();
        String email = data.get(USER_EMAIL).toString();
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_zvQhBxwwcpaCDA");
        final Activity activity = context;

        JSONObject object = new JSONObject();
        try {
            object.put("name", name);
            object.put("description", "Iwish");
            object.put("amount", Double.valueOf(amount) * 100);
            object.put("current", "INR");
            object.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");

            JSONObject preFill = new JSONObject();
            preFill.put("email", email);
            preFill.put("contact", mob);
            object.put("prfill", preFill);

            checkout.open(activity, object);


        } catch (JSONException e) {
//            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }


}
