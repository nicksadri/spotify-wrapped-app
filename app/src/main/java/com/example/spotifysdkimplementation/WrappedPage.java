package com.example.spotifysdkimplementation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.spotifysdkimplementation.databinding.WelcomePageBinding;
import com.example.spotifysdkimplementation.databinding.WrappedPageBinding;

public class WrappedPage extends AppCompatActivity {

    private WrappedPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrapped_page);
    }

}