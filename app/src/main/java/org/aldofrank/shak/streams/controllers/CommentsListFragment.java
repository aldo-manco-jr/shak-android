package org.aldofrank.shak.streams.controllers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.aldofrank.shak.R;
import org.aldofrank.shak.models.Post;
import org.aldofrank.shak.profile.controllers.ProfileFragment;
import org.aldofrank.shak.services.ServiceGenerator;
import org.aldofrank.shak.services.StreamsService;
import org.aldofrank.shak.streams.http.GetPostResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.aldofrank.shak.streams.controllers.CommentsListAdapter.postId;

public class CommentsListFragment extends Fragment implements OnBackPressed {
    private List<Post.Comment> listPostComments;

    private View view;

    private static Post post;

    private FloatingActionButton buttonAddComment;

    public CommentsListFragment() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PostsListFragment.
     */
    public static CommentsListFragment newInstance(String type, Post post) {

        CommentsListFragment fragment = new CommentsListFragment();

        Bundle args = new Bundle();
        args.putString("type", type);
        fragment.setArguments(args);

        CommentsListFragment.post = post;

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
        view = inflater.inflate(R.layout.fragment_comments_list, container, false);

        CommentsListAdapter.postId = post.getPostCreatedAt();

        buttonAddComment = view.findViewById(R.id.fab_add_comment);

        buttonAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getArguments().getString("type").equals("home")){
                    HomeFragment.getHomeFragment().getFragmentManager().beginTransaction().replace(R.id.home_fragment, HomeFragment.getHomeFragment().getCommentFormFragment()).addToBackStack("openCommentFormFragment").commit();
                }else if (getArguments().getString("type").equals("profile")){
                    ProfileFragment.getProfileFragment().getFragmentManager().beginTransaction().replace(R.id.profile_fragment, ProfileFragment.getProfileFragment().getCommentFormFragment()).commit();
                }
            }
        });

        getAllPostComments();

        return view;
    }

    public void getAllPostComments() {

        if (post == null) {
            return;
        }

        StreamsService streamsService = ServiceGenerator.createService(StreamsService.class, LoggedUserActivity.getToken());

        Call<GetPostResponse> httpRequest = streamsService.getPost(postId);

        httpRequest.enqueue(new Callback<GetPostResponse>() {

            @Override
            public void onResponse(Call<GetPostResponse> call, Response<GetPostResponse> response) {

                if (response.isSuccessful()) {

                    //TODO ERA NULL
                    listPostComments = response.body().getPost().getArrayComments();
                    initializeRecyclerView();

                } else {
                    Toast.makeText(getActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GetPostResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Viene collegata la recycler view con l'adapter
     */
    private void initializeRecyclerView() {
        RecyclerView recyclerView = view.findViewById(R.id.listComments);
        CommentsListAdapter adapter = new CommentsListAdapter(this.listPostComments, getArguments().getString("type"));

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onBackPressed() {
        getFragmentManager().beginTransaction().remove(HomeFragment.getHomeFragment().getCommentsListFragment()).commitAllowingStateLoss();
    }
}