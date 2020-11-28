package org.aldofrank.shak.streams.controllers;

import android.content.Context;
import android.net.ProxyInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.aldofrank.shak.R;

import java.util.LinkedList;
import java.util.List;

public class HomeFragment extends Fragment {

    public String TAG = "HomeFragment";

    private ViewPager viewPager;
    private TabLayout homeTabs;

    private PostsListFragment streamsFragment;
    private PostsListFragment favouritesFragment;

    private PostFormFragment postFormFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        viewPager = view.findViewById(R.id.view_pager);
        homeTabs = view.findViewById(R.id.home_tabs);

        streamsFragment = PostsListFragment.newInstance("all");
        favouritesFragment = PostsListFragment.newInstance("favourites");
        postFormFragment = new PostFormFragment();

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), 0);
        viewPagerAdapter.addFragment(streamsFragment, "Streams");
        viewPagerAdapter.addFragment(favouritesFragment, "Favourites");
        viewPager.setAdapter(viewPagerAdapter);

        homeTabs.setupWithViewPager(viewPager);

        homeTabs.getTabAt(0).setIcon(R.drawable.ic_library_books_black_24dp);
        homeTabs.getTabAt(1).setIcon(R.drawable.ic_favorite_black_24dp);

        /*
        BadgeDrawable unreadPostsBadge = homeTabs.getTabAt(0).getOrCreateBadge();
        unreadPostsBadge.setVisible(true);
        unreadPostsBadge.setNumber(12);
         */

        FloatingActionButton fab = view.findViewById(R.id.fab_switch_to_post_form);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                getChildFragmentManager().beginTransaction().replace(R.id.home_fragment, postFormFragment).commit();
            }
        });

        return view;
    }

    public PostsListFragment getStreamsFragment() {
        return streamsFragment;
    }

    public PostsListFragment getFavouritesFragment() {
        return favouritesFragment;
    }

    public PostFormFragment getPostFormFragment() {
        return postFormFragment;
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter{

        List<Fragment> listHomeFragments = new LinkedList<>();
        List<String> listHomeFragmentsTitles = new LinkedList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title){

            listHomeFragments.add(fragment);
            listHomeFragmentsTitles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return listHomeFragments.get(position);
        }

        @Override
        public int getCount() {
            return listHomeFragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return listHomeFragmentsTitles.get(position);
        }
    }
}
