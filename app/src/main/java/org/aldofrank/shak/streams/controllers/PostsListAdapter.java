package org.aldofrank.shak.streams.controllers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.aldofrank.shak.R;
import org.aldofrank.shak.models.Post;
import org.aldofrank.shak.models.User;
import org.aldofrank.shak.services.ServiceGenerator;
import org.aldofrank.shak.services.StreamsService;
import org.ocpsoft.prettytime.PrettyTime;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

/**
 * Permette il collegamento tra la struttura dell'oggetto e la recycler view che lo deve rappresentare
 */
public class PostsListAdapter extends RecyclerView.Adapter<PostsListAdapter.PostItemHolder> {

    private List<Post> listPosts;

    private FragmentActivity activity;

    private static PostsListFragment postsListFragment;

    private final String basicUrlImage = "http://res.cloudinary.com/dfn8llckr/image/upload/v";
    private String token;

    protected Socket socket;
    {
        try {
            socket = IO.socket("http://10.0.2.2:3000/");
        } catch (URISyntaxException ignored) {
        }
    }

    private CommentsListFragment commentsListFragment;

    public PostsListAdapter(List<Post> listPosts, FragmentActivity activity, PostsListFragment postsListFragment) {
        this.listPosts = listPosts;
        this.activity = activity;
        PostsListAdapter.postsListFragment = postsListFragment;
        this.token = LoggedUserActivity.getToken();

        socket.on("refreshPage", updatePostsList);
        socket.connect();
    }

    /**
     * Quando un post viene pubblicato la home page viene aggiornata.
     */
    private Emitter.Listener updatePostsList = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // quando un post viene pubblicato la socket avvisa del necessario aggiornmento
                        PostsListAdapter.postsListFragment.getAllPosts();
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

        String urlImageProfileUser = this.basicUrlImage + user.getProfileImageVersion() + "/"
                + user.getProfileImageId();

        Glide.with(activity)
                .asBitmap()
                .load(urlImageProfileUser)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(holder.imageProfile);

        holder.usernameText.setText(listPosts.get(position).getUsernamePublisher());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" );
        Date date = new Date();

        try {
            date = formatter.parse(listPosts.get(position).getCreatedAt());
            date.setTime(date.getTime()+3_600_000);
        }catch (Exception ignored){}

        PrettyTime formattedDateTime = new PrettyTime();
        holder.datePostText.setText(formattedDateTime.format(date));

        if (user.getCity() != null && user.getCountry() != null) {
            holder.locationText.setText("@" + user.getCity() + ", " + user.getCountry());
        }

        if (!post.getImageVersion().isEmpty()) {
            String urlImagePost = this.basicUrlImage + post.getImageVersion() + "/" + post.getImageId();

            Glide.with(activity)
                    .asBitmap()
                    .load(urlImagePost)
                    .into(holder.imagePost);
        } else {
            holder.imagePost.setVisibility(View.GONE);
        }

        holder.postContent.setText(post.getPostContent());

        if (!isLiked(post)) {
            holder.likeButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        } else {
            holder.likeButton.setImageResource(R.drawable.ic_favorite_real_black_24dp);
        }

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likeOrUnlike(listPosts.get(position), holder);
            }
        });

        holder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentsListFragment = CommentsListFragment.newInstance(listPosts.get(position));

                HomeFragment.fragmentManager.beginTransaction()
                        .replace(R.id.home_fragment, commentsListFragment).commit();
            }
        });

        holder.likesCounter.setText(listPosts.get(position).getTotalLikes() + "");
        holder.commentsCounter.setText(listPosts.get(position).getArrayComments().size() + "");

        if (post.getUsernamePublisher().equals(LoggedUserActivity.getUsernameLoggedUser())) {
            holder.deletePostButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deletePost(post);
                }
            });
        } else {
            holder.deletePostButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listPosts.size();
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
        StreamsService streamsService = ServiceGenerator.createService(StreamsService.class, token);

        Toast.makeText(activity, post.getPostContent(), Toast.LENGTH_LONG).show();

        if (!isLiked(post)) {
            Call<Object> httpRequest = streamsService.likePost(post);

            httpRequest.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful()) {
                        holder.likeButton.setImageResource(R.drawable.ic_favorite_real_black_24dp);
                        socket.emit("refresh");
                    } else {
                        Toast.makeText(activity, response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Toast.makeText(activity, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Call<Object> httpRequest = streamsService.unlikePost(post);

            httpRequest.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful()) {
                        holder.likeButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        socket.emit("refresh");
                    } else {
                        Toast.makeText(activity, response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Toast.makeText(activity, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * Questa funzione è accesibile solo per i post dell'utente autenticato e invia una richiesta
     * http in cui richieste la cancellazione del post.
     */
    private void deletePost(Post post) {
        StreamsService streamsService = ServiceGenerator.createService(StreamsService.class, token);
        Call<Object> httpRequest = streamsService.deletePost(post);

        Toast.makeText(activity, post.getPostContent(), Toast.LENGTH_LONG).show();

        httpRequest.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    socket.emit("refresh");
                } else {
                    Toast.makeText(activity, response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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
