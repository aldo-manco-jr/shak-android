package org.aldofrank.shak.authentication.controllers;

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
import android.widget.Toast;

import org.aldofrank.shak.R;
import org.aldofrank.shak.authentication.http.signup.SignupRequest;
import org.aldofrank.shak.authentication.http.signup.SignupResponse;
import org.aldofrank.shak.services.AuthenticationService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupFragment extends Fragment {

    Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl("http://ec2-15-237-74-79.eu-west-3.compute.amazonaws.com/api/shak/")
            .addConverterFactory(GsonConverterFactory.create());

    Retrofit retrofit = builder.build();

    AuthenticationService authService = retrofit.create(AuthenticationService.class);

    EditText emailField;
    EditText usernameField;
    EditText passwordField;

    private static String token;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void signup(){

        SignupRequest signupRequest = new SignupRequest(emailField.getText().toString().trim(), usernameField.getText().toString().trim(), passwordField.getText().toString().trim());

        Call<SignupResponse> call = authService.register(signupRequest);

        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {

                if (response.isSuccessful()){
                    //Toast.makeText(getActivity(), response.body().getUserFound().getProfileImageId() + " " + response.body().getToken(), Toast.LENGTH_LONG).show();
                    Log.i("filo", response.body().getUserRegistered().toString());
                }else {
                    Toast.makeText(getActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
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

        Button signUpButton = view.findViewById(R.id.signUpButton);
        emailField = view.findViewById(R.id.emailField);
        usernameField = view.findViewById(R.id.usernameField);
        passwordField = view.findViewById(R.id.passwordField);

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
}
