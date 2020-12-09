package org.aldofrank.shak.authentication.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import org.aldofrank.shak.R;
import org.aldofrank.shak.streams.controllers.LoggedUserActivity;

import java.util.concurrent.TimeUnit;

/**
 * Controlla se all'avvio è presente un token non scaduto, in caso affermativo avvia
 * {@link AccessActivity}, altrimenti {@link LoggedUserActivity}.
 * Nel caso non sia possibile accedere a Internet avvia {@link ConnectionProblems}.
 */
public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intentFirstActivity = null;

        if (isOnline()){
            // l'utente è connesso a internet
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.sharedpreferences_authentication), Context.MODE_PRIVATE);
            String token = sharedPreferences.getString(getString(R.string.sharedpreferences_token), null);

            if (token == null) {
                // non è mai stato effettuato alcun login
                intentFirstActivity = new Intent(this, AccessActivity.class);
            } else {
                try {
                    String username = JWTUtils.decodeUsernameLoggedUser(token).getString("username");
                    String id = JWTUtils.decodeUsernameLoggedUser(token).getString("_id");
                    long expirationDate = JWTUtils.decodeUsernameLoggedUser(token).getLong("expirationDate");

                    long currentTimeMillis = System.currentTimeMillis();
                    long currentTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(currentTimeMillis);

                    if (currentTimeSeconds <= expirationDate) {
                        intentFirstActivity = new Intent(this, LoggedUserActivity.class);
                        intentFirstActivity.putExtra("authToken", token);
                        intentFirstActivity.putExtra("username", username);
                        intentFirstActivity.putExtra("_id", id);
                    } else {
                        // token scaduto, occorre effettuare nuovamente il login
                        intentFirstActivity = new Intent(this, AccessActivity.class);
                    }
                } catch (Exception ignored) {}
            }
        } else {
            // esistono problemi di connessione
            intentFirstActivity = new Intent(this, ConnectionProblems.class);
        }

        assert intentFirstActivity != null: "intentFirstActivity non poteva essere null";

        intentFirstActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentFirstActivity);
        ActivityCompat.finishAffinity(this);
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }
}
