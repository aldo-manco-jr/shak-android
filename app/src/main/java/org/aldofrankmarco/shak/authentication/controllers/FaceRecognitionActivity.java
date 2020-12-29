package org.aldofrankmarco.shak.authentication.controllers;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.aldofrankmarco.shak.R;
import org.aldofrankmarco.shak.authentication.http.LoginResponse;
import org.aldofrankmarco.shak.streams.controllers.LoggedUserActivity;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FaceRecognitionActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView photoTaken;

    private Button openCameraButton;
    private Button faceAuthenticationButton;

    private int TAKE_PHOTO = 100;

    private String imageEncoded;

    private Python py;
    private PyObject pyObject;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);

        photoTaken = findViewById(R.id.photo_taken);
        openCameraButton = findViewById(R.id.open_camera_button);
        faceAuthenticationButton = findViewById(R.id.login_face_recognition_button);

        sharedPreferences = getSharedPreferences(getString(R.string.sharedpreferences_authentication), Context.MODE_PRIVATE);

        if (ContextCompat.checkSelfPermission(
                FaceRecognitionActivity.this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    FaceRecognitionActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    }, TAKE_PHOTO);
        }

        /*if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        py = Python.getInstance();
        pyObject = py.getModule("face-recognition");*/

        openCameraButton.setOnClickListener(this);
        faceAuthenticationButton.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO) {
            try {
                Bitmap photoTaken = (Bitmap) data.getExtras().get("data");
                this.photoTaken.setImageBitmap(photoTaken);
                imageEncoded = bitmapToBase64(photoTaken);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.open_camera_button) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, TAKE_PHOTO);
        } else if (view.getId() == R.id.login_face_recognition_button) {

            // mettere nome funzione python al posto di "main", poi inserire i parametri con la virgola
            // accedere al risultato della funzione mediante "object.toString"
            // PyObject faceOwner = pyObject.callAttr("main");
            // faceOwner.toString()

            loginFaceAuthenticaton();
        }
    }

    private void loginFaceAuthenticaton() {

         if (imageEncoded == null) {
            Snackbar.make(getWindow().getDecorView().getRootView(), "Can't Login Without Taking Photo of Yourself.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }

        JsonObject imageData = new JsonObject();
        imageData.addProperty("image", "data:image/png;base64," + imageEncoded);

        Call<LoginResponse> httpRequest = AccessActivity.getAuthenticationService().loginFaceAuthentication(imageData);

        final LoadingDialog loadingDialog = new LoadingDialog(FaceRecognitionActivity.this);
        loadingDialog.startLoadingDialog();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismissLoadingDialog();
            }
        }, 10000);

        httpRequest.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                loadingDialog.dismissLoadingDialog();

                if (response.isSuccessful()) {
                    assert response.body() != null : "body() non doveva essere null";

                    String token = response.body().getToken();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.sharedpreferences_token), token);
                    editor.apply();

                    Intent intentLoggedUser = new Intent(FaceRecognitionActivity.this, LoggedUserActivity.class);
                    intentLoggedUser.putExtra("authToken", token);
                    intentLoggedUser.putExtra("username", response.body().getUserFound().getUsername());
                    intentLoggedUser.putExtra("_id", response.body().getUserFound().getId());
                    intentLoggedUser.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentLoggedUser);

                    Intent intent = new Intent(FaceRecognitionActivity.this, AccessActivity.class);
                    stopService(intent);

                    finishAffinity();
                } else {

                    if (response.code() == 404){
                        new AlertDialog.Builder(getApplicationContext())
                                .setIcon(android.R.drawable.stat_notify_error)
                                .setTitle("User Not Found")
                                .setMessage("Face in the photo taken does not correspond\nto any SHAK registered user.")
                                .setPositiveButton("OK", null).show();
                    }else{
                        new AlertDialog.Builder(getApplicationContext())
                                .setIcon(android.R.drawable.stat_notify_error)
                                .setTitle("Server Error")
                                .setMessage("Internal server error.")
                                .setPositiveButton("OK", null).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * @param bitmap un'immagine in formato bitmap
     * @return una stringa che rappresenta un'immagine codificata secondo Base64
     */
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}