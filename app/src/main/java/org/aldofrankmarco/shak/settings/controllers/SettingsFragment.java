package org.aldofrankmarco.shak.settings.controllers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import org.aldofrankmarco.shak.R;
import org.aldofrankmarco.shak.authentication.controllers.AccessActivity;
import org.aldofrankmarco.shak.authentication.controllers.MainActivity;
import org.aldofrankmarco.shak.authentication.http.LoginResponse;
import org.aldofrankmarco.shak.streams.controllers.LoggedUserActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends Fragment {

    private ChangePasswordFragment changePasswordFragment;
    private AboutFragment aboutFragment;
    private static SettingsFragment settingsFragment;

    ListView listView;
    String mTitle[] = {"Change Password", "About", "Policy", "Logout", "Delete Account"};
    int images[] = {R.drawable.ic_change_password_white_24dp, R.drawable.ic_about_white_24dp, R.drawable.ic_baseline_privacy_tip_24, R.drawable.ic_logout_white_24dp, R.drawable.ic_delete_account_24};

    private SharedPreferences sharedPreferences;

    public SettingsFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        sharedPreferences = LoggedUserActivity.getLoggedUserActivity().getSharedPreferences(getString(R.string.sharedpreferences_authentication), Context.MODE_PRIVATE);
        listView = view.findViewById(R.id.settingmenu);
        MyAdapter adapter = new MyAdapter(getContext(), mTitle, images);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    getChangePasswordFragment();
                    LoggedUserActivity.getLoggedUserActivity().changeFragment(changePasswordFragment);
                } else if (position == 1) {
                    getAboutFragment();
                    LoggedUserActivity.getLoggedUserActivity().changeFragment(aboutFragment);
                } else if (position == 2) {
                    new AlertDialog.Builder(getContext())
                            .setIcon(R.drawable.ic_baseline_privacy_tip_24)
                            .setTitle(R.string.title_access)
                            .setMessage(R.string.application_permission_information)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("lawsRead", "yes");
                                    editor.commit();
                                }
                            }).show();

                } else if (position == 3) {

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove(getString(R.string.sharedpreferences_token));
                    editor.apply();

                    LoggedUserActivity.getSocket().disconnect();

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    Intent oldIntent = new Intent(getActivity(), LoggedUserActivity.class);
                    getActivity().stopService(oldIntent);

                    ActivityCompat.finishAffinity(getActivity());
                } else if (position == 4) {

                    new AlertDialog.Builder(getContext())
                            .setIcon(android.R.drawable.ic_delete)
                            .setTitle("Delete Account")
                            .setMessage("Are you sure to delete your account?\nThis operation can\'t be canceled")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    Call<Object> httpRequest = LoggedUserActivity.getUsersService().deleteUser();

                                    httpRequest.enqueue(new Callback<Object>() {
                                        @Override
                                        public void onResponse(Call<Object> call, final Response<Object> response) {

                                            if (response.isSuccessful()) {
                                                assert response.body() != null : "body() non doveva essere null";

                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.remove(getString(R.string.sharedpreferences_token));
                                                editor.apply();

                                                LoggedUserActivity.getSocket().disconnect();

                                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);

                                                Intent oldIntent = new Intent(getActivity(), LoggedUserActivity.class);
                                                getActivity().stopService(oldIntent);

                                                ActivityCompat.finishAffinity(getActivity());
                                            } else {
                                                new AlertDialog.Builder(getContext())
                                                        .setIcon(android.R.drawable.stat_notify_error)
                                                        .setTitle("Server Error")
                                                        .setMessage("Internal server error.")
                                                        .setPositiveButton("OK", null).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Object> call, Throwable t) {
                                            // errore a livello di rete

                                            new AlertDialog.Builder(getContext())
                                                    .setIcon(android.R.drawable.stat_notify_error)
                                                    .setTitle("Server Error")
                                                    .setMessage("Internal server error.")
                                                    .setPositiveButton("OK", null).show();
                                        }
                                    });

                                }
                            })
                            .setNegativeButton("CANCEL", null)
                            .show();
                }
            }
        });
        return view;
    }

    class MyAdapter extends ArrayAdapter<String> {

        Context context;
        String rTitle[];
        int rImgs[];

        MyAdapter(Context c, String title[], int imgs[]) {
            super(c, R.layout.settingrow, R.id.txtv1, title);
            this.context = c;
            this.rTitle = title;
            this.rImgs = imgs;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.settingrow, parent, false);
            ImageView images = row.findViewById(R.id.img);
            TextView myTitle = row.findViewById(R.id.txtv1);
            images.setImageResource(rImgs[position]);
            myTitle.setText(rTitle[position]);
            return row;
        }
    }

    public ChangePasswordFragment getChangePasswordFragment() {
        if (this.changePasswordFragment == null) {
            this.changePasswordFragment = new ChangePasswordFragment();
        }
        return changePasswordFragment;
    }

    public AboutFragment getAboutFragment() {
        if (this.aboutFragment == null) {
            this.aboutFragment = new AboutFragment();
        }
        return aboutFragment;
    }

    public static SettingsFragment getSettingsFragment() {
        if (settingsFragment == null) {
            settingsFragment = new SettingsFragment();
        }
        return settingsFragment;
    }

}