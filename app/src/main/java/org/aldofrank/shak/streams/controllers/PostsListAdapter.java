package org.aldofrank.shak.streams.controllers;

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
import com.github.nkzawa.emitter.Emitter;

import org.aldofrank.shak.R;
import org.aldofrank.shak.models.Post;
import org.aldofrank.shak.models.User;
import org.aldofrank.shak.profile.controllers.ImageViewerActivity;
import org.aldofrank.shak.profile.controllers.ProfileFragment;
import org.aldofrank.shak.services.ServiceGenerator;
import org.aldofrank.shak.services.StreamsService;
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

    private List<Post> listPosts;
    private String type;
    private PostsListFragment postsListFragment;
    private View fragmentView;

    private final String basicUrlImage = "http://res.cloudinary.com/dfn8llckr/image/upload/v";

    public PostsListAdapter(List<Post> listPosts, String type, PostsListFragment postsListFragment, View view) {
        this.listPosts = listPosts;
        this.type = type;
        this.postsListFragment = postsListFragment;
        this.fragmentView = view;

        //LoggedUserActivity.getSocket().on("refreshPage", updatePostsList);
    }

    /**
     * Quando un post viene pubblicato la home page viene aggiornata.
     */
    private Emitter.Listener updatePostsList = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            if (LoggedUserActivity.getLoggedUserActivity() != null) {
                LoggedUserActivity.getLoggedUserActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), "lancia da Adapter", Toast.LENGTH_LONG).show();
                        // quando un post viene pubblicato la socket avvisa del necessario aggiornmento
                        //TODO FA DUE CHIAMATE PER LO STESSO DATO???
                        //HomeFragment.getHomeFragment().getStreamsFragment().getAllNewPosts();
                        //HomeFragment.getHomeFragment().getFavouritesFragment().getAllNewPosts();
                        //HomeFragment.getHomeFragment().getStreamsFragment().getAllPosts();
                        //HomeFragment.getHomeFragment().getFavouritesFragment().getAllPosts();
                        // TODO QUESTA MODIFICA IMPEDISCE IL CRASH DEL PROGRAMMA NEL CASO IN CUI
                        //  LO SMARTPHONE NON SIA CONNESSO
                        ProfileFragment profileFragment = ProfileFragment.getProfileFragment();
                        if (profileFragment != null) {
                            //era getAllpost
                            //TODO NON SO SE è GIUSTO
                            //profileFragment.getProfilePostsFragment(type).getAllPosts();
                            //profileFragment.getProfilePostsFragment(type).getAllNewPosts();
                        }
                    }
                });
            }
        }
    };

    @NonNull
    @Override
    public PostItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_post, parent, false);
        PostItemHolder viewHolder = new PostItemHolder(itemView);

        return viewHolder;
    }

    /**
     * Questo metodo viene eseguito per ogni elemento nella lista, ogni elemento quindi viene
     * processato e aggiunto alla lista.
     */
    @Override
    public void onBindViewHolder(@NonNull final PostItemHolder holder, final int position) {
        final Post post = listPosts.get(position);
        final User user = post.getUserId();

        final String urlImageProfileUser = this.basicUrlImage + user.getProfileImageVersion() + "/"
                + user.getProfileImageId();

        Glide.with(LoggedUserActivity.getLoggedUserActivity())
                .asBitmap()
                .load(urlImageProfileUser)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(holder.imageProfile);

        Date date = null;
        try {
            date = localTimeToUtc(listPosts.get(position).getCreatedAt());
        } catch (ParseException ignored) {}

        PrettyTime formattedDateTime = new PrettyTime();
        holder.datePostText.setText(formattedDateTime.format(date));

        if (user.getCity() != null && user.getCountry() != null) {
            holder.locationText.setText("@" + user.getCity() + ", " + user.getCountry());
        }

        if (!post.getImageVersion().isEmpty()) {
            final String urlImagePost = this.basicUrlImage + post.getImageVersion() + "/" + post.getImageId();

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
        } else {
            holder.imagePost.setVisibility(View.GONE);
        }

        holder.usernameText.setText(post.getUsernamePublisher());
        holder.postContent.setText(post.getPostContent());

        if (!isLiked(post)) {
            holder.likeButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            holder.likeButton.setTag("unlike");
        } else {
            holder.likeButton.setImageResource(R.drawable.ic_favorite_real_black_24dp);
            holder.likeButton.setTag("like");
        }

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo potrebbe perdere delle funzionalità dopo la cancellazione o l'aggiunta di nuovi post
                likeOrUnlike(listPosts.get(position), holder);
            }
        });

        holder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (type.equals("all") || type.equals("favourites")) {
                    HomeFragment.getHomeFragment().getFragmentManager().beginTransaction()
                            .replace(R.id.home_fragment, HomeFragment.getHomeFragment().getCommentsListFragment(listPosts.get(position))).commit();
                } else if (type.equals("profile")) {
                    ProfileFragment.getProfileFragment().getFragmentManager().beginTransaction()
                            .replace(R.id.profile_fragment, ProfileFragment.getProfileFragment().getCommentsListFragment(listPosts.get(position))).commit();
                }
            }
        });

        holder.likesCounter.setText(listPosts.get(position).getTotalLikes() + "");
        holder.commentsCounter.setText(listPosts.get(position).getArrayComments().size() + "");

        if (post.getUsernamePublisher().equals(LoggedUserActivity.getUsernameLoggedUser())) {
            holder.deletePostButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deletePost(post, view);
                }
            });
        } else {
            holder.deletePostButton.setVisibility(View.GONE);
        }
    }

    /**
     * TODO
     */
    private int getPostPosition(Post post){
        for (int i = 0; i < listPosts.size(); i++){
            if (listPosts.get(i) == post){
                return i;
            }
        }

        return -1;
    }

    /**
     * @param dateString una data in formato UDC contenuta nel database remoto
     *
     * @return un valore di tipo Date convertito da UTC (formato atteso dal server) nel fuso orario
     *         usato dall'utente
     */
    protected Date localTimeToUtc(String dateString) throws ParseException {
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

    public void addPosts(List<Post> newListPosts) {
        assert newListPosts.size() > 0: "newLIstPost doveva avere almeno un elemento";

        List<Post> newAndOldPost = new ArrayList<>();
        newAndOldPost.addAll(newListPosts);
        newAndOldPost.addAll(this.listPosts);

        int originaListPostSize = this.listPosts.size() - 1;
        this.listPosts = newAndOldPost;
        //singolo post nofityiteminserted
        postsListFragment.adapter.notifyItemInserted(0);
        //multiple post notifyitemrangechanged
        postsListFragment.adapter.notifyItemRangeChanged(originaListPostSize, listPosts.size());
    }

    /**
     * @param post un post generico in input
     * @return true se l'utente ha espresso una preferenza verso il post, false altrimenti
     */
    private boolean isLiked(Post post) {
        String loggedUser = LoggedUserActivity.getUsernameLoggedUser();

        for (Post.Like like : post.getArrayLikes()) {
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
    private void likeOrUnlike(Post post, final PostItemHolder holder) {
        StreamsService streamsService = ServiceGenerator.createService(StreamsService.class, LoggedUserActivity.getToken());

        if (!isLiked(post)) {
            Call<Object> httpRequest = streamsService.likePost(post);

            httpRequest.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful()) {

                        //TODO occorre chiedere tramite una socket di avere i risultati dai socket
                        // specie nel caso di like inseriti da altri utenti, per cui non si conosceva l'esistenza
                        String likeIdentifier = (String)  holder.likeButton.getTag();
                        int numberOfLike = Integer.parseInt(holder.likesCounter.getText().toString());
                        if (likeIdentifier.equals("like")) {
                            // l'utente ha rimosso la sua preferenza
                            holder.likeButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            holder.likesCounter.setText(String.valueOf(numberOfLike - 1));
                            holder.likeButton.setTag("unlike");
                        } else {
                            holder.likeButton.setImageResource(R.drawable.ic_favorite_real_black_24dp);
                            holder.likesCounter.setText(String.valueOf(numberOfLike + 1));
                            holder.likeButton.setTag("like");
                        }

                        //TODO OCCORRE AGGIORNARE I NUOVI POST PIACIUTI, NON FARE EMIT(),
                        // SONO SOLO LIKE, occorre sfruttare la logica degli update
                        Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), "boom", Toast.LENGTH_SHORT).show();
                        HomeFragment.getHomeFragment().getFavouritesFragment().getAllPosts();
                        //LoggedUserActivity.getSocket().emit("refresh");
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
            Call<Object> httpRequest = streamsService.unlikePost(post);

            httpRequest.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful()) {
                        holder.likeButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        //LoggedUserActivity.getSocket().emit("refresh");
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

    /**
     * Questa funzione è accesibile solo per i post dell'utente autenticato e invia una richiesta
     * http in cui richieste la cancellazione del post.
     */
    private void deletePost(final Post post, final View view) {

        //TODO SE SI CANELLA UN MESSAGGIO LA POSZIONE DEGLI ALTRI ALLì'INTERNO DELL'ARRAY CAMBIA, QUINDI OCCORRE CAMBIARE POSITION
        // altrimenti va in crash perchè non trova una posizione valida
        StreamsService streamsService = ServiceGenerator.createService(StreamsService.class, LoggedUserActivity.getToken());
        Call<Object> httpRequest = streamsService.deletePost(post);

        httpRequest.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    final int position = getPostPosition(post);

                    removePostFromRecyclerView(position, view);
                    listPosts.remove(position);
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

    private void removePostFromRecyclerView(int position, View view){
        RecyclerView recyclerView = fragmentView.findViewById(R.id.listPosts);

        recyclerView.removeView(view);//recyclerView.removeViewAt(position);
        postsListFragment.adapter.notifyItemRemoved(position);
        postsListFragment.adapter.notifyItemRangeChanged(position, listPosts.size());
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