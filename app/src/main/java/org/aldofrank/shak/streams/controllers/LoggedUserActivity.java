package org.aldofrank.shak.streams.controllers;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.aldofrank.shak.R;
import org.aldofrank.shak.notifications.controllers.NotificationsFragment;
import org.aldofrank.shak.people.controllers.PeopleFragment;
import org.aldofrank.shak.profile.controllers.ProfileFragment;

public class LoggedUserActivity extends AppCompatActivity {

    private String token;

    private Fragment homeFragment;
    private Fragment profileFragment;
    private Fragment peopleFragment;
    private Fragment notificationsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_user);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        BottomNavigationView navbarLoggedUser = findViewById(R.id.logged_user_navbar);
        navbarLoggedUser.setOnNavigationItemSelectedListener(navbarListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.logged_user_fragment, new HomeFragment()).commit();

        try {
            token = getIntent().getExtras().getString("authToken");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(getApplicationContext(), token, Toast.LENGTH_LONG).show();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navbarListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment selectedFragment = null;

            switch (item.getItemId()) {

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
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.logged_user_fragment, selectedFragment).commit();

            return true;
        }
    };

    protected String getToken() {
        return this.token;
    }
}
