package org.aldofrank.shak.authentication.controllers;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.method.PasswordTransformationMethod;
import android.util.Log;
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
import org.aldofrank.shak.authentication.http.signup.SignupRequest;
import org.aldofrank.shak.authentication.http.signup.SignupResponse;
import org.aldofrank.shak.services.AuthenticationService;
import org.aldofrank.shak.services.ServiceGenerator;
import org.aldofrank.shak.streams.controllers.LoggedUserActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupFragment extends Fragment {

    AuthenticationService authService = ServiceGenerator.createService(AuthenticationService.class);

    private EditText emailField;
    TextView emailAlert;

    private EditText usernameField;
    TextView usernameAlert;

    private EditText passwordField;
    TextView passwordAlert;

    private Button signUpButton;

    private ProgressBar loadingBar;

    private String token;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void signup(){

        SignupRequest signupRequest = new SignupRequest(emailField.getText().toString().trim(), usernameField.getText().toString().trim(), passwordField.getText().toString().trim());

        Call<SignupResponse> call = authService.register(signupRequest);

        loadingBar = getActivity().findViewById(R.id.loadingBar);
        loadingBar.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {

                if (response.isSuccessful()){

                    token = response.body().getToken();

                    loadingBar.setVisibility(View.GONE);

                    Intent intentLoggedUser = new Intent(getActivity(), LoggedUserActivity.class);
                    intentLoggedUser.putExtra("authToken", token);
                    startActivity(intentLoggedUser);

                }else {

                    Toast.makeText(getActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();

                    loadingBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        emailField = view.findViewById(R.id.emailField);
        emailField.setTag("email");
        emailAlert = view.findViewById(R.id.alert_email_invalid);
        emailField.setOnFocusChangeListener(focusListener);

        usernameField = view.findViewById(R.id.usernameField);
        usernameField.setTag("username");
        usernameAlert = view.findViewById(R.id.alert_username_invalid);
        usernameField.setOnFocusChangeListener(focusListener);

        passwordField = view.findViewById(R.id.passwordField);
        passwordField.setTag("password");
        passwordAlert = view.findViewById(R.id.alert_password_invalid);
        passwordField.setOnFocusChangeListener(focusListener);

        signUpButton = view.findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup();
            }
        });

        passwordField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (event.getRawX() >= passwordField.getWidth() - 32) {

                        if (passwordField.getTransformationMethod() == null) {
                            passwordField.setTransformationMethod(new PasswordTransformationMethod());
                            passwordField.setCompoundDrawablesWithIntrinsicBounds(R.drawable.password_drawable_left, 0, R.drawable.eye_open_drawable_right, 0);
                            return true;
                        } else {
                            passwordField.setTransformationMethod(null);
                            passwordField.setCompoundDrawablesWithIntrinsicBounds(R.drawable.password_drawable_left, 0, R.drawable.eye_closed_drawable_right, 0);
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {

        public void onFocusChange(View v, boolean hasFocus) {

            EditText editText = (EditText) v;
            int fieldLength = editText.getText().toString().trim().length();

            if (!hasFocus) {

                if (v.getTag()=="username"){

                    if (fieldLength<4 || fieldLength>16){
                        usernameAlert.setVisibility(View.VISIBLE);
                    }

                }else if (v.getTag()=="password"){

                    if (fieldLength<8 || fieldLength>64){
                        passwordAlert.setVisibility(View.VISIBLE);
                    }

                }else if (v.getTag()=="email"){

                    String field = editText.getText().toString().trim();
                    Pattern regularExpression = Pattern.compile("@(.*?).");
                    Matcher m = regularExpression.matcher(field);

                    if (!m.find()){
                        emailAlert.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    };
}
