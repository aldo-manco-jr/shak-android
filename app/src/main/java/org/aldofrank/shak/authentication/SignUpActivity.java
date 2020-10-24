package org.aldofrank.shak.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.aldofrank.shak.R;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    protected void switchToLogin(View view){

        Intent switchToLogin = new Intent(this, MainActivity.class);
        startActivity(switchToLogin);
    }
}
