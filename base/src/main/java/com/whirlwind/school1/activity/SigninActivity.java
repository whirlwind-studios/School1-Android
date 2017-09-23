package com.whirlwind.school1.activity;

import android.os.Bundle;
import android.widget.Button;

import com.whirlwind.school1.R;
import com.whirlwind.school1.base.BaseActivity;

public class SigninActivity extends BaseActivity {

    Button logoutButton, anonymousButton, googleButton, facebookButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        anonymousButton = findViewById(R.id.activity_signin_button_anonymous);
        googleButton = findViewById(R.id.activity_signin_button_google);
        facebookButton = findViewById(R.id.activity_signin_button_facebook);

        //logoutButton=findViewById(R.id.activity_signin_button_logout);
        //logoutButton.setVisibility(View.GONE);
    }
}
