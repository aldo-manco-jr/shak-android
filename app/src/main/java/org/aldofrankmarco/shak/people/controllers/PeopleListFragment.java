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
import org.aldofrankmarco.shak.services.ServiceGenerator;
import org.aldofrankmarco.shak.services.UsersService;
import org.aldofrankmarco.shak.streams.controllers.LoggedUserActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PeopleListFragment extends Fragment {

    private List<User> listUsers;

    private PeopleListAdapter adapter;

    private String username;

    private TextView titleTextView;

    private View view;

    private static PeopleListFragment peopleListFragment;

    public PeopleListFragment() {
    }

    public static PeopleListFragment getPeopleListFragment() {
        return peopleListFragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PostsListFragment.
     */
    public static PeopleListFragment newInstance(String type) {
        PeopleListFragment fragment = new PeopleListFragment();

        Bundle args = new Bundle();
        args.putString("type", type);
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

        getAllUsers();
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
    public void getAllUsers() {

        if (getArguments() == null) {
            return;
        }

        UsersService usersService = ServiceGenerator.createService(UsersService.class, LoggedUserActivity.getToken());

        titleTextView.setVisibility(View.VISIBLE);

        Call<GetAllUsersResponse> httpRequest = usersService.getAllUsers();

        httpRequest.enqueue(new Callback<GetAllUsersResponse>() {
            @Override
            public void onResponse(Call<GetAllUsersResponse> call, Response<GetAllUsersResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null : "body() non doveva essere null";

                    listUsers = response.body().getAllUsers();

                    initializeRecyclerView();
                }
            }

            @Override
            public void onFailure(Call<GetAllUsersResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Viene collegata la recycler view con l'adapter
     */
    private void initializeRecyclerView() {
        RecyclerView recyclerView = view.findViewById(R.id.list_users);
        if (adapter == null){
            adapter = new PeopleListAdapter(this.listUsers);
        }

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

   /* private void clearRecyclerView() {
        recyclerView.getRecycledViewPool().clear();
        adapter.notifyDataSetChanged();
    }*/
}
