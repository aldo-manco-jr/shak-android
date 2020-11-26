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

import java.util.LinkedList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsListAdapter extends RecyclerView.Adapter<PostsListAdapter.PostItemHolder> {

    private LinkedList<Post> listPosts;
    private Context context;

    public PostsListAdapter(LinkedList<Post> listPosts, Context context) {

        this.listPosts = listPosts;
        this.context = context;
    }

    @NonNull
    @Override
    public PostItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_post, parent, false);
        PostItemHolder viewHolder = new PostItemHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostItemHolder holder, int position) {

        /*Glide.with(context)
                .asBitmap()
                .load(listPosts.get(position).getUserId().getProfileImageId())
                .into(holder.imageProfile);*/


    }

    @Override
    public int getItemCount() {
        return 0;
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
