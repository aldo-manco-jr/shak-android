package org.aldofrankmarco.shak.streams.controllers;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.aldofrankmarco.shak.R;
import org.aldofrankmarco.shak.streams.controllers.posts.PostFormFragment;
import org.aldofrankmarco.shak.streams.controllers.posts.PostsListFragment;

import java.util.LinkedList;
import java.util.List;

/**
 * Contiene al suo interno i frammenti che permettorno di visualizzare gli elementi "streams" e
 * "favourites", inoltre gestisce un oggetto di tipo TabLayout che consente di scegliere tra le
 * due voci.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private ViewPager viewPager;

    private TabLayout homeTabs;

    private PostsListFragment streamsFragment;
    private PostsListFragment favouritesFragment;

    private static HomeFragment homeFragment;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View homeFragmentView = inflater.inflate(R.layout.fragment_home, container, false);

        homeFragment = this;

        viewPager = homeFragmentView.findViewById(R.id.view_pager);
        homeTabs = homeFragmentView.findViewById(R.id.home_tabs);
        FloatingActionButton fab = homeFragmentView.findViewById(R.id.fab_switch_to_post_form);

        streamsFragment = getStreamsFragment();
        favouritesFragment = getFavouritesFragment();

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), 0);
        viewPagerAdapter.addFragment(getStreamsFragment(), "Streams");
        viewPagerAdapter.addFragment(getFavouritesFragment(), "Favourites");
        viewPager.setAdapter(viewPagerAdapter);

        homeTabs.setupWithViewPager(viewPager);

        homeTabs.getTabAt(0).setIcon(R.drawable.ic_library_books_black_24dp);
        homeTabs.getTabAt(1).setIcon(R.drawable.ic_favorite_black_24dp);

        fab.setOnClickListener(this);

        return homeFragmentView;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_switch_to_post_form) {
            // viene mostrata la form di inserimento del nuovo post
            FragmentTransaction transactionsManager = getFragmentManager().beginTransaction();
            transactionsManager
                    .replace(R.id.home_fragment, new PostFormFragment())
                    .commit();
        }
    }

    public static HomeFragment getHomeFragment() {
        return homeFragment;
    }

    public PostsListFragment getStreamsFragment() {
        return LoggedUserActivity.getLoggedUserActivity().getStreamsFragment();
    }

    public PostsListFragment getFavouritesFragment() {
        return LoggedUserActivity.getLoggedUserActivity().getFavouritesFragment();
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        List<Fragment> listHomeFragments = new LinkedList<>();
        List<String> listHomeFragmentsTitles = new LinkedList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title) {
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

    public void resetAll(){
        streamsFragment.resetAdapter();
        favouritesFragment.resetAdapter();
    }
}
