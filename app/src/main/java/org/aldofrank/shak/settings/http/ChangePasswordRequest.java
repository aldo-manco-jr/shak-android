package org.aldofrank.shak.settings.http;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePasswordRequest {
    private JSONObject changePasswordForm;

    public ChangePasswordRequest(String currentPassword, String newPassword, String confirmPassword){
        try {
            changePasswordForm.put("currentPassword", currentPassword);
            changePasswordForm.put("newPassword", newPassword);
            changePasswordForm.put("confirmPassword", confirmPassword);
        } catch (JSONException ignored) {}
    }
}
