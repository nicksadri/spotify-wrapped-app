package com.example.spotifysdkimplementation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class TopTrackPage extends AppCompatActivity {

    private Button trackNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_track_page);

        trackNext = findViewById(R.id.tracksNextButton);

        trackNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopTrackPage.this, ListeningTimePage.class);
                startActivity(intent);
            }
        });

    }

}
