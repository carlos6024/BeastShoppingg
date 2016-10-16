package com.carlosapps.beastshopping.activites;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carlosapps.beastshopping.R;
import com.carlosapps.beastshopping.services.AccountServices;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.activity_register_logginButton)
    Button loginButton;

    @BindView(R.id.activity_register_linear_layout)
    LinearLayout linearLayout;

    @BindView(R.id.activity_register_userEmail)
    EditText userEmail;

    @BindView(R.id.activity_register_userName)
    EditText userName;

    @BindView(R.id.activity_register_registerButton)
    Button registerButton;


    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        linearLayout.setBackgroundResource(R.drawable.background_screen_two);


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading....");
        mProgressDialog.setMessage("Attempting to Register Account");
        mProgressDialog.setCancelable(false);


    }


    @OnClick(R.id.activity_register_logginButton)
    public void setLoginButton(){
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }

    @OnClick(R.id.activity_register_registerButton)
    public void setRegisterButton(){
        bus.post(new AccountServices.RegisterUserRequest(userName.getText().toString(),userEmail.getText().toString(),mProgressDialog));
    }

    @Subscribe
    public void RegisterUser(AccountServices.RegisteruserResponse response){
        if (!response.didSuceed()){
            userEmail.setError(response.getPropertyError("email"));
            userName.setError(response.getPropertyError("userName"));
        }
    }
}
