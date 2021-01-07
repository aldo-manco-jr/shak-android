package org.aldofrankmarco.shak.streams.controllers.posts;

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
import org.aldofrankmarco.shak.streams.controllers.AdapterNotifyType;
import org.aldofrankmarco.shak.streams.controllers.HomeFragment;
import org.aldofrankmarco.shak.streams.controllers.LoggedUserActivity;
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

    private String type;
    private String username;

    RecyclerView recyclerView;

    private PostsListAdapter adapter;

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

        if (this.adapter == null) {
            this.adapter = new PostsListAdapter(this, view);
        }
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
        username = getArguments().getString("username");

        //TODO verificare se la viene aggiornata se nel caso in cui ci si trovi in un'altra schermata e
        // vengano ricevuti dei messaggi, verificare quindi se le socket continua a funzionare in
        // un'altra schermata dell'applicazione
        boolean isNotEmptyList = (this.adapter.getListPosts() != null && this.adapter.getListPosts().size() > 0);
        if (isNotEmptyList) {
            // la clase era già stata istanziata, deve essere ricostrutita la "view"
            if (type.equals("profile")) {
                assert username != null : "Nel profilo doveva" +
                        "essere sempre specificato l'username nel bundle";

                //TODO come faccio a riprendere l'user visualizzato?
                initializeRecyclerView(
                        null
                );
            } else if (type.equals("streams") || type.equals("favourites")) {
                initializeRecyclerView(
                        this,
                        null);
            }
        }

        recyclerView = view.findViewById(R.id.list_posts);

        if (adapter.getListPosts().size() == 0) {
            getAllPosts();
        }
    }

    private List<Post> getListPosts() {
        if (this.adapter == null) {
            this.adapter = new PostsListAdapter(this, view);
        }

        return this.adapter.getListPosts();
    }

    /**
     * Consente di recuperare tutti i post tramite una richiesta http al server:
     * - streams: lista dei post dell'utente e dei suoi following
     * - favorites: sono i post a cui l'utente ha espresso la preferenza
     * - profile: sono i post di un utente specifico (sono visualizzati nel profilo di quell'utente)
     */
    public void getAllPosts() {
        if (getArguments() == null) {
            return;
        }

        final String lastPostDate;

        if (type.equals("streams") || type.equals("favourites")) {

            /*final PostsListFragment streamsFragment = HomeFragment.getHomeFragment().getStreamsFragment();
            final PostsListFragment favouritesFragment =
                    HomeFragment.getHomeFragment().getFavouritesFragment();
*/
            Call<GetPostsListResponse> httpRequest = null;
            List<Post> postList = null;
            if (this.adapter != null) {
                postList = getListPosts();
            }
            if (postList != null && postList.size() > 0) {
                // è necessario il post più vecchio per capire da quale post riprendere la ricerca
                /*if (type.equals("streams")) {
                    lastPostDate =
                            streamsFragment.getListPosts().get(streamsFragment.getListPosts().size() - 1).getCreatedAt();
                } else {
                    lastPostDate =
                            favouritesFragment.getListPosts().get(favouritesFragment.getListPosts().size() - 1).getCreatedAt();
                }*/
                lastPostDate = getListPosts().get(getListPosts().size() - 1).getCreatedAt();
            } else {
                //type = "all";
                lastPostDate = "1970-01-01'T'00:00:01.000'Z'";
            }

  /*
            if (adapter == null || adapter.getListPosts().size() == 0) {

            } else if (type.equals("streams")) {
                httpRequest =
                        LoggedUserActivity.getStreamsService().getAllStreamsPosts(lastPostDate);
            } else {
                httpRequest =
                        LoggedUserActivity.getStreamsService().getAllFavouritesPosts(lastPostDate);
            }
*/
            httpRequest =
                    LoggedUserActivity.getStreamsService().getAllPosts(type, lastPostDate);

            httpRequest.enqueue(new Callback<GetPostsListResponse>() {
                @Override
                public void onResponse(Call<GetPostsListResponse> call, Response<GetPostsListResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null : "body() non doveva essere null";

                        /*if (chosenType.equals("all")) {
                            // viene creata la recycler view per entrambe le tabs
                            streamsFragment.initializeRecyclerView(
                                    streamsFragment,
                                    response.body().getStreamPosts()
                            );

                            favouritesFragment.initializeRecyclerView(
                                    favouritesFragment,
                                    response.body().getFavouritePosts()
                            );*/
                        if (type.equals("streams")) {
                            if (adapter.getListPosts().size() == 0) {
                                initializeRecyclerView(
                                        response.body().getStreamPosts()
                                );
                            } else {
                                updateOldPostRecyclerView(response.body().getStreamPosts());
                            }
                        } else {
                            // è il frammento dei preferiti
                            if (adapter.getListPosts().size() == 0) {
                                initializeRecyclerView(
                                        response.body().getFavouritePosts()
                                );
                            } else {
                                updateOldPostRecyclerView(response.body().getFavouritePosts());
                            }
                        }/*
                         else if (chosenType.equals("streams")){
                            streamsFragment.updateOldPostRecyclerView(response.body().getStreamPosts());
                        } else {
                            favouritesFragment.updateOldPostRecyclerView(response.body().getFavouritePosts());
                        }
                        */
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
            final PostsListFragment profileFragment =
                    LoggedUserActivity.getLoggedUserActivity().getProfileFragments().getProfilePostsFragment(username);

            List<Post> postList = null;
            if (adapter != null) {
                postList = getListPosts();
            }
            if (postList != null && postList.size() > 0) {
                lastPostDate = getListPosts().get(getListPosts().size() - 1).getCreatedAt();
            } else {
                lastPostDate = "1970-01-01'T'00:00:01.000'Z'";
            }

            Call<GetAllUserPostsResponse> httpRequest =
                    LoggedUserActivity.getStreamsService().getAllUserPosts(username, lastPostDate);

            httpRequest.enqueue(new Callback<GetAllUserPostsResponse>() {
                @Override
                public void onResponse(Call<GetAllUserPostsResponse> call,
                                       Response<GetAllUserPostsResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null : "body() non doveva essere null";

                        if (getListPosts().size() == 0) {
                            initializeRecyclerView(
                                    response.body().getArrayUserPosts()
                            );
                        } else {
                            updateOldPostRecyclerView(response.body().getArrayUserPosts());
                        }
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

        final PostsListFragment streamsFragment = HomeFragment.getHomeFragment().getStreamsFragment();
        final String lastPostDate =
                (streamsFragment.getListPosts().size() > 0) ?
                        streamsFragment.getListPosts().get(0).getCreatedAt() :
                        "1970-01-01'T'00:00:01.000'Z'";

        Call<GetNewPostsListResponse> httpRequest = LoggedUserActivity.getStreamsService().getAllNewPosts(lastPostDate);

        httpRequest.enqueue(new Callback<GetNewPostsListResponse>() {
            @Override
            public void onResponse(Call<GetNewPostsListResponse> call, Response<GetNewPostsListResponse> response) {

                if (response.isSuccessful()) {
                    assert response.body() != null : "body() non doveva essere null";

                    List<Post> newListPosts = response.body().getArrayPosts();
                    boolean isNewPostExist = (newListPosts != null && newListPosts.size() > 0);

                    if (isNewPostExist) {
                        streamsFragment.updateRecyclerView(streamsFragment, newListPosts);
                    }
                } else {
                    Toast.makeText(getActivity(), response.message() + "  " + response.code() + "  " + response.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GetNewPostsListResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "t.getMessage()", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void resetAdapter() {
        while (adapter.getListPosts().size() != 0) {
            // viene rimosso il primo elemento fino a svuotare la lista
            adapter.getListPosts().remove(0);
        }

        adapterNotifyChange(this, AdapterNotifyType.dataSetChanged);
    }

    /**
     * Viene collegata la recycler view con l'adapter
     */
    protected void initializeRecyclerView(PostsListFragment postsListFragment, List<Post> listPosts) {
        if (postsListFragment == null) {
            return;
        }

        //if (HomeFragment.getHomeFragment().getSearchField().getText().toString().trim().equals("") || HomeFragment.getHomeFragment().getSearchField() == null) {
        if (postsListFragment.adapter != null && listPosts != null) {
            postsListFragment.adapter.addOldPosts(listPosts);
        }

        recyclerView = view.findViewById(R.id.list_posts);
        recyclerView.setAdapter(postsListFragment.adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    protected void initializeRecyclerView(List<Post> listPosts) {
        //if (HomeFragment.getHomeFragment().getSearchField().getText().toString().trim().equals("") || HomeFragment.getHomeFragment().getSearchField() == null) {
        if (this.adapter != null && listPosts != null && this.adapter.getListPosts().size()==0) {
            this.adapter.addOldPosts(listPosts);
        }

        recyclerView = this.view.findViewById(R.id.list_posts);
        recyclerView.setAdapter(this.adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    /**
     * @param newListPosts contiene la lista dei post non in possesso attualmente dell'utente
     *                     <p>
     *                     Viene aggiornato l'adapter aggiungendo nuovi elementi, l'aggiunta viene notifica
     *                     successivamente.
     */
    private void updateALLRecyclerView(List<Post> newListPosts) {
        // è in grado di aggiornare con lo stesso output tutte le recycler view
        assert newListPosts != null && newListPosts.get(0) != null : "newListPost e il primo elemento" +
                " non potevano essere null";

        PostsListFragment streamsFragment = HomeFragment.getHomeFragment().getStreamsFragment();

        if (newListPosts.size() > 0) {
            streamsFragment.adapter.addPosts(newListPosts);
            streamsFragment.adapterNotifyChange(streamsFragment, AdapterNotifyType.itemInserted);

            boolean isExisting = LoggedUserActivity.getLoggedUserActivity().checkProfileFragmentExist();
            if (isExisting) {
                PostsListFragment profilePostsFragment = LoggedUserActivity.getLoggedUserActivity()
                        .getProfileFragments().getProfilePostsFragment(null);

                if (profilePostsFragment != null) {
                    profilePostsFragment.adapter.addLoggedUserPosts(newListPosts);
                    profilePostsFragment.adapterNotifyChange(profilePostsFragment, AdapterNotifyType.itemInserted);
                }
            }
        }
    }

    /**
     * @param newListPosts contiene la lista dei post non in possesso attualmente dell'utente
     *                     <p>
     *                     Viene aggiornato l'adapter aggiungendo nuovi elementi, l'aggiunta viene notifica
     *                     successivamente.
     */
    private void updateRecyclerView(PostsListFragment chosenFragment, List<Post> newListPosts) {
        assert newListPosts != null : "newListPosts non poteva essere null";

        if (newListPosts.size() > 0) {
            chosenFragment.adapter.addPosts(newListPosts);
            chosenFragment.adapterNotifyChange(chosenFragment, AdapterNotifyType.itemInserted);

            if (chosenFragment.type.equals("streams")
                    && LoggedUserActivity.getLoggedUserActivity().checkStreamsProfileFragmentExist()) {
                // l'utente aveva già aperto il profilo e adesso và aggiornato
                PostsListFragment profilePostsFragment =
                        LoggedUserActivity.getLoggedUserActivity()
                                .getProfilePostsFragment(username);
                profilePostsFragment.adapter.addProfilePosts(newListPosts);
            }
        }
    }

    /**
     * @param newListPosts contiene la lista dei post non in possesso attualmente dell'utente
     *                     <p>
     *                     Viene aggiornato l'adapter aggiungendo nuovi elementi, l'aggiunta viene notifica
     *                     successivamente.
     */
    private void updateOldPostRecyclerView(List<Post> newListPosts) {
        assert newListPosts != null && newListPosts.get(0) != null : "newListPost e il primo elemento" +
                " non potevano essere null";

        if (newListPosts.size() > 0) {
            adapter.addOldPosts(newListPosts);
            adapterNotifyChange(this, AdapterNotifyType.dataSetChanged);
        }
    }

    /*
    private void updateOldPostRecyclerView(List<Post> newListPosts) {
        assert newListPosts != null && newListPosts.get(0) != null : "newListPost e il primo elemento" +
                " non potevano essere null";

        if (newListPosts.size() > 0) {
            PostsListFragment streamsFragment = HomeFragment.getHomeFragment().getStreamsFragment();
            // TODO POTREBBERO ESSERE DEI PREFERITI IN QUANTO SONO VECCHI, passare anche a
            //  favourites per verificare se li può prendere
            if (type.equals("streams")) {
                streamsFragment.adapter.addOldPosts(newListPosts);
                //streamsFragment.adapterNotifyChange(streamsFragment, AdapterNotifyType.itemInserted);
                streamsFragment.adapterNotifyChange(streamsFragment, AdapterNotifyType.dataSetChanged);

                boolean isExisting = LoggedUserActivity.getLoggedUserActivity().checkProfileFragmentExist();
                if (isExisting) {
                    PostsListFragment profilePostsFragment = LoggedUserActivity.getLoggedUserActivity()
                            .getProfileFragments().getProfilePostsFragment(null);

                    if (profilePostsFragment != null) {
                        profilePostsFragment.adapter.addLoggedUserPosts(newListPosts);
                        profilePostsFragment.adapterNotifyChange(profilePostsFragment, AdapterNotifyType.itemInserted);
                    }
                }
            //} else {
                PostsListFragment favouritesFragment = HomeFragment.getHomeFragment().getFavouritesFragment();

                favouritesFragment.adapter.addOldPosts(newListPosts);
                favouritesFragment.adapterNotifyChange(favouritesFragment, AdapterNotifyType.dataSetChanged);
           // }
        }
    }
*/
    void adapterNotifyChange(PostsListFragment fragment, @NonNull AdapterNotifyType notifyType) {
        if (notifyType.equals(AdapterNotifyType.dataSetChanged)) {
            fragment.adapter.notifyDataSetChanged();
        } else if (notifyType.equals(AdapterNotifyType.itemInserted)) {
            fragment.adapter.notifyItemInserted(0);
        }
    }

    void pushOnFavoritesList(Post post) {
        PostsListFragment favouritesFragment = HomeFragment.getHomeFragment().getFavouritesFragment();

        favouritesFragment.getListPosts().add(0, post);
        favouritesFragment.adapter.notifyItemInserted(0);
    }

    void removeLikeFromStreamsList(Post post,
                                   PostsListAdapter.PostItemHolder holder,
                                   PostsListFragment streamsFragment,
                                   PostsListFragment favouritesFragment) {
        List<Post> listPosts = streamsFragment.getListPosts();

        for (int i = 0; i < listPosts.size(); i++) {
            if (listPosts.get(i).equals(post)) {
                listPosts.get(i).putIsLiked(false);

                //listPosts.get(i).removeLikeFromArray(LoggedUserActivity.getUsernameLoggedUser());

                streamsFragment.adapter.notifyItemChanged(i, listPosts.get(i));

                break;
            }
        }

        View favoritesView = favouritesFragment.getView();
        if (favoritesView != null) {
            // se la view è nascota (ci troviamo in un'altra pagina)
            RecyclerView recyclerView = favoritesView.findViewById(R.id.list_posts);
            recyclerView.removeView(recyclerView);
        }
        //favouritesFragment.adapter.notifyItemRemoved(holder.getAdapterPosition());
        //favouritesFragment.adapter.notifyItemRangeChanged(holder.getAdapterPosition(), listPosts.size());
        favouritesFragment.getListPosts().remove(post);
        favouritesFragment.adapter.notifyItemRemoved(holder.getAdapterPosition());
    }

    void removeLikeFromProfilePostsList(Post post,
                                        PostsListAdapter.PostItemHolder holder,
                                        PostsListFragment profilePostsFragment) {

        List<Post> listPosts = profilePostsFragment.getListPosts();

        for (int i = 0; i < listPosts.size(); i++) {
            if (listPosts.get(i).equals(post)) {
                listPosts.get(i).putIsLiked(false);
                //listPosts.get(i).removeLikeFromArray(LoggedUserActivity.getUsernameLoggedUser());

                profilePostsFragment.adapter.notifyItemChanged(i, listPosts.get(i));

                break;
            }
        }
    }

    void removeLikeFromFavoritesList(Post post) {
        //devo aggiornare il post di streams e levarlo da favourites
        PostsListFragment favouritesFragment = HomeFragment.getHomeFragment().getFavouritesFragment();
        View favoritesView = favouritesFragment.getView();
        if (favoritesView != null) {
            // se la view è nascosta (siamo in un'altra pagina)
            RecyclerView recyclerView = favoritesView.findViewById(R.id.list_posts);
        }
        List<Post> listPosts = favouritesFragment.getListPosts();

        for (int i = 0; i < listPosts.size(); i++) {
            if (listPosts.get(i).equals(post)) {
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

                    PostsListFragment favouritesFragment = HomeFragment.getHomeFragment().getFavouritesFragment();
                    RecyclerView favouritesRecyclerView = favouritesFragment.view.findViewById(R.id.list_posts);

                    PostsListFragment streamsFragment = HomeFragment.getHomeFragment().getStreamsFragment();
                    RecyclerView streamsRecyclerView = streamsFragment.view.findViewById(R.id.list_posts);

                    if (!type.equals("profile")) {

                        if (type.equals("favourites")) {
                            // il post da riuovere è stato selezionato dal pannello favourites
                            removePostFromPrimaryTab(favouritesFragment, selectPost, view, favouritesRecyclerView, holder);
                            removePostFromSecondaryTab(streamsFragment, selectPost);
                        } else if (type.equals("streams")) {
                            removePostFromPrimaryTab(streamsFragment, selectPost, view, streamsRecyclerView, holder);
                            removePostFromSecondaryTab(favouritesFragment, selectPost);
                        }

                        boolean isExisting = LoggedUserActivity.getLoggedUserActivity().checkProfileFragmentExist();
                        if (isExisting) {
                            // occorre aggiornare anche profile
                            removePostFromSecondaryTab(
                                    LoggedUserActivity.getLoggedUserActivity()
                                            .getProfilePostsFragment(username),
                                    selectPost
                            );
                        }
                    } else {
                        PostsListFragment profilePostsFragment =
                                LoggedUserActivity.getLoggedUserActivity().getProfileFragments()
                                        .getProfilePostsFragment(selectPost.getUsernamePublisher());

                        RecyclerView profilePostsRecyclerView = profilePostsFragment.getView().findViewById(R.id.list_posts);

                        removePostFromPrimaryTab(profilePostsFragment, selectPost, view, profilePostsRecyclerView, holder);
                        removePostFromSecondaryTab(streamsFragment, selectPost);
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
     * @param post
     * @param view
     * @param recyclerView
     * @param holder           TODO COMMENTARE
     */
    private static void removePostFromPrimaryTab(PostsListFragment postListFragment,
                                                 Post post,
                                                 View view,
                                                 RecyclerView recyclerView,
                                                 PostsListAdapter.PostItemHolder holder) {
        recyclerView.removeView(view);

        if (postListFragment.getListPosts().remove(post)) {
            postListFragment.adapter.notifyItemRemoved(holder.getAdapterPosition());
        }
    }

    /**
     * @param postsListFragment
     * @param selectPost        TODO COMMENTARE
     */
    private static void removePostFromSecondaryTab(PostsListFragment postsListFragment, Post selectPost) {
        List<Post> listPosts = postsListFragment.getListPosts();
        for (int i = 0; i < listPosts.size(); i++) {
            if (listPosts.get(i).equals(selectPost)) {
                // rimuovo il post
                postsListFragment.getListPosts().remove(listPosts.get(i));
                postsListFragment.adapter.notifyItemRemoved(i);
                postsListFragment.adapter.notifyItemRangeChanged(i, listPosts.size());

                break;
            }
        }
    }

    /**
     * @param postsListFragment Il frammento che deve contenere il post
     * @param selectPost        il post selezionato
     *                          <p>
     *                          Il metodo sostituisce un post in una specifica posizione se questo è considerato identico
     *                          sotto caratteristiche prefefinite dell'oggetto, controllare {@link Post} per le
     *                          caratteristiche attribuite all'oggetto.
     * @return true se il post selezionato è stato sostituito nella lista dell'adapter del frammento,
     * false altrimenti
     */
    boolean pullPost(PostsListFragment postsListFragment, Post selectPost) {
        List<Post> listPosts = postsListFragment.getListPosts();
        for (int i = 0; i < listPosts.size(); i++) {
            if (listPosts.get(i).equals(selectPost)) {
                listPosts.set(i, selectPost);

                return true;
            }
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public boolean incrementNumberOfTotalCommentsIfExist(Post post) {
        List<Post> listPosts = this.getListPosts();
        for (int i = 0; i < listPosts.size(); i++) {
            if (listPosts.get(i).equals(post)) {
                listPosts.get(i).incrementTotalComments();

                return true;
            }
        }

        return false;
    }

    public boolean decrementNumberOfTotalCommentsIfExist(Post post) {
        List<Post> listPosts = this.getListPosts();
        for (int i = 0; i < listPosts.size(); i++) {
            if (listPosts.get(i).equals(post)) {
                listPosts.get(i).decrementTotalComments();

                return true;
            }
        }

        return false;
    }
}