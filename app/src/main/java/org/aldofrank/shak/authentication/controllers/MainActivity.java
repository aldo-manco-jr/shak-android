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
import org.json.JSONObject;

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

            if (token != null) {
                try {
                    JSONObject decodeData = JWTUtils.decodeUsernameLoggedUser(token);

                    if (decodeData != null){
                        // è stato possibile recuperare i dati
                        String username = decodeData.getString("username");
                        String id = decodeData.getString("_id");
                        long expirationDate = decodeData.getLong("expirationDate");

                        long currentTimeMillis = System.currentTimeMillis();
                        long currentTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(currentTimeMillis);

                        if (currentTimeSeconds <= expirationDate) {
                            // il token è ancora valido
                            intentFirstActivity = new Intent(this, LoggedUserActivity.class);
                            intentFirstActivity.putExtra("authToken", token);
                            intentFirstActivity.putExtra("username", username);
                            intentFirstActivity.putExtra("_id", id);
                        }
                    }
                } catch (Exception ignored) {}
            }
        } else {
            // esistono problemi di connessione
            intentFirstActivity = new Intent(this, ConnectionProblems.class);
        }

        if (intentFirstActivity == null){
            // non era stata effettuata alcuna scelta, quindi l'utente non era connesso
            intentFirstActivity = new Intent(this, AccessActivity.class);
        }

        intentFirstActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentFirstActivity);
        ActivityCompat.finishAffinity(this);
    }

    /**
     * Verifica se l'utente è connesso a internet
     */
    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }
}
