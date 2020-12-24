package org.aldofrankmarco.shak.streams.controllers.postslist;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import org.aldofrankmarco.shak.R;
import org.aldofrankmarco.shak.models.Like;
import org.aldofrankmarco.shak.models.Post;
import org.aldofrankmarco.shak.models.User;
import org.aldofrankmarco.shak.profile.controllers.ImageViewerActivity;
import org.aldofrankmarco.shak.profile.controllers.ProfileFragment;
import org.aldofrankmarco.shak.streams.controllers.AdapterNotifyType;
import org.aldofrankmarco.shak.streams.controllers.HomeFragment;
import org.aldofrankmarco.shak.streams.controllers.LoggedUserActivity;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Permette il collegamento tra la struttura dell'oggetto e la recycler view che lo deve rappresentare
 */
public class PostsListAdapter extends RecyclerView.Adapter<PostsListAdapter.PostItemHolder> {

    private boolean allOldPostsAreLoaded = false;

    private List<Post> lastNewElementFound = new ArrayList<>();
    private List<Post> listPosts;
    private PostsListFragment fatherListFragment;

    private final String basicUrlImage = "http://res.cloudinary.com/dfn8llckr/image/upload/v";

    public PostsListAdapter(PostsListFragment fatherListFragment, View view) {
        this.fatherListFragment = fatherListFragment;
        this.listPosts = new ArrayList<>();
    }

    public List<Post> getListPosts() {
        if (this.listPosts == null){
            this.listPosts = new ArrayList<>();
        }
        return this.listPosts;
    }

