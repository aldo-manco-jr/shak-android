package org.aldofrankmarco.shak.streams.controllers;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.aldofrankmarco.shak.R;
import org.aldofrankmarco.shak.models.Post;
import org.aldofrankmarco.shak.profile.controllers.ProfileFragment;
import org.aldofrankmarco.shak.services.ServiceGenerator;
import org.aldofrankmarco.shak.services.StreamsService;
import org.aldofrankmarco.shak.streams.http.GetAllUserPostsResponse;
import org.aldofrankmarco.shak.streams.http.GetNewPostsListResponse;
import org.aldofrankmarco.shak.streams.http.GetPostsListResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Permette il collegamento tra la struttura dell'oggetto e la recycler view che lo deve rappresentare
 */
public class PostsListFragment extends Fragment {

    private List<Post> listPosts;

    private int listPostsSize;
    protected static int itemRemoved;

    private String type;

    protected RecyclerView recyclerView;
    protected String lastPostDate = null;
    protected PostsListAdapter adapter = null;

    private View view;

    public PostsListFragment() {
    }

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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PostsListFragment.
     */
    public static PostsListFragment newInstance(String type, String username) {
        PostsListFragment fragment = new PostsListFragment();

        Bundle args = new Bundle();
        args.putString("type", type);
        args.putString("username", username);
        fragment.setArguments(args);

        return fragment;
    }

