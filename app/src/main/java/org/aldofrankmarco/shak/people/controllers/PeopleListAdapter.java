package org.aldofrankmarco.shak.people.controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import org.aldofrankmarco.shak.R;
import org.aldofrankmarco.shak.models.User;
import org.aldofrankmarco.shak.people.http.IsFollowingResponse;
import org.aldofrankmarco.shak.profile.controllers.ProfileFragment;
import org.aldofrankmarco.shak.streams.controllers.LoggedUserActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Permette il collegamento tra la struttura dell'oggetto e la recycler view che lo deve rappresentare
 */
public class PeopleListAdapter extends RecyclerView.Adapter<PeopleListAdapter.UserItemHolder> {

    private List<User> listUsers;

    private final String basicUrlImage = "http://res.cloudinary.com/dfn8llckr/image/upload/v";

    public PeopleListAdapter(List<User> listUsers) {
        this.listUsers = listUsers;
    }

    @NonNull
    @Override
    public PeopleListAdapter.UserItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_user, parent, false);
        PeopleListAdapter.UserItemHolder viewHolder = new PeopleListAdapter.UserItemHolder(itemView);

        return viewHolder;
    }

    /**
     * Questo metodo viene eseguito per ogni elemento nella lista, ogni elemento quindi viene
     * processato e aggiunto alla lista.
     */
    @Override
    public void onBindViewHolder(@NonNull final UserItemHolder holder, final int position) {
        final User user = listUsers.get(position);

        String urlImageProfileUser = this.basicUrlImage + user.getProfileImageVersion() + "/"
                + user.getProfileImageId();

        Glide.with(LoggedUserActivity.getLoggedUserActivity())
                .asBitmap()
                .load(urlImageProfileUser)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(holder.imageProfile);

        holder.usernameText.setText(listUsers.get(position).getUsername());
        holder.emailText.setText(listUsers.get(position).getEmail());

        if (user.getCity() != null && user.getCountry() != null) {
            holder.locationText.setText("@" + user.getCity() + ", " + user.getCountry());
        }

        if (!user.getProfileImageVersion().isEmpty()) {
            // nel caso in cui non vi sia un dato valorizzato "profile image version" nel database
            final String urlImagePost = this.basicUrlImage +
                    user.getProfileImageVersion() +
                    "/" +
                    user.getProfileImageId();

            Glide.with(LoggedUserActivity.getLoggedUserActivity())
                    .asBitmap()
                    .load(urlImagePost)
                    .into(holder.imageProfile);

            holder.imageProfile.setVisibility(View.VISIBLE);
        } else {
            holder.imageProfile.setVisibility(View.GONE);
        }

       isFollow(user, holder);

        holder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                followOrUnfollow(listUsers.get(position), holder);
            }
        });

        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ProfileFragment profileFragment = ProfileFragment.newInstance(holder.usernameText.getText().toString().trim());
                //LoggedUserActivity.getLoggedUserActivity().changeFragment(profileFragment);
                ProfileFragment profileFragment = LoggedUserActivity.getLoggedUserActivity()
                        .getProfileFragments();
                ProfileFragment userInformationProfile = profileFragment
                        .newInstanceUserViewInformation(holder.usernameText.getText().toString().trim());
                LoggedUserActivity.getLoggedUserActivity().changeFragment(userInformationProfile);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listUsers.size();
    }

    /**
     * In base ai dati ricavati da {@link #isFollow(User, UserItemHolder)} viene inviata una richiesta http di
     * "follow" o di "unfollow" verso il post.
     */
    private void followOrUnfollow(User user, final UserItemHolder holder) {
        Call<Object> httpRequest;

        if (holder.followButton.getText().equals("unfollow")) {
            // l'utente che ha effettuato l'accesso è un follower dell'utente considerato
            //httpRequest = usersService.unfollowUser(new FollowOrUnfollowRequest(user.getId()));
            httpRequest = LoggedUserActivity.getUsersService().unfollowUser(user.getId());
        } else {
            httpRequest = LoggedUserActivity.getUsersService().followUser(user.getId());
        }

        httpRequest.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {

                    if (holder.followButton.getText().equals("unfollow")){
                        holder.followButton.setText("follow");
                    }else if (holder.followButton.getText().equals("follow")){
                        holder.followButton.setText("unfollow");
                    }
                    //LoggedUserActivity.getSocket().emit("refresh");
                } else {
                    Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), response.code() + "   " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * un user generico in input
     *
     * @return true se l'utente selezionato è un follower, false altrimenti
     */
    private void isFollow(User user, final UserItemHolder holder) {

        holder.followButton.setText("follow");

        Call<IsFollowingResponse> httpRequest = LoggedUserActivity.getUsersService().isFollowing(user.getUsername());

        httpRequest.enqueue(new Callback<IsFollowingResponse>() {
            @Override
            public void onResponse(Call<IsFollowingResponse> call, Response<IsFollowingResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getMessage().equals("yes")) {
                            holder.followButton.setText("unfollow");
                    }

                    holder.loadingFollow.setVisibility(View.GONE);
                    holder.followButton.setVisibility(View.VISIBLE);
                    //LoggedUserActivity.getSocket().emit("refresh");
                } else {
                    Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<IsFollowingResponse> call, Throwable t) {
                Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }



    public class UserItemHolder extends RecyclerView.ViewHolder {
        //TODO METTERE I DATI CHE SI HANNO SUL RECYCLER VIEW
        ConstraintLayout layoutItem;

        CircleImageView imageProfile;

        TextView usernameText;
        TextView emailText;
        TextView locationText;

        ImageButton imageButton;

        Button followButton;

        RelativeLayout loadingFollow;

        public UserItemHolder(@NonNull View itemView) {
            super(itemView);

            layoutItem = itemView.findViewById(R.id.layout_item_comment);
            imageProfile = itemView.findViewById(R.id.image_profile_circle);
            usernameText = itemView.findViewById(R.id.username_comment_text);
            emailText = itemView.findViewById(R.id.email_comment_text);
            locationText = itemView.findViewById(R.id.location_comment_text);
            imageButton = itemView.findViewById(R.id.imageButton);
            followButton = itemView.findViewById(R.id.follow);
            loadingFollow =itemView.findViewById(R.id.loading_follow);
        }
    }
}