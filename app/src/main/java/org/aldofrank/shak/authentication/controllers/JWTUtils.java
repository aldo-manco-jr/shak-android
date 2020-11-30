package org.aldofrank.shak.authentication.controllers;

import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class JWTUtils {

    /*public static void decoded(String JWTEncoded) throws Exception {
        try {
            String[] split = JWTEncoded.split("\\.");
            Log.d("JWT_DECODED", "Header: " + getJson(split[0]));
            Log.d("JWT_DECODED", "Body: " + getJson(split[1]));
        } catch (UnsupportedEncodingException e) {
            //Error
        }
    }*/

    public static String decodeUsernameLoggedUser(String JWTEncoded) throws Exception {

        String username = null;

        try {
            String[] split = JWTEncoded.split("\\.");

            JSONObject json = new JSONObject(getJson(split[1]));
            JSONObject jsonUserData = (JSONObject) json.get("data");
            username = jsonUserData.getString("username");

        } catch (UnsupportedEncodingException e) {
            //Error
        }
        return username;
    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException {
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }
}
