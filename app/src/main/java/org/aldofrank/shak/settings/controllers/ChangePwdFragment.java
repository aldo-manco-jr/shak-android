package org.aldofrank.shak.settings.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import org.aldofrank.shak.authentication.http.SignupResponse;
import org.aldofrank.shak.models.User;
import org.aldofrank.shak.people.http.GetUserByUsernameResponse;
import org.aldofrank.shak.services.AuthenticationService;
import org.aldofrank.shak.services.NotificationsService;
import org.aldofrank.shak.services.ServiceGenerator;
import org.aldofrank.shak.services.UsersService;
import org.aldofrank.shak.settings.http.ChangePasswordRequest;
import org.aldofrank.shak.streams.controllers.LoggedUserActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.internal.Version;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *  ChangePwdFragment è il fragment che ci permette di utilizzare la funzione di cambio password
 *  dell'utente loggato
 */
public class ChangePwdFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {

    private EditText oldPwd;
    private EditText newPwd;
    private EditText confPwd;

    private TextView passwordAlert;

    private ProgressBar loadingBar;
    private User user;
    private SettingsFragment settings;

    private Button changeButton;

    public ChangePwdFragment (){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); }

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
        changeButton= view.findViewById(R.id.changeButton);

        // oldPasswordAlert = view.findViewById(R.id.alert_oldPassword_invalid);
        // newPasswordAlert = view.findViewById(R.id.alert_newPassword_invalid);
        // confirmPasswordAlert = view.findViewById(R.id.alert_confirmPassword_invalid);

        oldPwd.setOnFocusChangeListener(focusListener);
        newPwd.setOnFocusChangeListener(focusListener);
        confPwd.setOnFocusChangeListener(focusListener);

        changeButton.setOnClickListener(this);

        return view;
    }

    /**
     * invio dellla change password al server
     */
    public void changePwd(){

        NotificationsService changePwdService = ServiceGenerator.createService(NotificationsService.class);

        //inseriamo i dati nel json

        String test1 = oldPwd.getText().toString().trim();
        String test2 = newPwd.getText().toString().trim();
        String test3 = confPwd.getText().toString().trim();


        ChangePasswordRequest changePasswordJson = new ChangePasswordRequest(
            test1,
            test2,
            test3
        );
        Call<Object> httpRequest =  changePwdService.changePassword(changePasswordJson);
        //loadingBar = getActivity().findViewById(R.id.loadingBar);
        //loadingBar.setVisibility(View.VISIBLE);
        httpRequest.enqueue(new Callback<Object>() {


            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()){
                    getSettingsFragment();
                    getChildFragmentManager()
                            .beginTransaction()
                            .replace(R.id.changePwdFragment, settings)
                            .addToBackStack("openChangePwdFragment")
                            .commit();
                    Toast.makeText(getActivity(), R.string.password_changed, Toast.LENGTH_LONG).show();
                }else {
                    getSettingsFragment();
                    getChildFragmentManager()
                            .beginTransaction()
                            .replace(R.id.changePwdFragment, settings)
                            .addToBackStack("openChangePwdFragment")
                            .commit();
                    Toast.makeText(getActivity(), R.string.password_changed_error, Toast.LENGTH_LONG).show();
//                    loadingBar.setVisibility(View.GONE);
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
                 if (v.getTag()=="oldpwd"){
                    if (fieldLength<8 || fieldLength>64){
                        passwordAlert.setVisibility(View.VISIBLE);
                    }else {
                        passwordAlert.setVisibility(View.GONE);
                    }
                }else if (v.getTag()=="newPwd"){
                     if (fieldLength<8 || fieldLength>64){
                         passwordAlert.setVisibility(View.VISIBLE);
                     }else {
                         passwordAlert.setVisibility(View.GONE);
                     }
                 }else if (v.getTag()=="confPwd"){
                     if (fieldLength<8 || fieldLength>64){
                         passwordAlert.setVisibility(View.VISIBLE);
                     }else {
                         passwordAlert.setVisibility(View.GONE);
                     }
                 }
            }
        }
    };

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        boolean isTouched = event.getAction() == MotionEvent.ACTION_DOWN;
        if (isTouched) {
            final int eyeIconSize = 32;
            final int passwordFieldWidth = oldPwd.getWidth();
            final int eyeWidthSize = passwordFieldWidth - eyeIconSize;

            if (event.getRawX() >= eyeWidthSize) {
                if (oldPwd.getTransformationMethod() == null) {
                    // nascondi password
                    oldPwd.setTransformationMethod(new PasswordTransformationMethod());
                    oldPwd.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.password_drawable_left,
                            0,
                            R.drawable.eye_open_drawable_right,
                            0
                    );
                } else {
                    // mostra password
                    oldPwd.setTransformationMethod(null);
                    oldPwd.setCompoundDrawablesWithIntrinsicBounds(
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
        if (view.getId() == R.id.changeButton) {
            changePwd();
        }
    }

    public SettingsFragment getSettingsFragment() {
        if (this.settings == null) {
            this.settings = new SettingsFragment();
        }
        return settings;
    }

}