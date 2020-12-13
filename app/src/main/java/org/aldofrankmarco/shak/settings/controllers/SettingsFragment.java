package org.aldofrankmarco.shak.settings.controllers;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.aldofrankmarco.shak.R;
import org.aldofrankmarco.shak.authentication.controllers.MainActivity;
import org.aldofrankmarco.shak.streams.controllers.HomeFragment;
import org.aldofrankmarco.shak.streams.controllers.LoggedUserActivity;

public class SettingsFragment extends Fragment {

    private ChangePasswordFragment changePasswordFragment;
    private AboutFragment aboutFragment;

    SharedPreferences sharedPreferences;

    private static SettingsFragment settingsFragment;

    ListView listView;
    String mTitle[] = {"Change Password", "About", "Logout"};
    int images[] = {R.drawable.ic_change_password_white_24dp,R.drawable.ic_about_white_24dp, R.drawable.ic_logout_white_24dp};

    @Override
   public View onCreateView(@NonNull LayoutInflater inflater,
                            @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        sharedPreferences = LoggedUserActivity.getLoggedUserActivity().getSharedPreferences(getString(R.string.sharedpreferences_authentication), Context.MODE_PRIVATE);

        listView = view.findViewById(R.id.settingmenu);

        MyAdapter adapter = new MyAdapter(getContext(), mTitle, images);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                if (position == 0){
                       getChangePasswordFragment();
                       // sostituisce il fragment attuale con un nuovo fragment
                       /*getChildFragmentManager()
                               .beginTransaction()
                               .replace(R.id.settings, changePwdFragment)
                               .commit();*/
                    LoggedUserActivity.getLoggedUserActivity().changeFragment(changePasswordFragment);
                }
                if (position == 1){
                    getAboutFragment();
                    // sostituisce il fragment attuale con un nuovo fragment
                    /*getChildFragmentManager()
                            .beginTransaction()
                            .replace(R.id.settings, aboutFragment)
                            .commit();*/
                    LoggedUserActivity.getLoggedUserActivity().changeFragment(aboutFragment);
                }
                if (position == 2){

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("authToken");
                    editor.commit();

                    LoggedUserActivity.getSocket().disconnect();

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);

                    LoggedUserActivity.getLoggedUserActivity().finish();
                }
            }
        });

        return view;
    }

    class MyAdapter extends ArrayAdapter<String>{

        Context context;
        String rTitle[];
        int rImgs[];

        MyAdapter (Context c, String title[], int imgs[]){
            super(c, R.layout.settingrow, R.id.txtv1, title);
            this.context = c;
            this.rTitle = title;
            this.rImgs = imgs;
        }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){

                LayoutInflater layoutInflater =(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View row = layoutInflater.inflate(R.layout.settingrow, parent, false);
                ImageView images = row.findViewById(R.id.img);
                TextView myTitle = row.findViewById(R.id.txtv1);
                images.setImageResource(rImgs[position]);
                myTitle.setText(rTitle[position]);

                return row;
            }
        }

        private void closePost(){
            getFragmentManager().beginTransaction().remove(HomeFragment.getHomeFragment().getPostFormFragment()).commitAllowingStateLoss();
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


//public class SettingsFragment extends Fragment {
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState){
//
//        View view = inflater.inflate(R.layout.fragment_settings, container, false);
//
//        String[] menuItems = {"Change Password","About","Logout"};
//
//        ListView listView = view.findViewById(R.id.settingmenu);
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                getActivity(),
//                R.layout.settingrow,
//                menuItems
//        );
//
//        listView.setAdapter(adapter);
//
//        return view;
//    }
//
//
//}