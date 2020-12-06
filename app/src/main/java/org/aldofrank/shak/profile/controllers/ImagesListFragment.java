package org.aldofrank.shak.profile.controllers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.aldofrank.shak.R;
import org.aldofrank.shak.models.User;
import org.aldofrank.shak.people.http.GetUserByUsernameResponse;
import org.aldofrank.shak.services.ImagesService;
import org.aldofrank.shak.services.ServiceGenerator;
import org.aldofrank.shak.services.StreamsService;
import org.aldofrank.shak.services.UsersService;
import org.aldofrank.shak.streams.controllers.LoggedUserActivity;

import java.io.ByteArrayOutputStream;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImagesListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImagesListFragment extends Fragment implements View.OnClickListener {

    private List<User.Image> listImages;

    private View view;

    private FloatingActionButton addUserImageButton;

    private String imageEncoded;

    private Uri uri;
    private final int spaceOccupiedByTheImage = 850;
    private final int SELECT_PHOTO = 1;

    private ImageView chosenImagePost;

    public ImagesListFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ImagesListFragment.
     */
    public static ImagesListFragment newInstance(String username) {

        ImagesListFragment fragment = new ImagesListFragment();

        Bundle args = new Bundle();
        args.putString("username", username);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LoggedUserActivity.getSocket().on("refreshPage", updateUserImagesList);
    }

    Emitter.Listener updateUserImagesList = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getAllUserImages();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // recyclerview = dynamic list view
        // glide = image loading framework that fetch, decode, display video, images and GIF
        // circleimageview = wrap images in a circle
        view = inflater.inflate(R.layout.fragment_images_list, container, false);

        addUserImageButton = view.findViewById(R.id.fab_add_user_image);
        addUserImageButton.setOnClickListener(this);

        getAllUserImages();

        return view;
    }

    @Override
    public void onClick(View view) {

        switch(view.getId()){
            case R.id.fab_add_user_image:
                uploadUserImage();
                break;
        }
    }


    /**
     * Avvia la fase di caricamento di un immagine dalla memoria dell'utente all'applicazione
     */
    private void uploadUserImage(){
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
                addUserImage(imageEncoded);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void addUserImage(String imageEncoded){

        ImagesService imagesService = ServiceGenerator.createService(ImagesService.class, LoggedUserActivity.getToken());

        if (imageEncoded == null) {
            return;
        }

        Call<Object> httpRequest = imagesService.uploadImage("data:image/png;base64," + imageEncoded);

        httpRequest.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    assert getView() != null : "getView() non doveva essere null";
                    assert getFragmentManager() != null : "getFragmentManager() non doveva essere null";

                    Snackbar.make(getView(), "Image Added Successfully!!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    LoggedUserActivity.getSocket().emit("refresh");

                    Toast.makeText(getActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
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
     * Consente di recuperare tutti i Image:
     * - streams: lista dei Image dell'utente e dei suoi following
     * - favorites: sono i Image a cui l'utente ha espresso la preferenza
     * Viene mandata una richiesta http per recuperati i dal server.
     */
    public void getAllUserImages() {

        if (getArguments() == null) {
            return;
        }

        final String username = getArguments().getString("username");
        UsersService usersService = ServiceGenerator.createService(UsersService.class, LoggedUserActivity.getToken());

        Call<GetUserByUsernameResponse> httpRequest = usersService.getUserByUsername(username);

        httpRequest.enqueue(new Callback<GetUserByUsernameResponse>() {
            @Override
            public void onResponse(Call<GetUserByUsernameResponse> call, Response<GetUserByUsernameResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null : "body() non doveva essere null";

                    listImages = response.body().getUserFoundByUsername().getArrayImages();
                    initializeRecyclerView();
                } else {
                    Toast.makeText(getActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GetUserByUsernameResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Viene collegata la recycler view con l'adapter
     */
    private void initializeRecyclerView() {
        RecyclerView recyclerView = view.findViewById(R.id.listImages);
        ImagesListAdapter adapter = new ImagesListAdapter(this.listImages);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(LoggedUserActivity.getLoggedUserActivity(), 2));
    }
}