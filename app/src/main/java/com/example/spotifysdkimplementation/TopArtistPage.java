package com.example.spotifysdkimplementation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class TopArtistPage extends AppCompatActivity {

    private Button artistNext;

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

    }

}
