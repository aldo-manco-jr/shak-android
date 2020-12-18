package org.aldofrankmarco.shak.authentication.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import org.aldofrankmarco.shak.R;
import org.aldofrankmarco.shak.authentication.http.LoginRequest;
import org.aldofrankmarco.shak.authentication.http.LoginResponse;
import org.aldofrankmarco.shak.services.AuthenticationService;
import org.aldofrankmarco.shak.services.ServiceGenerator;
import org.aldofrankmarco.shak.streams.controllers.LoggedUserActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Frammento che consente l'inserimento dei dati inerenti il login e gestisce il contatto iniziale
 * con il server remoto shak.
 */
public class LoginFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {

    private SharedPreferences sharedPreferences;

    private EditText usernameField;
    private EditText passwordField;

    private TextView usernameAlert;
    private TextView passwordAlert;

    private Button loginButton;

    private ProgressBar loadingBar;

    /**
     * Consente l'accesso tramite l'inserimento di username e password.
     * I dati vengono inseriti in una richiesta http e mandati al server, se i dati sono corretti
     * l'utente eggettua l'accesso.
     */
    private void login() {
        String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        LoginRequest loginRequest = new LoginRequest(username, password);

        Call<LoginResponse> httpRequest = AccessActivity.getAuthenticationService().login(loginRequest);

        loadingBar = getActivity().findViewById(R.id.loadingBar);
        loadingBar.setVisibility(View.VISIBLE);

        httpRequest.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, final Response<LoginResponse> response) {

                loadingBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    assert response.body() != null : "body() non doveva essere null";

                    String token = response.body().getToken();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.sharedpreferences_token), token);
                    editor.commit();

                    Intent intentLoggedUser = new Intent(getActivity(), LoggedUserActivity.class);
                    intentLoggedUser.putExtra("authToken", token);
                    intentLoggedUser.putExtra("username", response.body().getUserFound().getUsername());
                    intentLoggedUser.putExtra("_id", response.body().getUserFound().getId());
                    intentLoggedUser.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentLoggedUser);
                    ActivityCompat.finishAffinity(getActivity());
                } else {

                    if (response.code() == 404){
                        new AlertDialog.Builder(getContext())
                                .setIcon(android.R.drawable.stat_notify_error)
                                .setTitle("User Not Found")
                                .setMessage("Username entered does not correspond\nto any SHAK registered user.")
                                .setPositiveButton("OK", null).show();
                    }else if (response.code() == 403){
                        new AlertDialog.Builder(getContext())
                                .setIcon(android.R.drawable.stat_notify_error)
                                .setTitle("Wrong Password")
                                .setMessage("Password entered does not match the account password of user.")
                                .setPositiveButton("OK", null).show();
                    }else{
                        new AlertDialog.Builder(getContext())
                                .setIcon(android.R.drawable.stat_notify_error)
                                .setTitle("Server Error")
                                .setMessage("Internal server error.")
                                .setPositiveButton("OK", null).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // errore a livello di rete

                loadingBar.setVisibility(View.GONE);

                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.stat_notify_error)
                        .setTitle("Server Error")
                        .setMessage("Internal server error.")
                        .setPositiveButton("OK", null).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View loginFragmentView = inflater.inflate(R.layout.fragment_login, container, false);

        usernameField = loginFragmentView.findViewById(R.id.usernameField);
        usernameAlert = loginFragmentView.findViewById(R.id.alert_username_invalid);
        passwordField = loginFragmentView.findViewById(R.id.passwordField);
        passwordAlert = loginFragmentView.findViewById(R.id.alert_password_invalid);
        loginButton = loginFragmentView.findViewById(R.id.loginButton);

        usernameField.setTag("username");
        passwordField.setTag("password");

        usernameField.addTextChangedListener(checkUsernameField);
        passwordField.addTextChangedListener(checkPasswordField);

        loginButton.setOnClickListener(this);
        passwordField.setOnTouchListener(this);

        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.sharedpreferences_authentication), Context.MODE_PRIVATE);

        return loginFragmentView;
    }

    private boolean isUsernameValid(EditText usernameField){
        int usernameTextFieldLength = usernameField.getText().toString().trim().length();
        return (usernameTextFieldLength>=4 && usernameTextFieldLength<=16);
    }

    private boolean isPasswordValid(EditText passwordField){
        int passwordTextFieldLength = passwordField.getText().toString().trim().length();
        return (passwordTextFieldLength>=8 && passwordTextFieldLength<=64);
    }

    TextWatcher checkUsernameField = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.length() < 4 || charSequence.length() > 16) {
                loginButton.setEnabled(false);
                loginButton.setTextColor(Color.parseColor("#a8acaf"));
                usernameAlert.setVisibility(View.VISIBLE);
            } else {
                usernameAlert.setVisibility(View.GONE);

                if (isPasswordValid(passwordField)) {
                    loginButton.setEnabled(true);
                    loginButton.setTextColor(Color.parseColor("#004317"));
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    TextWatcher checkPasswordField = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.length() < 8 || charSequence.length() > 64) {
                loginButton.setEnabled(false);
                loginButton.setTextColor(Color.parseColor("#a8acaf"));
                passwordAlert.setVisibility(View.VISIBLE);
            } else {
                passwordAlert.setVisibility(View.GONE);

                if (isUsernameValid(usernameField)) {
                    loginButton.setEnabled(true);
                    loginButton.setTextColor(Color.parseColor("#004317"));
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.loginButton) {
            login();
        }
    }

    /**
     * Gestisce la visibilità del campo di testo contentente la password
     * @return true se è stata eseguita un azione, false altrimenti
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean isTouched = event.getAction() == MotionEvent.ACTION_DOWN;
        EditText editText = (EditText) v;

        if (isTouched) {
            final int eyeIconSize = 64;
            final int passwordFieldWidth = editText.getWidth();
            final int eyeWidthSize = passwordFieldWidth - eyeIconSize;

            if (event.getRawX() >= eyeWidthSize) {
                if (editText.getTransformationMethod() == null) {
                    // nascondi password
                    editText.setTransformationMethod(new PasswordTransformationMethod());
                    editText.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.password_drawable_left,
                            0,
                            R.drawable.eye_open_drawable_right,
                            0
                    );
                } else {
                    // mostra password
                    editText.setTransformationMethod(null);
                    editText.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.password_drawable_left,
                            0,
                            R.drawable.eye_closed_drawable_right,
                            0
                    );
                }

                return true;
            }
        }
        return false;
    }
}