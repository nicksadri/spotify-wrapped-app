package com.example.spotifysdkimplementation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.spotifysdkimplementation.databinding.WelcomePageBinding;
import com.example.spotifysdkimplementation.databinding.WrappedPageBinding;

public class WrappedPage extends AppCompatActivity {

    private WrappedPageBinding binding;
    private Button previous;
    private View wrapped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrapped_page);

        previous = findViewById(R.id.previous_wrapped_button);
        wrapped = findViewById(R.id.wrapped_art);

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WrappedPage.this, PreviousWrappedPage.class);
                startActivity(intent);
            }
        });

        wrapped.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WrappedPage.this, TopArtistPage.class);
                startActivity(intent);
            }
        });
    }

}