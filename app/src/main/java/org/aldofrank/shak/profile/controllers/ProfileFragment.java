package org.aldofrank.shak.profile.controllers;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.aldofrank.shak.R;
import org.aldofrank.shak.models.Post;
import org.aldofrank.shak.models.User;
import org.aldofrank.shak.people.controllers.PeopleListFragment;
import org.aldofrank.shak.people.http.GetUserByUsernameResponse;
import org.aldofrank.shak.services.ServiceGenerator;
import org.aldofrank.shak.services.StreamsService;
import org.aldofrank.shak.services.UsersService;
import org.aldofrank.shak.streams.controllers.CommentFormFragment;
import org.aldofrank.shak.streams.controllers.CommentsListFragment;
import org.aldofrank.shak.streams.controllers.HomeFragment;
import org.aldofrank.shak.streams.controllers.LoggedUserActivity;
import org.aldofrank.shak.streams.controllers.PostFormFragment;
import org.aldofrank.shak.streams.controllers.PostsListFragment;

import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private User user;

    private final String basicUrlImage = "http://res.cloudinary.com/dfn8llckr/image/upload/v";

    private ImageView coverImage;
    private CircleImageView profileImage;
    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView locationTextView;

    private Button followButton;

    private ViewPager viewPager;

    private TabLayout profileTabs;

    private PostsListFragment profilePostsFragment;
    private PeopleListFragment profileFollowingFragment;
    private PeopleListFragment profileFollowersFragment;
    private ImagesListFragment profileImagesFragment;

    private CommentsListFragment commentsListFragment;
    private CommentFormFragment commentFormFragment;

    private static ProfileFragment profileFragment;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View profileFragmentView = inflater.inflate(R.layout.fragment_profile, container, false);

        profileFragment = this;

        coverImage = profileFragmentView.findViewById(R.id.profile_cover_image);
        profileImage = profileFragmentView.findViewById(R.id.image_profile_circle);
        usernameTextView = profileFragmentView.findViewById(R.id.username_profile);
        emailTextView = profileFragmentView.findViewById(R.id.email_profile);
        locationTextView = profileFragmentView.findViewById(R.id.location_profile);

        userDataBinding(getArguments().getString("username"));

        viewPager = profileFragmentView.findViewById(R.id.view_pager_profile);
        profileTabs = profileFragmentView.findViewById(R.id.profile_tabs);

        getProfilePostsFragment(getArguments().getString("username"));
        //getProfileFollowingFragment(getArguments().getString("username"));
        //getProfileFollowersFragment(getArguments().getString("username"));
        //getProfileImagesFragment(getArguments().getString("username"));

        ProfileFragment.ViewPagerAdapter viewPagerAdapter = new ProfileFragment.ViewPagerAdapter(getChildFragmentManager(), 0);
        viewPagerAdapter.addFragment(profilePostsFragment, "Streams");
        //viewPagerAdapter.addFragment(profileFollowingFragment, "Following");
        //viewPagerAdapter.addFragment(profileFollowersFragment, "Followers");
        //viewPagerAdapter.addFragment(profileImagesFragment, "Images");
        viewPager.setAdapter(viewPagerAdapter);

        profileTabs.setupWithViewPager(viewPager);

        profileTabs.getTabAt(0).setIcon(R.drawable.ic_library_books_black_24dp);
        //profileTabs.getTabAt(1).setIcon(R.drawable.ic_group_black_24dp);
        //profileTabs.getTabAt(2).setIcon(R.drawable.ic_baseline_people_outline_white_24);
        //profileTabs.getTabAt(3).setIcon(R.drawable.ic_baseline_photo_library_white_24);

        return profileFragmentView;
    }

    public static ProfileFragment getProfileFragment() {
        return profileFragment;
    }

    public static ProfileFragment newInstance(String username) {

        ProfileFragment profileFragment = new ProfileFragment();

        System.out.println(username);
        Bundle args = new Bundle();
        args.putString("username", username);
        profileFragment.setArguments(args);

        return profileFragment;
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
            this.profileImagesFragment = new ImagesListFragment();
        }

        return profileImagesFragment;
    }

    public CommentsListFragment getCommentsListFragment() {
        return commentsListFragment;
    }

    public CommentsListFragment getCommentsListFragment(Post post) {
        this.commentsListFragment = CommentsListFragment.newInstance("profile", post);
        return commentsListFragment;
    }

    public CommentFormFragment getCommentFormFragment() {

        if (this.commentFormFragment == null) {
            this.commentFormFragment = CommentFormFragment.newInstance("profile");
        }

        return commentFormFragment;
    }

    private void userDataBinding(String username) {

        UsersService usersService = ServiceGenerator.createService(UsersService.class, LoggedUserActivity.getToken());
        Call<GetUserByUsernameResponse> httpRequest = usersService.getUserByUsername(username);

        httpRequest.enqueue(new Callback<GetUserByUsernameResponse>() {
            @Override
            public void onResponse(Call<GetUserByUsernameResponse> call, Response<GetUserByUsernameResponse> response) {
                if (response.isSuccessful()) {
                    user = response.body().getUserFoundByUsername();

                    String urlImageCoverUser = basicUrlImage + user.getCoverImageVersion() + "/"
                            + user.getCoverImageId();

                    Glide.with(LoggedUserActivity.getLoggedUserActivity())
                            .asBitmap()
                            .load(urlImageCoverUser)
                            .into(coverImage);

                    String urlImageProfileUser = basicUrlImage + user.getProfileImageVersion() + "/"
                            + user.getProfileImageId();

                    Glide.with(LoggedUserActivity.getLoggedUserActivity())
                            .asBitmap()
                            .load(urlImageProfileUser)
                            .into(profileImage);

                    usernameTextView.setText(user.getUsername());
                    emailTextView.setText(user.getEmail());

                    if (user.getCity()!=null && user.getCountry()!=null){
                        locationTextView.setText("@" + user.getCity() + ", " + user.getCountry());
                    }else {
                        locationTextView.setText("SET LOCATION");
                    }
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
