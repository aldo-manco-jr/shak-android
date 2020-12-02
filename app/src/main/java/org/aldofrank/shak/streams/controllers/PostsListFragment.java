package org.aldofrank.shak.streams.controllers;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.aldofrank.shak.R;
import org.aldofrank.shak.models.Post;
import org.aldofrank.shak.services.ServiceGenerator;
import org.aldofrank.shak.services.StreamsService;
import org.aldofrank.shak.streams.http.PostsListResponse;

import java.net.URISyntaxException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Permette il collegamento tra la struttura dell'oggetto e la recycler view che lo deve rappresentare
 */
public class PostsListFragment extends Fragment {

    private List<Post> listPosts;

    private String token;

    private View view;

    protected Socket socket;
    {
        try {
            socket = IO.socket("http://10.0.2.2:3000/");
        } catch (URISyntaxException ignored) {
        }
    }

    public PostsListFragment() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PostsListFragment.
     */
    public static PostsListFragment newInstance(String type) {
        PostsListFragment fragment = new PostsListFragment();
        Bundle args = new Bundle();

        args.putString("type", type);
        fragment.setArguments(args);

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

    /**
     * Consente di recuperare tutti i post:
     *  - streams: lista dei post dell'utente e dei suoi following
     *  - favorites: sono i post a cui l'utente ha espresso la preferenza
     * Viene mandata una richiesta http per recuperati i dal server.
     */
    public void getAllPosts() {
        if (getArguments() == null) {
            return;
        }

        final String type = getArguments().getString("type");
        StreamsService streamsService = ServiceGenerator.createService(StreamsService.class, token);
        Call<PostsListResponse> httpRequest = streamsService.getAllPosts();

        httpRequest.enqueue(new Callback<PostsListResponse>() {
            @Override
            public void onResponse(Call<PostsListResponse> call, Response<PostsListResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null : "body() non doveva essere null";

                    if (type.equals("all")) {
                        listPosts = response.body().getArrayPosts();
                    } else if (type.equals("favourites")) {
                        listPosts = response.body().getFavouritePosts();
                    }

                    initializeRecyclerView();
                } else {
                    Toast.makeText(getActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PostsListResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "adada " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        socket.disconnect();
        //socket.off("disconnect");
    }

    /**
     * Viene collegata la recycler view con l'adapter
     */
    private void initializeRecyclerView() {
        RecyclerView recyclerView = view.findViewById(R.id.listPosts);
        PostsListAdapter adapter = new PostsListAdapter(this.listPosts, getActivity(), this);
        this.socket = adapter.socket;

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}