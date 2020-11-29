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

public class PostFormFragment extends Fragment {

    private String token;

    private Fragment postFormFragment;
    private static PostsListFragment postsListFragment;

    private Context context;

    private Socket socket;
    {
        try {
            socket = IO.socket("http://10.0.2.2:3000/");
        } catch (URISyntaxException e) {}
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

        context=getContext();

        socket.on("refreshPage", updatePostsList);
        socket.connect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post_form, container, false);

        token = LoggedUserActivity.getToken();

        postFormFragment = this;

        postContentField = view.findViewById(R.id.post_content_field);
        buttonClosePostForm = view.findViewById(R.id.fab_close_post_form);
        buttonUploadImagePost = view.findViewById(R.id.fab_add_post_image);
        buttonSubmitPost = view.findViewById(R.id.fab_submit_button);

        buttonSubmitPost.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                submitPost();
            }
        });

        buttonClosePostForm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().remove(postFormFragment).commitAllowingStateLoss();
            }
        });

        buttonUploadImagePost.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                uploadImagePost();
            }
        });

        postContentField.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.toString().isEmpty()){
                    buttonSubmitPost.setVisibility(View.GONE);
                }else {
                    buttonSubmitPost.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    private void submitPost(){

        StreamsService streamsService = ServiceGenerator.createService(StreamsService.class, token);

        JsonObject postData = new JsonObject();
        postData.addProperty("post", postContentField.getText().toString().trim());

        Call<Object> httpRequest = streamsService.submitPost(postData);

        httpRequest.enqueue(new Callback<Object>() {

            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {

                if (response.isSuccessful()) {

                    Snackbar.make(getView(), "Post Added Successfully!!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    getFragmentManager().beginTransaction().remove(postFormFragment).commitAllowingStateLoss();

                    JSONObject json = new JSONObject();

                    try {
                        json.put("mex", "mex");
                    }catch (Exception e){}

                    socket.emit("refresh");

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

            if (getActivity()!=null){
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(context, "wwww", Toast.LENGTH_LONG).show();
                        PostFormFragment.postsListFragment.getAllPosts();
                    }
                });
            }

            // HomeFragment homeFragment = (HomeFragment) getParentFragment();
            //Toast.makeText(getActivity(), homeFragment.TAG, Toast.LENGTH_LONG).show();
        }
    };
}
