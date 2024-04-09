package com.example.spotifysdkimplementation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ListeningTimePage extends AppCompatActivity {

    private Button wrappedDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listening_time_page);

        wrappedDone = findViewById(R.id.wrappedDoneButton);

        wrappedDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListeningTimePage.this, WrappedPage.class);
                startActivity(intent);
            }
        });
    }

}
