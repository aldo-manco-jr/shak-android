package org.aldofrank.shak.streams.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.aldofrank.shak.R;
import org.aldofrank.shak.models.Post;
import org.aldofrank.shak.services.ServiceGenerator;
import org.aldofrank.shak.services.StreamsService;
import org.aldofrank.shak.streams.http.posts.PostsListResponse;
import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostsListAdapter extends RecyclerView.Adapter<PostsListAdapter.PostItemHolder> {

    private List<Post> listPosts;
    private Context context;

    private String token;

    public PostsListAdapter(List<Post> listPosts, Context context) {

        this.listPosts = listPosts;
        this.context = context;

        token = LoggedUserActivity.getToken();
    }

    @NonNull
    @Override
    public PostItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_post, parent, false);
        PostItemHolder viewHolder = new PostItemHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PostItemHolder holder, final int position) {

        String urlImageProfileUser = "http://res.cloudinary.com/dfn8llckr/image/upload/v" + listPosts.get(position).getUserId().getProfileImageVersion() + "/" + listPosts.get(position).getUserId().getProfileImageId();

        Glide.with(context)
                .asBitmap()
                .load(urlImageProfileUser)
                .into(holder.imageProfile);

        holder.usernameText.setText(listPosts.get(position).getUsernamePublisher());

        if (listPosts.get(position).getUserId().getCity() != null && listPosts.get(position).getUserId().getCountry() != null) {
            holder.locationText.setText("@" + listPosts.get(position).getUserId().getCity() + ", " + listPosts.get(position).getUserId().getCountry());
        }

        holder.datePostText.setText(listPosts.get(position).getCreatedAt());

        if (!listPosts.get(position).getImageVersion().isEmpty()) {

            String urlImagePost = "http://res.cloudinary.com/dfn8llckr/image/upload/v" + listPosts.get(position).getImageVersion() + "/" + listPosts.get(position).getImageId();

            Glide.with(context)
                    .asBitmap()
                    .load(urlImagePost)
                    .into(holder.imagePost);

        } else {
            holder.imagePost.setVisibility(View.GONE);
        }

        holder.postContent.setText(listPosts.get(position).getPostContent());

        if (!isLiked(listPosts.get(position))) {
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

        holder.likesCounter.setText(listPosts.get(position).getTotalLikes() + "");

        holder.commentButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(context, "comment on " + listPosts.get(position), Toast.LENGTH_LONG).show();
            }
        });

        holder.commentsCounter.setText(listPosts.get(position).getArrayComments().size() + "");

        if (listPosts.get(position).getUsernamePublisher().equals(LoggedUserActivity.getUsernameLoggedUser())) {

            holder.deletePostButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    deletePost(listPosts.get(position));
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

    private boolean isLiked(Post post) {

        for (Post.Like like : post.getArrayLikes()) {

            if (like.getUsernamePublisher().equals(LoggedUserActivity.getUsernameLoggedUser())) {
                return true;
            }
        }

        return false;
    }

    private void likeOrUnlike(Post post, final PostItemHolder holder) {

        StreamsService streamsService = ServiceGenerator.createService(StreamsService.class, token);

        Toast.makeText(context, post.getPostContent(), Toast.LENGTH_LONG).show();

        if (!isLiked(post)) {

            Call<Object> httpRequest = streamsService.likePost(post);

            httpRequest.enqueue(new Callback<Object>() {

                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {

                    if (response.isSuccessful()) {
                        holder.likeButton.setImageResource(R.drawable.ic_favorite_real_black_24dp);
                        Toast.makeText(context, "like", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } else {

            Call<Object> httpRequest = streamsService.unlikePost(post);

            httpRequest.enqueue(new Callback<Object>() {

                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {

                    if (response.isSuccessful()) {
                        holder.likeButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        Toast.makeText(context, "unlike", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void deletePost(Post post) {

        StreamsService streamsService = ServiceGenerator.createService(StreamsService.class, token);

        Toast.makeText(context, post.getPostContent(), Toast.LENGTH_LONG).show();

        Call<Object> httpRequest = streamsService.deletePost(post);

        httpRequest.enqueue(new Callback<Object>() {

            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(context, "deleted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public class PostItemHolder extends RecyclerView.ViewHolder {

        RelativeLayout layoutItem;

        CircleImageView imageProfile;

        TextView usernameText;
        TextView locationText;
        TextView datePostText;

        ImageView imagePost;
        TextView postContent;

        ImageView likeButton;
        TextView likesCounter;

        ImageView commentButton;
        TextView commentsCounter;

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
