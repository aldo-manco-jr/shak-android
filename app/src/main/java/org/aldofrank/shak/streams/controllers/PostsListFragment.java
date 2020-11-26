package org.aldofrank.shak.streams.controllers;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.aldofrank.shak.R;
import org.aldofrank.shak.models.Post;
import org.aldofrank.shak.services.ServiceGenerator;
import org.aldofrank.shak.services.StreamsService;
import org.aldofrank.shak.streams.http.posts.PostsListResponse;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostsListFragment extends Fragment {

    private List<Post> listPosts;

    private static String typePostList;

    private String token;

    private View view;

    public PostsListFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PostsListFragment.
     */

    public static PostsListFragment newInstance(String typePostList) {

        PostsListFragment fragment = new PostsListFragment();

        Bundle args = new Bundle();
        args.putString("type", typePostList);
        fragment.setArguments(args);

        //PostsListFragment.typePostList = typePostList;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // recyclerview = dynamic list view
        // glide = image loading framework that fetch, decode, display video, images and GIF
        // circleimageview = wrap images in a circle

        view = inflater.inflate(R.layout.fragment_posts_list, container, false);

        token = LoggedUserActivity.getToken();

        getAllPosts();

        return view;
    }

    private void getAllPosts() {

        StreamsService streamsService = ServiceGenerator.createService(StreamsService.class, token);

        Call<PostsListResponse> httpRequest = streamsService.getAllPosts();

        httpRequest.enqueue(new Callback<PostsListResponse>() {

            @Override
            public void onResponse(Call<PostsListResponse> call, Response<PostsListResponse> response) {

                if (response.isSuccessful()) {

                    if (getArguments() != null) {
                        PostsListFragment.typePostList = getArguments().getString("type");
                    }

                    if (typePostList.equals("all")){

                        listPosts = response.body().getArrayPosts();
                        initializeRecyclerView();

                    }else if (typePostList.equals("favourites")){

                        listPosts = response.body().getFavouritePosts();
                        initializeRecyclerView();
                    }

                } else {
                    Toast.makeText(getActivity(), response.code() + " " + response.message() + " " + token, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PostsListResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initializeRecyclerView() {

        RecyclerView recyclerView = view.findViewById(R.id.listPosts);
        PostsListAdapter adapter = new PostsListAdapter(this.listPosts, getActivity());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}