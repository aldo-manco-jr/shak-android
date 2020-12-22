package org.aldofrankmarco.shak.notifications.controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import org.aldofrankmarco.shak.R;
import org.aldofrankmarco.shak.models.Notification;
import org.aldofrankmarco.shak.profile.controllers.ProfileFragment;
import org.aldofrankmarco.shak.profile.http.GetUserProfileImageResponse;
import org.aldofrankmarco.shak.streams.controllers.LoggedUserActivity;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NotificationsListAdapter extends RecyclerView.Adapter<NotificationsListAdapter.NotifyItemHolder> {
    private List<Notification> listUserNotification;
    private NotificationsListFragment notificationsListFragment;
    private View fragmentView;
    private RecyclerView recyclerView;

    private final String basicUrlImage = "http://res.cloudinary.com/dfn8llckr/image/upload/v";

    private String senderImageProfileId;
    private String senderImageProfileVersion;

    public NotificationsListAdapter(List<Notification> listUser, NotificationsListFragment notificationsListFragment, View view, RecyclerView recyclerView) {
        this.listUserNotification = listUser;
        this.notificationsListFragment = notificationsListFragment;
        this.fragmentView = view;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public NotificationsListAdapter.NotifyItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_notification, parent, false);
        NotificationsListAdapter.NotifyItemHolder viewHolder = new NotificationsListAdapter.NotifyItemHolder(itemView);

        return viewHolder;
    }

    /**
     * Questo metodo viene eseguito per ogni elemento nella lista, ogni elemento quindi viene
     * processato e aggiunto alla lista.
     */

    @Override
    public void onBindViewHolder(@NonNull final NotificationsListAdapter.NotifyItemHolder holder, final int position) {

        final Notification notification = listUserNotification.get(position);
        final String senderUsername = notification.getSenderUsername();

        setSenderImageProfile(senderUsername, holder);

        //data
        Date date = null;
        try {
            date = localTimeToUtc(notification.getCreatedAt());
        } catch (ParseException ignored) {
        }

        PrettyTime formattedDateTime = new PrettyTime();
        holder.dateNotificationText.setText(formattedDateTime.format(date));

        //contenuto notifica
        holder.notifyContent.setText(notification.getNotificationContent());

        if (notification.isRead()) {
            holder.markButton.setVisibility(View.INVISIBLE);
            holder.status.setImageResource(R.drawable.ic_read_notification_status_24);
        } else {
            holder.markButton.setVisibility(View.VISIBLE);
            holder.status.setImageResource(R.drawable.ic_unread_notification_status_24);
        }

        //eliminazione notifica
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteNotification(notification, holder);
            }
        });

        //notifica segnata letta
        holder.markButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markNotificationAsRead(notification, holder);
            }
        });

        notificationsListFragment.buttonMarkAllNotificationAsRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationsListFragment.markAllNotificationsAsRead(holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listUserNotification.size();
    }

    public List<Notification> getListNotification() {
        return listUserNotification;
    }

    private void setSenderImageProfile(final String senderUsername, final NotifyItemHolder holder) {

        Call<GetUserProfileImageResponse> httpRequest = LoggedUserActivity.getImagesService().getUserProfileImage(senderUsername);

        httpRequest.enqueue(new Callback<GetUserProfileImageResponse>() {
            @Override
            public void onResponse(Call<GetUserProfileImageResponse> call, final Response<GetUserProfileImageResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null : "body() non doveva essere null";

                    senderImageProfileId = response.body().getUserProfileImageId();
                    senderImageProfileVersion = response.body().getUserProfileImageVersion();

                    final String urlImageProfileUser = basicUrlImage
                            + senderImageProfileVersion + "/"
                            + senderImageProfileId;

                    Glide.with(LoggedUserActivity.getLoggedUserActivity())
                            .asBitmap()
                            .load(urlImageProfileUser)
                            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .into(holder.imageProfile);

                    holder.imageProfile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ProfileFragment profileFragment = LoggedUserActivity.getLoggedUserActivity()
                                    .getProfileFragments();
                            ProfileFragment userInformationProfile = profileFragment
                                    .newInstanceUserViewInformation(senderUsername);
                            LoggedUserActivity.getLoggedUserActivity().changeFragment(userInformationProfile);
                        }
                    });

                    holder.notifyContent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //ProfileFragment profileFragment = ProfileFragment.newInstance(senderUsername);
                            //LoggedUserActivity.getLoggedUserActivity().changeFragment(profileFragment);
                            ProfileFragment profileFragment = LoggedUserActivity.getLoggedUserActivity()
                                    .getProfileFragments();
                            ProfileFragment userInformationProfile = profileFragment
                                    .newInstanceUserViewInformation(senderUsername);
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

    /**
     * l'utente autenticato e invia una richiesta http in cui richiede la cancellazione della
     * notifica.
     */
    private void deleteNotification(final Notification notification, final NotifyItemHolder holder) {
        Call<Object> httpRequest = LoggedUserActivity.getNotificationsService().deleteNotification(notification.getNotificationId());

        httpRequest.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    notificationsListFragment.removeUserNotification(notificationsListFragment, notification, fragmentView, recyclerView, holder);
                } else {
                    //Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), notification.getNotificationId() + "", Toast.LENGTH_LONG).show();
                    Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void markNotificationAsRead(final Notification notification, final NotifyItemHolder holder) {
        Call<Object> httpRequest = LoggedUserActivity.getNotificationsService().markNotificationAsRead(notification.getNotificationId());

        httpRequest.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    holder.markButton.setVisibility(View.INVISIBLE);
                    holder.status.setImageResource(R.drawable.ic_read_notification_status_24);
                } else {
                    //Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), notification.getNotificationId() + "", Toast.LENGTH_LONG).show();
                    Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public class NotifyItemHolder extends RecyclerView.ViewHolder {
        CircleImageView imageProfile;
        TextView dateNotificationText;
        TextView notifyContent;
        ImageView deleteButton;
        ImageView markButton;
        ImageView status;

        public NotifyItemHolder(@NonNull View itemView) {
            super(itemView);
            imageProfile = itemView.findViewById(R.id.image_profile_circle);
            dateNotificationText = itemView.findViewById(R.id.dateNotify);
            notifyContent = itemView.findViewById(R.id.notificationContent);
            deleteButton = itemView.findViewById(R.id.delete_notify_button);
            markButton = itemView.findViewById(R.id.mark_button);
            status = itemView.findViewById(R.id.notifyStatus);
        }
    }
}
