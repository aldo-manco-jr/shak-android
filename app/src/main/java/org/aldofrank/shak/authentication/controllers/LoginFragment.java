package org.aldofrank.shak.authentication.controllers;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

import org.aldofrank.shak.R;
import org.aldofrank.shak.authentication.http.login.LoginRequest;
import org.aldofrank.shak.authentication.http.login.LoginResponse;
import org.aldofrank.shak.services.AuthenticationService;
import org.aldofrank.shak.services.ServiceGenerator;
import org.aldofrank.shak.streams.controllers.LoggedUserActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {

    private final AuthenticationService authService = ServiceGenerator.createService(AuthenticationService.class);

    private EditText usernameField;
    private EditText passwordField;

    private TextView usernameAlert;
    private TextView passwordAlert;

    private Button loginButton;

    private ProgressBar loadingBar;

    /**
     * Consente l'autenticazione tramite username e password.
     * I dati vengono inseriti in una richiesta http e mandati al server, se i dati sono corretti
     * l'utente viene autenticato.
     */
    private void login() {
        String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        LoginRequest loginRequest = new LoginRequest(username, password);

        Call<LoginResponse> httpRequest = authService.login(loginRequest);

        loadingBar = getActivity().findViewById(R.id.loadingBar);
        loadingBar.setVisibility(View.VISIBLE);

        httpRequest.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, final Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null : "body() non doveva essere null";

                    String token = response.body().getToken();
                    Intent intentLoggedUser = new Intent(getActivity(), LoggedUserActivity.class);

                    loadingBar.setVisibility(View.GONE);

                    intentLoggedUser.putExtra("authToken", token);
                    intentLoggedUser.putExtra("username", response.body().getUserFound().getUsername());
                    startActivity(intentLoggedUser);
                } else {
                    // errore a livello di applicazione
                    // response.code() == (401) -> token expired
                    Toast.makeText(getActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();

                    loadingBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // errore a livello di rete
                // network error, establishing connection with server, error creating http request, response
                // when there is an exception
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();

                loadingBar.setVisibility(View.GONE);
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

        usernameField.setOnFocusChangeListener(focusListener);
        passwordField.setOnFocusChangeListener(focusListener);

        loginButton.setOnClickListener(this);

        passwordField.setOnTouchListener(this);

        // Inflate the layout for this fragment
        return loginFragmentView;
    }

    /**
     * Se l'username e la password non sono valide, vengono mostrati degli errori
     */
    private View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {
            EditText editText = (EditText) v;
            int inputTextLength = editText.getText().toString().trim().length();

            if (!hasFocus) {
                if (v.getTag() == "username") {
                    if (inputTextLength < 4 || inputTextLength > 16) {
                        usernameAlert.setVisibility(View.VISIBLE);
                    } else {
                        usernameAlert.setVisibility(View.GONE);
                    }
                } else if (v.getTag() == "password") {
                    if (inputTextLength < 8 || inputTextLength > 64) {
                        passwordAlert.setVisibility(View.VISIBLE);
                    } else {
                        passwordAlert.setVisibility(View.GONE);
                    }
                }
            }
        }
    };

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.loginButton) {
            login();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean isTouched = event.getAction() == MotionEvent.ACTION_DOWN;
        if (isTouched) {
            final int eyeIconSize = 32;
            final int passwordFieldWidth = passwordField.getWidth();
            final int eyeWidthSize = passwordFieldWidth - eyeIconSize;

            if (event.getRawX() >= eyeWidthSize) {
                if (passwordField.getTransformationMethod() == null) {
                    // nascondi password
                    passwordField.setTransformationMethod(new PasswordTransformationMethod());
                    passwordField.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.password_drawable_left,
                            0,
                            R.drawable.eye_open_drawable_right,
                            0
                    );
                } else {
                    // mostra password
                    passwordField.setTransformationMethod(null);
                    passwordField.setCompoundDrawablesWithIntrinsicBounds(
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
