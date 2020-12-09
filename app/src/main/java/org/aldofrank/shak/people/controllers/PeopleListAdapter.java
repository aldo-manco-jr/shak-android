package org.aldofrank.shak.people.controllers;

import android.util.Log;
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
import org.aldofrank.shak.people.http.FollowOrUnfollowRequest;
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

    private final String basicUrlImage = "http://res.cloudinary.com/dfn8llckr/image/upload/v";

    public PeopleListAdapter(List<User> listUsers) {
        for (User user: PeopleListFragment.listUsers){
            if (user.getId().equals(LoggedUserActivity.getIdLoggedUser())){
                // se l'utente nella lista era l'utente che aveva effettuato il login lo rimuove
                PeopleListFragment.listUsers.remove(user);
                break;
            }
        }

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
                        // quando un follow viene aggiunto/rimosso la socket avvisa del necessario aggiornmento
                        PeopleListFragment.getPeopleListFragment().getAllUsers();
                    }
                });
            }
        }
    };

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
        final User user = PeopleListFragment.listUsers.get(position);

       /* if (user.getId().equals(LoggedUserActivity.getIdLoggedUser())) {
            // se l'utente da mostrare è l'utente loggato
            listUsers.remove(holder.getAdapterPosition());
        }
*/
        String urlImageProfileUser = this.basicUrlImage + user.getProfileImageVersion() + "/"
                + user.getProfileImageId();

        Glide.with(LoggedUserActivity.getLoggedUserActivity())
                .asBitmap()
                .load(urlImageProfileUser)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(holder.imageProfile);

        holder.usernameText.setText(PeopleListFragment.listUsers.get(position).getUsername());
        holder.emailText.setText(PeopleListFragment.listUsers.get(position).getEmail());

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

        if (isFollow(user)) {
            //l'utente selezionato è già follow dell'utente
            holder.followButton.setText("Unfollow");
        } else {
            holder.followButton.setText("Follow");
        }

        holder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                followOrUnfollow(PeopleListFragment.listUsers.get(position), holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return PeopleListFragment.listUsers.size();
    }

    /**
     * In base ai dati ricavati da {{@link #isFollow(User)}} viene inviata una richiesta http di
     * "follow" o di "unfollow" verso il post.
     */
    private void followOrUnfollow(User user, final UserItemHolder holder) {
        UsersService usersService = ServiceGenerator.createService(UsersService.class, LoggedUserActivity.getToken());

        Call<Object> httpRequest;

        if (isFollow(user)) {
            // l'utente che ha effettuato l'accesso è un follower dell'utente considerato
            //httpRequest = usersService.unfollowUser(new FollowOrUnfollowRequest(user.getId()));
            httpRequest = usersService.unfollowUser(new FollowOrUnfollowRequest(user.getId()));
        } else {
            httpRequest = usersService.followUser(new FollowOrUnfollowRequest(user.getId()));
        }

        httpRequest.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), "wewe", Toast.LENGTH_LONG).show();
                    LoggedUserActivity.getSocket().emit("refresh");
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
     * @param user un utente che potrebbe essere un follower
     *
     * @return true se l'utente selezionato è un follower, false altrimenti
     */
    private boolean isFollow(User user) {
        Log.d("non si sa", "no nsi sa");

        String userIdFollowed = user.getId();
        String nome = PeopleListFragment.userConnected.getEmail();
        /*List<User.Follower> userFollowers = PeopleListFragment.userConnected.getArrayFollowers();
        if (userFollowers != null){
            for (int i = 0; i < userFollowers.size(); i++){
                JsonArray followerId = userFollowers.get(i).getFollowerId();
                if (followerId != null) {
                    for (int j = 0; j < followerId.size(); j++) {
                        if (followerId.get(j).getAsJsonObject().get("follower") != null) {
                            // se uno dei followed è uguale all'userIdFollowed è un follower
                            String followed = followerId.get(j).getAsJsonObject().get("follower").getAsString();
                            String id = followerId.get(j).getAsJsonObject().get("_id").getAsString();
                            //Log.d("confronta", followed + "==" + userIdFollowed + " ..... " + id);

                            if (followed.compareTo(userIdFollowed) == 0) {
                                return true;
                            }
                        }
                    }
                }
            }
        }*/

        return false;
    }
        /*    List<User.Follower> userFollowers = PeopleListFragment.userConnected.getArrayFollowers();
        //Log.v("followerssss", userFollowers.toString());
        if (userFollowers == null || userFollowers.isEmpty()){
            // non esitono dei follower
            return false;
        }

        //for (User.Follower userFollow : userFollowers) {
            // per ogni follow dell'utente connesso
        JsonArray followeArray = userFollowers.get(0).getFollowerId();

        for(User.Follower userrr: userFollowers){
            followeArray = userrr.getFollowerId();
        for (int i = 0; i < followeArray.size(); i++){
            //per ogni follow
            String followId = followeArray.get(i).getAsJsonObject().get("follower").getAsString();

            if (userIdFollower.equals(followId)) {
                // è un follow
                return true;
            }
        }}

        //JsonArray userFollowers = userFollowers.get(0).getFollowerId();//.get(0).getAsJsonObject().get("_id").getAsString();


            /*
            Log.v("ddddyy", userFollowId + "------" + userIdFollower);
           */
        //}
/*
        return false;
    }
*/

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
}