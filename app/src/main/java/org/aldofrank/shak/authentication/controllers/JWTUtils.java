package org.aldofrank.shak.authentication.controllers;

import android.util.Base64;
import android.util.Log;

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

    public static JSONObject decodeUsernameLoggedUser(String JWTEncoded) throws Exception {

        JSONObject tokenDataJson = null;

        try {
            String[] split = JWTEncoded.split("\\.");
            Log.d("JWT_DECODED", "Header: " + getJson(split[0]));
            Log.d("JWT_DECODED", "Body: " + getJson(split[1]));
            JSONObject json = new JSONObject(getJson(split[1]));
            JSONObject jsonUserData = (JSONObject) json.get("data");

            tokenDataJson = new JSONObject();
            tokenDataJson.put("username", jsonUserData.getString("username"));
            tokenDataJson.put("_id", jsonUserData.getString("_id"));
            tokenDataJson.put("expirationDate", json.getLong("exp"));
        } catch (UnsupportedEncodingException e) {
            //Error
        }
        return tokenDataJson;
    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException {
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }
}
