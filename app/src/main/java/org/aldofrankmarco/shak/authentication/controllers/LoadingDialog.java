package org.aldofrankmarco.shak.authentication.controllers;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import org.aldofrankmarco.shak.R;

public class LoadingDialog {

    private Activity activity;
    private AlertDialog loadingDialog;

    public LoadingDialog(Activity activity){
        this.activity = activity;
    }

    void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_loading, null));
        builder.setCancelable(false);

        loadingDialog = builder.create();
        loadingDialog.show();
    }

    void dismissLoadingDialog(){
        if (loadingDialog!=null){
            loadingDialog.dismiss();
        }
    }
}
