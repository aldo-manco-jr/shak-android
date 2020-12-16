package org.aldofrankmarco.shak.authentication.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;

import org.aldofrankmarco.shak.R;
import org.aldofrankmarco.shak.authentication.http.SignupRequest;
import org.aldofrankmarco.shak.authentication.http.SignupResponse;
import org.aldofrankmarco.shak.services.AuthenticationService;
import org.aldofrankmarco.shak.services.ServiceGenerator;
import org.aldofrankmarco.shak.streams.controllers.LoggedUserActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Frammento che consente l'inserimento dei dati inerenti la registrazione e gestisce il contatto
 * iniziale con il server remoto shak.
 */
public class SignupFragment extends Fragment implements View.OnClickListener, View.OnTouchListener{

    private final AuthenticationService authService = ServiceGenerator.createService(AuthenticationService.class);

    private SharedPreferences sharedPreferences;

    private EditText emailField;
    private EditText usernameField;
    private EditText passwordField;

    private TextView emailAlert;
    private TextView usernameAlert;
    private TextView passwordAlert;

    private Button signUpButton;

    private ProgressBar loadingBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Consente la registrazione tramite l'inserimento di email, username e password.
     * I dati vengono inseriti in una richiesta http e mandati al server, se i dati sono corretti
     * l'utente viene registrato.
     */
    protected void signup(){

        SignupRequest signupRequest = new SignupRequest(
                emailField.getText().toString().trim(),
                usernameField.getText().toString().trim(),
                passwordField.getText().toString().trim()
        );

        Call<SignupResponse> httpRequest = authService.register(signupRequest);

        loadingBar = getActivity().findViewById(R.id.loadingBar);
        loadingBar.setVisibility(View.VISIBLE);

        httpRequest.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {

                loadingBar.setVisibility(View.GONE);

                if (response.isSuccessful()){
                    String token = response.body().getToken();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.sharedpreferences_token), token);
                    editor.commit();

                    Intent intentLoggedUser = new Intent(getActivity(), LoggedUserActivity.class);
                    intentLoggedUser.putExtra("authToken", token);
                    startActivity(intentLoggedUser);
                }else {

                    if (response.code() == 409){
                        new AlertDialog.Builder(getContext())
                                .setIcon(android.R.drawable.stat_notify_error)
                                .setTitle("Email Already Signed Up")
                                .setMessage("Email entered is used by another user in SHAK.")
                                .setPositiveButton("OK", null).show();
                    }else if (response.code() == 403){
                        new AlertDialog.Builder(getContext())
                                .setIcon(android.R.drawable.stat_notify_error)
                                .setTitle("Username Already Signed Up")
                                .setMessage("Username entered is used by another user in SHAK.")
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
            public void onFailure(Call<SignupResponse> call, Throwable t) {

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
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        emailField = view.findViewById(R.id.emailField);
        emailAlert = view.findViewById(R.id.alert_email_invalid);
        usernameField = view.findViewById(R.id.usernameField);
        usernameAlert = view.findViewById(R.id.alert_username_invalid);
        passwordField = view.findViewById(R.id.passwordField);
        passwordAlert = view.findViewById(R.id.alert_password_invalid);
        signUpButton = view.findViewById(R.id.signUpButton);

        emailField.setTag("email");
        usernameField.setTag("username");
        passwordField.setTag("password");

        emailField.setOnFocusChangeListener(focusListener);
        usernameField.setOnFocusChangeListener(focusListener);
        passwordField.setOnFocusChangeListener(focusListener);

        signUpButton.setOnClickListener(this);

        passwordField.setOnTouchListener(this);

        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.sharedpreferences_authentication), Context.MODE_PRIVATE);

        return view;
    }

    /**
     * Se l'username, la password o l'email non sono validi vengono mostrati degli errori
     */
    private View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {

        public void onFocusChange(View v, boolean hasFocus) {
            EditText editText = (EditText) v;
            int fieldLength = editText.getText().toString().trim().length();

            if (!hasFocus) {
                if (v.getTag()=="username"){
                    if (fieldLength<4 || fieldLength>16){
                        usernameAlert.setVisibility(View.VISIBLE);
                    }else {
                        usernameAlert.setVisibility(View.GONE);
                    }
                }else if (v.getTag()=="password"){
                    if (fieldLength<8 || fieldLength>64){
                        passwordAlert.setVisibility(View.VISIBLE);
                    }else {
                        passwordAlert.setVisibility(View.GONE);
                    }
                }else if (v.getTag()=="email"){
                    String field = editText.getText().toString().trim();
                    Pattern regularExpression = Pattern.compile("@(.*?).");
                    Matcher m = regularExpression.matcher(field);

                    if (!m.find()){
                        emailAlert.setVisibility(View.VISIBLE);
                    }else {
                        emailAlert.setVisibility(View.GONE);
                    }
                }
            }
        }
    };

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.signUpButton) {
            signup();
        }
    }

    /**
     * Gestisce la visibilità del campo di testo contentente la password
     * @return true se è stata eseguita un azione, false altrimenti
     */
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        boolean isTouched = event.getAction() == MotionEvent.ACTION_DOWN;
        EditText editText = (EditText) view;

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
