package org.aldofrankmarco.shak.streams.controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.aldofrankmarco.shak.R;
import org.aldofrankmarco.shak.models.Post;
import org.aldofrankmarco.shak.notifications.controllers.NotificationsListFragment;
import org.aldofrankmarco.shak.people.controllers.PeopleListFragment;
import org.aldofrankmarco.shak.profile.controllers.ProfileFragment;
import org.aldofrankmarco.shak.services.ImagesService;
import org.aldofrankmarco.shak.services.NotificationsService;
import org.aldofrankmarco.shak.services.ServiceGenerator;
import org.aldofrankmarco.shak.services.StreamsService;
import org.aldofrankmarco.shak.services.UsersService;
import org.aldofrankmarco.shak.settings.controllers.AboutFragment;
import org.aldofrankmarco.shak.settings.controllers.ChangePasswordFragment;
import org.aldofrankmarco.shak.settings.controllers.SettingsFragment;
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
    private NotificationsListFragment notificationsFragment;
    private SettingsFragment settingsFragment;

    private CommentsListFragment commentsListFragment;
    private CommentFormFragment commentFormFragment;

    private SharedPreferences sharedPreferences;

    private static LoggedUserActivity loggedUserActivity;

    private static StreamsService streamsService;
    private static UsersService usersService;
    private static ImagesService imagesService;
    private static NotificationsService notificationsService;

    private static Socket socket;

    {
        try {
            socket = IO.socket("http://10.0.2.2:3000/");
        } catch (URISyntaxException ignored) {
        }
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
            } catch (JSONException ignored) {
            }

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
                        assert args[0] != null : "arg doveva essere un array non vuoto";

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
        //profileFragment = ProfileFragment.newInstance(usernameLoggedUser);
        // sostituisce il fragment attuale con un HomeFragment
        /*getSupportFragmentManager().beginTransaction()
                .replace(R.id.logged_user_fragment, homeFragment).commit();*/
        changeFragment(homeFragment);
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
                    homeFragment = new HomeFragment();
                    selectedFragment = homeFragment;
                    break;
                case R.id.navigation_profile:
                    profileFragment = ProfileFragment.newInstance(LoggedUserActivity.getUsernameLoggedUser());
                    selectedFragment = profileFragment;
                    break;
                case R.id.navigation_users:
                    peopleFragment = PeopleListFragment.newInstance("all");
                    selectedFragment = peopleFragment;
                    break;
                case R.id.navigation_notifications:
                    notificationsFragment = new NotificationsListFragment();
                    selectedFragment = notificationsFragment;
                    break;
                case R.id.navigation_settings:
                    settingsFragment = new SettingsFragment();
                    selectedFragment = settingsFragment;
                    break;
            }

            // sostituisce il fragment attuale con un fragment scelto
            assert selectedFragment != null : "selectedFragment non poteva essere null";
            changeFragment(selectedFragment);

            return true;
        }
    };

    public static StreamsService getStreamsService() {

        if (streamsService == null) {
            streamsService = ServiceGenerator.createService(StreamsService.class, getToken());
        }
        return streamsService;
    }

    public static UsersService getUsersService() {

        if (usersService == null) {
            usersService = ServiceGenerator.createService(UsersService.class, getToken());
        }
        return usersService;
    }

    public static ImagesService getImagesService() {

        if (imagesService == null) {
            imagesService = ServiceGenerator.createService(ImagesService.class, getToken());
        }
        return imagesService;
    }

    public static NotificationsService getNotificationsService() {

        if (notificationsService == null) {
            notificationsService = ServiceGenerator.createService(NotificationsService.class, getToken());
        }
        return notificationsService;
    }

    public static String getToken() {
        return LoggedUserActivity.token;
    }

    public static String getUsernameLoggedUser() {
        return LoggedUserActivity.usernameLoggedUser;
    }

    public static String getIdLoggedUser() {
        return LoggedUserActivity.idLoggedUser;
    }

    @Override
    public void onDestroy() {
        homeFragment = null;
        profileFragment = null;
        peopleFragment = null;
        notificationsFragment = null;
        settingsFragment = null;

        streamsService = null;
        imagesService = null;
        usersService = null;
        notificationsService = null;

        socket.disconnect();
        //socket.off("disconnect");

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        tellFragments();
    }

    private void tellFragments() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof PostFormFragment)
                ((PostFormFragment) fragment).onBackPressed();
            else if (fragment instanceof CommentsListFragment) {
                ((CommentsListFragment) fragment).onBackPressed();
            } else if (fragment instanceof CommentFormFragment) {
                ((CommentFormFragment) fragment).onBackPressed();
            } else if (fragment instanceof ProfileFragment) {
                ((ProfileFragment) fragment).onBackPressed();
            } else if (fragment instanceof ChangePasswordFragment) {
                ((ChangePasswordFragment) fragment).onBackPressed();
            } else if (fragment instanceof AboutFragment) {
                ((AboutFragment) fragment).onBackPressed();
            }
        }
    }

    public CommentsListFragment getCommentsListFragment() {
        return commentsListFragment;
    }

    public CommentsListFragment getCommentsListFragment(Post post) {
        this.commentsListFragment = CommentsListFragment.newInstance(post);
        return commentsListFragment;
    }

    public CommentFormFragment getCommentFormFragment() {

        if (this.commentFormFragment == null) {
            this.commentFormFragment = new CommentFormFragment();
        }

        return commentFormFragment;
    }

    //TODO NON VA BENE QUESTO METODO
    // le socket vengono ripetute tante volte quante sono le volte in cui il frmmento è STATO creato
    public void changeFragment(Fragment newFragment) {
//TODO è sbagliata, i frammenti continuano a esistere da qualche parte
        Fragment oldFragment = getSupportFragmentManager().findFragmentById(R.id.logged_user_fragment);

        if (oldFragment != newFragment) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transactionsManager = fragmentManager.beginTransaction();

            if (oldFragment != null) {
                transactionsManager
                        .replace(R.id.logged_user_fragment, newFragment)
                        .remove(oldFragment)
                        .commit();
            } else {
                transactionsManager
                        .replace(R.id.logged_user_fragment, newFragment)
                        .commit();
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
