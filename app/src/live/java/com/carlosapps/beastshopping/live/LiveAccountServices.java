package com.carlosapps.beastshopping.live;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.carlosapps.beastshopping.activites.LoginActivity;
import com.carlosapps.beastshopping.activites.MainActivity;
import com.carlosapps.beastshopping.entities.User;
import com.carlosapps.beastshopping.infrastructure.BeastShoppingApplication;
import com.carlosapps.beastshopping.infrastructure.Utils;
import com.carlosapps.beastshopping.services.AccountServices;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.squareup.otto.Subscribe;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;

public class LiveAccountServices extends BaseLiveService {
    public LiveAccountServices(BeastShoppingApplication application) {
        super(application);
    }

    @Subscribe
    public void RegisterUser(final AccountServices.RegisterUserRequest request){
        AccountServices.RegisteruserResponse response = new AccountServices.RegisteruserResponse();

        if (request.userEmail.isEmpty()){
            response.setPropertyErrors("email","Please put in your email.");
        }

        if (request.userName.isEmpty()){
            response.setPropertyErrors("userName","Please pur in your name.");
        }

        if (response.didSuceed()){
            request.progressDialog.show();

            SecureRandom random = new SecureRandom();
            final String randomPassword = new BigInteger(32,random).toString();



            auth.createUserWithEmailAndPassword(request.userEmail,randomPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                request.progressDialog.dismiss();
                                Toast.makeText(application.getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            } else{
                                auth.sendPasswordResetEmail(request.userEmail)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (!task.isSuccessful()){
                                                    request.progressDialog.dismiss();
                                                    Toast.makeText(application.getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                                } else{
                                                    Firebase reference = new Firebase(Utils.FIRE_BASE_USER_REFERENCE + Utils.encodeEmail(request.userEmail));

                                                    HashMap<String,Object> timeJoined = new HashMap<>();
                                                    timeJoined.put("dateJoined", ServerValue.TIMESTAMP);

                                                    reference.child("email").setValue(request.userEmail);
                                                    reference.child("name").setValue(request.userName);
                                                    reference.child("hasLoggedInWithPassword").setValue(false);
                                                    reference.child("timeJoined").setValue(timeJoined);

                                                    Toast.makeText(application.getApplicationContext(),"Please Check Your Email",Toast.LENGTH_LONG)
                                                            .show();

                                                    request.progressDialog.dismiss();

                                                    Intent intent = new Intent(application.getApplicationContext(), LoginActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    application.startActivity(intent);
                                                }
                                            }
                                        });
                            }
                        }
                    });

        }

        bus.post(response);
    }

    @Subscribe
    public void LogInUser(final AccountServices.LogUserInRequest request){
        AccountServices.LogUserInResponse response = new AccountServices.LogUserInResponse();

        if (request.userEmail.isEmpty()){
            response.setPropertyErrors("email","Please enter your email");
        }

        if (request.userPassword.isEmpty()){
            response.setPropertyErrors("password","Please enter your password");
        }

        if (response.didSuceed()){
            request.progressDialog.show();
            auth.signInWithEmailAndPassword(request.userEmail,request.userPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                request.progressDialog.dismiss();
                                Toast.makeText(application.getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            } else{
                                final Firebase userLocation = new Firebase(Utils.FIRE_BASE_USER_REFERENCE + Utils.encodeEmail(request.userEmail));
                                userLocation.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);
                                        if (user!=null){
                                            userLocation.child("hasLoggedInWithPassword").setValue(true);
                                            SharedPreferences sharedPreferences = request.sharedPreferences;
                                            sharedPreferences.edit().putString(Utils.EMAIL,Utils.encodeEmail(user.getEmail())).apply();
                                            sharedPreferences.edit().putString(Utils.USERNAME,user.getName()).apply();

                                            request.progressDialog.dismiss();
                                            Intent intent = new Intent(application.getApplicationContext(), MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_NEW_TASK);
                                            application.startActivity(intent);


                                        } else {
                                            request.progressDialog.dismiss();
                                            Toast.makeText(application.getApplicationContext(),"Failed to connect to server: Please try again",Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {
                                        request.progressDialog.dismiss();
                                        Toast.makeText(application.getApplicationContext(),firebaseError.getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    });

        }
        bus.post(response);
    }


    @Subscribe
    public void FacebookLogin(final AccountServices.LogUserInFacebookRequest request){
        request.progressDialog.show();

        AuthCredential authCredential = FacebookAuthProvider.getCredential(request.accessToken.getToken());


        auth.signInWithCredential(authCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            request.progressDialog.dismiss();
                            Toast.makeText(application.getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        } else{
                            final Firebase reference = new Firebase(Utils.FIRE_BASE_USER_REFERENCE + Utils.encodeEmail(request.userEmail));
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() == null){
                                        HashMap<String,Object> timeJoined = new HashMap<>();
                                        timeJoined.put("dateJoined", ServerValue.TIMESTAMP);

                                        reference.child("email").setValue(request.userEmail);
                                        reference.child("name").setValue(request.userName);
                                        reference.child("hasLoggedInWithPassword").setValue(true);
                                        reference.child("timeJoined").setValue(timeJoined);

                                    }
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                    request.progressDialog.dismiss();
                                    Toast.makeText(application.getApplicationContext(),firebaseError.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                            SharedPreferences sharedPreferences = request.sharedPreferences;
                            sharedPreferences.edit().putString(Utils.EMAIL,Utils.encodeEmail(request.userEmail)).apply();
                            sharedPreferences.edit().putString(Utils.USERNAME,request.userName).apply();

                            request.progressDialog.dismiss();
                            Intent intent = new Intent(application.getApplicationContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_NEW_TASK);
                            application.startActivity(intent);

                        }
                    }
                });
    }


}