    protected List<Post> getListPosts(){
        return this.listPosts;
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

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.listPosts);
    }

    /**
     * Consente di recuperare tutti i post:
     * - streams: lista dei post dell'utente e dei suoi following
     * - favorites: sono i post a cui l'utente ha espresso la preferenza
     * Viene mandata una richiesta http per recuperati i dal server.
     */
    public void getAllPosts() {
        if (getArguments() == null) {
            return;
        }

        this.type = getArguments().getString("type");
        final String username = getArguments().getString("username");
        StreamsService streamsService = ServiceGenerator.createService(StreamsService.class, LoggedUserActivity.getToken());

        if (type.equals("all") || type.equals("favourites")) {

            Call<GetPostsListResponse> httpRequest = streamsService.getAllPosts();

            httpRequest.enqueue(new Callback<GetPostsListResponse>() {
                @Override
                public void onResponse(Call<GetPostsListResponse> call, Response<GetPostsListResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null : "body() non doveva essere null";

                        PostsListFragment streamsFragment = HomeFragment.getHomeFragment().getStreamsFragment();
                        streamsFragment.listPosts = response.body().getStreamPosts();
                        streamsFragment.listPostsSize = streamsFragment.listPosts.size();

                        if (streamsFragment.listPostsSize > 0){
                            streamsFragment.lastPostDate = streamsFragment.listPosts.get(0).getCreatedAt();
                        }

                        streamsFragment.initializeRecyclerView();

                        PostsListFragment favouritesFragment = HomeFragment.getHomeFragment().getFavouritesFragment();
                        favouritesFragment.listPosts = response.body().getFavouritePosts();
                        favouritesFragment.listPostsSize = favouritesFragment.listPosts.size();

                        if (favouritesFragment.listPostsSize > 0){
                            favouritesFragment.lastPostDate = listPosts.get(0).getCreatedAt();
                        }

                        favouritesFragment.initializeRecyclerView();
                    } else {
                        Toast.makeText(getActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<GetPostsListResponse> call, Throwable t) {
                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } else if (type.equals("profile") && !username.isEmpty()) {

            Call<GetAllUserPostsResponse> httpRequest = streamsService.getAllUserPosts(username);

            httpRequest.enqueue(new Callback<GetAllUserPostsResponse>() {
                @Override
                public void onResponse(Call<GetAllUserPostsResponse> call, Response<GetAllUserPostsResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null : "body() non doveva essere null";

                        listPosts = response.body().getArrayUserPosts();

                        if (listPostsSize > 0){
                            lastPostDate = listPosts.get(0).getCreatedAt();
                        }

                        initializeRecyclerView();
                    } else {
                        //TODO nel caso in cui non sia possibile accedere a internet usare response.code
                        // e response.message causa il crash dell'applicazione
                        Toast.makeText(getActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<GetAllUserPostsResponse> call, Throwable t) {
                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    /**
     * Consente di recuperare tutti i post aggiornati a partire dal pià recente visualizzato:
     * - streams: lista dei post dell'utente e dei suoi following
     * - favorites: sono i post a cui l'utente ha espresso la preferenza
     * Viene mandata una richiesta http per recuperati i dal server.
     */
    public void getAllNewPosts() {
        Bundle arguments = getArguments();
        if (arguments == null) {
            return;
        }

        this.type = arguments.getString("type");
        final String username = arguments.getString("username");
        StreamsService streamsService = ServiceGenerator.createService(StreamsService.class, LoggedUserActivity.getToken());

        if (type.equals("all") || type.equals("favourites")) {
            Call<GetNewPostsListResponse> httpRequest = streamsService.getAllNewPosts(lastPostDate);

            httpRequest.enqueue(new Callback<GetNewPostsListResponse>() {
                @Override
                public void onResponse(Call<GetNewPostsListResponse> call, Response<GetNewPostsListResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null : "body() non doveva essere null";

                        Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), "QUANTI===??"+response.body().getArrayPosts().size(), Toast.LENGTH_SHORT).show();

                        List<Post> newListPosts = response.body().getArrayPosts();
                        boolean isNewPostExist = (newListPosts != null && newListPosts.size() > 0);

                        if (isNewPostExist) {
                            lastPostDate = newListPosts.get(0).getCreatedAt();

                            updateRecyclerView(newListPosts);
                            //Toast.makeText(getActivity(), "AGGIORNATTTTTTTTOOOOO: " + newListPosts.get(0).getPostContent(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), "NON ESISTEEEEE" + (newListPosts == null), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), response.message() + "  " + response.code() + "  " +response.toString(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<GetNewPostsListResponse> call, Throwable t) {
                    Toast.makeText(getActivity(), "trow getNewpost", Toast.LENGTH_LONG).show();

                    //Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } else if (type.equals("profile") && !username.isEmpty()) {
            Toast.makeText(getActivity(), "PROFILLLEEEE", Toast.LENGTH_LONG).show();
            // profile fragments: visualizza i post relativi al profilo di una persona
            Call<GetAllUserPostsResponse> httpRequest = streamsService.getAllUserPosts(username);

            httpRequest.enqueue(new Callback<GetAllUserPostsResponse>() {
                @Override
                public void onResponse(Call<GetAllUserPostsResponse> call, Response<GetAllUserPostsResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null : "body() non doveva essere null";

                        listPosts = response.body().getArrayUserPosts();
                        if (listPosts.size() > 0) {
                            lastPostDate = listPosts.get(0).getCreatedAt();
                        }

                        initializeRecyclerView();
                    } else {
                        //TODO nel caso in cui non sia possibile accedere a internet usare response.code
                        // e response.message causa il crash dell'applicazione
                        Toast.makeText(getActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<GetAllUserPostsResponse> call, Throwable t) {
                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * Viene collegata la recycler view con l'adapter
     */
    private void initializeRecyclerView() {
        adapter = new PostsListAdapter(this.listPosts, this.type, this, view);

        //RecyclerView recyclerView = view.findViewById(R.id.listPosts);
        //PostsListFragment.recyclerView = view.findViewById(R.id.listPosts);
        RecyclerView recyclerView = view.findViewById(R.id.listPosts);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    /**
     * Viene collegata la recycler view con l'adapter
     */
    private void updateRecyclerView(List<Post> newListPosts) {
        assert newListPosts != null && newListPosts.get(0) != null: "newListPost e il primo elemento" +
                " non potevano essere null";

        lastPostDate = newListPosts.get(0).getCreatedAt();
        this.listPostsSize += newListPosts.size();

        Log.v("update", String.valueOf(newListPosts.size()));

        adapter.addPosts(newListPosts);

        HomeFragment.getHomeFragment().getStreamsFragment().adapter.notifyItemInserted(0);
    }

    void pushOnFavoritesList(Post post){
        PostsListFragment favouritesFragment = HomeFragment.getHomeFragment().getFavouritesFragment();

        favouritesFragment.listPosts.add(0, post);
        favouritesFragment.adapter.notifyItemInserted(0);
    }

    void  removeLikeFromStreamsList(Post post,
                                    PostsListAdapter.PostItemHolder holder,
                                    PostsListFragment streamsFragment,
                                    PostsListFragment favouritesFragment){
        List<Post> listPosts = streamsFragment.adapter.getListPosts();

        for (int i = 0; i < listPosts.size(); i++) {
            if (listPosts.get(i).equals(post)){
                listPosts.get(i).removeLikeFromArray(LoggedUserActivity.getUsernameLoggedUser());

                streamsFragment.adapter.notifyItemChanged(i, listPosts.get(i));

                break;
            }
        }

        View favoritesView =  favouritesFragment.getView();
        RecyclerView recyclerView = favoritesView.findViewById(R.id.listPosts);
        recyclerView.removeView(recyclerView);

        //favouritesFragment.adapter.notifyItemRemoved(holder.getAdapterPosition());
        //favouritesFragment.adapter.notifyItemRangeChanged(holder.getAdapterPosition(), listPosts.size());
        favouritesFragment.adapter.getListPosts().remove(post);
        favouritesFragment.adapter.notifyItemRemoved(holder.getAdapterPosition());
    }

    void  removeLikeFromProfilePostsList(Post post,
                                    PostsListAdapter.PostItemHolder holder,
                                    PostsListFragment profilePostsFragment){

        List<Post> listPosts = profilePostsFragment.adapter.getListPosts();

        for (int i = 0; i < listPosts.size(); i++) {
            if (listPosts.get(i).equals(post)){
                listPosts.get(i).removeLikeFromArray(LoggedUserActivity.getUsernameLoggedUser());

                profilePostsFragment.adapter.notifyItemChanged(i, listPosts.get(i));

                break;
            }
        }
    }

    void removeLikeFromFavoritesList(Post post){
        //devo aggiornare il post di streams e levarlo da favourites
        PostsListFragment favouritesFragment = HomeFragment.getHomeFragment().getFavouritesFragment();
        View favoritesView =  favouritesFragment.getView();
        RecyclerView recyclerView = favoritesView.findViewById(R.id.listPosts);
        List<Post> listPosts = favouritesFragment.adapter.getListPosts();

        for (int i = 0; i < listPosts.size(); i++) {
            if (listPosts.get(i).equals(post)){
                recyclerView.removeView(recyclerView);

                favouritesFragment.adapter.notifyItemRemoved(i);
                favouritesFragment.adapter.notifyItemRangeChanged(i, listPosts.size());
                favouritesFragment.adapter.getListPosts().remove(post);

                break;
            }
        }
    }

    /**
     * TODO
     */
    private int getPostPosition(Post post){
        for (int i = 0; i < listPosts.size(); i++){
            if (listPosts.get(i).equals(post)){
                return i;
            }
        }

        return -1;
    }

    /**
     * Questa funzione è accesibile solo per i post dell'utente autenticato e invia una richiesta
     * http in cui richieste la cancellazione del post.
     */
    void deletePost(final Post selectPost,
                    final View view,
                    final PostsListAdapter.PostItemHolder holder,
                    final String type) {
        StreamsService streamsService = ServiceGenerator.createService(StreamsService.class, LoggedUserActivity.getToken());
        Call<Object> httpRequest = streamsService.deletePost(selectPost);

        httpRequest.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {

                    if (!type.equals("profile")) {

                        PostsListFragment favouritesFragment = HomeFragment.getHomeFragment().getFavouritesFragment();
                        RecyclerView favouritesRecyclerView = favouritesFragment.getView().findViewById(R.id.listPosts);

                        PostsListFragment streamsFragment = HomeFragment.getHomeFragment().getStreamsFragment();
                        RecyclerView streamsRecyclerView = streamsFragment.getView().findViewById(R.id.listPosts);

                        if (type.equals("favourites")) {
                            // il post da riuovere è stato selezionato dal pannello favourites
                            removePostFromPrimaryTab(favouritesFragment, selectPost, view, favouritesRecyclerView, holder);
                            removePostFromSecondaryTab(streamsFragment, selectPost);
                        } else if (type.equals("all")) {
                            Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), "rimuovo anche da favourites", Toast.LENGTH_SHORT).show();
                            removePostFromPrimaryTab(streamsFragment, selectPost, view, streamsRecyclerView, holder);
                            removePostFromSecondaryTab(favouritesFragment, selectPost);
                        }

                    }else{

                        PostsListFragment profilePostsFragment = ProfileFragment.getProfileFragment().getProfilePostsFragment(selectPost.getUsernamePublisher());
                        RecyclerView profilePostsRecyclerView = profilePostsFragment.getView().findViewById(R.id.listPosts);

                        removePostFromPrimaryTab(profilePostsFragment, selectPost, view, profilePostsRecyclerView, holder);
                    }

                } else {
                    Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * @param postListFragment
     * @param post
     * @param view
     * @param recyclerView
     * @param holder
     *
     *TODO COMMENTARE
     */
    private static void removePostFromPrimaryTab(PostsListFragment postListFragment, Post post,
                                                 View view, RecyclerView recyclerView, PostsListAdapter.PostItemHolder holder){
        recyclerView.removeView(view);

        postListFragment.adapter.getListPosts().remove(post);
        postListFragment.adapter.notifyItemRemoved(holder.getAdapterPosition());
    }

    /**
     * @param postsListFragment
     * @param selectPost
     *
     *TODO COMMENTARE
     */
    private static void removePostFromSecondaryTab(PostsListFragment postsListFragment, Post selectPost){
        List<Post> listPosts = postsListFragment.adapter.getListPosts();
        for (int i = 0; i < listPosts.size(); i++) {
            if (listPosts.get(i).equals(selectPost)){
                // rimuovo il post
                postsListFragment.adapter.getListPosts().remove(listPosts.get(i));
                postsListFragment.adapter.notifyItemRemoved(i);
                postsListFragment.adapter.notifyItemRangeChanged(i, listPosts.size());

                break;
            }
        }
        Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), "ITERATOOO????::: " + listPosts.size(), Toast.LENGTH_SHORT).show();
    }

    //TODO IMPLEMENTATO per recuperare l'istanza se non è più in memoria
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        /*savedInstanceState.putBoolean("MyBoolean", true);
        savedInstanceState.putDouble("myDouble", 1.9);
        savedInstanceState.putInt("MyInt", 1);
        savedInstanceState.putString("MyString", "Welcome back to Android");*/
        // etc.
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        /*boolean myBoolean = savedInstanceState.getBoolean("MyBoolean");
        double myDouble = savedInstanceState.getDouble("myDouble");
        int myInt = savedInstanceState.getInt("MyInt");
        String myString = savedInstanceState.getString("MyString");*/
    }
}