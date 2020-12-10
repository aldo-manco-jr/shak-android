package org.aldofrank.shak.streams.controllers;

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

import org.aldofrank.shak.R;
import org.aldofrank.shak.models.Post;
import org.aldofrank.shak.services.ServiceGenerator;
import org.aldofrank.shak.services.StreamsService;
import org.aldofrank.shak.streams.http.GetAllUserPostsResponse;
import org.aldofrank.shak.streams.http.GetNewPostsListResponse;
import org.aldofrank.shak.streams.http.GetPostsListResponse;

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

        getAllPosts();
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

                        if (type.equals("all")) {
                            listPosts = response.body().getArrayPosts();
                        } else if (type.equals("favourites")) {
                            listPosts = response.body().getFavouritePosts();
                        }

                        listPostsSize = listPosts.size();
                        if (listPostsSize > 0){
                            lastPostDate = listPosts.get(0).getCreatedAt();
                        }

                        initializeRecyclerView();
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
                        //Toast.makeText(getActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
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
            //home: streams fragment e fovourites fragment
            //Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), "--- " + lastPostDate, Toast.LENGTH_SHORT).show();

            //TODO, FORSE OCCORRE METTERE NON IL PRIMO POST MA L'ULTIMO
            Call<GetNewPostsListResponse> httpRequest = null;
            /*JsonObject lastPostDateObject = new JsonObject();
            lastPostDateObject.addProperty("created_at", lastPostDate);*/
/*
            try {
               // httpRequest = streamsService.getAllNewPosts(PostsListAdapter.localTimeToUtc(lastPostDate));
                httpRequest = streamsService.getAllNewPosts(PostsListAdapter.localTimeToUtc(lastPostDate));
            } catch (ParseException e) {
                e.printStackTrace();
            };
*/
            httpRequest = streamsService.getAllNewPosts(lastPostDate);
            /*if (lastPostDate != null) {
                JsonObject lastPostDateObject = new JsonObject();
                lastPostDateObject.addProperty("created_at", lastPostDate);
                httpRequest = streamsService.getAllNewPosts(lastPostDateObject);
            } else {
                //httpRequest = streamsService.getAllPosts();
            }*/

            httpRequest.enqueue(new Callback<GetNewPostsListResponse>() {
                @Override
                public void onResponse(Call<GetNewPostsListResponse> call, Response<GetNewPostsListResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null : "body() non doveva essere null";

                        //Toast.makeText(getActivity(), "Success getAllNewResponse", Toast.LENGTH_SHORT).show();

                        /*List<Post> newListPosts = null;

                        if (type.equals("all")) {
                            newListPosts = response.body().getArrayPosts();
                        } else if (type.equals("favourites")) {
                            newListPosts = response.body().getArrayPosts();
                        }*/
                        Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), "QUANTI===??"+response.body().getArrayPosts().size(), Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getActivity(),  response.body().getMessage().toString(), Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getActivity(),  response.body().toString(), Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getActivity(),  response.body().getArrayPosts().toString(), Toast.LENGTH_SHORT).show();
                        List<Post> newListPosts = response.body().getArrayPosts();
                        //Toast.makeText(getActivity(), "NEW POST", Toast.LENGTH_LONG).show();
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
                        //Toast.makeText(getActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
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

        favouritesFragment.adapter.notifyItemRemoved(holder.getAdapterPosition());
        favouritesFragment.adapter.notifyItemRangeChanged(holder.getAdapterPosition(), listPosts.size());
        favouritesFragment.adapter.getListPosts().remove(post);
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

    void updateUnlikeOnStreamsList(Post post, View v){
        int position = getPostPosition(post);
        PostsListFragment favouritesFragment = HomeFragment.getHomeFragment().getFavouritesFragment();

        if (position > -1) {
            // la posizione del post è valida
            View favoritesView =  HomeFragment.getHomeFragment().getFavouritesFragment().getView();
            RecyclerView recyclerView = favoritesView.findViewById(R.id.listPosts);
            recyclerView.removeView(v);

            favouritesFragment.adapter.notifyItemRemoved(position);
            favouritesFragment.adapter.notifyItemRangeChanged(position, listPosts.size());

            favouritesFragment.listPosts.remove(post);
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
     * @param position Indica la posizione di un elemento nella recycler view
     *
     * Rimuove un oggetto nella posizione specificata dalla recycler view
     */
    //TODO non più usato perchò usato dall'altra class
    /*protected void removeItemByPositionFromRecyclerView(int position) {
        RecyclerView recyclerView = view.findViewById(R.id.listPosts);

        recyclerView.removeViewAt(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, listPosts.size());
    }*/


    /**
     * Questa funzione è accesibile solo per i post dell'utente autenticato e invia una richiesta
     * http in cui richieste la cancellazione del post.
     */
    void deletePost(final Post selectPost, final View view,
                    final PostsListAdapter.PostItemHolder holder, final String type) {
        StreamsService streamsService = ServiceGenerator.createService(StreamsService.class, LoggedUserActivity.getToken());
        Call<Object> httpRequest = streamsService.deletePost(selectPost);

        httpRequest.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    PostsListFragment favouritesFragment = HomeFragment.getHomeFragment().getFavouritesFragment();
                    RecyclerView favouritesRecyclerView = favouritesFragment.getView().findViewById(R.id.listPosts);

                    PostsListFragment streamsFragment = HomeFragment.getHomeFragment().getStreamsFragment();
                    RecyclerView streamsRecyclerView = streamsFragment.getView().findViewById(R.id.listPosts);

                    if (type.equals("favourites")){
                        // il post da riuovere è stato selezionato dal pannello favourites
                        removePostFromPrimaryTab(favouritesFragment, selectPost, view, favouritesRecyclerView, holder);
                        removePostFromSecondaryTab(streamsFragment, selectPost);
                    } else {
                        removePostFromPrimaryTab(streamsFragment, selectPost, view, streamsRecyclerView, holder);
                        removePostFromSecondaryTab(favouritesFragment, selectPost);
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
     * @param selectPost
     * @param view
     * @param recyclerView
     *
     *TODO COMMENTARE
     */
    private static void removePostFromPrimaryTab(PostsListFragment postListFragment, Post selectPost,
                                                 View view, RecyclerView recyclerView, PostsListAdapter.PostItemHolder holder){
        recyclerView.removeView(view);
        postListFragment.adapter.getListPosts().remove(selectPost);
        postListFragment.adapter.notifyItemRemoved(holder.getAdapterPosition());
        postListFragment.adapter.notifyItemRangeChanged(holder.getAdapterPosition(), postListFragment.adapter.getListPosts().size());
    }

    /**
     * @param postsListFragment
     * @param selectPost
     *
     *TODO COMMENTARE
     */
    private static void removePostFromSecondaryTab(PostsListFragment postsListFragment, Post selectPost){
        for (int i = 0; i < postsListFragment.adapter.getListPosts().size(); i++) {
            Post post = postsListFragment.adapter.getListPosts().get(i);

            if (post.equals(selectPost)){
                // rimuovo il post
                postsListFragment.adapter.getListPosts().remove(post);
                postsListFragment.adapter.notifyItemRemoved(i);
                postsListFragment.adapter.notifyItemRangeChanged(i, postsListFragment.adapter.getListPosts().size());

                break;
            }
        }
    }
}