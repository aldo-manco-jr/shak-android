package org.aldofrankmarco.shak.streams.controllers;

import android.os.Bundle;
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

    protected static int itemRemoved;

    private String type;

    protected RecyclerView recyclerView;
    private PostsListAdapter adapter;

    private View view;
    StreamsService streamsService;

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

    private List<Post> getListPosts(){
        return this.adapter.getListPosts();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.adapter = new PostsListAdapter(this, view);
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

        type = getArguments().getString("type");
        if (type.equals("profile")){
            // non ci sono altri frammenti simili in profile, può essere inizializzato direttamente
            getAllPosts();
        }

        recyclerView = view.findViewById(R.id.listPosts);
    }

    protected String getType(){
        return this.type;
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

        if (type.equals("all") || type.equals("favourites")) {

            Call<GetPostsListResponse> httpRequest = LoggedUserActivity.getStreamsService().getAllPosts();

            httpRequest.enqueue(new Callback<GetPostsListResponse>() {
                @Override
                public void onResponse(Call<GetPostsListResponse> call, Response<GetPostsListResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null : "body() non doveva essere null";

                        PostsListFragment streamsFragment =
                                HomeFragment.getHomeFragment().getStreamsFragment();
                        streamsFragment.initializeRecyclerView(
                                streamsFragment,
                                response.body().getStreamPosts()
                        );

                        PostsListFragment favouritesFragment =
                                HomeFragment.getHomeFragment().getFavouritesFragment();
                        favouritesFragment.initializeRecyclerView(
                                favouritesFragment,
                                response.body().getFavouritePosts()
                        );
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

            Call<GetAllUserPostsResponse> httpRequest = LoggedUserActivity.getStreamsService().getAllUserPosts(username);

            httpRequest.enqueue(new Callback<GetAllUserPostsResponse>() {
                @Override
                public void onResponse(Call<GetAllUserPostsResponse> call, Response<GetAllUserPostsResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null : "body() non doveva essere null";

                        PostsListFragment profileFragment =
                                ProfileFragment.getProfileFragment().getProfilePostsFragment(username);
                        initializeRecyclerView(
                                profileFragment,
                                response.body().getArrayUserPosts()
                        );
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

        PostsListFragment streamsFragment = HomeFragment.getHomeFragment().getStreamsFragment();
        final String lastPostDate =
                (streamsFragment.getListPosts().size() > 0)?
                        streamsFragment.getListPosts().get(0).getCreatedAt():
                        "1970-01-01'T'00:00:01.000'Z'";

        if (type.equals("all") || type.equals("favourites")) {
            Call<GetNewPostsListResponse> httpRequest = LoggedUserActivity.getStreamsService().getAllNewPosts(lastPostDate);

            httpRequest.enqueue(new Callback<GetNewPostsListResponse>() {
                @Override
                public void onResponse(Call<GetNewPostsListResponse> call, Response<GetNewPostsListResponse> response) {

                    if (response.isSuccessful()) {
                        assert response.body() != null : "body() non doveva essere null";

                        List<Post> newListPosts = response.body().getArrayPosts();
                        boolean isNewPostExist = (newListPosts != null && newListPosts.size() > 0);

                        if (isNewPostExist) {
                            updateRecyclerView(newListPosts);
                        }
                    } else {
                        Toast.makeText(getActivity(), response.message() + "  " + response.code() + "  " +response.toString(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<GetNewPostsListResponse> call, Throwable t) {
                    Toast.makeText(getActivity(),"t.getMessage()", Toast.LENGTH_LONG).show();
                }
            });

        } else if (type.equals("profile") && !username.isEmpty()) {
            // profile fragments: visualizza i post relativi al profilo di una persona
            Call<GetAllUserPostsResponse> httpRequest = LoggedUserActivity.getStreamsService().getAllUserPosts(username);

            httpRequest.enqueue(new Callback<GetAllUserPostsResponse>() {
                @Override
                public void onResponse(Call<GetAllUserPostsResponse> call, Response<GetAllUserPostsResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null : "body() non doveva essere null";

                        /*if (listPosts.size() > 0) {
                            lastPostDate = listPosts.get(0).getCreatedAt();
                        }*/

                        PostsListFragment profileFragment =
                                ProfileFragment.getProfileFragment().getProfilePostsFragment(username);
                        initializeRecyclerView(
                                profileFragment,
                                response.body().getArrayUserPosts()
                        );
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
    private void initializeRecyclerView(PostsListFragment postsListFragment, List<Post> listPosts) {
        assert listPosts != null && postsListFragment != null: "listPost e postListFragment non potevano" +
                "essere null";

        postsListFragment.adapter.addPosts(listPosts);

        //RecyclerView recyclerView = view.findViewById(R.id.listPosts);
        //PostsListFragment.recyclerView = view.findViewById(R.id.listPosts);
        RecyclerView recyclerView = view.findViewById(R.id.listPosts);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    /**
     * @param newListPosts contiene la lista dei post non in possesso attualmente dell'utente
     *
     * Viene aggiornato l'adapter aggiungendo nuovi elementi, l'aggiunta viene notifica
     * successivamente.
     */
    private void updateRecyclerView(List<Post> newListPosts) {
        assert newListPosts != null && newListPosts.get(0) != null: "newListPost e il primo elemento" +
                " non potevano essere null";

        PostsListFragment streamsFragment  = HomeFragment.getHomeFragment().getStreamsFragment();

        if (newListPosts.size() > 0) {
            streamsFragment.adapter.addPosts(newListPosts);
            streamsFragment.adapterNotifyChange(streamsFragment, AdapterNotifyType.itemInserted);
        }
    }

    protected void adapterNotifyChange(PostsListFragment fragment, @NonNull AdapterNotifyType notifyType){
        if (notifyType.equals(AdapterNotifyType.dataSetChanged)) {
            fragment.adapter.notifyDataSetChanged();
        } else if (notifyType.equals(AdapterNotifyType.itemInserted)) {
            fragment.adapter.notifyItemInserted(0);
        }
    }

    void pushOnFavoritesList(Post post){
        PostsListFragment favouritesFragment = HomeFragment.getHomeFragment().getFavouritesFragment();

        favouritesFragment.getListPosts().add(0, post);
        favouritesFragment.adapter.notifyItemInserted(0);
    }

    void  removeLikeFromStreamsList(Post post,
                                    PostsListAdapter.PostItemHolder holder,
                                    PostsListFragment streamsFragment,
                                    PostsListFragment favouritesFragment){
        List<Post> listPosts = streamsFragment.getListPosts();

        //holder.likeButton.getTag("unlike");
        //post.getUzgetUserLike;

        for (int i = 0; i < listPosts.size(); i++) {
            if (listPosts.get(i).equals(post)){
                listPosts.get(i).putIsLiked(false);

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
        favouritesFragment.getListPosts().remove(post);
        favouritesFragment.adapter.notifyItemRemoved(holder.getAdapterPosition());
    }

    void  removeLikeFromProfilePostsList(Post post,
                                    PostsListAdapter.PostItemHolder holder,
                                    PostsListFragment profilePostsFragment){

        List<Post> listPosts = profilePostsFragment.getListPosts();

        for (int i = 0; i < listPosts.size(); i++) {
            if (listPosts.get(i).equals(post)){
                listPosts.get(i).putIsLiked(false);
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
        List<Post> listPosts = favouritesFragment.getListPosts();

        for (int i = 0; i < listPosts.size(); i++) {
            if (listPosts.get(i).equals(post)){
                listPosts.get(i).putIsLiked(false);

                recyclerView.removeView(recyclerView);

                favouritesFragment.adapter.notifyItemRemoved(i);
                favouritesFragment.adapter.notifyItemRangeChanged(i, listPosts.size());
                favouritesFragment.getListPosts().remove(post);

                break;
            }
        }
    }

    /**
     * Questa funzione è accesibile solo per i post dell'utente autenticato e invia una richiesta
     * http in cui richieste la cancellazione del post.
     */
    void deletePost(final Post selectPost,
                    final View view,
                    final PostsListAdapter.PostItemHolder holder) {
        Call<Object> httpRequest = LoggedUserActivity.getStreamsService().deletePost(selectPost.getPostId());

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

        postListFragment.getListPosts().remove(post);
        postListFragment.adapter.notifyItemRemoved(holder.getAdapterPosition());
    }

    /**
     * @param postsListFragment
     * @param selectPost
     *
     *TODO COMMENTARE
     */
    private static void removePostFromSecondaryTab(PostsListFragment postsListFragment, Post selectPost){
        List<Post> listPosts = postsListFragment.getListPosts();
        for (int i = 0; i < listPosts.size(); i++) {
            if (listPosts.get(i).equals(selectPost)){
                // rimuovo il post
                postsListFragment.getListPosts().remove(listPosts.get(i));
                postsListFragment.adapter.notifyItemRemoved(i);
                postsListFragment.adapter.notifyItemRangeChanged(i, listPosts.size());

                break;
            }
        }
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