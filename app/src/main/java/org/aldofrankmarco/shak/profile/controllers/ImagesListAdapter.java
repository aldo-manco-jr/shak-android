package org.aldofrankmarco.shak.profile.controllers;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.github.nkzawa.emitter.Emitter;
import com.google.android.material.snackbar.Snackbar;

import org.aldofrankmarco.shak.R;
import org.aldofrankmarco.shak.models.Image;
import org.aldofrankmarco.shak.models.User;
import org.aldofrankmarco.shak.services.ImagesService;
import org.aldofrankmarco.shak.services.ServiceGenerator;
import org.aldofrankmarco.shak.streams.controllers.LoggedUserActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ImagesListAdapter extends RecyclerView.Adapter<ImagesListAdapter.ImageItemHolder> {

    private List<Image> listImages;

    private String username;

    private final String basicUrlImage = "http://res.cloudinary.com/dfn8llckr/image/upload/v";

    private ImagesService imagesService;

    public ImagesListAdapter(List<Image> listImages, String username) {
        this.listImages = listImages;
        this.username = username;

        imagesService = ServiceGenerator.createService(ImagesService.class, LoggedUserActivity.getToken());
        LoggedUserActivity.getSocket().on("refreshPage", updateImagesList);
    }

    /**
     * Quando un post viene pubblicato la home page viene aggiornata.
     */
    private Emitter.Listener updateImagesList = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            if (LoggedUserActivity.getLoggedUserActivity() != null) {
                LoggedUserActivity.getLoggedUserActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // quando un post viene pubblicato la socket avvisa del necessario aggiornmento
                        LoggedUserActivity.getSocket().on("refreshPage",
                                ProfileFragment.getProfileFragment().getProfileImagesFragment(LoggedUserActivity.getUsernameLoggedUser()).updateUserImagesList);
                    }
                });
            }
        }
    };

    @NonNull
    @Override
    public ImagesListAdapter.ImageItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_image, parent, false);
        ImagesListAdapter.ImageItemHolder viewHolder = new ImagesListAdapter.ImageItemHolder(itemView);

        return viewHolder;
    }

    /**
     * Questo metodo viene eseguito per ogni elemento nella lista, ogni elemento quindi viene
     * processato e aggiunto alla lista.
     */
    @Override
    public void onBindViewHolder(@NonNull final ImagesListAdapter.ImageItemHolder holder, final int position) {
        final Image image = listImages.get(position);

        final String urlImage = this.basicUrlImage + image.getImageVersion() + "/"
                + image.getImageId();

        Glide.with(LoggedUserActivity.getLoggedUserActivity())
                .asBitmap()
                .load(urlImage)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoggedUserActivity.getLoggedUserActivity(), ImageViewerActivity.class);
                intent.putExtra("urlImage", urlImage);
                LoggedUserActivity.getLoggedUserActivity().startActivity(intent);
            }
        });

        if (!username.equals(LoggedUserActivity.getUsernameLoggedUser())){
            holder.setAsCoverImageButton.setVisibility(View.GONE);
            holder.setAsDefaultImageButton.setVisibility(View.GONE);
        }else{
            holder.setAsCoverImageButton.setVisibility(View.VISIBLE);
            holder.setAsDefaultImageButton.setVisibility(View.VISIBLE);

            holder.setAsDefaultImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setAsDefaultImage(listImages.get(position).getImageVersion(), listImages.get(position).getImageId(), holder);
                }
            });

            holder.setAsCoverImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setAsCoverImage(listImages.get(position).getImageVersion(), listImages.get(position).getImageId(), holder);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listImages.size();
    }

    private void setAsDefaultImage(String imageVersion, String imageId, final ImagesListAdapter.ImageItemHolder holder) {
        Call<Object> httpRequest = imagesService.setUserProfilePhoto(imageId, imageVersion);

        httpRequest.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    Snackbar.make(ProfileFragment.getProfileFragment().getView(), "Image Set As Profile Successfully!!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    LoggedUserActivity.getSocket().emit("refresh");
                } else {
                    Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setAsCoverImage(String imageVersion, String imageId, final ImagesListAdapter.ImageItemHolder holder) {
        Call<Object> httpRequest = imagesService.setUserCoverPhoto(imageId, imageVersion);

        httpRequest.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    Snackbar.make(ProfileFragment.getProfileFragment().getView(), "Image Set As Cover Successfully!!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    LoggedUserActivity.getSocket().emit("refresh");
                } else {
                    Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public class ImageItemHolder extends RecyclerView.ViewHolder {

        ImageView image;

        Button setAsDefaultImageButton;
        Button setAsCoverImageButton;

        public ImageItemHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image_user);
            setAsCoverImageButton = itemView.findViewById(R.id.button_set_as_cover_image);
            setAsDefaultImageButton = itemView.findViewById(R.id.button_set_as_profile_image);
        }
    }
}
