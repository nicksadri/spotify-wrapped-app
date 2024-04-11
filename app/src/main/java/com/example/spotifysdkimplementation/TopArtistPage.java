package com.example.spotifysdkimplementation;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_artist_page);

        artistNext = findViewById(R.id.artistsNextButton);

        artistNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopArtistPage.this, TopTrackPage.class);
                startActivity(intent);
            }
        });
        test();
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
                .url("https://api.spotify.com/v1/me/top/artists?limit=5")
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
                    String responseData = response.body().string();
                    final JSONObject jsonObject = new JSONObject(responseData);

                    JSONArray artistsArray = jsonObject.getJSONArray("items");
                    int numArtists = Math.min(5, artistsArray.length());

                    for (int i = 0; i < numArtists; i++) {
                        JSONObject artist = artistsArray.getJSONObject(i);
                        String artistName = artist.getString("name");
                        topArtistsList.add(artistName);
                    }
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse top artists data: " + e);
                    Toast.makeText(TopArtistPage.this, "Failed to parse top artists data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        return topArtistsList;
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
