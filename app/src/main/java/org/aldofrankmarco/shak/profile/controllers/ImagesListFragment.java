package org.aldofrankmarco.shak.profile.controllers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ProxyInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nkzawa.emitter.Emitter;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.aldofrankmarco.shak.R;
import org.aldofrankmarco.shak.models.Image;
import org.aldofrankmarco.shak.profile.http.ImagesResponse;
import org.aldofrankmarco.shak.profile.http.GetImagesListResponse;
import org.aldofrankmarco.shak.streams.controllers.LoggedUserActivity;

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
public class ImagesListFragment extends Fragment {

    private List<Image> listImages;

    private View view;

    private String username;

    private String imageEncoded;

    private Uri uri;
    private final int SELECT_PHOTO = 1;
    private final int TAKE_PHOTO = 100;

    private ImagesListAdapter adapter;
    private RecyclerView recyclerView;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // recyclerview = dynamic list view
        // glide = image loading framework that fetch, decode, display video, images and GIF
        // circleimageview = wrap images in a circle
        view = inflater.inflate(R.layout.fragment_images_list, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getAllUserImages();
    }

    /**
     * Avvia la fase di caricamento di un immagine dalla memoria dell'utente all'applicazione
     */
    protected void uploadUserImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PHOTO);
    }

    protected void takeUserImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    /**
     * @param bitmap un'immagine in formato bitmap
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(requestCode == TAKE_PHOTO){
            try {
                Bitmap photoTaken = (Bitmap) data.getExtras().get("data");
                imageEncoded = bitmapToBase64(photoTaken);
                addUserImage(imageEncoded);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addUserImage(String imageEncoded) {

        if (imageEncoded == null) {
            return;
        }

        JsonObject imageData = new JsonObject();
        imageData.addProperty("image", "data:image/png;base64," + imageEncoded);

        Call<ImagesResponse> httpRequest = LoggedUserActivity.getImagesService().uploadImage(imageData);

        httpRequest.enqueue(new Callback<ImagesResponse>() {
            @Override
            public void onResponse(Call<ImagesResponse> call, Response<ImagesResponse> response) {
                if (response.isSuccessful()) {
                    assert getView() != null : "getView() non doveva essere null";
                    assert getFragmentManager() != null : "getFragmentManager() non doveva essere null";

                    Snackbar.make(getView(), "Image Added Successfully!!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    Image uploadedImage = new Image(response.body().getImageId(), response.body().getImageVersion());

                    listImages.add(0, uploadedImage);
                    adapter.notifyItemInserted(0);

                } else {
                    Toast.makeText(getActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ImagesResponse> call, Throwable t) {
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

        username = getArguments().getString("username");

        Call<GetImagesListResponse> httpRequest = LoggedUserActivity.getImagesService().getAllUserImages(username);

        httpRequest.enqueue(new Callback<GetImagesListResponse>() {
            @Override
            public void onResponse(Call<GetImagesListResponse> call, Response<GetImagesListResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null : "body() non doveva essere null";

                    listImages = response.body().getImageList();
                    initializeRecyclerView();
                } else {
                    Toast.makeText(getActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GetImagesListResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Viene collegata la recycler view con l'adapter
     */
    private void initializeRecyclerView() {
        recyclerView = view.findViewById(R.id.listImages);
        adapter = new ImagesListAdapter(this.listImages, this.username);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(LoggedUserActivity.getLoggedUserActivity(), 2));
    }
}