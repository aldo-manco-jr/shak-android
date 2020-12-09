package org.aldofrank.shak.streams.controllers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.github.nkzawa.emitter.Emitter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.aldofrank.shak.R;
import org.aldofrank.shak.services.ServiceGenerator;
import org.aldofrank.shak.services.StreamsService;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Permette all'utente di pubblicare un post con un'immagine collegata.
 */
public class PostFormFragment extends Fragment implements View.OnClickListener {

    private String imageEncoded;

    private Uri uri;
    private final int spaceOccupiedByTheImage = 850;
    private final int SELECT_PHOTO = 1;
    private int fragmentHeight;

    private ImageView chosenImagePost;
    private FloatingActionButton buttonDeleteImagePost;

    private EditText postContentField;

    private FloatingActionButton buttonClosePostForm;
    private FloatingActionButton buttonUploadImagePost;
    private FloatingActionButton buttonSubmitPost;

    public PostFormFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageEncoded = null;

        //LoggedUserActivity.getSocket().on("refreshPage", updatePostsList);
        LoggedUserActivity.getSocket().on("refreshListPosts", updatePostsList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View postFormFragmentView = inflater.inflate(R.layout.fragment_post_form, container, false);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        fragmentHeight = display.getHeight();

        postContentField = postFormFragmentView.findViewById(R.id.post_content_field);
        buttonClosePostForm = postFormFragmentView.findViewById(R.id.fab_close_post_form);
        buttonUploadImagePost = postFormFragmentView.findViewById(R.id.fab_add_post_image);
        buttonSubmitPost = postFormFragmentView.findViewById(R.id.fab_submit_button);

        chosenImagePost = postFormFragmentView.findViewById(R.id.image_chosen);
        buttonDeleteImagePost = postFormFragmentView.findViewById(R.id.fab_delete_image_post);

        buttonSubmitPost.setOnClickListener(this);
        buttonClosePostForm.setOnClickListener(this);
        buttonUploadImagePost.setOnClickListener(this);
        buttonDeleteImagePost.setOnClickListener(this);

        postContentField.addTextChangedListener(checkPostContent);

        return postFormFragmentView;
    }

    TextWatcher checkPostContent = new TextWatcher() {
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
    };

    /**
     * Consente la pubblicazione di un post ad un'utente autenticato.
     * I dati vengono inseriti in una richiesta http e mandati al server.
     */
    private void submitPost(){
        StreamsService streamsService = ServiceGenerator.createService(StreamsService.class, LoggedUserActivity.getToken());

        JsonObject postData = new JsonObject();
        postData.addProperty("post", postContentField.getText().toString().trim());

        if (imageEncoded != null) {
            postData.addProperty("image", "data:image/png;base64," + imageEncoded);
        }

        Call<Object> httpRequest = streamsService.submitPost(postData);

        httpRequest.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    assert getView() != null : "getView() non doveva essere null";
                    assert getFragmentManager() != null : "getFragmentManager() non doveva essere null";

                    Snackbar.make(getView(), "Post Added Successfully!!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    //LoggedUserActivity.getSocket().emit("refresh");
                    LoggedUserActivity.getSocket().emit("refreshPosts");


                    // ripristino stato iniziale del contenitore
                    ConstraintLayout.LayoutParams layoutParams =
                            new ConstraintLayout.LayoutParams(
                                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                                    ConstraintLayout.LayoutParams.MATCH_PARENT
                            );
                    postContentField.setLayoutParams(layoutParams);

                    // il fragment chiude se stesso
                    closePost();
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
    private void closePost(){
        getFragmentManager().beginTransaction().remove(HomeFragment.getHomeFragment().getPostFormFragment()).commitAllowingStateLoss();
        postContentField.setText("");
    }

    /**
     * Cancella un immagine selezionata. L'immagine non sarà più visibile e non verrà caricata sul
     * server con il messaggio.
     */
    private void deleteImagePost(){
        imageEncoded = null;

        chosenImagePost.setVisibility(View.GONE);
        buttonDeleteImagePost.setVisibility(View.GONE);

        chosenImagePost.setImageBitmap(null);

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) postContentField.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        postContentField.setLayoutParams(layoutParams);
    }

    /**
     * Avvia la fase di caricamento di un immagine dalla memoria dell'utente all'applicazione
     */
    private void uploadImagePost(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PHOTO);
    }

    /**
     * @param bitmap un'immagine in formato bitmap
     *
     * @return una stringa che rappresenta un'immagine codificata secondo Base64
     */
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /**
     * L'immagine selezionata viene aggiunta e mostrata all'utente tramite un'anteprima, vengono
     * anche mostrate eventuali azioni eseguibili sulle immagini
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PHOTO && resultCode == getActivity().RESULT_OK
                && data != null && data.getData() != null) {
            uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                imageEncoded = bitmapToBase64(bitmap);


                ConstraintLayout.LayoutParams layoutParams =
                        (ConstraintLayout.LayoutParams) postContentField.getLayoutParams();
                layoutParams.height = fragmentHeight - spaceOccupiedByTheImage;
                postContentField.setLayoutParams(layoutParams);

                chosenImagePost.setImageBitmap(bitmap);

                chosenImagePost.setVisibility(View.VISIBLE);
                buttonDeleteImagePost.setVisibility(View.VISIBLE);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Quando un post viene pubblicato la home page viene aggiornata.
     */
    private Emitter.Listener updatePostsList = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            if (getActivity() != null){
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // quando un post viene pubblicato la socket avvisa del necessario aggiornmento
                        // HomeFragment.getHomeFragment().getStreamsFragment().getAllPosts();
                        HomeFragment.getHomeFragment().getStreamsFragment().getAllNewPosts();
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
                closePost();
                break;
            case R.id.fab_add_post_image:
                uploadImagePost();
                break;
            case R.id.fab_delete_image_post:
                deleteImagePost();
                break;
        }
    }
}
