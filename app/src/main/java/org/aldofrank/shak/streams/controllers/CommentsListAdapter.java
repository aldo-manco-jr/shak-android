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
import org.aldofrank.shak.profile.controllers.ProfileFragment;
import org.aldofrank.shak.services.ServiceGenerator;
import org.aldofrank.shak.services.StreamsService;
import org.aldofrank.shak.services.UsersService;
import org.aldofrank.shak.streams.http.DeleteCommentRequest;
import org.aldofrank.shak.streams.http.GetPostResponse;
import org.ocpsoft.prettytime.PrettyTime;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsListAdapter extends RecyclerView.Adapter<CommentsListAdapter.CommentItemHolder> {

    private List<Post.Comment> listComments;

    public static String postId;

    private final String basicUrlImage = "http://res.cloudinary.com/dfn8llckr/image/upload/v";

    private User user;

    public CommentsListAdapter(List<Post.Comment> listComments, String type) {

        this.listComments = new ArrayList<>();

        for (int i = listComments.size()-1; i >= 0; i--) {
            this.listComments.add(listComments.get(i));
        }
    }

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

        getUserByUsername(comment.getUsernamePublisher());

        /*String urlImageProfileUser = this.basicUrlImage + user.getProfileImageVersion() + "/" + user.getProfileImageId();

        Glide.with(LoggedUserActivity.getLoggedUserActivity())
                .asBitmap()
                .load(urlImageProfileUser)
                .into(holder.imageProfile);*/

        holder.usernameText.setText(listComments.get(position).getUsernamePublisher());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" );
        Date date = new Date();

        try {
            date = formatter.parse(listComments.get(position).getCreatedAt());
            date.setTime(date.getTime()+3_600_000);
        }catch (Exception ignored){}

        PrettyTime formattedDateTime = new PrettyTime();
        holder.dateCommentText.setText(formattedDateTime.format(date));

        holder.commentContent.setText(comment.getCommentContent());

        if (comment.getUsernamePublisher().equals(LoggedUserActivity.getUsernameLoggedUser())) {
            holder.deleteCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteComment(CommentsListAdapter.postId, comment);
                }
            });
        } else {
            holder.deleteCommentButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listComments.size();
    }

    private void getUserByUsername(final String username){

        UsersService usersService = ServiceGenerator.createService(UsersService.class, LoggedUserActivity.getToken());
        Call<GetUserByUsernameResponse> httpRequest = usersService.getUserByUsername(username);

        httpRequest.enqueue(new Callback<GetUserByUsernameResponse>() {
            @Override
            public void onResponse(Call<GetUserByUsernameResponse> call, Response<GetUserByUsernameResponse> response) {
                if (response.isSuccessful()) {
                    user = response.body().getUserFoundByUsername();
                } else {
                    Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GetUserByUsernameResponse> call, Throwable t) {
                Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Questa funzione è accesibile solo per i post dell'utente autenticato e invia una richiesta
     * http in cui richieste la cancellazione del post.
     */
    private void deleteComment(String postId, Post.Comment comment) {

        DeleteCommentRequest deleteCommentRequest = new DeleteCommentRequest(CommentsListAdapter.postId, comment);

        StreamsService streamsService = ServiceGenerator.createService(StreamsService.class, LoggedUserActivity.getToken());
        Call<Object> httpRequest = streamsService.deleteComment(deleteCommentRequest);

        httpRequest.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    LoggedUserActivity.getSocket().emit("refresh");
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

    public class CommentItemHolder extends RecyclerView.ViewHolder {
        ConstraintLayout layoutItem;

        CircleImageView imageProfile;

        TextView usernameText;
        TextView dateCommentText;
        TextView commentContent;

        ImageView deleteCommentButton;

        public CommentItemHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile_circle_comment);
            layoutItem = itemView.findViewById(R.id.layout_item_comment);
            usernameText = itemView.findViewById(R.id.username_text_comment);
            dateCommentText = itemView.findViewById(R.id.date_comment_text);
            commentContent = itemView.findViewById(R.id.comment_content);
            deleteCommentButton = itemView.findViewById(R.id.delete_comment_button);
        }
    }
}
