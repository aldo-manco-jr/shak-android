package org.aldofrank.shak.streams.controllers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.aldofrank.shak.R;
import org.aldofrank.shak.models.Post;
import org.aldofrank.shak.profile.controllers.ProfileFragment;
import org.aldofrank.shak.services.ServiceGenerator;
import org.aldofrank.shak.services.StreamsService;
import org.aldofrank.shak.streams.http.AddCommentRequest;

import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.aldofrank.shak.streams.controllers.CommentsListAdapter.postId;

public class CommentFormFragment extends Fragment implements View.OnClickListener {

    private EditText commentContentField;

    private FloatingActionButton buttonCloseCommentForm;
    private FloatingActionButton buttonSubmitComment;

    public CommentFormFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LoggedUserActivity.getSocket().on("refreshPage", updateCommentsList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View postFormFragmentView = inflater.inflate(R.layout.fragment_comment_form, container, false);

        commentContentField = postFormFragmentView.findViewById(R.id.comment_content_field);
        buttonCloseCommentForm = postFormFragmentView.findViewById(R.id.fab_close_comment_form);
        buttonSubmitComment = postFormFragmentView.findViewById(R.id.fab_submit_comment_button);

        buttonSubmitComment.setOnClickListener(this);
        buttonCloseCommentForm.setOnClickListener(this);

        commentContentField.addTextChangedListener(checkCommentContent);

        return postFormFragmentView;
    }

    TextWatcher checkCommentContent = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.toString().isEmpty()){
                // quando il campo di testo del post è vuoto
                buttonSubmitComment.setVisibility(View.GONE);
            }else {
                buttonSubmitComment.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    public static CommentFormFragment newInstance(String type) {

        CommentFormFragment fragment = new CommentFormFragment();

        Bundle args = new Bundle();
        args.putString("type", type);
        fragment.setArguments(args);

        return fragment;
    }

    private void submitComment(){

        StreamsService streamsService = ServiceGenerator.createService(StreamsService.class, LoggedUserActivity.getToken());

        AddCommentRequest addCommentRequest = new AddCommentRequest(postId, commentContentField.getText().toString().trim());

        Call<Object> httpRequest = streamsService.submitComment(addCommentRequest);

        httpRequest.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    assert getView() != null : "getView() non doveva essere null";
                    assert getFragmentManager() != null : "getFragmentManager() non doveva essere null";

                    Snackbar.make(getView(), "Comment Added Successfully!!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    // il fragment chiude se stesso
                    closeCommentBox();

                    LoggedUserActivity.getSocket().emit("refresh");
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

    /**
     * Il post viene chiuso e le informazioni scritte dall'utente vengono cancellate
     */
    private void closeCommentBox(){

        if (getArguments().getString("type").equals("home")){
            getFragmentManager().beginTransaction().replace(R.id.home_fragment, HomeFragment.getHomeFragment().getCommentsListFragment()).addToBackStack("backCommentsListFragment").commit();
        }else if (getArguments().getString("type").equals("profile")){
            getFragmentManager().beginTransaction().replace(R.id.profile_fragment, ProfileFragment.getProfileFragment().getCommentsListFragment()).commit();
        }

        commentContentField.setText("");
        getFragmentManager().beginTransaction().remove(HomeFragment.getHomeFragment().getCommentFormFragment()).commitAllowingStateLoss();
    }

    /**
     * Quando un post viene pubblicato la home page viene aggiornata.
     */
    private Emitter.Listener updateCommentsList = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            if (getActivity() != null){
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // quando un post viene pubblicato la socket avvisa del necessario aggiornamento
                        if (getArguments().getString("type").equals("home")){
                            HomeFragment.getHomeFragment().getCommentsListFragment().getAllPostComments();
                        }else if (getArguments().getString("type").equals("profile")){
                            ProfileFragment.getProfileFragment().getCommentsListFragment().getAllPostComments();
                        }
                    }
                });
            }
        }
    };

    @Override
    public void onClick(View view) {
        int idButtonPressed = view.getId();

        switch (idButtonPressed){
            case R.id.fab_submit_comment_button:
                submitComment();
                break;
            case R.id.fab_close_comment_form:
                closeCommentBox();
                break;
        }
    }
}
