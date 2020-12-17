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
import org.aldofrankmarco.shak.models.Notification;
import org.aldofrankmarco.shak.models.User;
import org.aldofrankmarco.shak.notifications.http.GetNotificationsListResponse;
import org.aldofrankmarco.shak.people.http.GetUserByUsernameResponse;
import org.aldofrankmarco.shak.services.NotificationsService;
import org.aldofrankmarco.shak.services.ServiceGenerator;
import org.aldofrankmarco.shak.services.UsersService;
import org.aldofrankmarco.shak.streams.controllers.LoggedUserActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsListFragment extends Fragment {

    protected List<Notification> listNotification;
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
        Call<GetNotificationsListResponse> httpRequest = LoggedUserActivity.getNotificationsService().getAllNotifications();

        httpRequest.enqueue(new Callback<GetNotificationsListResponse>() {
            @Override
            public void onResponse(Call<GetNotificationsListResponse> call, Response<GetNotificationsListResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null : "body() non doveva essere null";
                    listNotification = response.body().getNotificationsList();

                    initializeRecyclerView();
                } else {
                    Toast.makeText(getActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GetNotificationsListResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    void removeUserNotification(NotificationsListFragment notificationsListFragment, Notification notification, View view, RecyclerView recyclerView, NotificationsListAdapter.NotifyItemHolder holder){
        recyclerView.removeView(view);

        notificationsListFragment.adapter.getListNotification().remove(notification);
        notificationsListFragment.adapter.notifyItemRemoved(holder.getAdapterPosition());
    }

    void markAllNotificationsAsRead(final NotificationsListAdapter.NotifyItemHolder holder){
        Call<Object> httpRequest = LoggedUserActivity.getNotificationsService().markAllNotificationsAsRead();

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