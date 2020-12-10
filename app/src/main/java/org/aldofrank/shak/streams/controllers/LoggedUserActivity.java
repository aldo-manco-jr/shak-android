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

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.aldofrank.shak.R;
import org.aldofrank.shak.notifications.controllers.NotificationsFragment;
import org.aldofrank.shak.people.controllers.PeopleListFragment;
import org.aldofrank.shak.profile.controllers.ProfileFragment;
import org.aldofrank.shak.settings.controllers.SettingsFragment;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Permette di accedere alle varie funzionalità del programma.
 * Gestisce la visualizzazione di vari tipi di fragment utilizzati dal programma, consentendo
 * la navigazione nell'applicazione da parte dell'utente.
 */
public class LoggedUserActivity extends AppCompatActivity {

    private static String token;
    private static String usernameLoggedUser;
    private static String idLoggedUser;
    private static boolean isSocketCorrectlyCreate = false;

    private HomeFragment homeFragment;
    private ProfileFragment profileFragment;
    private PeopleListFragment peopleFragment;
    private NotificationsFragment notificationsFragment;
    private SettingsFragment settingsFragment;

    private SharedPreferences sharedPreferences;

    private static LoggedUserActivity loggedUserActivity;

    private static Socket socket;
    {
        try {
            socket = IO.socket("http://10.0.2.2:3000/");
        } catch (URISyntaxException ignored) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_user);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        loggedUserActivity = this;

        BottomNavigationView navbarLoggedUser = findViewById(R.id.logged_user_navbar);

        navbarLoggedUser.setOnNavigationItemSelectedListener(navbarListener);

        try {
            token = getIntent().getExtras().getString("authToken");
            usernameLoggedUser = getIntent().getExtras().getString("username");
            idLoggedUser = getIntent().getExtras().getString("_id");

            JSONObject infoSocketConnection = new JSONObject();
            try {
                infoSocketConnection.put("username", usernameLoggedUser);
                //infoSocketConnection.put("room", "global");
            } catch (JSONException ignored) {}

            // occorre registrare l'esito della connessione per verificare se la connessione è stata
            // stabilita correttamente
            socket.on("online", onLoginEmitter);
            socket.connect();
            // invio tramite acknowledgement, per stabilire se l'utente ha impostato i dati del
            // socket correttamente
            socket.emit("online", infoSocketConnection, new Ack() {
                @Override
                public void call(Object... args) {
//                Toast.makeText(LoggedUserActivity.this, "sendMessage IOAcknowledge" + args.toString(), Toast.LENGTH_LONG).show();
                    if (args != null) {
                        assert args[0] != null: "arg doveva essere un array non vuoto";

                        isSocketCorrectlyCreate = true;
                        System.out.println("sendMessage IOAcknowledge" + args[0].toString());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        sharedPreferences = getSharedPreferences(getString(R.string.sharedpreferences_authentication), Context.MODE_PRIVATE);
        homeFragment = new HomeFragment();
        profileFragment = ProfileFragment.newInstance(usernameLoggedUser);
        // sostituisce il fragment attuale con un HomeFragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.logged_user_fragment, homeFragment).commit();
    }

    private Emitter.Listener onLoginEmitter = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            LoggedUserActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // ignorare, non ha azione da effettuare sui messaggi in arrivo
                }
            });
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener navbarListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            Integer navigationSectionIdentifier = item.getItemId();

            switch (navigationSectionIdentifier) {
                case R.id.navigation_home:
                    if (homeFragment == null){
                        homeFragment = new HomeFragment();
                    }

                    selectedFragment = homeFragment;
                    break;
                case R.id.navigation_profile:
                    if (profileFragment == null) {
                        profileFragment = ProfileFragment.newInstance(LoggedUserActivity.getUsernameLoggedUser());
                    }

                    selectedFragment = profileFragment;
                    break;
                case R.id.navigation_users:
                    if (peopleFragment == null) {
                        peopleFragment = PeopleListFragment.newInstance("all");
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

    public static String getToken() {
        return LoggedUserActivity.token;
    }

    public static String getUsernameLoggedUser(){
        return LoggedUserActivity.usernameLoggedUser;
    }

    public static String getIdLoggedUser(){
        return LoggedUserActivity.idLoggedUser;
    }

    /*@Override
    public void onBackPressed()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("authToken");
        editor.commit();

        super.onBackPressed();  // optional depending on your needs
    }*/

    @Override
    public void onDestroy() {
        socket.disconnect();
        super.onDestroy();
        //socket.off("disconnect");
    }

    @Override
    public void onBackPressed() {
        tellFragments();
        super.onBackPressed();
    }

    private void tellFragments(){
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for(Fragment fragment : fragments){
            if(fragment != null && fragment instanceof PostFormFragment)
                ((PostFormFragment)fragment).onBackPressed();
            else if (fragment != null && fragment instanceof CommentsListFragment){
                ((CommentsListFragment)fragment).onBackPressed();
            }else if (fragment != null && fragment instanceof CommentFormFragment){
                ((CommentFormFragment)fragment).onBackPressed();
            }
        }
    }

    public static Socket getSocket() {
        return socket;
    }

    public static LoggedUserActivity getLoggedUserActivity() {
        return loggedUserActivity;
    }


}
