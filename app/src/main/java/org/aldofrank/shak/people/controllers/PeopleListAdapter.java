package org.aldofrank.shak.people.controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.github.nkzawa.emitter.Emitter;

import org.aldofrank.shak.R;
import org.aldofrank.shak.models.User;
import org.aldofrank.shak.services.ServiceGenerator;
import org.aldofrank.shak.services.UsersService;
import org.aldofrank.shak.streams.controllers.LoggedUserActivity;

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

        LoggedUserActivity.getSocket().on("refreshPage", updateUsersList);
    }

    /**
     * Quando un post viene pubblicato la home page viene aggiornata.
     */
    private Emitter.Listener updateUsersList = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            if (LoggedUserActivity.getLoggedUserActivity() != null) {
                LoggedUserActivity.getLoggedUserActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // quando un post viene pubblicato la socket avvisa del necessario aggiornmento
                        PeopleListFragment.getPeopleListFragment().getAllUsers();
                    }
                });
            }
        }
    };

    @NonNull
    @Override
    public UserItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_user, parent, false);
        UserItemHolder viewHolder = new UserItemHolder(itemView);

        return viewHolder;
    }
    /*
    @NonNull
    @Override
    public PostsListAdapter.PostItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_post, parent, false);
        PostsListAdapter.PostItemHolder viewHolder = new PostsListAdapter.PostItemHolder(itemView);

        return viewHolder;
    }
*/
    /**
     * Questo metodo viene eseguito per ogni elemento nella lista, ogni elemento quindi viene
     * processato e aggiunto alla lista.
     */
    @Override
    public void onBindViewHolder(@NonNull final UserItemHolder holder, final int position) {
        final User user = listUsers.get(position);
        //final User user = post.getUserId();

        String urlImageProfileUser = this.basicUrlImage + user.getProfileImageVersion() + "/"
                + user.getProfileImageId();

        Glide.with(LoggedUserActivity.getLoggedUserActivity())
                .asBitmap()
                .load(urlImageProfileUser)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(holder.imageProfile);

        holder.usernameText.setText(listUsers.get(position).getUsername());

        if (user.getCity() != null && user.getCountry() != null) {
            holder.locationText.setText("@" + user.getCity() + ", " + user.getCountry());
        }

        if (!user.getProfileImageVersion().isEmpty()) {
            String urlImagePost = this.basicUrlImage + user.getProfileImageVersion() + "/"
                    + user.getProfileImageId();

            Glide.with(LoggedUserActivity.getLoggedUserActivity())
                    .asBitmap()
                    .load(urlImagePost)
                    .into(holder.imageProfile);
        } else {
            holder.imageProfile.setVisibility(View.GONE);
        }

        holder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                followOrUnfollow(listUsers.get(position), holder);
            }
        });

        holder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO MANCA UN'AZIONE per followbutton
            }
        });
    }

    @Override
    public int getItemCount() {
        return listUsers.size();
    }

    /**
     * In base ai dati ricavati da {{@link #isFollow(User)}} viene inviata una richiesta http di
     * "follow" o di "unfollow" verso il post.
     */
    private void followOrUnfollow(User user, final UserItemHolder holder) {
        UsersService usersService = ServiceGenerator.createService(UsersService.class, LoggedUserActivity.getToken());

        //if (!isFollow(user)) {
            Call<Object> httpRequest = usersService.followUser(user.getUsername());

            httpRequest.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), "refresh", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        /*} else {
            Call<Object> httpRequest = streamsService.unlikePost(post);

            httpRequest.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful()) {
                        holder.likeButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
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
        }*/
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

        public UserItemHolder(@NonNull View itemView) {
            super(itemView);

            layoutItem = itemView.findViewById(R.id.layout_item_comment);
            imageProfile = itemView.findViewById(R.id.image_profile_circle);
            usernameText = itemView.findViewById(R.id.username_comment_text);
            emailText = itemView.findViewById(R.id.email_comment_text);
            locationText = itemView.findViewById(R.id.location_comment_text);
            imageButton = itemView.findViewById(R.id.imageButton);
            followButton =  itemView.findViewById(R.id.follow);
        }
    }
/*





    private List<Post> listPosts;

    private final String basicUrlImage = "http://res.cloudinary.com/dfn8llckr/image/upload/v";

    public PostsListAdapter(List<Post> listPosts) {
        this.listPosts = listPosts;

        LoggedUserActivity.getSocket().on("refreshPage", updatePostsList);
    }

    /**
     * Quando un post viene pubblicato la home page viene aggiornata.
     */
/*    private Emitter.Listener updatePostsList = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            if (LoggedUserActivity.getLoggedUserActivity() != null) {
                LoggedUserActivity.getLoggedUserActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // quando un post viene pubblicato la socket avvisa del necessario aggiornmento
                        HomeFragment.getHomeFragment().getStreamsFragment().getAllPosts();
                        HomeFragment.getHomeFragment().getFavouritesFragment().getAllPosts();
                    }
                });
            }
        }
    };
*/}