package org.aldofrank.shak.streams.controllers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nkzawa.emitter.Emitter;
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

        LoggedUserActivity.getSocket().on("refreshPage", updatePostCommentsList);
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
                    LoggedUserActivity.getLoggedUserActivity().getSupportFragmentManager().beginTransaction().replace(R.id.logged_user_fragment, HomeFragment.getHomeFragment().getCommentFormFragment()).commit();
                    //LoggedUserActivity.getLoggedUserActivity().changeFragment();
                }else if (getArguments().getString("type").equals("profile")){
                    LoggedUserActivity.getLoggedUserActivity().getSupportFragmentManager().beginTransaction().replace(R.id.logged_user_fragment, ProfileFragment.getProfileFragment().getCommentFormFragment()).commit();
                }
            }
        });

        getAllPostComments();

        return view;
    }

    /**
     * Quando un post viene pubblicato la home page viene aggiornata.
     */
    private Emitter.Listener updatePostCommentsList = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            if (LoggedUserActivity.getLoggedUserActivity() != null) {
                LoggedUserActivity.getLoggedUserActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // quando un post viene pubblicato la socket avvisa del necessario aggiornmento
                        if (getArguments().getString("type").equals("home")){
                            Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), "home", Toast.LENGTH_LONG).show();
                            HomeFragment.getHomeFragment().getCommentsListFragment().getAllPostComments();
                        }else if (getArguments().getString("type").equals("profile")){
                            Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), "profile", Toast.LENGTH_LONG).show();
                            ProfileFragment.getProfileFragment().getCommentsListFragment().getAllPostComments();
                        }
                    }
                });
            }
        }
    };

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
        //getFragmentManager().beginTransaction().remove(HomeFragment.getHomeFragment().getCommentsListFragment()).commitAllowingStateLoss();
        LoggedUserActivity.getLoggedUserActivity().changeFragment(HomeFragment.getHomeFragment());
    }
}