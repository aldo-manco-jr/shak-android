package org.aldofrankmarco.shak.settings.controllers;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.aldofrankmarco.shak.R;
import org.aldofrankmarco.shak.streams.controllers.LoggedUserActivity;
import org.aldofrankmarco.shak.streams.controllers.OnBackPressed;

public class AboutFragment extends Fragment implements OnBackPressed {

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_about, container, false);
        Button button = (Button) view.findViewById(R.id.exit);
        TextView tv = (TextView) view.findViewById(R.id.storiaAbout);
        tv.setText(Html.fromHtml(
                "<H1>SHAK TEAM</H1><br>" +
                        "<H3>Students:</H3>" +
                            "<p>Brunetti Marco</p>" +
                            "<p>Ferrini Francesco</p>" +
                            "<p>Manco Aldo</p><br>" +
                        "<H3>Coordinator:</H3><br>" +
                            "<p>Mercaldo Francesco</p><br>" +
                        "<H3>University: </H3><br>" +
                            "<p>Unimol 2020/2021</p>"));

        //bottone d'uscita che mi rimanda a Settings'
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SettingsFragment.getSettingsFragment();
                LoggedUserActivity.getLoggedUserActivity().changeFragment(SettingsFragment.getSettingsFragment());
            }
        });
        return view;
    }

    @Override
    public void onBackPressed() {
        LoggedUserActivity.getLoggedUserActivity().changeFragment(SettingsFragment.getSettingsFragment());
    }
}