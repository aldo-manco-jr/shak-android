package org.aldofrank.shak.authentication.controllers;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.aldofrank.shak.R;

/**
 * Activity principale dell'applicazione, gestisce la visibilità dei tipi di autenticazione
 * (login e signup)
 */
public class AccessActivity extends AppCompatActivity implements View.OnClickListener {

    private Fragment loginFragment;
    private Fragment signupFragment;

    private Button switchButton;

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
            Boolean isAuthSignupString = switchButtonText.equals(getString(R.string.auth_signup));

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

}