package org.aldofrankmarco.shak.people.controllers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.aldofrankmarco.shak.R;
import org.aldofrankmarco.shak.models.User;
import org.aldofrankmarco.shak.people.http.GetAllUsersResponse;
import org.aldofrankmarco.shak.people.http.GetFollowersResponse;
import org.aldofrankmarco.shak.people.http.GetFollowingResponse;
import org.aldofrankmarco.shak.streams.controllers.LoggedUserActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PeopleListFragment extends Fragment {

    private PeopleListAdapter adapter;

    private String username;
    private String type;

    private TextView titleTextView;

    private View view;

    private static PeopleListFragment peopleListFragment;

    public static PeopleListFragment getPeopleListFragment() {
        return peopleListFragment;
    }

    /**
     * Costruttore di default del frammento, prevede l'uso del tipo "streams"
     */
    public static PeopleListFragment newInstance() {
        PeopleListFragment fragment = new PeopleListFragment();

        Bundle args = new Bundle();
        args.putString("type", "streams");
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PostsListFragment.
     */
    public static PeopleListFragment newInstance(String type, String username) {
        PeopleListFragment fragment = new PeopleListFragment();

        Bundle args = new Bundle();
        args.putString("type", type);
        args.putString("username", username);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        type = getArguments().getString("type");
        username = getArguments().getString("username");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_people, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;
        peopleListFragment = this;

        titleTextView = view.findViewById(R.id.title_users_list);

        getUsersList();
    }

    /*Emitter.Listener updateUsersList = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            if (LoggedUserActivity.getLoggedUserActivity() != null) {
                LoggedUserActivity.getLoggedUserActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // quando un follow viene aggiunto/rimosso la socket avvisa del necessario aggiornmento
                        //ProfileFragment.getProfileFragment().getAllUsersFragment().newGetAllUsers();
                        //ProfileFragment.getProfileFragment().getProfileFollowingFragment(username).newGetAllUsers();
                        //ProfileFragment.getProfileFragment().getProfileFollowersFragment(username).newGetAllUsers();
                    }
                });
            }
        }
    };*/

    /**
     * Consente di recuperare tutti i post:
     * - streams: lista dei post dell'utente e dei suoi following
     * - favorites: sono i post a cui l'utente ha espresso la preferenza
     * Viene mandata una richiesta http per recuperati i dal server.
     */
    public void getUsersList() {

        if (getArguments() == null) {
            return;
        }

        if (type.equals("streams")) {

            titleTextView.setVisibility(View.VISIBLE);

            Call<GetAllUsersResponse> httpRequest = LoggedUserActivity.getUsersService().getAllUsers();

            httpRequest.enqueue(new Callback<GetAllUsersResponse>() {
                @Override
                public void onResponse(Call<GetAllUsersResponse> call, Response<GetAllUsersResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null : "body() non doveva essere null";

                        initializeRecyclerView(response.body().getAllUsers());
                    }
                }

                @Override
                public void onFailure(Call<GetAllUsersResponse> call, Throwable t) {
                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else if (type.equals("following") && !username.isEmpty()) {

            titleTextView.setVisibility(View.GONE);

            Call<GetFollowingResponse> httpRequest = LoggedUserActivity.getUsersService().getFollowing(username);

            httpRequest.enqueue(new Callback<GetFollowingResponse>() {
                @Override
                public void onResponse(Call<GetFollowingResponse> call, Response<GetFollowingResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null : "body() non doveva essere null";

                        initializeRecyclerView(response.body().getFollowingList());
                    }else {
                        Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<GetFollowingResponse> call, Throwable t) {
                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else if (type.equals("followers") && !username.isEmpty()) {

            titleTextView.setVisibility(View.GONE);

            Call<GetFollowersResponse> httpRequest = LoggedUserActivity.getUsersService().getFollowers(username);

            httpRequest.enqueue(new Callback<GetFollowersResponse>() {
                @Override
                public void onResponse(Call<GetFollowersResponse> call, Response<GetFollowersResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null : "body() non doveva essere null";

                        initializeRecyclerView(response.body().getFollowersList());
                    }else {
                        Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<GetFollowersResponse> call, Throwable t) {
                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    /**
     * Viene collegata la recycler view con l'adapter
     */
    private void initializeRecyclerView(List<User> listUsers) {
        RecyclerView recyclerView = view.findViewById(R.id.list_users);
        if (adapter == null){
            adapter = new PeopleListAdapter(listUsers, type);
        }

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public void resetList(){
        if (adapter != null && adapter.getList() != null) {
            while (adapter.getList().size() != 0) {
                adapter.getList().remove(0);
            }
        }
    }
}
