package org.aldofrank.shak.streams.controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.aldofrank.shak.R;
import org.aldofrank.shak.notifications.controllers.NotificationsFragment;
import org.aldofrank.shak.people.controllers.PeopleFragment;
import org.aldofrank.shak.profile.controllers.ProfileFragment;
import org.aldofrank.shak.settings.controllers.SettingsFragment;

/**
 * Permette di accedere alle varie funzionalità del programma.
 * Gestisce la visualizzazione di vari tipi di fragment utilizzati dal programma, consentendo
 * la navigazione nell'applicazione da parte dell'utente.
 */
public class LoggedUserActivity extends AppCompatActivity {

    private static String token;
    private static String usernameLoggedUser;

    private Fragment homeFragment;
    private Fragment profileFragment;
    private Fragment peopleFragment;
    private Fragment notificationsFragment;
    private Fragment settingsFragment;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_user);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        BottomNavigationView navbarLoggedUser = findViewById(R.id.logged_user_navbar);
        homeFragment =  new HomeFragment();

        sharedPreferences = getSharedPreferences(getString(R.string.sharedpreferences_authentication), Context.MODE_PRIVATE);

        navbarLoggedUser.setOnNavigationItemSelectedListener(navbarListener);

        // sostituisce il fragment attuale con un HomeFragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.logged_user_fragment, homeFragment).commit();

        try {
            token = getIntent().getExtras().getString("authToken");
            usernameLoggedUser = getIntent().getExtras().getString("username");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Toast.makeText(getApplicationContext(), token, Toast.LENGTH_LONG).show();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navbarListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            Integer navigationSectionIdentifier = item.getItemId();

            switch (navigationSectionIdentifier) {
                case R.id.navigation_home:
                    if (homeFragment == null) {
                        homeFragment = new HomeFragment();
                    }

                    selectedFragment = homeFragment;
                    break;
                case R.id.navigation_profile:
                    if (profileFragment == null) {
                        profileFragment = new ProfileFragment();
                    }

                    selectedFragment = profileFragment;
                    break;
                case R.id.navigation_users:
                    if (peopleFragment == null) {
                        peopleFragment = new PeopleFragment();
                    }

                    selectedFragment = peopleFragment;
                    break;
                case R.id.navigation_notifications:
                    if (notificationsFragment == null) {
                        notificationsFragment = new NotificationsFragment();
                    }

                    selectedFragment = notificationsFragment;
                    break;
                case R.id.navigation_settings:
                    if (settingsFragment == null) {
                        settingsFragment = new SettingsFragment();
                    }

                    selectedFragment = settingsFragment;
                    break;
            }

            // sostituisce il fragment attuale con un fragment scelto
            assert selectedFragment != null : "selectedFragment non poteva essere null";
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.logged_user_fragment, selectedFragment).commit();

            return true;
        }
    };

    protected static String getToken() {
        return LoggedUserActivity.token;
    }

    protected static String getUsernameLoggedUser(){
        return LoggedUserActivity.usernameLoggedUser;
    }

    /*
    TODO @Aldo perchè lo avevi creato?
    @Override
    public void onBackPressed()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("authToken");
        editor.commit();

        super.onBackPressed();  // optional depending on your needs
    }
     */
}
