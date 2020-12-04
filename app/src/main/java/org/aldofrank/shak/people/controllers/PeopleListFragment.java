package org.aldofrank.shak.people.controllers;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.aldofrank.shak.R;
import org.aldofrank.shak.models.User;
import org.aldofrank.shak.people.http.GetAllUsersResponse;
import org.aldofrank.shak.services.ServiceGenerator;
import org.aldofrank.shak.services.UsersService;
import org.aldofrank.shak.streams.controllers.LoggedUserActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PeopleListFragment extends Fragment {

    private List<User> listUsers;

    private static User user;
    private static String type;
    private static PeopleListAdapter adapter;

    private static View view;

    @SuppressLint("StaticFieldLeak")
    private static PeopleListFragment peopleListFragment;

    public PeopleListFragment() { }

    protected static PeopleListFragment getPeopleListFragment() {
        if (peopleListFragment == null){
            peopleListFragment = new PeopleListFragment();
        }

        return peopleListFragment;
    }

    protected static PeopleListFragment getPeopleListFragment(User user) {
        //TODO posizione specifica
        if (peopleListFragment == null){
            peopleListFragment = new PeopleListFragment();
        }

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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PostsListFragment.
     */
    public static PeopleListFragment newInstance(User user) {
        PeopleListFragment fragment = new PeopleListFragment();

        PeopleListFragment.user = user;

        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PostsListFragment.
     */
    public static PeopleListFragment newInstance(User user, String type) {
        PeopleListFragment fragment = new PeopleListFragment();

        PeopleListFragment.user = user;
        PeopleListFragment.type = type;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_people, container, false);

        getAllUsers();

        return view;
    }

    /**
     * Consente di recuperare tutti i post:
     *  - streams: lista dei post dell'utente e dei suoi following
     *  - favorites: sono i post a cui l'utente ha espresso la preferenza
     * Viene mandata una richiesta http per recuperati i dal server.
     */
    public void getAllUsers() {
        UsersService usersService = ServiceGenerator.createService(UsersService.class, LoggedUserActivity.getToken());
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
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list_users);
        PeopleListFragment.adapter = new PeopleListAdapter(this.listUsers);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
