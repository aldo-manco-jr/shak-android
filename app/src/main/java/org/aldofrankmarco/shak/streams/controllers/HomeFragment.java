package org.aldofrankmarco.shak.streams.controllers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.aldofrankmarco.shak.R;

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
    private PostFormFragment postFormFragment;

    private static HomeFragment homeFragment;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View homeFragmentView = inflater.inflate(R.layout.fragment_home, container, false);

        //TODO Aldo non mi dire che questa riga è il costruttore, visto che manca ma la instanzi lo stesso
        homeFragment = this;

        // TODO Aldo ho bisogno di resettare i pannelli perchè hai aggiunto il comportamento di
        // cancellare i frammenti che vengono chiusi e causa problemi, LE ALTRE CLASSI NON VENGONO
        // INSTANZIATE NUOVAMENTE
        streamsFragment = null;
        favouritesFragment = null;
        postFormFragment = null;

        viewPager = homeFragmentView.findViewById(R.id.view_pager);
        homeTabs = homeFragmentView.findViewById(R.id.home_tabs);
        FloatingActionButton fab = homeFragmentView.findViewById(R.id.fab_switch_to_post_form);

        getStreamsFragment();
        getFavouritesFragment();

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
        if (view.getId() == R.id.fab_switch_to_post_form) {
            LoggedUserActivity.getLoggedUserActivity().changeFragment(getPostFormFragment());
        }
    }

    public static HomeFragment getHomeFragment() {
        return homeFragment;
    }

    public PostsListFragment getStreamsFragment() {

        if (this.streamsFragment == null) {
            this.streamsFragment = PostsListFragment.newInstance("all");
            this.streamsFragment.getAllPosts();
        }

        return streamsFragment;
    }

    public PostsListFragment getFavouritesFragment() {

        if (this.favouritesFragment == null) {
            this.favouritesFragment = PostsListFragment.newInstance("favourites");
        }

        return favouritesFragment;
    }

    public PostFormFragment getPostFormFragment() {

        if (this.postFormFragment == null) {
            this.postFormFragment = new PostFormFragment();
        }

        return postFormFragment;
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
}
