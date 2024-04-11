package com.example.spotifysdkimplementation;

import static android.content.ContentValues.TAG;
import static com.example.spotifysdkimplementation.MainActivity.AUTH_TOKEN_REQUEST_CODE;
import static com.example.spotifysdkimplementation.MainActivity.CLIENT_ID;
import static com.example.spotifysdkimplementation.MainActivity.getRedirectUri;

import android.app.Activity;
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
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TopTrackPage extends AppCompatActivity {

    private Button trackNext;

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
                Intent intent = new Intent(TopTrackPage.this, ListeningTimePage.class);
                startActivity(intent);
            }
        });
        getTrackAsList();
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
                    Log.d("Successful:", "response sent");

                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    Log.d("Test", jsonObject.toString());
                    JSONArray tracksArray = jsonObject.getJSONArray("items");
                    List<String> images = new ArrayList<>();
                    for (int i = 0; i < 5; i++) {
                        JSONObject track = tracksArray.getJSONObject(i);
                        String trackName = track.getString("name");
                        topTracksList.add(trackName);
                        JSONArray imagesArray = track.getJSONArray("images");

                        String imageUrl = null;
                        if (imagesArray.length() > 0) {
                            JSONObject imageObject = imagesArray.getJSONObject(0);
                            imageUrl = imageObject.getString("url");
                            images.add(imageUrl);
                        }
                    }
                    setTextAsync(topTracksList.get(0), track1Name);
                    setTextAsync(topTracksList.get(1), track2Name);
                    setTextAsync(topTracksList.get(2), track3Name);
                    setTextAsync(topTracksList.get(3), track4Name);
                    setTextAsync(topTracksList.get(4), track5Name);
                    setImageAsync(images.get(0), imageView1);
                    setImageAsync(images.get(1), imageView2);
                    setImageAsync(images.get(2), imageView3);
                    setImageAsync(images.get(3), imageView4);
                    setImageAsync(images.get(4), imageView5);
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
}
