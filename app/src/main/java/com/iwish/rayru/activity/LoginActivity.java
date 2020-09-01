package com.iwish.rayru.activity;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.iwish.rayru.R;
import com.iwish.rayru.fragment.LoginFragment_1;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        getSupportFragmentManager().beginTransaction().replace(R.id.Login_fragmentLayout ,  new LoginFragment_1()).commit() ;

    }
}
