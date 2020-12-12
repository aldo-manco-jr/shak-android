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
    }

    public List<Post> getListPosts() {
        return listPosts;
    }

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
            date = localTimeToUtc(post.getCreatedAt());
        } catch (ParseException ignored) {
        }

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

        holder.usernameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileFragment profileFragment = ProfileFragment.newInstance(holder.usernameText.getText().toString().trim());
                LoggedUserActivity.getLoggedUserActivity().changeFragment(profileFragment);
            }
        });

        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileFragment profileFragment = ProfileFragment.newInstance(holder.usernameText.getText().toString().trim());
                LoggedUserActivity.getLoggedUserActivity().changeFragment(profileFragment);
            }
        });

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
                likeOrUnlike(post, holder, view, type);
            }
        });

        holder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoggedUserActivity.getLoggedUserActivity().changeFragment(LoggedUserActivity.getLoggedUserActivity().getCommentsListFragment(post));
            }
        });

        holder.likesCounter.setText(post.getTotalLikes() + "");
        holder.commentsCounter.setText(post.getArrayComments().size() + "");

        if (post.getUsernamePublisher().equals(LoggedUserActivity.getUsernameLoggedUser())) {
            holder.deletePostButton.setVisibility(View.VISIBLE);
            holder.deletePostButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    postsListFragment.deletePost(post, view, holder, type);
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
        assert newListPosts.size() > 0 : "newLIstPost doveva avere almeno un elemento";

        List<Post> newAndOldPost = new ArrayList<>();
        newAndOldPost.addAll(newListPosts);
        newAndOldPost.addAll(this.listPosts);

        int originaListPostSize = this.listPosts.size() - 1;
        this.listPosts = newAndOldPost;
        //HomeFragment.getHomeFragment().getStreamsFragment().adapter.notifyDataSetChanged();
        //TODO USARE QUESTO HomeFragment.getHomeFragment().getStreamsFragment().adapter.notifyItemInserted(0);
        //singolo post nofityiteminserted
        //this.notifyItemInserted(0);
        //this.notifyDataSetChanged();
        //postsListFragment.adapter.notifyDataSetChanged();
        //postsListFragment.adapter.notifyItemInserted(0);
        //multiple post notifyitemrangechanged
        //postsListFragment.adapter.notifyItemRangeChanged(originaListPostSize, listPosts.size());
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

    private void likeOrUnlike(final Post post, final PostItemHolder holder, final View view, final String type) {
        StreamsService streamsService = ServiceGenerator.createService(StreamsService.class, LoggedUserActivity.getToken());

        if (!isLiked(post)) {
            // viene aggiunto ai preferiti
            Call<Object> httpRequest = streamsService.likePost(post);

            httpRequest.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful()) {
                        holder.likeButton.setImageResource(R.drawable.ic_favorite_real_black_24dp);
                        holder.likeButton.setTag("like");

                        post.addLiketoArray(LoggedUserActivity.getUsernameLoggedUser());
                        postsListFragment.adapter.notifyDataSetChanged();
                        HomeFragment.getHomeFragment().getFavouritesFragment().pushOnFavoritesList(post);
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
            Call<Object> httpRequest = streamsService.unlikePost(post);

            httpRequest.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), "LO FACIIO======??", Toast.LENGTH_SHORT).show();
                        holder.likeButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        holder.likeButton.setTag("unlike");

                        if (!type.equals("profile")) {
                            PostsListFragment streamsFragment = HomeFragment.getHomeFragment().getStreamsFragment();
                            PostsListFragment favouritesFragment = HomeFragment.getHomeFragment().getFavouritesFragment();

                            if (type.equals("all")) {
                                post.removeLikeFromArray(LoggedUserActivity.getUsernameLoggedUser());
                                //HomeFragment.getHomeFragment().getStreamsFragment().adapter.notifyItemChanged(holder.getAdapterPosition());
                                HomeFragment.getHomeFragment().getStreamsFragment().adapter.notifyDataSetChanged();
                                favouritesFragment.removeLikeFromFavoritesList(post);
                            } else if (type.equals("favourites")) {
                                postsListFragment.removeLikeFromStreamsList(post, holder, streamsFragment, favouritesFragment);
                            }
                        } else {
                            PostsListFragment profilePostsFragment = ProfileFragment.getProfileFragment().getProfilePostsFragment(post.getUsernamePublisher());
                            postsListFragment.removeLikeFromProfilePostsList(post, holder, profilePostsFragment);
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