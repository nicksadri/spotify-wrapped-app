package com.example.spotifysdkimplementation;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String CLIENT_ID = "d857953ebaca4345b0be666d38a4037c";
    public static final String REDIRECT_URI = "spotifysdkimplementation://auth";

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken, mAccessCode;
    private Call mCall;

    private TextView tokenTextView, codeTextView, profileTextView, topArtistsTextView, topTracksTextView, createAccountTextView;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    public void onStart() {
        super.onStart();
        String email = "advaybalak@gmail.com";
        String password = "spotifywrapped";
        setContentView(R.layout.login_page);

//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, task -> {
//                    if (task.isSuccessful()) {
//                        // Sign in success, update UI with the signed-in user's information
//                        Log.d(TAG, "createUserWithEmail:success");
//                        FirebaseUser user = mAuth.getCurrentUser();
//                    } else {
//                        // If sign in fails, display a message to the user.
//                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                        Toast.makeText(MainActivity.this, "Authentication failed.",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success");
                    Toast.makeText(MainActivity.this, "User Signed In",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(MainActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            });

        FirebaseUser currentUser = mAuth.getCurrentUser();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        // Initialize the views
//        tokenTextView = (TextView) findViewById(R.id.token_text_view);
//        codeTextView = (TextView) findViewById(R.id.code_text_view);
//        profileTextView = (TextView) findViewById(R.id.profile_text_view);
//        topArtistsTextView = (TextView) findViewById(R.id.top_artist_text_view);
//        topTracksTextView = (TextView) findViewById(R.id.top_track_text_view);
//
//        // Initialize the buttons
//        Button tokenBtn = (Button) findViewById(R.id.token_btn);
//        Button codeBtn = (Button) findViewById(R.id.code_btn);
//        Button profileBtn = (Button) findViewById(R.id.profile_btn);
//        Button topArtistButton = (Button) findViewById(R.id.top_artist_btn);
////        Button topTrackButton = (Button) findViewById(R.id.top_track_btn);
        Button logInButton = (Button) findViewById(R.id.button_prim);
        createAccountTextView = (TextView) findViewById(R.id.don_t_have_);

        // Set the click listeners for the buttons

        createAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

//        tokenBtn.setOnClickListener((v) -> {
//            getToken();
//        });
//
//        codeBtn.setOnClickListener((v) -> {
//            getCode();
//        });
//
//        profileBtn.setOnClickListener((v) -> {
//            onGetUserDataClicked();
//        });
//
//        topArtistButton.setOnClickListener((v) -> {
//            onGetTopArtistDataClicked();
//        });
//
//        topTrackButton.setOnClickListener((v) -> {
//            onGetTopTrackDataClicked();




        mAuth = FirebaseAuth.getInstance();


    }

    /**
     * Get token from Spotify
     * This method will open the Spotify login activity and get the token
     * What is token?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getToken() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(MainActivity.this, AUTH_TOKEN_REQUEST_CODE, request);

    }

    /**
     * Get code from Spotify
     * This method will open the Spotify login activity and get the code
     * What is code?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getCode() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(MainActivity.this, AUTH_CODE_REQUEST_CODE, request);
    }


    /**
     * When the app leaves this activity to momentarily get a token/code, this function
     * fetches the result of that external activity to get the response from Spotify
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        // Check which request code is present (if any)
        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();
            System.out.println("access token: " + mAccessToken);
            FirebaseUser currentUser = mAuth.getCurrentUser();

            // creating a user entry
            Map<String, Object> user = new HashMap<>();
            assert currentUser != null;
            user.put("email", currentUser.getEmail());
            user.put("api token", mAccessToken);
            user.put("user_id", currentUser.getUid());

            db.collection("users")
                    .add(user)
                    .addOnSuccessListener(
                            documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: "
                                    + documentReference.getId()))
                    .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));

            setTextAsync(mAccessToken, tokenTextView);


        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.getCode();
            setTextAsync(mAccessCode, codeTextView);
        }
    }

    /**
     * Get user data clicked
     * This method will get the user data using the token
     * Data includes the user's profile data
     */
    public void onGetUserDataClicked() {
        if (mAccessToken == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a request to get the user profile
        final Request profileRequest = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();






        cancelCall();
        mCall = mOkHttpClient.newCall(profileRequest);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(MainActivity.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    setTextAsync(jsonObject.toString(3), profileTextView);
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    Toast.makeText(MainActivity.this, "Failed to parse data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });




    }

    /**
     * Get top artist data for specific user
     */
    public void onGetTopArtistDataClicked() {
        if (mAccessToken == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        final Request topArtistRequest = new Request.Builder()
                //replace id for testing
                .url("https://api.spotify.com/v1/me/top/artists")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();
        cancelCall();
        mCall = mOkHttpClient.newCall(topArtistRequest);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch top artists data: " + e);
                Toast.makeText(MainActivity.this, "Failed to fetch top artists data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    setTextAsync(jsonObject.toString(3), profileTextView);
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse top artists data: " + e);
                    Toast.makeText(MainActivity.this, "Failed to parse top artists data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**
     * Get top track data for specific user
     */

    public void onGetTopTrackDataClicked() {
        if (mAccessToken == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }
        final Request topTrackRequest = new Request.Builder()
                //replace playlist_id for testing
                .url("https://api.spotify.com/v1/me/top/tracks")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();
        cancelCall();
        // Request for top tracks
        mCall = mOkHttpClient.newCall(topTrackRequest);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch top tracks data: " + e);
                Toast.makeText(MainActivity.this, "Failed to fetch top tracks data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    setTextAsync(jsonObject.toString(3), profileTextView);
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse top tracks data: " + e);
                    Toast.makeText(MainActivity.this, "Failed to parse top tracks data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Creates a UI thread to update a TextView in the background
     * Reduces UI latency and makes the system perform more consistently
     *
     * @param text the text to set
     * @param textView TextView object to update
     */
    private void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }

    /**
     * Get authentication request
     *
     * @param type the type of the request
     * @return the authentication request
     */
    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[] { "user-read-email", "user-top-read" }) // <--- Change the scope of your requested token here
                .setCampaign("your-campaign-token")
                .build();
    }

    /**
     * Gets the redirect Uri for Spotify
     *
     * @return redirect Uri object
     */
    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }



    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        cancelCall();
        super.onDestroy();
    }
}