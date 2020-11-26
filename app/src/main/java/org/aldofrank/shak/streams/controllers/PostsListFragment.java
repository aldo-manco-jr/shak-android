package org.aldofrank.shak.streams.controllers;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.aldofrank.shak.R;

public class PostsListFragment extends Fragment {

    /*StreamsService streamsService = ServiceGenerator.createService(StreamsService.class, token);

                    Toast.makeText(getActivity(), token, Toast.LENGTH_LONG).show();

                    Call<PostsListResponse> httpRequest = streamsService.getAllPosts();

                    httpRequest.enqueue(new Callback<PostsListResponse>() {

                        @Override
                        public void onResponse(Call<PostsListResponse> call, Response<PostsListResponse> response) {

                            if (response.isSuccessful()){
                                Toast.makeText(getActivity(), response.body().getArrayPosts().get(0).getUserId(), Toast.LENGTH_LONG).show();
                                //Log.i("list", response.body().getArrayPosts().get(0).getPostContent());
                            }else {
                                Toast.makeText(getActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<PostsListResponse> call, Throwable t) {
                            Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });*/

    private static String typePostList;

    public PostsListFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PostsListFragment.
     */

    public static PostsListFragment newInstance(String typePostList) {

        PostsListFragment fragment = new PostsListFragment();
        Bundle args = new Bundle();
        args.putString(PostsListFragment.typePostList, typePostList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // recyclerview = dynamic list view
        // glide = image loading framework that fetch, decode, display video, images and GIF
        // circleimageview = wrap images in a circle

        View view = inflater.inflate(R.layout.fragment_posts_list, container, false);



        return view;
    }
}