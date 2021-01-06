package org.aldofrankmarco.shak.streams.controllers.comments;

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

import org.aldofrankmarco.shak.R;
import org.aldofrankmarco.shak.models.Comment;
import org.aldofrankmarco.shak.profile.controllers.ProfileFragment;
import org.aldofrankmarco.shak.profile.http.GetUserProfileImageResponse;
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

public class CommentsListAdapter extends RecyclerView.Adapter<CommentsListAdapter.CommentItemHolder> {

    private List<Comment> listComments;

    private CommentsListFragment fatherFragment;

    public static String postId;

    private final String basicUrlImage = "http://res.cloudinary.com/dfn8llckr/image/upload/v";

    public CommentsListAdapter(List<Comment> listComments, CommentsListFragment commentsListFragment) {

        this.fatherFragment = commentsListFragment;
        this.listComments = new ArrayList<>();

        for (int i = (listComments.size() - 1); i >= 0; i--) {
            this.listComments.add(listComments.get(i));
        }
     }

    @NonNull
    @Override
    public CommentItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_comment, parent, false);

        CommentItemHolder viewHolder = new CommentItemHolder(itemView);
        return viewHolder;
    }

    /**
     * Questo metodo viene eseguito per ogni elemento nella lista, ogni elemento quindi viene
     * processato e aggiunto alla lista.
     */
    @Override
    public void onBindViewHolder(@NonNull final CommentItemHolder holder, final int position) {

        final Comment comment = listComments.get(position);

        setProfileImage(comment.getUsernamePublisher(), holder);

        holder.usernameText.setText(listComments.get(position).getUsernamePublisher());

        Date date = null;
        try {
            date = localTimeToUtc(comment.getCreatedAt());
        } catch (ParseException ignored) {
        }

        PrettyTime formattedDateTime = new PrettyTime();
        holder.dateCommentText.setText(formattedDateTime.format(date));

        holder.commentContent.setText(comment.getCommentContent());

        if (comment.getUsernamePublisher().equals(LoggedUserActivity.getUsernameLoggedUser())) {
            holder.deleteCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteComment(comment);
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

    private void setProfileImage(final String username, final CommentItemHolder holder) {
        Call<GetUserProfileImageResponse> httpRequest = LoggedUserActivity.getImagesService().getUserProfileImage(username);

        httpRequest.enqueue(new Callback<GetUserProfileImageResponse>() {
            @Override
            public void onResponse(Call<GetUserProfileImageResponse> call, Response<GetUserProfileImageResponse> response) {
                if (response.isSuccessful()) {

                    String profileImageId = response.body().getUserProfileImageId();
                    String profileImageVersion = response.body().getUserProfileImageVersion();

                    String urlImageProfileUser = basicUrlImage + profileImageVersion + "/" + profileImageId;

                    Glide.with(LoggedUserActivity.getLoggedUserActivity())
                            .asBitmap()
                            .load(urlImageProfileUser)
                            .into(holder.imageProfile);

                    holder.imageProfile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //ProfileFragment profileFragment = ProfileFragment.newInstance(username);
                            //LoggedUserActivity.getLoggedUserActivity().changeFragment(profileFragment);
                            ProfileFragment profileFragment = LoggedUserActivity.getLoggedUserActivity()
                                    .getProfileFragments();
                            ProfileFragment userInformationProfile = profileFragment
                                    .newInstanceUserViewInformation(holder.usernameText.getText().toString().trim());
                            LoggedUserActivity.getLoggedUserActivity().changeFragment(userInformationProfile);
                        }
                    });

                    holder.usernameText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //ProfileFragment profileFragment = ProfileFragment.newInstance(username);
                            //LoggedUserActivity.getLoggedUserActivity().changeFragment(profileFragment);
                            ProfileFragment profileFragment = LoggedUserActivity.getLoggedUserActivity()
                                    .getProfileFragments();
                            ProfileFragment userInformationProfile = profileFragment
                                    .newInstanceUserViewInformation(holder.usernameText.getText().toString().trim());
                            LoggedUserActivity.getLoggedUserActivity().changeFragment(userInformationProfile);
                        }
                    });
                } else {
                    Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GetUserProfileImageResponse> call, Throwable t) {
                Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Questa funzione è accesibile solo per i post dell'utente autenticato e invia una richiesta
     * http in cui richieste la cancellazione del post.
     */
    private void deleteComment(Comment comment) {
        Call<Object> httpRequest = LoggedUserActivity.getStreamsService()
                .deleteComment(CommentsListAdapter.postId, comment.getCommentId());

        httpRequest.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    LoggedUserActivity.getSocket().emit("refreshListAfterDeleteComment");
                    fatherFragment.getAllPostComments();
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
