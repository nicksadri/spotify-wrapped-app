package com.example.spotifysdkimplementation;

import static android.content.ContentValues.TAG;
import static com.example.spotifysdkimplementation.MainActivity.AUTH_TOKEN_REQUEST_CODE;
import static com.example.spotifysdkimplementation.MainActivity.CLIENT_ID;
import static com.example.spotifysdkimplementation.MainActivity.currentUserID;
import static com.example.spotifysdkimplementation.MainActivity.getRedirectUri;

import android.app.Activity;
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

public class TopTrackPage extends AppCompatActivity {

    private Button trackNext;

    private String stringURLEndPoint = "https://api.openai.com/v1/chat/completions";
    private String stringAPIKey = "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";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private Call mCall;

    private TextView track1Name, track2Name, track3Name, track4Name, track5Name;

    private ImageView imageView1, imageView2, imageView3, imageView4, imageView5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_track_page);

        trackNext = findViewById(R.id.tracksNextButton);


        track1Name = findViewById(R.id.trackName1);
        track2Name = findViewById(R.id.trackName2);
        track3Name = findViewById(R.id.trackName3);
        track4Name = findViewById(R.id.trackName4);
        track5Name = findViewById(R.id.trackName5);

        imageView1 = findViewById(R.id.trackImage1);
        imageView2 = findViewById(R.id.trackImage2);
        imageView3 = findViewById(R.id.trackImage3);
        imageView4 = findViewById(R.id.trackImage4);
        imageView5 = findViewById(R.id.trackImage5);

        trackNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopTrackPage.this, WrappedPage.class);
                startActivity(intent);
            }
        });
        getTrackAsList();

        Button recommendationsButton = findViewById(R.id.artistsButton);

        recommendationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Trigger the ChatGPT functionality
                buttonChatGPT(v);
            }
        });
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    public List<String> getTrackAsList() {
        List<String> topTracksList = new ArrayList<>();

        if (MainActivity.mAccessToken == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return topTracksList;
        }
        final Request topTrackRequest = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks")
                .addHeader("Authorization", "Bearer " + MainActivity.mAccessToken)
                .build();
        cancelCall();

        mCall = mOkHttpClient.newCall(topTrackRequest);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch top tracks data: " + e);
                Toast.makeText(TopTrackPage.this, "Failed to fetch top tracks data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    Log.d("Test", jsonObject.toString());
                    JSONArray tracksArray = jsonObject.getJSONArray("items");
                    List<String> images = new ArrayList<>();
                    for (int i = 0; i < 5; i++) {
                        JSONObject track = tracksArray.getJSONObject(i);
                        JSONObject album = track.getJSONObject("album");
                        String trackName = album.getString("name");
                        topTracksList.add(trackName);
                        JSONArray imagesArray = album.getJSONArray("images");

                        String imageUrl = null;
                        if (imagesArray.length() > 0) {
                            JSONObject imageObject = imagesArray.getJSONObject(0);
                            imageUrl = imageObject.getString("url");
                            images.add(imageUrl);
                        }
                    }
                    if (topTracksList.get(0).length() > 15) {
                        setTextAsync(topTracksList.get(0).substring(0, 15), track1Name);
                    } else {
                        setTextAsync(topTracksList.get(0), track1Name);
                    }
                    if (topTracksList.get(1).length() > 15) {
                        setTextAsync(topTracksList.get(1).substring(0, 15), track2Name);
                    } else {
                        setTextAsync(topTracksList.get(1), track2Name);
                    }
                    if (topTracksList.get(2).length() > 15) {
                        setTextAsync(topTracksList.get(2).substring(0, 15), track3Name);
                    } else {
                        setTextAsync(topTracksList.get(2), track3Name);
                    }
                    if (topTracksList.get(3).length() > 15) {
                        setTextAsync(topTracksList.get(3).substring(0, 15), track4Name);
                    } else {
                        setTextAsync(topTracksList.get(3), track4Name);
                    }
                    if (topTracksList.get(4).length() > 15) {
                        setTextAsync(topTracksList.get(4).substring(0, 15), track5Name);
                    } else {
                        setTextAsync(topTracksList.get(4), track5Name);
                    }

//                    setTextAsync(topTracksList.get(1), track2Name);
//                    setTextAsync(topTracksList.get(2), track3Name);
//                    setTextAsync(topTracksList.get(3), track4Name);
//                    setTextAsync(topTracksList.get(4), track5Name);
                    setImageAsync(images.get(0), imageView1);
                    setImageAsync(images.get(1), imageView2);
                    setImageAsync(images.get(2), imageView3);
                    setImageAsync(images.get(3), imageView4);
                    setImageAsync(images.get(4), imageView5);

                    Map<String, Object> historyData = new HashMap<>();
                    historyData.put("topArtistsList", topTracksList);
                    historyData.put("images", images);
                    updateHistoryTracks(historyData);

                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse top tracks data: " + e);
                    Toast.makeText(TopTrackPage.this, "Failed to parse top tracks data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        return topTracksList;
    }





    /**
     * Creates a UI thread to update a TextView in the background
     * Reduces UI latency and makes the system perform more consistently
     *
     * @param text     the text to set
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
            jsonObjectMessage.put("content", "If someone likes Carnival by Kanye, Jimmy Cooks, Wolves, IDGAF, and My Eyes, give them 5 song recommendations numbered 1 to 5 and only give those recommendations.");
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

    private void updateHistoryTracks(Map<String, Object> historyData) {
        db.collection("users")
                .whereEqualTo("user_id", currentUserID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Get the reference to the document
                        DocumentReference userRef = documentSnapshot.getReference();

                        // Now update the auth_code field for this user document
                        userRef.update("history", historyData)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "History updated successfully");
                                })
                                .addOnFailureListener(e -> Log.w(TAG, "Error updating auth code", e));
                    }
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error searching for user by user ID", e));
    }


}
