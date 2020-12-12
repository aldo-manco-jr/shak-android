package org.aldofrank.shak.profile.controllers;

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
import com.google.android.material.tabs.TabLayout;

import org.aldofrank.shak.R;
import org.aldofrank.shak.models.Post;
import org.aldofrank.shak.models.User;
import org.aldofrank.shak.people.controllers.PeopleListFragment;
import org.aldofrank.shak.people.http.FollowOrUnfollowRequest;
import org.aldofrank.shak.people.http.GetUserByUsernameResponse;
import org.aldofrank.shak.people.http.IsFollowingResponse;
import org.aldofrank.shak.people.http.SetUserLocationRequest;
import org.aldofrank.shak.services.ServiceGenerator;
import org.aldofrank.shak.services.UsersService;
import org.aldofrank.shak.streams.controllers.CommentFormFragment;
import org.aldofrank.shak.streams.controllers.CommentsListFragment;
import org.aldofrank.shak.streams.controllers.HomeFragment;
import org.aldofrank.shak.streams.controllers.LoggedUserActivity;
import org.aldofrank.shak.streams.controllers.OnBackPressed;
import org.aldofrank.shak.streams.controllers.PostsListFragment;

import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment implements View.OnClickListener, OnBackPressed {

    private User user;

    private final String basicUrlImage = "http://res.cloudinary.com/dfn8llckr/image/upload/v";

    private CollapsingToolbarLayout toolBarLayout;
    private ImageView coverImage;
    private CircleImageView profileImage;
    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView locationTextView;
    private Button setLocationButton;

    private Button followButton;

    private ViewPager viewPager;

    private TabLayout profileTabs;

    private PostsListFragment profilePostsFragment;
    private PeopleListFragment profileFollowingFragment;
    private PeopleListFragment profileFollowersFragment;
    private ImagesListFragment profileImagesFragment;

    private static ProfileFragment profileFragment;

    private View view;

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

        coverImage = view.findViewById(R.id.profile_cover_image);
        profileImage = view.findViewById(R.id.image_profile_circle);
        usernameTextView = view.findViewById(R.id.username_profile);
        emailTextView = view.findViewById(R.id.email_profile);
        locationTextView = view.findViewById(R.id.location_profile);
        setLocationButton = view.findViewById(R.id.button_set_location);
        followButton = view.findViewById(R.id.followUser);
        viewPager = view.findViewById(R.id.view_pager_profile);
        profileTabs = view.findViewById(R.id.profile_tabs);

        emailTextView.setOnClickListener(this);
        locationTextView.setOnClickListener(this);
        setLocationButton.setOnClickListener(this);
        followButton.setOnClickListener(this);

        userDataBinding(getArguments().getString("username"));

        getProfilePostsFragment(getArguments().getString("username"));
        getProfileFollowingFragment(getArguments().getString("username"));
        getProfileFollowersFragment(getArguments().getString("username"));
        getProfileImagesFragment(getArguments().getString("username"));

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), 0);
        viewPagerAdapter.addFragment(profilePostsFragment, "Streams");
        viewPagerAdapter.addFragment(profileFollowingFragment, "Following");
        viewPagerAdapter.addFragment(profileFollowersFragment, "Followers");
        viewPagerAdapter.addFragment(profileImagesFragment, "Images");
        viewPager.setAdapter(viewPagerAdapter);

        profileTabs.setupWithViewPager(viewPager);
    }

    public static ProfileFragment getProfileFragment() {
        return profileFragment;
    }

    public static ProfileFragment newInstance(String username) {

        ProfileFragment profileFragment = new ProfileFragment();

        Bundle args = new Bundle();
        args.putString("username", username);
        profileFragment.setArguments(args);

        return profileFragment;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.button_set_location:
                getLocation();
                break;
            case R.id.email_profile:
                Intent intent = new Intent(LoggedUserActivity.getLoggedUserActivity(), SendMailActivity.class);
                intent.putExtra("email", user.getEmail());
                intent.putExtra("username", user.getUsername());
                startActivity(intent);
                break;
            case R.id.followUser:
                followOrUnfollow(user, followButton);
                break;
        }
    }

    /**
     * In base ai dati ricavati da {@link #isFollow(User, Button)} viene inviata una richiesta http
     * di "follow" o di "unfollow" verso il post.
     */
    public void followOrUnfollow(User user, final Button followButton) {
        UsersService usersService = ServiceGenerator.createService(UsersService.class, LoggedUserActivity.getToken());

        Call<Object> httpRequest;

        if (followButton.getText().equals("unfollow")) {
            // l'utente che ha effettuato l'accesso è un follower dell'utente considerato
            httpRequest = usersService.unfollowUser(new FollowOrUnfollowRequest(user.getId()));
        } else {
            httpRequest = usersService.followUser(new FollowOrUnfollowRequest(user.getId()));
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

        UsersService usersService = ServiceGenerator.createService(UsersService.class, LoggedUserActivity.getToken());

        Call<IsFollowingResponse> httpRequest = usersService.isFollowing(user.getUsername());

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
            UsersService usersService = ServiceGenerator.createService(UsersService.class, LoggedUserActivity.getToken());
            SetUserLocationRequest setUserLocationRequest = new SetUserLocationRequest(city, country);
            Call<Object> httpRequest = usersService.setUserLocation(setUserLocationRequest);

            httpRequest.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful()) {

                        if (city == null && country != null) {
                            locationTextView.setText("@" + country);
                        } else if (city != null && country != null) {
                            locationTextView.setText("@" + city + ", " + country);
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

    public PostsListFragment getProfilePostsFragment(String username) {

        if (this.profilePostsFragment == null) {
            this.profilePostsFragment = PostsListFragment.newInstance("profile", username);
        }

        return profilePostsFragment;
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

    public void userDataBinding(String username) {

        UsersService usersService = ServiceGenerator.createService(UsersService.class, LoggedUserActivity.getToken());
        Call<GetUserByUsernameResponse> httpRequest = usersService.getUserByUsername(username);

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
                        locationTextView.setText("@" + user.getCity() + ", " + user.getCountry());
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
}
