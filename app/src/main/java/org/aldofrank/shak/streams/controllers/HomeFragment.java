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

public class HomeFragment extends Fragment implements View.OnClickListener {
    private ViewPager viewPager;

    private TabLayout homeTabs;

    private PostsListFragment streamsFragment;
    private PostsListFragment favouritesFragment;

    private PostFormFragment postFormFragment;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View homeFragmentView = inflater.inflate(R.layout.fragment_home, container, false);

        viewPager = homeFragmentView.findViewById(R.id.view_pager);
        homeTabs = homeFragmentView.findViewById(R.id.home_tabs);
        FloatingActionButton fab = homeFragmentView.findViewById(R.id.fab_switch_to_post_form);

        streamsFragment = PostsListFragment.newInstance("all");
        favouritesFragment = PostsListFragment.newInstance("favourites");
        postFormFragment = PostFormFragment.newInstance(streamsFragment);

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

        fab.setOnClickListener(this);

        return homeFragmentView;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_switch_to_post_form){
            // sostituisce il fragment attuale con un nuovo fragment
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.home_fragment, postFormFragment).commit();
        }
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
