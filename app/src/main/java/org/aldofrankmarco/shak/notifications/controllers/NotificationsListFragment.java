package org.aldofrankmarco.shak.notifications.controllers;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.aldofrankmarco.shak.R;
import org.aldofrankmarco.shak.models.User;
import org.aldofrankmarco.shak.people.http.GetUserByUsernameResponse;
import org.aldofrankmarco.shak.services.NotificationsService;
import org.aldofrankmarco.shak.services.ServiceGenerator;
import org.aldofrankmarco.shak.services.UsersService;
import org.aldofrankmarco.shak.streams.controllers.LoggedUserActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to create an instance of this fragment.
 */
public class NotificationsListFragment extends Fragment {

    protected List<User.Notification> listNotification;
    protected RecyclerView recyclerView;
    protected NotificationsListAdapter adapter;
    private View view;

    FloatingActionButton buttonMarkAllNotificationAsRead;

    public NotificationsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notifications_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.listNotifications);
        buttonMarkAllNotificationAsRead = view.findViewById(R.id.fab_mark_all_notification_as_read);
        getAllNotifications();
    }

    /**
     * Consente di recuperare tutte le notifiche
     */
    public void getAllNotifications() {
        UsersService usersService = ServiceGenerator.createService(UsersService.class, LoggedUserActivity.getToken());
        Call<GetUserByUsernameResponse> httpRequest = usersService.getUserByUsername(LoggedUserActivity.getUsernameLoggedUser());

        httpRequest.enqueue(new Callback<GetUserByUsernameResponse>() {
            @Override
            public void onResponse(Call<GetUserByUsernameResponse> call, Response<GetUserByUsernameResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null : "body() non doveva essere null";
                    listNotification = response.body().getUserFoundByUsername().getArrayNotifications();
                    initializeRecyclerView();

                } else {
                    Toast.makeText(getActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<GetUserByUsernameResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    void removeUserNotification(NotificationsListFragment notificationsListFragment, User.Notification notification, View view, RecyclerView recyclerView, NotificationsListAdapter.NotifyItemHolder holder){
        recyclerView.removeView(view);

        notificationsListFragment.adapter.getListNotification().remove(notification);
        notificationsListFragment.adapter.notifyItemRemoved(holder.getAdapterPosition());
    }

    void markAllNotificationsAsRead(final NotificationsListAdapter.NotifyItemHolder holder){

        NotificationsService notificationsService = ServiceGenerator.createService(NotificationsService.class, LoggedUserActivity.getToken());

        Call<Object> httpRequest = notificationsService.markAllNotificationsAsRead();

        httpRequest.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    getAllNotifications();
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

    /**
     * Viene collegata la recycler view con l'adapter
     */
    private void initializeRecyclerView() {
        adapter = new NotificationsListAdapter(this.listNotification, this, view, recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}