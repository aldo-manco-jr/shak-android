package org.aldofrank.shak.streams.controllers;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.aldofrank.shak.R;

public class StreamsFragment extends Fragment {

    private PostsListFragment listAllPosts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_streams, container, false);

        listAllPosts = PostsListFragment.newInstance("all");

        getChildFragmentManager().beginTransaction().replace(R.id.list_posts_fragment, listAllPosts).commit();

        return view;
    }
}
