package org.aldofrank.shak.streams.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.aldofrank.shak.R;
import org.aldofrank.shak.models.Post;
import org.aldofrank.shak.services.ServiceGenerator;
import org.aldofrank.shak.services.StreamsService;
import org.aldofrank.shak.streams.http.PostsListResponse;
import org.json.JSONObject;

import java.lang.ref.PhantomReference;
import java.net.URISyntaxException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostFormFragment extends Fragment implements View.OnClickListener {

    private String token;

    private Fragment postFormFragment;

    private static PostsListFragment postsListFragment;

    private Context context;

    private Socket socket;
    {
        try {
            socket = IO.socket("http://10.0.2.2:3000/");
        } catch (URISyntaxException ignored) {}
    }

    private EditText postContentField;

    private FloatingActionButton buttonClosePostForm;
    private FloatingActionButton buttonUploadImagePost;
    private FloatingActionButton buttonSubmitPost;

    public PostFormFragment() {
        // Required empty public constructor
    }

    public static PostFormFragment newInstance(PostsListFragment postsListFragment) {
        PostFormFragment fragment = new PostFormFragment();
        PostFormFragment.postsListFragment = postsListFragment;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();

        socket.on("refreshPage", updatePostsList);
        socket.connect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View postFormFragmentView = inflater.inflate(R.layout.fragment_post_form, container, false);
        token = LoggedUserActivity.getToken();
        postFormFragment = this;

        postContentField = postFormFragmentView.findViewById(R.id.post_content_field);
        buttonClosePostForm = postFormFragmentView.findViewById(R.id.fab_close_post_form);
        buttonUploadImagePost = postFormFragmentView.findViewById(R.id.fab_add_post_image);
        buttonSubmitPost = postFormFragmentView.findViewById(R.id.fab_submit_button);

        buttonSubmitPost.setOnClickListener(this);
        buttonClosePostForm.setOnClickListener(this);
        buttonUploadImagePost.setOnClickListener(this);

        postContentField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().isEmpty()){
                    // quando il campo di testo del post è vuoto
                    buttonSubmitPost.setVisibility(View.GONE);
                }else {
                    buttonSubmitPost.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        return postFormFragmentView;
    }

    private void submitPost(){
        StreamsService streamsService = ServiceGenerator.createService(StreamsService.class, token);
        JsonObject postData = new JsonObject();
        Call<Object> httpRequest = streamsService.submitPost(postData);

        postData.addProperty("post", postContentField.getText().toString().trim());

        httpRequest.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    assert getView() != null : "getView() non doveva essere null";
                    assert getFragmentManager() != null : "getFragmentManager() non doveva essere null";

                    Snackbar.make(getView(), "Post Added Successfully!!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    socket.emit("refresh");

                    // il fragment chiude se stesso
                    getFragmentManager().beginTransaction().remove(postFormFragment).commitAllowingStateLoss();
                } else {
                    Toast.makeText(getActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void uploadImagePost(){
        //Intent intent = new Intent(Intent.ACTION_PICK);
        // intent.
    }

    private Emitter.Listener updatePostsList = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            if (getActivity() != null){
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // quando un post viene pubblicato la socket avvisa del necessario aggiornmento
                        PostFormFragment.postsListFragment.getAllPosts();
                    }
                });
            }
        }
    };

    @Override
    public void onClick(View view) {
        int idButtonPressed = view.getId();

        switch (idButtonPressed){
            case R.id.fab_submit_button:
                submitPost();
                break;
            case R.id.fab_close_post_form:
                getFragmentManager().beginTransaction().remove(postFormFragment).commitAllowingStateLoss();
                break;
            case R.id.fab_add_post_image:
                uploadImagePost();
                break;
        }
    }
}
