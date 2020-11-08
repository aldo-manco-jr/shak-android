package org.aldofrank.shak.authentication.controllers;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.aldofrank.shak.R;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private Fragment loginFragment;
    private Fragment signupFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginFragment = new LoginFragment();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment, loginFragment);
        transaction.commit();
    }

    protected void switchButton(View view){

        Button switchButton = findViewById(R.id.switchButton);

        if (switchButton.getText().toString().equals("SIGN UP")){

            switchButton.setText("LOG IN");

            signupFragment = new SignupFragment();

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.fragment, signupFragment);
            transaction.commit();

        }else if (switchButton.getText().toString().equals("LOG IN")){

            switchButton.setText("SIGN UP");

            loginFragment = new LoginFragment();

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.fragment, loginFragment);
            transaction.commit();
        }
    }
}
