package org.aldofrank.shak.authentication.controllers;

public class mancanti {
/*
package org.aldofrank.shak.authentication.controllers;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.aldofrank.shak.R;
import org.aldofrank.shak.authentication.http.LoginRequest;
import org.aldofrank.shak.authentication.http.LoginResponse;
import org.aldofrank.shak.models.Post;
import org.aldofrank.shak.services.AuthenticationService;
import org.aldofrank.shak.services.ServiceGenerator;
import org.aldofrank.shak.services.StreamsService;
import org.aldofrank.shak.streams.http.posts.GetPostsListResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.POST;

public class LoginFragment extends Fragment {

    AuthenticationService authService = ServiceGenerator.createService(AuthenticationService.class);

    EditText usernameField;
    EditText passwordField;

    ProgressBar loadingBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private static String token;

    private void login() {

        LoginRequest loginRequest = new LoginRequest(usernameField.getText().toString().trim(), passwordField.getText().toString().trim());
        /////Toast.makeText(getActivity(), loginRequest.getUsername() + " " + loginRequest.getPassword(), Toast.LENGTH_LONG).show();

        Call<LoginResponse> call = authService.login(loginRequest);

        loadingBar.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call, final Response<LoginResponse> response) {

                if (response.isSuccessful()) {
                    /////Toast.makeText(getActivity(), response.body().getUserFound().getEmail(), Toast.LENGTH_LONG).show();
                    token = "bearer " + response.body().getToken();
                    loadingBar.setVisibility(View.GONE);

                    StreamsService streamsService = ServiceGenerator.createService(StreamsService.class, token);

                    /////Toast.makeText(getActivity(), token, Toast.LENGTH_LONG).show();

                    Call<GetPostsListResponse> httpRequest = streamsService.getAllPosts();

                    httpRequest.enqueue(new Callback<GetPostsListResponse>() {

                        @Override
                        public void onResponse(Call<GetPostsListResponse> call, Response<GetPostsListResponse> response) {

                            if (response.isSuccessful()){

                                //vuole che li si passi una jsonstring
                                //getAsJsonObject
                                String jsonSring = response.body().getArrayPosts().toString();
                                Toast.makeText(getActivity(), jsonSring, Toast.LENGTH_LONG).show();
                                //per traformare serializzato un oggetto in json: new Gson().toJson(obj)
                                //per traformare serializzato un json in oggetto: new Gson().fromJson(jsonSring, Post.class);

                                //TODO è LA SOLUZIONE, IMPLEMENTARE QUESTA
                                Gson gson = new Gson();
                                Type userListType = new TypeToken<ArrayList<Post>>(){}.getType();
                                ArrayList<Post> postArray = gson.fromJson(jsonSring, userListType);

                                Post firstPost = postArray.get(0);
                                Toast.makeText(getActivity(), firstPost.getUserId(), Toast.LENGTH_LONG).show();
                                ArrayList<Post.Comment> userComments = firstPost.getArrayComments();//.get(0).getCommentContent();
                                Toast.makeText(getActivity(), firstPost.getCreatedAt(), Toast.LENGTH_LONG).show();
                                Toast.makeText(getActivity(), userComments.get(0).getCommentContent(), Toast.LENGTH_LONG).show();
                                //Toast.makeText(getActivity(), response.body().getArrayPosts().get(0).getPostContent(), Toast.LENGTH_LONG).show();
                                GetPostsListResponse body = response.body();

                                //JSONObject userData = null;
                                //JSONObject comment = null;
                                //JSONArray comments = null;
                                JsonObject userData = null;
                                JsonArray comments = null;

                                String name = null;
                                String titolo = null;
                                String commentText = null;

                                //Toast.makeText(getActivity(), body.getArrayPosts().toString(), Toast.LENGTH_LONG).show();
                                userData = body.getArrayPosts().get(0).getAsJsonObject();

                                // prendo e stampo l'id
                                String _id = userData.get("_id").getAsString();
                                Toast.makeText(getActivity(), _id, Toast.LENGTH_LONG).show();

                                comments = userData.get("comments").getAsJsonArray();
                                for (int i = 0; i < comments.size(); i++) {
                                    JsonObject comment = comments.get(i).getAsJsonObject();
                                    commentText = comment.get("comment_text").getAsString();

                                    Toast.makeText(getActivity(), commentText, Toast.LENGTH_LONG).show();
                                }

                                //l'array list serve per creare in android i vari commenti, perchè ogni molemmo di Post si moltiplica in base al contenuto dell'array
                                ArrayList<Post> posts = new ArrayList<>();

                                Post post = new Post();
                                comments = userData.get("comments").getAsJsonArray();
                                for (int i = 0; i < comments.size(); i++) {
                                    JsonObject comment = comments.get(i).getAsJsonObject();
                                    String userId = comment.get("user_id").getAsString();
                                    String username = comment.get("username").getAsString();
                                    commentText = comment.get("comment_text").getAsString();
                                    String createdAt = comment.get("created_at").getAsString();

                                    //TODO l'errore è questa riga, ma non è questa classe, ma la classe Post
                                    //post.addComment(userId, username, commentText, createdAt);

                                    Toast.makeText(getActivity(), createdAt, Toast.LENGTH_LONG).show();
                                }

                                posts.add(post);


                                /*
                                try {
                                    // passo il json che contiene i dati utente ottenuto dal Post

                                    ////****userDataa = new JsonObject();
                                    //userDataa.getAsJsonArray(body.getArrayPosts().toString());
                                    userDataa = body.getArrayPosts();

                                    userDataa.get("comments").getAsJsonArray().get(0);

                                    JsonArray commentsa = null;
                                    commentsa = userDataa.get("comments").getAsJsonArray();
                                    for (int i = 0; i < commentsa.size(); i++) {
                                    }********\/
                                    //Toast.makeText(getActivity(), body.getArrayPosts().toString(), Toast.LENGTH_LONG).show();
                                    JsonObject userDatab = body.getArrayPosts().get(0).getAsJsonObject();
                                    String _id = userDatab.get("_id").getAsString();
                                    Toast.makeText(getActivity(), _id, Toast.LENGTH_LONG).show();

                                    JsonArray commentss = userDatab.get("comments").getAsJsonArray();
                                    for (int i = 0; i < commentss.size(); i++) {
                                        JsonObject commento = commentss.get(i).getAsJsonObject();
                                        commentText = commento.get("comment_text").getAsString();

                                        Toast.makeText(getActivity(), commentText, Toast.LENGTH_LONG).show();
                                    }

                                    userData  = new JSONObject(body.getArrayPosts().toString());
                                    name = userData.getString("_id");

                                    //JSONObject comments = _jObject.getJSONArray("comments");
                                    //titolo = comments.getString("comment_text");
                                    //titolo = _jObject.getString("comments");
                                    comments = new JSONArray(userData.getString("comments"));
                                    String stringComments = comments.toString();
                                    Toast.makeText(getActivity(), stringComments, Toast.LENGTH_LONG).show();
                                    for (int i = 0; i < comments.length(); i++) {
                                        comment = new JSONObject(
                                                new JSONArray(userData.getString("comments")).get(i).toString()
                                        );
                                        commentText = comment.getString("comment_text");

                                        Toast.makeText(getActivity(), commentText, Toast.LENGTH_LONG).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
*\/
    // Toast.makeText(getActivity(), commentText, Toast.LENGTH_LONG).show();

    //ArrayList<Post> arrayPosts = like.getFavouritePosts();
    //Toast.makeText(getActivity(), arrayPosts.toString(), Toast.LENGTH_LONG).show();
    //.getMessage(), Toast.LENGTH_LONG).show();
}else {
        //Toast.makeText(getActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_LONG).show();
        }
        }

@Override
public void onFailure(Call<GetPostsListResponse> call, Throwable t) {
        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
        }
        });

        } else {
        // errore a livello di applicazione
        // response.code() == (401) -> token expired
        Toast.makeText(getActivity(), response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
        loadingBar.setVisibility(View.GONE);
        }
        }

@Override
public void onFailure(Call<LoginResponse> call, Throwable t) {
        // errore a livello di rete
        // network error, establishing connection with server, error creating http request, response
        // when there is an exception
        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
        loadingBar.setVisibility(View.GONE);
        }
        });
        }

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        Button loginButton = view.findViewById(R.id.loginButton);
        usernameField = view.findViewById(R.id.usernameField);
        passwordField = view.findViewById(R.id.passwordField);
        loadingBar = getActivity().findViewById(R.id.loadingBar);

        loginButton.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
        login();
        }
        });

        passwordField.setOnTouchListener(new View.OnTouchListener() {
@Override
public boolean onTouch(View v, MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
        if (event.getRawX() >= passwordField.getWidth() - 32) {

        if (passwordField.getTransformationMethod() == null) {
        passwordField.setTransformationMethod(new PasswordTransformationMethod());
        passwordField.setCompoundDrawablesWithIntrinsicBounds(R.drawable.password_drawable_left, 0, R.drawable.eye_open_drawable_right, 0);
        return true;
        } else {
        passwordField.setTransformationMethod(null);
        passwordField.setCompoundDrawablesWithIntrinsicBounds(R.drawable.password_drawable_left, 0, R.drawable.eye_closed_drawable_right, 0);
        return true;
        }
        }
        }
        return false;
        }
        });

        // Inflate the layout for this fragment
        return view;
        }
        }





        package org.aldofrank.shak.models;

        import com.google.gson.JsonObject;
        import com.google.gson.annotations.SerializedName;

        import java.util.ArrayList;

public class Post {

    @SerializedName("user_id")
    private JsonObject userId;

    @SerializedName("username")
    private String usernamePublisher;

    @SerializedName("post")
    private String postContent;

    @SerializedName("imageVersion")
    private String imageVersion;

    @SerializedName("imageId")
    private String imageId;

    @SerializedName("comments")
    public ArrayList<Comment> arrayComments;

    @SerializedName("total_likes")
    private int totalLikes;

    @SerializedName("likes")
    private ArrayList<Like> arrayLikes;

    @SerializedName("created_at")
    private String createdAt;

    public String getUserId() {
        return userId.get("_id").getAsString();
    }

    public String getUsernamePublisher() {
        return usernamePublisher;
    }

    public String getPostContent() {
        return postContent;
    }

    public String getImageVersion() {
        return imageVersion;
    }

    public String getImageId() {
        return imageId;
    }

    public ArrayList<Comment> getArrayComments() {
        return arrayComments;
    }

    public int getTotalLikes() {
        return totalLikes;
    }

    public ArrayList<Like> getArrayLikes() {
        return arrayLikes;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public class Comment{

        @SerializedName("user_id")
        private String userId;

        @SerializedName("username")
        private String usernamePublisher;

        @SerializedName("comment_text")
        private String commentContent;

        @SerializedName("created_at")
        private String createdAt;

        public String getUserId() {
            return userId;
        }

        public String getUsernamePublisher() {
            return usernamePublisher;
        }

        public String getCommentContent() {
            return commentContent;
        }

        public String getCreatedAt() {
            return createdAt;
        }
    }

    class Like{

        @SerializedName("username")
        private String usernamePublisher;

        public String getUsernamePublisher() {
            return usernamePublisher;
        }
    }
}


package org.aldofrank.shak.streams.http.posts;

        import android.widget.Toast;

        import com.google.gson.Gson;
        import com.google.gson.JsonArray;
        import com.google.gson.JsonElement;
        import com.google.gson.JsonObject;
        import com.google.gson.annotations.SerializedName;

        import org.aldofrank.shak.models.Post;
        import org.json.JSONObject;

        import java.util.ArrayList;

        import retrofit2.http.POST;

public class GetPostsListResponse {

    @SerializedName("message")
    String message;

    @SerializedName("allPosts")
    //List<Post> arrayPosts;
            JsonArray arrayPosts;

    @SerializedName("top")
    JsonArray favouritePosts;

    public String getMessage() {
        return message;
    }

    //public ArrayList<Post> getArrayPosts() {
    public JsonArray getArrayPosts() {
        return arrayPosts;//.get(0).getAsJsonObject();
    }

    public JsonArray getFavouritePosts() {
        return favouritePosts;
    }
}
* */

}
