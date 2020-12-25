package org.aldofrankmarco.shak.profile.controllers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.github.nkzawa.emitter.Emitter;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.aldofrankmarco.shak.R;
import org.aldofrankmarco.shak.models.User;
import org.aldofrankmarco.shak.people.controllers.PeopleListFragment;
import org.aldofrankmarco.shak.people.http.GetUserByUsernameResponse;
import org.aldofrankmarco.shak.people.http.IsFollowingResponse;
import org.aldofrankmarco.shak.people.http.SetUserLocationRequest;
import org.aldofrankmarco.shak.streams.controllers.HomeFragment;
import org.aldofrankmarco.shak.streams.controllers.LoggedUserActivity;
import org.aldofrankmarco.shak.streams.controllers.OnBackPressed;
import org.aldofrankmarco.shak.streams.controllers.postslist.PostsListFragment;

import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment implements View.OnClickListener, OnBackPressed {

    private User user = null;

    private final String basicUrlImage = "http://res.cloudinary.com/dfn8llckr/image/upload/v";

    private CollapsingToolbarLayout toolBarLayout;
    private ImageView coverImage;
    private CircleImageView profileImage;
    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView locationTextView;
    private Button setLocationButton;

    private Button followButton;

    private FloatingActionButton addUserImageButton;

    private ViewPager viewPager;

    private TabLayout profileTabs;

    private PostsListFragment profilePostsFragment = null;
    private PeopleListFragment profileFollowingFragment = null;
    private PeopleListFragment profileFollowersFragment = null;
    private ImagesListFragment profileImagesFragment = null;

    private static ProfileFragment profileFragment;

    private View view;

    private ProfileFragment userViewInformationFragment = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        profileFragment = this;

        LoggedUserActivity.getSocket().on("refreshPage", updateProfilePage);
    }

    public Emitter.Listener updateProfilePage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            userDataBinding(getArguments().getString("username"));
        }
    };

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.setVisibility(View.GONE);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolBarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.toolbar_layout);
/*        toolBarLayout.addOnLayoutChangeListener(this);

        if (username.equals(LoggedUserActivity.getUsernameLoggedUser())){
            addUserImageButton.setVisibility(View.VISIBLE);
        }else {
            addUserImageButton.setVisibility(View.GONE);
        }
*/
        coverImage = view.findViewById(R.id.profile_cover_image);
        profileImage = view.findViewById(R.id.image_profile_circle);
        usernameTextView = view.findViewById(R.id.username_profile);
        emailTextView = view.findViewById(R.id.email_profile);
        locationTextView = view.findViewById(R.id.location_profile);
        setLocationButton = view.findViewById(R.id.button_set_location);
        followButton = view.findViewById(R.id.followUser);
        viewPager = view.findViewById(R.id.view_pager_profile);
        profileTabs = view.findViewById(R.id.profile_tabs);
        addUserImageButton = view.findViewById(R.id.fab_add_user_image);

        emailTextView.setOnClickListener(this);
        locationTextView.setOnClickListener(this);
        setLocationButton.setOnClickListener(this);
        followButton.setOnClickListener(this);
        addUserImageButton.setOnClickListener(this);

        userDataBinding(getArguments().getString("username"));

        boolean isUserOwner = getArguments().getString("username").equals(LoggedUserActivity.getUsernameLoggedUser());
        if (isUserOwner) {
            // il frammento riguarda l'utente che ha effettuato il login
            boolean isExistStreamsFragment = LoggedUserActivity.getLoggedUserActivity().checkStreamsProfileFragmentExist();
            if (isExistStreamsFragment) {
                // esiste già un profilo utente caricato e deve essere ripreso
                profilePostsFragment = LoggedUserActivity.getLoggedUserActivity()
                        .getProfilePostsFragment(null);
            } else {
                // occorre creare il frammento con il profilo dell'utente che ha effettuato l'accesso
                profilePostsFragment = LoggedUserActivity.getLoggedUserActivity()
                        .getProfilePostsFragment(LoggedUserActivity.getUsernameLoggedUser());
            }
        } else {
            // è stato aperto per visualizzare le info di un utente diverso dall'utilizzatore
            profilePostsFragment = getProfilePostsFragment(getArguments().getString("username"));
        }
        /*boolean isUserOwner = getArguments().getString("username")
                .equals(LoggedUserActivity.getUsernameLoggedUser());
        if (isUserOwner) {
            profilePostsFragment = getProfilePostsFragment(getArguments().getString("username"));
            //profilePostsFragment = LoggedUserActivity.getLoggedUserActivity()
            //        .getProfilePostsFragment(LoggedUserActivity.getUsernameLoggedUser());
            // il frammento riguarda l'utente che ha effettuato il login
            /*boolean isExistStreamsFragment = LoggedUserActivity.getLoggedUserActivity().checkStreamsProfileFragmentExist();
            if (isExistStreamsFragment) {
                // esiste già un profilo utente caricato e deve essere ripreso
                profilePostsFragment = LoggedUserActivity.getLoggedUserActivity()
                        .getProfilePostsFragment(null);
            } else {
                // occorre creare il frammento con il profilo dell'utente che ha effettuato l'accesso
                profilePostsFragment = LoggedUserActivity.getLoggedUserActivity()
                        .getProfilePostsFragment(LoggedUserActivity.getUsernameLoggedUser());
            }
        } else {
            // è stato aperto per visualizzare le info di un utente diverso dall'utilizzatore
            profilePostsFragment = getProfilePostsFragment(getArguments().getString("username"));
        }*/

        profileFollowingFragment = getProfileFollowingFragment(getArguments().getString("username"));
        profileFollowingFragment = getProfileFollowersFragment(getArguments().getString("username"));
        profileImagesFragment = getProfileImagesFragment(getArguments().getString("username"));

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), 0);
        viewPagerAdapter.addFragment(profilePostsFragment, "Streams");
        viewPagerAdapter.addFragment(profileFollowingFragment, "Following");
        viewPagerAdapter.addFragment(profileFollowersFragment, "Followers");
        viewPagerAdapter.addFragment(profileImagesFragment, "Images");
        viewPager.setAdapter(viewPagerAdapter);

        profileTabs.setupWithViewPager(viewPager);

        // addUserImageButton è usato solo per il tab di ImageListFragment e solo se l'utente
        // è l'utente connesso è anche l'utente visualizzato
        profileTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 3) {
                    if (user.getUsername().equals(LoggedUserActivity.getUsernameLoggedUser())) {
                        addUserImageButton.setVisibility(View.VISIBLE);
                    } else {
                        addUserImageButton.setVisibility(View.GONE);
                    }
                } else {
                    addUserImageButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab ignored) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab ignored) {
            }
        });

        //profilePostsFragment.getAllPosts();
    }

    public static ProfileFragment getProfileFragment() {
        return profileFragment;
    }

    private ProfileFragment(){
        //TODO TOGLIERE
    }

    /**
     * Il costruttore precefinito (senza argomenti) prevede di creare l'istanza per l'utente loggato
     */
    public static ProfileFragment newInstance() {

        ProfileFragment profileFragment = new ProfileFragment();

        Bundle args = new Bundle();
        args.putString("username", LoggedUserActivity.getUsernameLoggedUser());
        profileFragment.setArguments(args);

        return profileFragment;
    }

    public ProfileFragment newInstanceUserViewInformation(String username) {

        userViewInformationFragment = new ProfileFragment();

        Bundle args = new Bundle();
        args.putString("username", username);
        userViewInformationFragment.setArguments(args);

        return userViewInformationFragment;
    }

    private static void addNotificationProfileViewed(String userId){

        Call<Object> httpRequest = LoggedUserActivity.getNotificationsService().addNotificationProfileViewed(userId);

        httpRequest.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) { }

            @Override
            public void onFailure(Call<Object> call, Throwable t) { }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_set_location:
                getLocation();
                break;
            case R.id.email_profile:
                if (!user.getUsername().equals(LoggedUserActivity.getUsernameLoggedUser())) {
                    Intent intent = new Intent(LoggedUserActivity.getLoggedUserActivity(), SendMailActivity.class);
                    intent.putExtra("email", user.getEmail());
                    intent.putExtra("username", user.getUsername());
                    startActivity(intent);
                }
                break;
            case R.id.followUser:
                followOrUnfollow(user, followButton);
                break;
            case R.id.fab_add_user_image:
                profileImagesFragment.uploadUserImage();
                break;
        }
    }

    /**
     * In base ai dati ricavati da {@link #isFollow(User, Button)} viene inviata una richiesta http
     * di "follow" o di "unfollow" verso il post.
     */
    public void followOrUnfollow(User user, final Button followButton) {
        Call<Object> httpRequest;

        if (followButton.getText().equals("unfollow")) {
            // l'utente che ha effettuato l'accesso è un follower dell'utente considerato
            httpRequest = LoggedUserActivity.getUsersService().unfollowUser(user.getId());
        } else {
            httpRequest = LoggedUserActivity.getUsersService().followUser(user.getId());
        }

        httpRequest.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {

                    if (followButton.getText().equals("unfollow")) {
                        followButton.setText("follow");
                    } else if (followButton.getText().equals("follow")) {
                        followButton.setText("unfollow");
                    }
                } else {
                    Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), response.code() + "   " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * un user generico in input
     *
     * @return true se l'utente selezionato è un follower, false altrimenti
     */
    public void isFollow(User user, final Button followButton) {

        followButton.setText("follow");

        Call<IsFollowingResponse> httpRequest = LoggedUserActivity.getUsersService().isFollowing(user.getUsername());

        httpRequest.enqueue(new Callback<IsFollowingResponse>() {
            @Override
            public void onResponse(Call<IsFollowingResponse> call, Response<IsFollowingResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getMessage().equals("yes")) {
                        followButton.setText("unfollow");
                    }
                    //LoggedUserActivity.getSocket().emit("refresh");
                } else {
                    Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<IsFollowingResponse> call, Throwable t) {
                Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getLocation() {
        Intent intent = new Intent(LoggedUserActivity.getLoggedUserActivity(), MapsActivity.class);
        startActivity(intent);
    }

    public void setUserLocation(final String city, final String country) {

        if (country != null) {
            SetUserLocationRequest setUserLocationRequest = new SetUserLocationRequest(city, country);
            Call<Object> httpRequest = LoggedUserActivity.getUsersService().setUserLocation(LoggedUserActivity.getIdLoggedUser(), setUserLocationRequest);

            httpRequest.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful()) {

                        StringBuilder location = new StringBuilder();
                        location.append("@");

                        if (city == null) {
                            location.append(country);
                            locationTextView.setText(location);
                        } {
                            location.append(city).append(", ").append(country);
                            locationTextView.setText(location);
                        }
                        setLocationButton.setVisibility(View.GONE);
                        locationTextView.setVisibility(View.VISIBLE);
                        LoggedUserActivity.getSocket().emit("refresh");
                    } else {
                        Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public PostsListFragment getProfilePostsFragment(@Nullable String username) {
        if (this.profilePostsFragment == null && username != null) {
            this.profilePostsFragment = PostsListFragment.newInstance("profile", username);
        }

        return this.profilePostsFragment;
    }

    public PeopleListFragment getProfileFollowingFragment(String username) {
        if (this.profileFollowingFragment == null) {
            this.profileFollowingFragment = PeopleListFragment.newInstance("following", username);
        }

        return profileFollowingFragment;
    }

    public PeopleListFragment getProfileFollowersFragment(String username) {
        if (this.profileFollowersFragment == null) {
            this.profileFollowersFragment = PeopleListFragment.newInstance("followers", username);
        }

        return profileFollowingFragment;
    }

    public ImagesListFragment getProfileImagesFragment(String username) {
        if (this.profileImagesFragment == null) {
            this.profileImagesFragment = ImagesListFragment.newInstance(username);
        }

        return profileImagesFragment;
    }

    public void changeImageProfile(String imageId, String imageVersion){
        final String urlImageProfileUser = basicUrlImage + imageVersion + "/"
                + imageId;

        Glide.with(LoggedUserActivity.getLoggedUserActivity())
                .asBitmap()
                .load(urlImageProfileUser)
                .into(profileImage);
    }

    public void changeImageCover(String imageId, String imageVersion){
        final String urlImageCoverUser = basicUrlImage + imageVersion + "/"
                + imageId;

        Glide.with(LoggedUserActivity.getLoggedUserActivity())
                .asBitmap()
                .load(urlImageCoverUser)
                .into(coverImage);
    }


    public void userDataBinding(final String username) {
        Call<GetUserByUsernameResponse> httpRequest = LoggedUserActivity.getUsersService().getUserByUsername(username);

        httpRequest.enqueue(new Callback<GetUserByUsernameResponse>() {
            @Override
            public void onResponse(Call<GetUserByUsernameResponse> call, Response<GetUserByUsernameResponse> response) {
                if (response.isSuccessful()) {

                    user = response.body().getUserFoundByUsername();

                    final String urlImageCoverUser = basicUrlImage + user.getCoverImageVersion() + "/"
                            + user.getCoverImageId();

                    Glide.with(LoggedUserActivity.getLoggedUserActivity())
                            .asBitmap()
                            .load(urlImageCoverUser)
                            .into(coverImage);

                    final String urlImageProfileUser = basicUrlImage + user.getProfileImageVersion() + "/"
                            + user.getProfileImageId();

                    Glide.with(LoggedUserActivity.getLoggedUserActivity())
                            .asBitmap()
                            .load(urlImageProfileUser)
                            .into(profileImage);

                    usernameTextView.setText(user.getUsername());
                    emailTextView.setText(user.getEmail());

                    if (user.getCity() != null && user.getCountry() != null) {
                        StringBuilder location = new StringBuilder();
                        location.append("@").append(user.getCity()).append(", ").append(user.getCountry());

                        locationTextView.setText(location);
                        if (user.getUsername().equals(LoggedUserActivity.getUsernameLoggedUser())) {
                            locationTextView.setVisibility(View.VISIBLE);
                            setLocationButton.setVisibility(View.GONE);

                            locationTextView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    getLocation();
                                }
                            });
                        } else {
                            locationTextView.setVisibility(View.VISIBLE);
                            setLocationButton.setVisibility(View.GONE);
                        }
                    } else {
                        if (user.getUsername().equals(LoggedUserActivity.getUsernameLoggedUser())) {
                            locationTextView.setVisibility(View.GONE);
                            setLocationButton.setVisibility(View.VISIBLE);

                            locationTextView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    getLocation();
                                }
                            });
                        } else {
                            locationTextView.setVisibility(View.VISIBLE);
                            locationTextView.setText("Unknown Location");
                        }
                    }

                    if (user.getUsername().equals(LoggedUserActivity.getUsernameLoggedUser())) {
                        followButton.setVisibility(View.GONE);
                    }else {
                        addNotificationProfileViewed(user.getId());
                    }

                    profileImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(LoggedUserActivity.getLoggedUserActivity(), ImageViewerActivity.class);
                            intent.putExtra("urlImage", urlImageProfileUser);
                            LoggedUserActivity.getLoggedUserActivity().startActivity(intent);
                        }
                    });

                    coverImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(LoggedUserActivity.getLoggedUserActivity(), ImageViewerActivity.class);
                            intent.putExtra("urlImage", urlImageCoverUser);
                            LoggedUserActivity.getLoggedUserActivity().startActivity(intent);
                        }
                    });

                    isFollow(user, followButton);

                    view.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GetUserByUsernameResponse> call, Throwable t) {
                Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        LoggedUserActivity.getLoggedUserActivity().changeFragment(HomeFragment.getHomeFragment());
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        List<Fragment> listProfileFragments = new LinkedList<>();
        List<String> listProfileFragmentsTitles = new LinkedList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title) {
            listProfileFragments.add(fragment);
            listProfileFragmentsTitles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return listProfileFragments.get(position);
        }

        @Override
        public int getCount() {
            return listProfileFragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return listProfileFragmentsTitles.get(position);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        resetFollowList();
        resetUnfollowList();
    }

    private void resetFollowList(){
        if (profileFollowersFragment != null) {
            profileFollowersFragment.resetList();
            profileFollowersFragment = null;
        }
    }

    private void resetUnfollowList(){
        if (profileFollowingFragment != null) {
            profileFollowingFragment.resetList();
            profileFollowingFragment = null;
        }
    }
}
