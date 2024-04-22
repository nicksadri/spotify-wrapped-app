package com.example.spotifysdkimplementation;

import static android.content.ContentValues.TAG;
import static com.example.spotifysdkimplementation.MainActivity.AUTH_TOKEN_REQUEST_CODE;
import static com.example.spotifysdkimplementation.MainActivity.CLIENT_ID;
import static com.example.spotifysdkimplementation.MainActivity.currentUserID;
import static com.example.spotifysdkimplementation.MainActivity.getRedirectUri;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TopArtistPage extends AppCompatActivity {

    private Button artistNext, recomendationsButton;

    private String stringURLEndPoint = "https://api.openai.com/v1/chat/completions";
    private String stringAPIKey = "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";

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private Call mCall;
    private FirebaseAuth mAuth;

    private TextView artist1Name, artist2Name, artist3Name, artist4Name, artist5Name;

    private ImageView imageView1, imageView2, imageView3, imageView4, imageView5;
    private static String topArtistChat;
    private List<String> topArtistsRecommend;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final ArrayList<String> previousTopArtists = new ArrayList<>();;


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

        imageView1 = findViewById(R.id.artistImage1);
        imageView2 = findViewById(R.id.artistImage2);
        imageView3 = findViewById(R.id.artistImage3);
        imageView4 = findViewById(R.id.artistImage4);
        imageView5 = findViewById(R.id.artistImage5);

        recomendationsButton = findViewById(R.id.artistsButton);

        recomendationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Trigger the ChatGPT functionality
                buttonChatGPT(v);
            }
        });

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

    public List<String> getArtistsAsList() {
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
                    List<String> images = new ArrayList<>();

                    for (int i = 0; i < 5; i++) {
                        JSONObject artist = artistsArray.getJSONObject(i);
                        String artistName = artist.getString("name");
                        topArtistsList.add(artistName);
                        JSONArray imagesArray = artist.getJSONArray("images");

                        String imageUrl = null;
                        if (imagesArray.length() > 0) {
                            JSONObject imageObject = imagesArray.getJSONObject(0);
                            imageUrl = imageObject.getString("url");
                            images.add(imageUrl);
                        }
                    }
                    topArtistsRecommend = topArtistsList;
                    topArtistChat = topArtistsList.get(0);
                    if (topArtistsList.get(0).length() > 15) {
                        setTextAsync(topArtistsList.get(0).substring(0,14), artist1Name);
                        previousTopArtists.add(topArtistsList.get(0).substring(0,14));
                    } else {
                        setTextAsync(topArtistsList.get(0), artist1Name);
                        previousTopArtists.add(topArtistsList.get(0));
                    }
                    Log.d("TopArtistsLength", "" + previousTopArtists.size());
                    if (topArtistsList.get(1).length() > 15) {
                        setTextAsync(topArtistsList.get(1).substring(0,14), artist2Name);
                    } else {
                        setTextAsync(topArtistsList.get(1), artist2Name);
                    }
                    if (topArtistsList.get(2).length() > 15) {
                        setTextAsync(topArtistsList.get(2).substring(0,14), artist3Name);
                    } else {
                        setTextAsync(topArtistsList.get(2), artist3Name);
                    }
                    if (topArtistsList.get(3).length() > 15) {
                        setTextAsync(topArtistsList.get(3).substring(0,14), artist4Name);
                    } else {
                        setTextAsync(topArtistsList.get(3), artist4Name);
                    }
                    if (topArtistsList.get(4).length() > 15) {
                        setTextAsync(topArtistsList.get(4).substring(0,14), artist5Name);
                    } else {
                        setTextAsync(topArtistsList.get(4), artist5Name);
                    }
                    setImageAsync(images.get(0), imageView1);
                    setImageAsync(images.get(1), imageView2);
                    setImageAsync(images.get(2), imageView3);
                    setImageAsync(images.get(3), imageView4);
                    setImageAsync(images.get(4), imageView5);

                    Map<String, Object> historyData = new HashMap<>();
                    historyData.put("topArtistsList", topArtistsList);
                    historyData.put("images", images);
                    updateHistoryArtists(historyData);

                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse top artists data: " + e);
//                    Toast.makeText(TopArtistPage.this, "Failed to parse top artists data, watch Logcat for more details",
//                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        return topArtistsList;
    }

    public static String getTopArtistChat() {
        return topArtistChat;
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
        getArtistsAsList();
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

    private void setImageAsync(final String imageUrl, final ImageView imageView) {
        runOnUiThread(() -> {
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image);

            Glide.with(this)
                    .load(imageUrl)
                    .apply(requestOptions)
                    .into(imageView);
        });
    }
    public void buttonChatGPT(View view){
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("model", "gpt-3.5-turbo");

            JSONArray jsonArrayMessage = new JSONArray();
            JSONObject jsonObjectMessage = new JSONObject();
            jsonObjectMessage.put("role", "user");
            jsonObjectMessage.put("content", "If someone likes " + topArtistChat + ", " + topArtistsRecommend.get(1) + ", and " + topArtistsRecommend.get(2) +  ", what would they usually wear or dance like? It is fine to embellish a bit as this is for fun so don't worry to much about it.");
            jsonArrayMessage.put(jsonObjectMessage);

            jsonObject.put("messages", jsonArrayMessage);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                stringURLEndPoint, jsonObject, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                String stringText = null;
                try {
                    stringText = response.getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content");
                    showPopup(stringText);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error response
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> mapHeader = new HashMap<>();
                mapHeader.put("Authorization", "Bearer " + stringAPIKey);
                mapHeader.put("Content-Type", "application/json");

                return mapHeader;
            }

            @Override
            protected com.android.volley.Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };

        int intTimeoutPeriod = 60000; // 60 seconds timeout duration defined
        RetryPolicy retryPolicy = new DefaultRetryPolicy(intTimeoutPeriod,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);
    }

    private void showPopup(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing, or you can add functionality if needed
            }
        });
        alertDialogBuilder.create().show();
    }

    private void updateHistoryArtists(Map<String, Object> historyData) {
        db.collection("users")
                .whereEqualTo("user_id", currentUserID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Get the reference to the document
                        DocumentReference userRef = documentSnapshot.getReference();

                        // Now update the auth_code field for this user document
                        userRef.update("top_artist_history", historyData)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "History updated successfully");
                                })
                                .addOnFailureListener(e -> Log.w(TAG, "Error updating auth code", e));
                    }
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error searching for user by user ID", e));
    }

    public static ArrayList<String> getPreviousTopArtists() {
        return previousTopArtists;
    }

}
