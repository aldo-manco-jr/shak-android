package org.aldofrankmarco.shak.streams.controllers;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.aldofrankmarco.shak.R;
import org.aldofrankmarco.shak.services.ServiceGenerator;
import org.aldofrankmarco.shak.services.StreamsService;
import org.aldofrankmarco.shak.streams.http.AddCommentRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.aldofrankmarco.shak.streams.controllers.CommentsListAdapter.postId;

public class CommentFormFragment extends Fragment implements View.OnClickListener, OnBackPressed {

    private EditText commentContentField;

    private FloatingActionButton buttonCloseCommentForm;
    private FloatingActionButton buttonSubmitComment;

    public CommentFormFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LoggedUserActivity.getSocket().on("refreshPage", CommentsListFragment.updatePostCommentsList);
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
            if (charSequence.toString().isEmpty()) {
                // quando il campo di testo del post è vuoto
                buttonSubmitComment.setVisibility(View.GONE);
            } else {
                buttonSubmitComment.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private void submitComment() {
        AddCommentRequest addCommentRequest = new AddCommentRequest(postId, commentContentField.getText().toString().trim());

        Call<Object> httpRequest = LoggedUserActivity.getStreamsService().submitComment(addCommentRequest);

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

                    //LoggedUserActivity.getSocket().emit("refresh");
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
    private void closeCommentBox() {

        LoggedUserActivity.getLoggedUserActivity().changeFragment(LoggedUserActivity.getLoggedUserActivity().getCommentsListFragment());

        commentContentField.setText("");
        //getFragmentManager().beginTransaction().remove(HomeFragment.getHomeFragment().getCommentFormFragment()).commitAllowingStateLoss();
    }

    @Override
    public void onClick(View view) {
        int idButtonPressed = view.getId();

        switch (idButtonPressed) {
            case R.id.fab_submit_comment_button:
                submitComment();
                break;
            case R.id.fab_close_comment_form:
                closeCommentBox();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        closeCommentBox();
    }
}