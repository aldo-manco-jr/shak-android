package org.aldofrankmarco.shak.streams.controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
import org.aldofrankmarco.shak.streams.controllers.comments.CommentFormFragment;
import org.aldofrankmarco.shak.streams.controllers.comments.CommentsListFragment;
import org.aldofrankmarco.shak.streams.controllers.posts.PostFormFragment;
import org.aldofrankmarco.shak.streams.controllers.posts.PostsListFragment;
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

    private boolean isOnPause = false;

    private HomeFragment homeFragment = null;
    private ProfileFragment profileFragment = null;
    private PeopleListFragment peopleFragment = null;
    private NotificationsListFragment notificationsFragment = null;
    private SettingsFragment settingsFragment = null;
    private PostsListFragment streamsFragment = null;
    private PostsListFragment favouritesFragment = null;
    private PostsListFragment profileFragments = null;
    private PostsListFragment profilePostsFragment = null;

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
            socket.on("refreshListPosts", updatePostsList);
            socket.on("refreshAddedCommentToList", updateAddedPostCommentsList);
            socket.on("refreshRemovedCommentFromList", updateRemovedPostCommentsList);
            // invio tramite acknowledgement, per stabilire se l'utente ha impostato i dati del
            // socket correttamente
            socket.emit("online", infoSocketConnection, new Ack() {
                @Override
                public void call(Object... args) {
//                Toast.makeText(LoggedUserActivity.this, "sendMessage IOAcknowledge" + args.toString(), Toast.LENGTH_LONG).show();
                    if (args != null) {
                        assert args[0] != null : "arg doveva essere un array non vuoto";

                        System.out.println("sendMessage IOAcknowledge" + args[0].toString());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        streamsService = streamsService = ServiceGenerator.createService(StreamsService.class, getToken());
        usersService = ServiceGenerator.createService(UsersService.class, getToken());
        sharedPreferences = getSharedPreferences(getString(R.string.sharedpreferences_authentication), Context.MODE_PRIVATE);
        homeFragment = (HomeFragment) createNewInstanceIfNecessary(homeFragment, FragmentsIdentifier.home);

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

    /**
     * Quando un post viene pubblicato la home page viene aggiornata.
     */
    private Emitter.Listener updatePostsList = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            if (loggedUserActivity != null){
                loggedUserActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // quando un post viene pubblicato la socket avvisa del necessario aggiornmento
                        getStreamsFragment().getAllNewPosts();
                    }
                });
            }
        }
    };

    /**
     * Quando un commento viene pubblicato la home page viene aggiornata.
     */
    public static Emitter.Listener updateAddedPostCommentsList = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            if (LoggedUserActivity.getLoggedUserActivity() != null) {
                LoggedUserActivity.getLoggedUserActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // quando un commento viene pubblicato la socket avvisa del necessario aggiornmento
                        Post post = LoggedUserActivity.getLoggedUserActivity().getCommentsListFragment()
                                .getPost();
                        LoggedUserActivity.getLoggedUserActivity().incrementAllListsForAddAction(post);
                        /*LoggedUserActivity.getLoggedUserActivity().getCommentsListFragment().getAllPostComments();
                        LoggedUserActivity.getLoggedUserActivity().getCommentsListFragment()
                                .getPost().incrementTotalComments();
                         */
                    }
                });
            }
        }
    };

    /**
     * Quando un commento viene pubblicato la home page viene aggiornata.
     */
    public static Emitter.Listener updateRemovedPostCommentsList = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            if (LoggedUserActivity.getLoggedUserActivity() != null) {
                LoggedUserActivity.getLoggedUserActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // quando un commento viene cancellato la socket avvisa del necessario aggiornmento
                        Post post = LoggedUserActivity.getLoggedUserActivity().getCommentsListFragment()
                                .getPost();
                        LoggedUserActivity.getLoggedUserActivity().decrementAllListsForAddAction(post);
                        /*
                        LoggedUserActivity.getLoggedUserActivity().getCommentsListFragment().getAllPostComments();
                        LoggedUserActivity.getLoggedUserActivity().getCommentsListFragment()
                                .getPost().decrementTotalComments();
                        */
                    }
                });
            }
        }
    };

    private void incrementAllListsForAddAction(Post post){
        if (post.getUsernamePublisher().equals(getUsernameLoggedUser())) {
            streamsFragment.incrementNumberOfTotalCommentsIfExist(post);
            favouritesFragment.incrementNumberOfTotalCommentsIfExist(post);

            if (checkStreamsProfileFragmentExist()) {
                profilePostsFragment.incrementNumberOfTotalCommentsIfExist(post);
            }
        }
    }

    private void decrementAllListsForAddAction(Post post){
        if (post.getUsernamePublisher().equals(getUsernameLoggedUser())) {
            streamsFragment.decrementNumberOfTotalCommentsIfExist(post);
            favouritesFragment.decrementNumberOfTotalCommentsIfExist(post);

            if (checkStreamsProfileFragmentExist()) {
                profilePostsFragment.decrementNumberOfTotalCommentsIfExist(post);
            }
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navbarListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {

        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int navigationSectionIdentifier = item.getItemId();

            switch (navigationSectionIdentifier) {
                case R.id.navigation_home:
                    homeFragment = (HomeFragment) createNewInstanceIfNecessary(homeFragment, FragmentsIdentifier.home);
                    changeFragment(homeFragment);
                    break;
                case R.id.navigation_profile:
                    profileFragment = (ProfileFragment)
                            createNewInstanceIfNecessary(profileFragment,  FragmentsIdentifier.profile);
                    changeFragment(profileFragment);
                    break;
                case R.id.navigation_users:
                    peopleFragment = (PeopleListFragment)
                            createNewInstanceIfNecessary(peopleFragment,  FragmentsIdentifier.people);
                    changeFragment(peopleFragment);
                    break;
                case R.id.navigation_notifications:
                    notificationsFragment = (NotificationsListFragment)
                            createNewInstanceIfNecessary(notificationsFragment,  FragmentsIdentifier.notifications);
                    changeFragment(notificationsFragment);
                    break;
                case R.id.navigation_settings:
                    settingsFragment = (SettingsFragment)
                            createNewInstanceIfNecessary(settingsFragment,  FragmentsIdentifier.settings);
                    changeFragment(settingsFragment);
                    break;
            }

            return true;
        }
    };


    private Fragment createNewInstanceIfNecessary(Fragment fragment, FragmentsIdentifier identifier){
        if (fragment == null) {
            try {
                if (identifier == FragmentsIdentifier.home) {
                    fragment = HomeFragment.newInstance();
                } else if (identifier == FragmentsIdentifier.people) {
                    fragment = PeopleListFragment.newInstance();
                } else if (identifier == FragmentsIdentifier.profile) {
                    fragment = ProfileFragment.newInstance();
                } else if (identifier == FragmentsIdentifier.notifications) {
                    fragment = NotificationsListFragment.class.newInstance();
                } else if (identifier == FragmentsIdentifier.settings) {
                    fragment = SettingsFragment.class.newInstance();
                }
            } catch (Exception ignored) {
            }
        }

        return fragment;
    }

    public static StreamsService getStreamsService() {
        return streamsService;
    }

    public static UsersService getUsersService() {
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
        return token;
    }

    public static String getUsernameLoggedUser() {
        return usernameLoggedUser;
    }

    public static String getIdLoggedUser() {
        return idLoggedUser;
    }

    @Override
    public void onDestroy() {
        /*homeFragment = null;
        profileFragment = null;
        peopleFragment = null;
        notificationsFragment = null;
        settingsFragment = null;

        streamsService = null;
        imagesService = null;
        usersService = null;
        notificationsService = null;*/

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
    public void changeFragment(Fragment selectedFragment) {//newFragment) {
//TODO è sbagliata, i frammenti continuano a esistere da qualche parte
        try {
            //selectedFragment = (Fragment) fragmentClass.newInstance();
            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.logged_user_fragment, selectedFragment).commit();
        } catch (Exception ignored) {}
        /*Fragment oldFragment = getSupportFragmentManager().findFragmentById(R.id.logged_user_fragment);

        if (oldFragment != newFragment) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transactionsManager = fragmentManager.beginTransaction();

            if (oldFragment != null) {
                transactionsManager
                        .replace(R.id.logged_user_fragment, newFragment)
                        //.remove(oldFragment)
                        .commit();
            } else {
                transactionsManager
                        .replace(R.id.logged_user_fragment, newFragment)
                        .commit();
            }
        }*/
    }

    public static Socket getSocket() {
        return socket;
    }

    public static LoggedUserActivity getLoggedUserActivity() {
        return loggedUserActivity;
    }

    public PostsListFragment getStreamsFragment() {
        if (streamsFragment == null) {
            streamsFragment = PostsListFragment.newInstance("streams", usernameLoggedUser);
        }

        return streamsFragment;
    }

    public PostsListFragment getFavouritesFragment() {
        if (favouritesFragment == null) {
            favouritesFragment = PostsListFragment.newInstance("favourites", usernameLoggedUser);
        }

        return favouritesFragment;
    }

    public ProfileFragment getProfileFragments() {
        if (profileFragment == null) {
            profileFragment = ProfileFragment.newInstance();
        }

        return profileFragment;
    }

    /*public PostsListFragment getStreamsProfileFragments() {
        if (this.streamsProfileFragments == null) {
            this.streamsProfileFragments = PostsListFragment.newInstance("profile");
        }

        return this.streamsProfileFragments;
    }*/

    public PostsListFragment getProfilePostsFragment(@Nullable String username) {
        if (this.profilePostsFragment == null && username != null) {
            this.profilePostsFragment = PostsListFragment.newInstance("profile", username);
        }

        return profilePostsFragment;
    }

    public boolean checkProfileFragmentExist() {
        return (profileFragment != null);
    }

    public boolean checkStreamsProfileFragmentExist() {
        return (this.profileFragment != null && this.profilePostsFragment != null);
    }

    public void resetHomeFragment() {
        homeFragment.resetAll();
    }

    @Override
    protected void onPause(){
        super.onPause();
        //will be executed onResume
        isOnPause = true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        //will be executed onResume
        if (isOnPause) {
            // occorre verificare se mentre l'applicazione era in pausa sono stati ricevuti dei messaggi
            getStreamsFragment().getAllNewPosts();
        }
        // l'applicazione è stata ripresa, non è più in pausa
        isOnPause = false;
    }
}
