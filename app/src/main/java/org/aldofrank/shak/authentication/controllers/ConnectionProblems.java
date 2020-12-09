package org.aldofrank.shak.authentication.controllers;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.aldofrank.shak.R;

public class ConnectionProblems extends AppCompatActivity implements View.OnClickListener {

    private Button tryAgainButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_problems);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        tryAgainButton = findViewById(R.id.try_connect_again);

        tryAgainButton.setOnClickListener(this);
    }

    /**
     * Viene eseguito un nuovo intent verso {@link MainActivity} per riprovare a stabilire una
     * connessione
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.try_connect_again) {
            // avvio nuovamente di MainaAtivity per provare ad effettuare la connessione
            Intent intentFirstActivity = new Intent(this, ConnectionProblems.class);

            intentFirstActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentFirstActivity);
            ActivityCompat.finishAffinity(this);
        }
    }

}