    @NonNull
    @Override
    public PostItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_post, parent, false);
        PostItemHolder viewHolder = new PostItemHolder(itemView);

        return viewHolder;
    }

    boolean setSwitchAllPostsAreNotLoaded() {
        return (this.allOldPostsAreLoaded = !this.allOldPostsAreLoaded);
    }

    /**
     * Questo metodo viene eseguito per ogni elemento nella lista, ogni elemento quindi viene
     * processato e aggiunto alla lista.
     */
    @Override
    public void onBindViewHolder(@NonNull final PostItemHolder holder, final int position) {
        final Post post = this.listPosts.get(position);
        final User user = post.getUserId();

        if ((!allOldPostsAreLoaded) && this.listPosts.get(this.listPosts.size() - 1).equals(post)){
            // quando viene creato se è l'ultimo elemento e se non è stato rimosso il
//TODO, OCCORRE FERMARLO SE I RISULTATI AGGIUNTI SONO DA AGGIUNGERE SONO 0, NON CI SONO MESSAGGI,
            // QUINID NON OCCORRE CHIAMARE PIù
            this.fatherListFragment.getAllPosts();
        }

        final String urlImageProfileUser = this.basicUrlImage + user.getProfileImageVersion() + "/"
                + user.getProfileImageId();

        Glide.with(LoggedUserActivity.getLoggedUserActivity())
                .asBitmap()
                .load(urlImageProfileUser)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(holder.imageProfile);

        Date date = null;
        try {
            date = localTimeToUtc(post.getCreatedAt());
        } catch (ParseException ignored) {
        }

        PrettyTime formattedDateTime = new PrettyTime();
        holder.datePostText.setText(formattedDateTime.format(date));

        if (user.getCity() != null && user.getCountry() != null) {
            holder.locationText.setText("@" + user.getCity() + ", " + user.getCountry());
        }

        if (!post.getImageVersion().isEmpty()) {
            final String urlImagePost = this.basicUrlImage +
                    post.getImageVersion() +
                    '/' +
                    post.getImageId();

            Glide.with(LoggedUserActivity.getLoggedUserActivity())
                    .asBitmap()
                    .load(urlImagePost)
                    .into(holder.imagePost);

            holder.imagePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(LoggedUserActivity.getLoggedUserActivity(), ImageViewerActivity.class);
                    intent.putExtra("urlImage", urlImagePost);
                    LoggedUserActivity.getLoggedUserActivity().startActivity(intent);
                }
            });

            holder.imagePost.setVisibility(View.VISIBLE);
        } else {
            holder.imagePost.setVisibility(View.GONE);
        }


        //TODO OCCORRE DISTRUGGERE I FRAMMENTI EXTRA CREATI O RIUSARE SEMPRE LO STESSO
        holder.usernameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileFragment profileFragment = LoggedUserActivity.getLoggedUserActivity()
                        .getProfileFragments();
                ProfileFragment userInformationProfile = profileFragment
                        .newInstanceUserViewInformation(holder.usernameText.getText().toString().trim());
                fatherListFragment.eraseSearch();
                LoggedUserActivity.getLoggedUserActivity().changeFragment(userInformationProfile);
            }
        });

        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileFragment profileFragment = LoggedUserActivity.getLoggedUserActivity()
                        .getProfileFragments();
                ProfileFragment userInformationProfile = profileFragment
                        .newInstanceUserViewInformation(holder.usernameText.getText().toString().trim());
                fatherListFragment.eraseSearch();
                LoggedUserActivity.getLoggedUserActivity().changeFragment(userInformationProfile);
            }
        });

        holder.usernameText.setText(post.getUsernamePublisher());
        holder.postContent.setText(post.getPostContent());

        if (post.getIsLiked()){
            holder.likeButton.setImageResource(R.drawable.ic_favorite_real_black_24dp);
            holder.likeButton.setTag("like");
        } else {
            holder.likeButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            holder.likeButton.setTag("unlike");
        }
        /*if (!isLiked(post)) {
            holder.likeButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            holder.likeButton.setTag("unlike");
        } else {
            holder.likeButton.setImageResource(R.drawable.ic_favorite_real_black_24dp);
            holder.likeButton.setTag("like");
        }*/

        // se l'utente non è il proprietario del post è possibile mettere like
        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo potrebbe perdere delle funzionalità dopo la cancellazione o l'aggiunta di nuovi post
                likeOrUnlike(post, holder, view);
            }
        });

        holder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fatherListFragment.eraseSearch();
                LoggedUserActivity.getLoggedUserActivity().changeFragment(LoggedUserActivity.getLoggedUserActivity().getCommentsListFragment(post));
            }
        });

        holder.likesCounter.setText(post.getTotalLikes() + "");
        holder.commentsCounter.setText(post.getTotalComments() + "");

        if (post.getUsernamePublisher().equals(LoggedUserActivity.getUsernameLoggedUser())) {
            holder.deletePostButton.setVisibility(View.VISIBLE);
            holder.deletePostButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fatherListFragment.deletePost(post, view, holder);
                }
            });
        } else {
            holder.deletePostButton.setVisibility(View.GONE);
        }
    }

    /**
     * @param dateString una data in formato UDC contenuta nel database remoto
     * @return un valore di tipo Date convertito da UTC (formato atteso dal server) nel fuso orario
     * usato dall'utente
     */
    private Date localTimeToUtc(String dateString) throws ParseException {
        TimeZone timeZone = TimeZone.getDefault();
        String[] timeZoneSplitStrings = timeZone.getID().split("(/)");
        String CurrentTimeZone = timeZoneSplitStrings[timeZoneSplitStrings.length - 1];

        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone(CurrentTimeZone));
        Date correctDateForUserDevice = dateFormat.parse(dateString);

        return correctDateForUserDevice;
    }

    @Override
    public int getItemCount() {
        return listPosts.size();
    }

    /**
     * @param newListPosts
    * TODO
    */
    public void addPosts(List<Post> newListPosts) {
        assert (newListPosts != null) : "newLIstPost non può essere null";

        int newListMaxIndex = (newListPosts.size() -1);
        for (int i = newListMaxIndex; i >= 0; i--) {
            // i nuovi messaggi vengono aggiunti in cima alla lista
            if (!this.listPosts.contains(newListPosts.get(i))) {
                // se l'elemento non è già presente nella lista viene aggiunto
                this.listPosts.add(0, newListPosts.get(i));
            }
        }
    }

    /**
     * @param newListPosts
     * TODO
     */
    public void addProfilePosts(List<Post> newListPosts) {
        assert (newListPosts != null) : "newLIstPost non può essere null";

        int newListMaxIndex = (newListPosts.size() -1);
        for (int i = newListMaxIndex; i >= 0; i--) {
            // i nuovi messaggi vengono aggiunti in cima alla lista
            boolean isLoggedUserOwner = newListPosts.get(i).getUsernamePublisher()
                    .equals(LoggedUserActivity.getUsernameLoggedUser());

            if (isLoggedUserOwner &&!this.listPosts.contains(newListPosts.get(i))) {
                // se l'elemento non è già presente nella lista viene aggiunto
                this.listPosts.add(0, newListPosts.get(i));
            }
        }
    }

    public void addOldPosts(List<Post> newListPosts) {
        assert (newListPosts != null) : "newLIstPost non può essere null";

        if (this.listPosts.size() == 0) {
            this.listPosts.addAll(newListPosts);

            return;
        } else if (this.listPosts.get(this.listPosts.size() - 1).equals(newListPosts.get(newListPosts.size() - 1))){
            // viene fermata la ricerca di nuovi post e svuotata la variabile di appoggio
            this.allOldPostsAreLoaded = true;

            return;
        }

        //int newListMaxIndex = (newListPosts.size() - 1);
        for (int i = 0; i < newListPosts.size(); i++) {
            // i nuovi messaggi vengono aggiunti in cima alla lista
            // TODO è QUI IL PROBLEMA, NON AGGIUNGE
            if (!this.listPosts.contains(newListPosts.get(i))) {
                // se l'elemento non è già presente viene aggiunto
                this.listPosts.add(newListPosts.get(i));
            }
        }
    }

    public void setListPosts(List<Post> listPosts) {
        this.listPosts = listPosts;
    }

    /**
     * @param newListPosts la lista dei nuovi post
     *
     * Il metodo prende una lista di post e verifica quali post siano dell'utente che ha effettuato
     * il login, successivamente aggiunge solo i post opportuni alla lista dei post considerati
     */
    public void addLoggedUserPosts(List<Post> newListPosts) {
        assert (newListPosts != null) : "newLIstPost non può essere null";

        int newListMaxIndex = (newListPosts.size() -1);
        for (int i = newListMaxIndex; i >= 0; i--) {
            // i nuovi messaggi vengono aggiunti in cima alla lista
            boolean isUserPost = (newListPosts.get(i).getUsernamePublisher().equals(
                    LoggedUserActivity.getUsernameLoggedUser()));
            if (isUserPost && !this.listPosts.contains(newListPosts.get(i))){
                // i nuovi messaggi dell'utente vengono aggiunti in cima alla lista
                this.listPosts.add(0, newListPosts.get(i));
            }
        }
        /*for (Post post: newListPosts) {
            boolean isUserPost = (post.getUsernamePublisher().equals(LoggedUserActivity.getUsernameLoggedUser()));
            if (isUserPost){
                // i nuovi messaggi vengono aggiunti in cima alla lista
                this.listPosts.add(0, post);
            }
        }*/
    }


    /**
     * @param newListPosts la lista dei nuovi post
     *
     * Il metodo prende una lista di post e verifica quali post siano dell'utente che ha effettuato
     * il login, successivamente aggiunge solo i post opportuni alla lista dei post considerati
     */
    public void addLoggedUserOldPosts(List<Post> newListPosts) {
        assert (newListPosts != null) : "newLIstPost non può essere null";

        int newListMaxIndex = (newListPosts.size() -1);
        for (int i = newListMaxIndex; i >= 0; i--) {
            // i nuovi messaggi vengono aggiunti in cima alla lista
            boolean isUserPost = (newListPosts.get(i).getUsernamePublisher().equals(
                    LoggedUserActivity.getUsernameLoggedUser()));
            if (isUserPost && !this.listPosts.contains(newListPosts.get(i))) {
                // i nuovi messaggi dell'utente vengono aggiunti in cima alla lista
                this.listPosts.add((this.listPosts.size() - 1), newListPosts.get(i));
            }
        }
    }

    /**
     * @param post un post generico in input
     * @return true se l'utente ha espresso una preferenza verso il post, false altrimenti
     */
    private boolean isLiked(Post post) {
        String loggedUser = LoggedUserActivity.getUsernameLoggedUser();

        for (Like like : post.getArrayLikes()) {
            boolean isLiked = like.getUsernamePublisher().equals(loggedUser);

            if (isLiked) {
                return true;
            }
        }

        return false;
    }

    /**
     * In base ai dati ricavati da {{@link #isLiked(Post)}} viene inviata una richiesta http di
     * "like" o di "unlike" verso il post.
     */
    private void likeOrUnlike(final Post post, final PostItemHolder holder, final View view) {

        final LoggedUserActivity userActivity =
                LoggedUserActivity.getLoggedUserActivity();
        final PostsListFragment streamsFragment =
                HomeFragment.getHomeFragment().getStreamsFragment();
        final PostsListFragment favouritesFragment =
                HomeFragment.getHomeFragment().getFavouritesFragment();

        //if (!isLiked(post)) {
        boolean isUnlikePost = !post.getIsLiked();
        if(isUnlikePost){
            // viene aggiunto ai preferiti
            Call<Object> httpRequest = LoggedUserActivity.getStreamsService().likePost(post);

            httpRequest.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful()) {
                        post.putIsLiked(true);

                        ProfileFragment profileFragment = ProfileFragment.getProfileFragment();
                        if (profileFragment != null) {
                            PostsListFragment profilePostsFragment = profileFragment.getProfilePostsFragment(post.getUsernamePublisher());
                            profilePostsFragment.pullPost(profilePostsFragment, post);
                            profilePostsFragment.adapterNotifyChange(profilePostsFragment, AdapterNotifyType.dataSetChanged);
                        }

                        streamsFragment.pullPost(streamsFragment, post);
                        streamsFragment.adapterNotifyChange(streamsFragment, AdapterNotifyType.dataSetChanged);
                        favouritesFragment.pushOnFavoritesList(post);
                        /*post.putIsLiked(true);
                        //holder.likeButton.setImageResource(R.drawable.ic_favorite_real_black_24dp);
                        //holder.likeButton.setTag("like");

                        //post.addLiketoArray(LoggedUserActivity.getUsernameLoggedUser());
                        //postsListFragment.adapter.notifyDataSetChanged();
                        fatherListFragment.adapterNotifyChange(fatherListFragment, AdapterNotifyType.dataSetChanged);
                        HomeFragment.getHomeFragment().getFavouritesFragment().pushOnFavoritesList(post);*/
                    } else {
                        Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            // viene rimosso dai preferiti
            Call<Object> httpRequest = LoggedUserActivity.getStreamsService().unlikePost(post.getPostId());

            httpRequest.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful()) {
                        post.putIsLiked(false);

                        ProfileFragment profileFragment = ProfileFragment.getProfileFragment();
                        if (profileFragment != null) {
                            PostsListFragment profilePostsFragment = profileFragment.getProfilePostsFragment(post.getUsernamePublisher());
                            profilePostsFragment.pullPost(profilePostsFragment, post);
                            profilePostsFragment.adapterNotifyChange(profilePostsFragment, AdapterNotifyType.dataSetChanged);
                        }

                        streamsFragment.pullPost(streamsFragment, post);
                        streamsFragment.adapterNotifyChange(streamsFragment, AdapterNotifyType.dataSetChanged);
                        favouritesFragment.removeLikeFromFavoritesList(post);
                        //holder.likeButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        //holder.likeButton.setTag("unlike");
                        //post.putIsLiked(false);
                        /*ProfileFragment profileFragment = ProfileFragment.getProfileFragment();
                        if (profileFragment != null) {
                            PostsListFragment profilePostsFragment = profileFragment.getProfilePostsFragment(post.getUsernamePublisher());
                            fatherListFragment.removeLikeFromProfilePostsList(post, holder, profilePostsFragment);
                        }
                        favouritesFragment.removeLikeFromFavoritesList(post);
                        streamsFragment.removeLikeFromStreamsList(post, holder, streamsFragment, favouritesFragment);
                        /*if (!type.equals("profile")) {

                            if (type.equals("streams")) {
                                post.putIsLiked(false);
                                //post.removeLikeFromArray(LoggedUserActivity.getUsernameLoggedUser());
                                //HomeFragment.getHomeFragment().getStreamsFragment().adapter.notifyItemChanged(holder.getAdapterPosition());
                                streamsFragment.adapterNotifyChange(streamsFragment, AdapterNotifyType.dataSetChanged);
                                favouritesFragment.removeLikeFromFavoritesList(post);

                                // in entrambi i casi sopra citati occorre verificare se il like deve essere
                                //tolto anche in profile
                                if (userActivity.checkProfilePostsFragmentExist() &&
                                        userActivity.getProfileFragments().getOwnerUsername()
                                                .equals(LoggedUserActivity.getUsernameLoggedUser())){
                                    // esiste e con l'utente loggato, devono essere aggiornati i messaggi personali
                                    if (userActivity.checkProfilePostsFragmentExist()) {
                                        fatherListFragment.removeLikeFromProfilePostsList(post, holder,  userActivity.getProfilePostsFragment(null));
                                    }
                                }
                            } else if (type.equals("favourites")) {
                                post.putIsLiked(false);
                                streamsFragment.removeLikeFromStreamsList(post, holder, streamsFragment, favouritesFragment);
                                if (userActivity.checkProfilePostsFragmentExist()) {
                                    fatherListFragment.removeLikeFromProfilePostsList(post, holder, userActivity.getProfilePostsFragment(null));
                                }
                            }
                        } else {
                            post.putIsLiked(false);
                            //PostsListFragment profilePostsFragment = ProfileFragment.getProfileFragment().getProfilePostsFragment(post.getUsernamePublisher());
                            //fatherListFragment.removeLikeFromProfilePostsList(post, holder, profilePostsFragment);
                            favouritesFragment.removeLikeFromFavoritesList(post);
                            streamsFragment.removeLikeFromStreamsList(post, holder, streamsFragment, favouritesFragment);
                        }*/
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
    }

    public class PostItemHolder extends RecyclerView.ViewHolder {
        ConstraintLayout layoutItem;

        CircleImageView imageProfile;

        TextView usernameText;
        TextView locationText;
        TextView datePostText;
        TextView postContent;
        TextView likesCounter;
        TextView commentsCounter;

        ImageView imagePost;
        ImageView likeButton;
        ImageView commentButton;
        ImageView deletePostButton;

        public PostItemHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile_circle);
            layoutItem = itemView.findViewById(R.id.layout_item);
            usernameText = itemView.findViewById(R.id.username_text);
            locationText = itemView.findViewById(R.id.location_text);
            datePostText = itemView.findViewById(R.id.date_post_text);
            imagePost = itemView.findViewById(R.id.post_image);
            postContent = itemView.findViewById(R.id.post_content);
            likeButton = itemView.findViewById(R.id.like_button);
            likesCounter = itemView.findViewById(R.id.likes_counter);
            commentButton = itemView.findViewById(R.id.comment_button);
            commentsCounter = itemView.findViewById(R.id.comments_counter);
            deletePostButton = itemView.findViewById(R.id.delete_post_button);
        }
    }
}