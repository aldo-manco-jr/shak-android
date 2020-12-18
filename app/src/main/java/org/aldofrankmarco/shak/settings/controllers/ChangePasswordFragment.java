package org.aldofrankmarco.shak.settings.controllers;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

import org.aldofrankmarco.shak.R;
import org.aldofrankmarco.shak.authentication.controllers.AccessActivity;
import org.aldofrankmarco.shak.models.User;
import org.aldofrankmarco.shak.services.AuthenticationService;
import org.aldofrankmarco.shak.services.NotificationsService;
import org.aldofrankmarco.shak.services.ServiceGenerator;
import org.aldofrankmarco.shak.settings.http.ChangePasswordRequest;
import org.aldofrankmarco.shak.streams.controllers.LoggedUserActivity;
import org.aldofrankmarco.shak.streams.controllers.OnBackPressed;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ChangePwdFragment è il fragment che ci permette di utilizzare la funzione di cambio password
 * dell'utente loggato
 */
public class ChangePasswordFragment extends Fragment implements View.OnClickListener, View.OnTouchListener, OnBackPressed {

    private EditText oldPwd;
    private EditText newPwd;
    private EditText confPwd;

    String old, next, confirm;

    private TextView oldPasswordAlert, newPasswordAlert, confPasswordAlert;
    private ProgressBar loadingBar;

    private Button changeButton;
    private Button exitButton;

    public ChangePasswordFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * creazione della vista e istanziazione degli oggetti della view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_changepwd, container, false);

        oldPwd = view.findViewById(R.id.oldPassword);
        newPwd = view.findViewById(R.id.newPassword);
        confPwd = view.findViewById(R.id.confirmPassword);
        changeButton = view.findViewById(R.id.changeButton);
        exitButton = view.findViewById(R.id.exitButton);

        oldPwd.setTag("oldPwd");
        newPwd.setTag("newPwd");
        confPwd.setTag("confPwd");

        oldPwd.setOnFocusChangeListener(focusListener);
        newPwd.setOnFocusChangeListener(focusListener);
        confPwd.setOnFocusChangeListener(focusListener);

        oldPasswordAlert = view.findViewById(R.id.oldPasswordAlert);
        newPasswordAlert = view.findViewById(R.id.newPasswordAlert);
        confPasswordAlert = view.findViewById(R.id.confPasswordAlert);

        changeButton.setOnClickListener(this);
        exitButton.setOnClickListener(this);

        oldPwd.setOnTouchListener(this);
        newPwd.setOnTouchListener(this);
        confPwd.setOnTouchListener(this);

        return view;
    }

    /**
     * invio dellla change password al server
     */
    public void changePwd() {
        ChangePasswordRequest changePasswordJson = new ChangePasswordRequest(old, next, confirm);
        Call<Object> httpRequest = AccessActivity.getAuthenticationService().changePassword(changePasswordJson);
        httpRequest.enqueue(new Callback<Object>() {

            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                //success
                if (response.isSuccessful()) {
                    loadingBar = getActivity().findViewById(R.id.loadingBar);
                    loadingBar.setVisibility(View.VISIBLE);
                    new AlertDialog.Builder(getContext())
                            .setIcon(android.R.drawable.ic_menu_upload)
                            .setMessage("Password Changed")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    LoggedUserActivity.getLoggedUserActivity().changeFragment(SettingsFragment.getSettingsFragment());
                                }
                            }).show();
                } else {
                    //errors
                    if (response.code() == 400) {
                        new AlertDialog.Builder(getContext())
                                .setIcon(android.R.drawable.stat_notify_error)
                                .setMessage("Wrong current password")
                                .setPositiveButton("OK", null).show();
                    } else if (response.code() == 500) {
                        new AlertDialog.Builder(getContext())
                                .setIcon(android.R.drawable.stat_notify_error)
                                .setMessage("Wrong change password on failure server request")
                                .setPositiveButton("OK", null).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    //controlli sulle editext delle password
    private View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {

        public void onFocusChange(View v, boolean hasFocus) {
            EditText editText = (EditText) v;
            int fieldLength = editText.getText().toString().trim().length();
            if (!hasFocus) {
                if (v.getTag() == "oldPwd") {
                    if (fieldLength < 8 || fieldLength > 64) {
                        oldPasswordAlert.setVisibility(View.VISIBLE);
                        oldPasswordAlert.setText("Password should be of 8-64 characters.");
                        changeButton.setEnabled(false);
                    } else {
                        oldPasswordAlert.setVisibility(View.GONE);
                        confPasswordAlert.setText("");
                        changeButton.setEnabled(true);
                    }
                }
                else if (v.getTag() == "newPwd") {
                    if ((fieldLength < 8 || fieldLength > 64)) {
                        newPasswordAlert.setVisibility(View.VISIBLE);
                        newPasswordAlert.setText("Password should be of 8-64 characters.");
                        changeButton.setEnabled(false);
                    } else {
                        newPasswordAlert.setVisibility(View.GONE);
                        confPasswordAlert.setText("");
                        changeButton.setEnabled(true);
                    }
                }
                else if (v.getTag() == "confPwd") {
                    if ((fieldLength < 8 || fieldLength > 64)) {
                        confPasswordAlert.setVisibility(View.VISIBLE);
                        confPasswordAlert.setText("Password should be of 8-64 characters.");
                        changeButton.setEnabled(false);
                    }else{
                        confPasswordAlert.setVisibility(View.GONE);
                        confPasswordAlert.setText("");
                        changeButton.setEnabled(true);
                    }
                    if (!newPwd.equals(confPwd)) {
                        confPasswordAlert.setVisibility(View.VISIBLE);
                        confPasswordAlert.setText("New password and Confirm Password must be the same");
                        changeButton.setEnabled(false);
                    } else {
                        confPasswordAlert.setVisibility(View.GONE);
                        newPasswordAlert.setText("");
                        changeButton.setEnabled(true);
                    }
                }
            }
        }
    };

    public boolean onTouch(View view, MotionEvent event) {

        EditText editText = (EditText) view;

        boolean isTouched = event.getAction() == MotionEvent.ACTION_DOWN;
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


    @Override
    public void onClick(View view) {

        old = oldPwd.getText().toString().trim();
        next = newPwd.getText().toString().trim();
        confirm = confPwd.getText().toString().trim();

        switch (view.getId()) {
            case R.id.changeButton:
                changePwd();
                break;
            case R.id.exitButton:
                LoggedUserActivity.getLoggedUserActivity().changeFragment(SettingsFragment.getSettingsFragment());
                break;
        }
    }

    @Override
    public void onBackPressed() {
        LoggedUserActivity.getLoggedUserActivity().changeFragment(SettingsFragment.getSettingsFragment());
    }
}