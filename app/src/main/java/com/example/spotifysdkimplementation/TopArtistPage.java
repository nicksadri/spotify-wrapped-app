package com.example.spotifysdkimplementation;

import static android.content.ContentValues.TAG;
import static com.example.spotifysdkimplementation.MainActivity.AUTH_TOKEN_REQUEST_CODE;
import static com.example.spotifysdkimplementation.MainActivity.CLIENT_ID;
import static com.example.spotifysdkimplementation.MainActivity.getRedirectUri;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TopArtistPage extends AppCompatActivity {

    private Button artistNext;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private Call mCall;
    private FirebaseAuth mAuth;

    private TextView artist1, artist1Name, artist2, artist2Name, artist3, artist3Name, artist4, artist4Name, artist5, artist5Name;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_artist_page);

        artistNext = findViewById(R.id.artistsNextButton);

        artist1Name = findViewById(R.id.artistName1);
        artist2Name = findViewById(R.id.artistName2);
        artist3Name = findViewById(R.id.artistName3);
        artist4Name = findViewById(R.id.artistName4);
        artist5Name = findViewById(R.id.artistName5);

        artistNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopArtistPage.this, TopTrackPage.class);
                startActivity(intent);
            }
        });
        setToken();

//        String imageUrl = "https://images.app.goo.gl/wLbn9HxxC61bpLRq8";
//
//        ImageView imageView = findViewById(R.id.artist1);
//
//        // Using Glide library to load and display the image
//        Glide.with(this)
//                .load(imageUrl)
//                .into(imageView);

    }

    /**
     * Get top artist data for specific user
     */
    public void onGetUserTopArtist() {
        if (MainActivity.mAccessToken == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        final Request topArtistRequest = new Request.Builder()
                //replace id for testing
                .url("https://api.spotify.com/v1/me/top/artists")
                .addHeader("Authorization", "Bearer " + MainActivity.mAccessToken)
                .build();
        cancelCall();
        mCall = mOkHttpClient.newCall(topArtistRequest);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch top artists data: " + e);
                Toast.makeText(TopArtistPage.this, "Failed to fetch top artists data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse top artists data: " + e);
                    Toast.makeText(TopArtistPage.this, "Failed to parse top artists data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    public List<String> test() {
        List<String> topArtistsList = new ArrayList<>();

        if (MainActivity.mAccessToken == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return topArtistsList;
        }
        final Request topArtistRequest = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/artists")
                .addHeader("Authorization", "Bearer " + MainActivity.mAccessToken)
                .build();
        cancelCall();

        mCall = mOkHttpClient.newCall(topArtistRequest);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch top artists data: " + e);
                Toast.makeText(TopArtistPage.this, "Failed to fetch top artists data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    Log.d("Successful:", "response sent");

                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray artistsArray = jsonObject.getJSONArray("items");

                    for (int i = 0; i < 5; i++) {
                        JSONObject artist = artistsArray.getJSONObject(i);
                        String artistName = artist.getString("name");
                        topArtistsList.add(artistName);
                    }
                    Log.d("Artist 1", topArtistsList.get(0));
                    setTextAsync(topArtistsList.get(0), artist1Name);
                    setTextAsync(topArtistsList.get(1), artist2Name);
                    setTextAsync(topArtistsList.get(2), artist3Name);
                    setTextAsync(topArtistsList.get(3), artist4Name);
                    setTextAsync(topArtistsList.get(4), artist5Name);
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse top artists data: " + e);
                    Toast.makeText(TopArtistPage.this, "Failed to parse top artists data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        return topArtistsList;
    }


    public void setToken() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(TopArtistPage.this, AUTH_TOKEN_REQUEST_CODE, request);
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
     * When the app leaves this activity to momentarily get a token/code, this function
     * fetches the result of that external activity to get the response from Spotify
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            Log.d("Top artist page token", response.getAccessToken());
            MainActivity.mAccessToken = response.getAccessToken();

            db.collection("users")
                    .whereEqualTo("user_id", MainActivity.currentUserID)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Get the reference to the document
                            DocumentReference userRef = documentSnapshot.getReference();
                            // Now update the auth_code field for this user document
                            userRef.update("api_token", response.getAccessToken())
                                    .addOnSuccessListener(aVoid -> Log.d(TAG, "api token updated successfully"))
                                    .addOnFailureListener(e -> Log.w(TAG, "Error updating api token", e));
                        }
                    })
                    .addOnFailureListener(e -> Log.w(TAG, "Error searching for user by user ID", e));
        }
        test();
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

}
