 package org.aldofrankmarco.shak.authentication.controllers;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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

     private String imageEncoded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);

        photoTaken = findViewById(R.id.photo_taken);
        openCameraButton = findViewById(R.id.open_camera_button);
        faceAuthenticationButton = findViewById(R.id.login_face_recognition_button);

        if (ContextCompat.checkSelfPermission(
                FaceRecognitionActivity.this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    FaceRecognitionActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    }, 100);
        }

        openCameraButton.setOnClickListener(this);
        faceAuthenticationButton.setOnClickListener(this);
    }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         if (requestCode == 100) {
             try {
                 Bitmap photoTaken = (Bitmap) data.getExtras().get("data");
                 this.photoTaken.setImageBitmap(photoTaken);

                 imageEncoded = bitmapToBase64(photoTaken);
                 Toast.makeText(getApplicationContext(), imageEncoded+"", Toast.LENGTH_LONG).show();
             } catch (Exception e) {
                 e.printStackTrace();
             }
         }
     }

     @Override
     public void onClick(View view) {
         if (view.getId()==R.id.open_camera_button){
             Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
             startActivityForResult(intent, 100);
         }else if (view.getId()==R.id.login_face_recognition_button){
             faceAuthenticaton();
         }
     }

     private void faceAuthenticaton(){
         Toast.makeText(getApplicationContext(), "face authentication", Toast.LENGTH_LONG).show();

         if (imageEncoded == null) {
             return;
         }

         JsonObject imageData = new JsonObject();
         imageData.addProperty("image", "data:image/png;base64," + imageEncoded);

         Call<LoginResponse> httpRequest = AccessActivity.getAuthenticationService().faceAuthentication(imageData);

         httpRequest.enqueue(new Callback<LoginResponse>() {
             @Override
             public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                 if (response.isSuccessful()) {
                     Toast.makeText(getApplicationContext(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                 } else {
                     Toast.makeText(getApplicationContext(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
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
         bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
         byte[] byteArray = byteArrayOutputStream.toByteArray();
         return Base64.encodeToString(byteArray, Base64.DEFAULT);
     }
 }