package org.aldofrank.shak.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.aldofrank.shak.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    protected void switchToSignUp(View view){

        Intent switchToSignUp = new Intent(this, SignUpActivity.class);
        startActivity(switchToSignUp);
    }
}
