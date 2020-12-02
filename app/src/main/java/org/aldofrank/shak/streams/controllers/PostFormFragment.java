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
import org.aldofrank.shak.services.ServiceGenerator;
import org.aldofrank.shak.services.StreamsService;

import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Permette all'utente di pubblicare un post con un'immagine collegata.
 */
public class PostFormFragment extends Fragment implements View.OnClickListener {

    private String token;
    private String imageEncoded;

    private final int spaceOccupiedByTheImage = 850;
    private final int SELECT_PHOTO = 1;
    private int fragmentHeight;

    private ImageView chosenImagePost;
    private FloatingActionButton buttonDeleteImagePost;

    private Fragment postFormFragment;

    private static PostsListFragment postsListFragment;

    private Context context;

    private Uri uri;

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
        imageEncoded = null;

        socket.on("refreshPage", updatePostsList);
        socket.connect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View postFormFragmentView = inflater.inflate(R.layout.fragment_post_form, container, false);
        token = LoggedUserActivity.getToken();
        postFormFragment = this;

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

    /**
     * Consente la pubblicazione di un post ad un'utente autenticato.
     * I dati vengono inseriti in una richiesta http e mandati al server.
     */
    private void submitPost(){
        StreamsService streamsService = ServiceGenerator.createService(StreamsService.class, token);
        JsonObject postData = new JsonObject();
        Call<Object> httpRequest = streamsService.submitPost(postData);

        postData.addProperty("post", postContentField.getText().toString().trim());
        if (imageEncoded != null) {
            postData.addProperty("image", "data:image/png;base64," + imageEncoded);
            Toast.makeText(getActivity(), imageEncoded, Toast.LENGTH_LONG).show();
        }

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
                    postContentField.setText("");
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
        getFragmentManager().beginTransaction().remove(postFormFragment).commitAllowingStateLoss();
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

        ViewGroup.LayoutParams layoutParams = postContentField.getLayoutParams();
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
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
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

                ViewGroup.LayoutParams layoutParams = postContentField.getLayoutParams();
                layoutParams.height = fragmentHeight - spaceOccupiedByTheImage;

                chosenImagePost.setImageBitmap(bitmap);

                chosenImagePost.setVisibility(View.VISIBLE);
                buttonDeleteImagePost.setVisibility(View.VISIBLE);

                postContentField.setLayoutParams(layoutParams);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getActivity(), "prova " + uri.toString(), Toast.LENGTH_LONG).show();
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
