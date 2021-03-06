package org.aldofrankmarco.shak.authentication.controllers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.aldofrankmarco.shak.R;
import org.aldofrankmarco.shak.services.AuthenticationService;
import org.aldofrankmarco.shak.services.ServiceGenerator;

/**
 * Activity principale dell'applicazione, gestisce la visibilità dei tipi di autenticazione
 * (login e signup)
 */
public class AccessActivity extends AppCompatActivity implements View.OnClickListener {

    private Fragment loginFragment;
    private Fragment signupFragment;

    private Button switchButton;

    private SharedPreferences sharedPreferences;

    private static AuthenticationService authenticationService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        loginFragment = new LoginFragment();

        switchButton = findViewById(R.id.switchButton);

        switchButton.setText(getString(R.string.auth_signup));
        switchButton.setOnClickListener(this);

        setFragment(loginFragment);

        sharedPreferences = getSharedPreferences("lawsRead", Context.MODE_PRIVATE);
        String areLawsRead = sharedPreferences.getString("lawsRead", null);

        if (areLawsRead == null) {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_baseline_privacy_tip_24)
                    .setTitle(R.string.title_access)
                    .setMessage(R.string.application_permission_information)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("lawsRead", "yes");
                            editor.commit();
                        }
                    })
                    .setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finishAffinity();
                        }
                    })
                    .show();
        }
    }

    /**
     * Se viene premuto il pulsante con id "switchButton" viene invertita l'interfaccia di login
     * con quella di signup e viceversa
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.switchButton) {
            // Azione sul frammento "auth_signup" per cambiare la scritta a seconda del contesto
            String switchButtonText = switchButton.getText().toString().trim();
            boolean isAuthSignupString = switchButtonText.equals(getString(R.string.auth_signup));

            if (isAuthSignupString) {
                // imposta frammento di registrazione
                switchButton.setText(R.string.auth_login);

                if (signupFragment == null) {
                    signupFragment = new SignupFragment();
                }

                setFragment(signupFragment);
            } else {
                // imposta frammento di login
                switchButton.setText(R.string.auth_signup);

                setFragment(loginFragment);
            }
        }
    }

    /**
     * @param fragment stato futuro del frammento
     *                 la funzione permette di cambiare il frammento da login a signup e viceversa
     */
    private void setFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.auth_fragment, fragment);
        transaction.commit();
    }

    public static AuthenticationService getAuthenticationService() {
        if (authenticationService==null){
            authenticationService = ServiceGenerator.createService(AuthenticationService.class);
        }

        return authenticationService;
    }
}
