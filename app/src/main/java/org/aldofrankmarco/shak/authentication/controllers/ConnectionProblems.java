package org.aldofrankmarco.shak.authentication.controllers;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.aldofrankmarco.shak.R;

/**
 * L'activity gestisce una schermata che informa l'utente della presenza di errori dovuti alla
 * connessione (connessione assente) e invita tramite la pressione di un bottone a l'utente a
 * ritentare, rimandandolo a {@link MainActivity}
 */
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
            Intent intentFirstActivity = new Intent(this, MainActivity.class);

            intentFirstActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentFirstActivity);

            Intent intent = new Intent(this, ConnectionProblems.class);
            stopService(intent);

            ActivityCompat.finishAffinity(this);
        }
    }
}
