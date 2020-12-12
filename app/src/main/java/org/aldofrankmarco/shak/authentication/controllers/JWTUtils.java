package org.aldofrankmarco.shak.authentication.controllers;

import android.util.Base64;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class JWTUtils {

    /**
     * TODO
     */
    public static JSONObject decodeUsernameLoggedUser(String JWTEncoded) throws Exception {
        assert JWTEncoded != null: "JWTEncoded non poteva essere null";

        JSONObject tokenDataJson = null;

        String[] split = JWTEncoded.split("\\.");
        //Log.d("JWT_DECODED", "Header: " + getJson(split[0]));
        //Log.d("JWT_DECODED", "Body: " + getJson(split[1]));

        if (split != null && split.length > 1) {
            JSONObject json = new JSONObject(getJson(split[1]));
            JSONObject jsonUserData = (JSONObject) json.get("data");

            tokenDataJson = new JSONObject();
            tokenDataJson.put("username", jsonUserData.getString("username"));
            tokenDataJson.put("_id", jsonUserData.getString("_id"));
            tokenDataJson.put("expirationDate", json.getLong("exp"));
        }

        return tokenDataJson;
    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException {
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }
}
