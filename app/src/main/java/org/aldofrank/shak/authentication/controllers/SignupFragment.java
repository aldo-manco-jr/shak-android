package org.aldofrank.shak.authentication.controllers;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.aldofrank.shak.R;

public class SignupFragment extends Fragment {

    EditText usernameField;
    EditText passwordField;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        Button signUpButton = view.findViewById(R.id.signUpButton);
        usernameField = view.findViewById(R.id.usernameField);
        passwordField = view.findViewById(R.id.passwordField);

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
