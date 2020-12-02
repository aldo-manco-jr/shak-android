package org.aldofrank.shak.streams.controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.aldofrank.shak.R;
import org.aldofrank.shak.models.Post;
import org.aldofrank.shak.models.User;
import org.aldofrank.shak.people.http.GetUserByUsernameResponse;
import org.aldofrank.shak.services.ServiceGenerator;
import org.aldofrank.shak.services.StreamsService;
import org.aldofrank.shak.streams.http.DeleteCommentRequest;
import org.aldofrank.shak.streams.http.GetPostResponse;

import java.net.URISyntaxException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsListAdapter extends RecyclerView.Adapter<CommentsListAdapter.CommentItemHolder> {

    private List<Post.Comment> listComments;

    private FragmentActivity activity;

    private static CommentsListFragment commentsListFragment;

    public static String postId;

    private final String basicUrlImage = "http://res.cloudinary.com/dfn8llckr/image/upload/v";
    private String token;

    private User user;

    private Socket socket;

    {
        try {
            socket = IO.socket("http://10.0.2.2:3000/");
        } catch (URISyntaxException ignored) {
        }
    }

    public CommentsListAdapter(List<Post.Comment> listComments, FragmentActivity activity, CommentsListFragment commentsListFragment) {
        this.listComments = listComments;
        this.activity = activity;
        CommentsListAdapter.commentsListFragment = commentsListFragment;
        this.token = LoggedUserActivity.getToken();

        socket.on("refreshPage", updatePostCommentsList);
        socket.connect();
    }

    /**
     * Quando un post viene pubblicato la home page viene aggiornata.
     */
    private Emitter.Listener updatePostCommentsList = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // quando un post viene pubblicato la socket avvisa del necessario aggiornmento
                        CommentsListAdapter.commentsListFragment.getAllPostComments();
                    }
                });
            }
        }
    };

    @NonNull
    @Override
    public CommentsListAdapter.CommentItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_comment, parent, false);

        CommentsListAdapter.CommentItemHolder viewHolder = new CommentsListAdapter.CommentItemHolder(itemView);

        return viewHolder;
    }

    /**
     * Questo metodo viene eseguito per ogni elemento nella lista, ogni elemento quindi viene
     * processato e aggiunto alla lista.
     */
    @Override
    public void onBindViewHolder(@NonNull final CommentsListAdapter.CommentItemHolder holder, final int position) {

        final Post.Comment comment = listComments.get(position);

        System.out.println(comment.getUsernamePublisher());
        getUserByUsername(comment.getUsernamePublisher());

        //String urlImageProfileUser = this.basicUrlImage + user.getProfileImageVersion() + "/" + user.getProfileImageId();

        /*Glide.with(activity)
                .asBitmap()
                .load(urlImageProfileUser)
                .into(holder.imageProfile);*/

        holder.usernameText.setText(listComments.get(position).getUsernamePublisher());
        holder.datePostText.setText(listComments.get(position).getCreatedAt());

        holder.commentContent.setText(comment.getCommentContent());

        if (comment.getUsernamePublisher().equals(LoggedUserActivity.getUsernameLoggedUser())) {
            holder.deletePostButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteComment(CommentsListAdapter.postId, comment);
                }
            });
        } else {
            holder.deletePostButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listComments.size();
    }

    private void getUserByUsername(final String username){

        StreamsService streamsService = ServiceGenerator.createService(StreamsService.class, token);
        Call<GetUserByUsernameResponse> httpRequest = streamsService.getUserByUsername(username);

        httpRequest.enqueue(new Callback<GetUserByUsernameResponse>() {
            @Override
            public void onResponse(Call<GetUserByUsernameResponse> call, Response<GetUserByUsernameResponse> response) {
                if (response.isSuccessful()) {
                    user = response.body().getUserFoundByUsername();
                } else {
                    Toast.makeText(activity, response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GetUserByUsernameResponse> call, Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Questa funzione è accesibile solo per i post dell'utente autenticato e invia una richiesta
     * http in cui richieste la cancellazione del post.
     */
    private void deleteComment(String postId, Post.Comment comment) {

        DeleteCommentRequest deleteCommentRequest = new DeleteCommentRequest(CommentsListAdapter.postId, comment);
        System.out.println(CommentsListAdapter.postId);
        System.out.println(comment.getCommentContent());

        StreamsService streamsService = ServiceGenerator.createService(StreamsService.class, token);
        Call<Object> httpRequest = streamsService.deleteComment(deleteCommentRequest);

        httpRequest.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    socket.emit("refresh");
                    System.out.println("232323");
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

    public class CommentItemHolder extends RecyclerView.ViewHolder {
        ConstraintLayout layoutItem;

        CircleImageView imageProfile;

        TextView usernameText;
        TextView datePostText;
        TextView commentContent;

        ImageView deletePostButton;

        public CommentItemHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile_circle_comment);
            layoutItem = itemView.findViewById(R.id.layout_item_comment);
            usernameText = itemView.findViewById(R.id.username_text_comment);
            datePostText = itemView.findViewById(R.id.date_comment_text);
            commentContent = itemView.findViewById(R.id.comment_content);
            deletePostButton = itemView.findViewById(R.id.delete_comment_button);
        }
    }
}
