package org.aldofrank.shak.settings.controllers;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.aldofrank.shak.R;
import org.aldofrank.shak.streams.controllers.LoggedUserActivity;
import org.aldofrank.shak.streams.controllers.OnBackPressed;
import org.w3c.dom.Text;

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
                "<H1 style=\"text-align: center\">SHAK TEAM</H1>" +
                        "<H3>Students:</H3>" +
                            "<p style=\"text-align: center\">Brunetti Marco</p>" +
                            "<p style=\"text-align: center\">Ferrini Francesco</p>" +
                            "<p style=\"text-align: center\">Manco Aldo</p>" +
                        "<H3>Coordinator:</H3>" +
                            "<p style=\"text-align: center\">Mercaldo Francesco</p>" +
                        "<H3>University: </H3>" +
                            "<p style=\"text-align: center\">Unimol 2020/2021</p>"));

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SettingsFragment.getSettingsFragment();
                /*getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.AboutFragment, settings)
                        .commit();*/
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