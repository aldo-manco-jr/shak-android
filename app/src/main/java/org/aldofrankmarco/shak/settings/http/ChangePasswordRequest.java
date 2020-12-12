package org.aldofrankmarco.shak.settings.http;

//public class ChangePasswordRequest {
//    private JSONObject changePasswordForm =  new JSONObject();
//
//    public ChangePasswordRequest(String currentPassword, String newPassword, String confirmPassword){
//        try {
//            changePasswordForm.put("currentPassword", currentPassword);
//            changePasswordForm.put("newPassword", newPassword);
//            changePasswordForm.put("confirmPassword", confirmPassword);
//        } catch (JSONException ignored) {
//
//        } catch (Exception exc){
//            Log.d("test","errore: " +  exc);
//        }
//    }
//}

public class ChangePasswordRequest {

    String currentPassword;
    String newPassword;
    String confirmPassword;

    public ChangePasswordRequest(String currentPassword, String newPassword, String confirmPassword){
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
}