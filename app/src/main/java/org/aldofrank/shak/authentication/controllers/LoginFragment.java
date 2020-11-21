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
import android.widget.ProgressBar;
import android.widget.Toast;

import org.aldofrank.shak.R;
import org.aldofrank.shak.authentication.http.login.LoginRequest;
import org.aldofrank.shak.authentication.http.login.LoginResponse;
import org.aldofrank.shak.services.AuthenticationService;
import org.aldofrank.shak.services.ServiceGenerator;
import org.aldofrank.shak.services.StreamsService;
import org.aldofrank.shak.streams.http.posts.PostsListResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    AuthenticationService authService = ServiceGenerator.createService(AuthenticationService.class);

    EditText usernameField;
    EditText passwordField;

    ProgressBar loadingBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private static String token;

    private void login() {

        LoginRequest loginRequest = new LoginRequest(usernameField.getText().toString().trim(), passwordField.getText().toString().trim());
        Toast.makeText(getActivity(), loginRequest.getUsername() + " " + loginRequest.getPassword(), Toast.LENGTH_LONG).show();

        Call<LoginResponse> call = authService.login(loginRequest);

        loadingBar.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call, final Response<LoginResponse> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), response.body().getUserFound().getEmail(), Toast.LENGTH_LONG).show();
                    token = "bearer " + response.body().getToken();
                    loadingBar.setVisibility(View.GONE);

                    StreamsService streamsService = ServiceGenerator.createService(StreamsService.class, token);

                    Toast.makeText(getActivity(), token, Toast.LENGTH_LONG).show();

                    Call<PostsListResponse> httpRequest = streamsService.getAllPosts();

                    httpRequest.enqueue(new Callback<PostsListResponse>() {

                        @Override
                        public void onResponse(Call<PostsListResponse> call, Response<PostsListResponse> response) {

                            if (response.isSuccessful()){
                                Toast.makeText(getActivity(), response.body().getArrayPosts().get(0).getUserId(), Toast.LENGTH_LONG).show();
                                //Log.i("list", response.body().getArrayPosts().get(0).getPostContent());
                            }else {
                                Toast.makeText(getActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<PostsListResponse> call, Throwable t) {
                            Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

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

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        Button loginButton = view.findViewById(R.id.loginButton);
        usernameField = view.findViewById(R.id.usernameField);
        passwordField = view.findViewById(R.id.passwordField);
        loadingBar = getActivity().findViewById(R.id.loadingBar);

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
