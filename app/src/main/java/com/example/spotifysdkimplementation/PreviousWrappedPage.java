package com.example.spotifysdkimplementation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifysdkimplementation.databinding.LoginPageBinding;
import com.example.spotifysdkimplementation.databinding.LoginPageBinding;
import com.example.spotifysdkimplementation.databinding.PreviousWrappedPageBinding;

import java.util.ArrayList;

public class PreviousWrappedPage extends AppCompatActivity {
    private PreviousWrappedPageBinding binding;

    public static ArrayList<WrappedClass> getMasterList() {
        return masterList;
    }

    private static ArrayList<WrappedClass> masterList;
    private RecyclerView recyclerView;
    private static RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.previous_wrapped_page);

        masterList = new ArrayList<>();
        recyclerView = findViewById(R.id.wrappedRecyclerView);
        adapter = new RecyclerViewAdapter(this, masterList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    public void returnHome(View view) {
        startActivity(new Intent(PreviousWrappedPage.this, WrappedPage.class));
    }

    public static RecyclerViewAdapter getAdapter() {
        return adapter;
    }

}