package org.aldofrank.shak.authentication.controllers;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.aldofrank.shak.R;
import org.aldofrank.shak.authentication.models.LoginRequest;
import org.aldofrank.shak.authentication.models.LoginResponse;
import org.aldofrank.shak.authentication.models.User;
import org.aldofrank.shak.authentication.services.AuthenticationService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginFragment extends Fragment {

    Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl("http://ec2-15-237-74-79.eu-west-3.compute.amazonaws.com/api/shak/")
            .addConverterFactory(GsonConverterFactory.create());

    Retrofit retrofit = builder.build();
    AuthenticationService userClient = retrofit.create(AuthenticationService.class);

    EditText usernameField;
    EditText passwordField;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private static String token;

    private void login() {

        LoginRequest loginRequest = new LoginRequest(usernameField.getText().toString().trim(), passwordField.getText().toString().trim());
        Toast.makeText(getActivity(), loginRequest.getUsername() + " " + loginRequest.getPassword(), Toast.LENGTH_LONG).show();

        Call<LoginResponse> call = userClient.login(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), response.body().getUserFound().getEmail(), Toast.LENGTH_LONG).show();
                    token = response.body().getToken();
                } else {
                    Toast.makeText(getActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        Button loginButton = view.findViewById(R.id.loginButton);
        usernameField = view.findViewById(R.id.usernameField);
        passwordField = view.findViewById(R.id.passwordField);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
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
}
