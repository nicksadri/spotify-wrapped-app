package com.example.spotifysdkimplementation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.spotifysdkimplementation.databinding.AccountCreationPageBinding;
import com.example.spotifysdkimplementation.databinding.AccountInfoPageBinding;
import com.example.spotifysdkimplementation.databinding.ChangeUsernamePageBinding;
import com.example.spotifysdkimplementation.databinding.LoginPageBinding;
import com.example.spotifysdkimplementation.databinding.LoginPageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangeUsernamePage extends AppCompatActivity {
    private ChangeUsernamePageBinding binding;
    private Button confirm, cancel;
    private EditText newUser, confUser;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    static FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_username_page);

        confirm = findViewById(R.id.confirmUsernameButton);
        cancel = findViewById(R.id.cancelConfirmButton);
        newUser = findViewById(R.id.new_username);
        confUser = findViewById(R.id.confirm_username);

        mAuth = FirebaseAuth.getInstance();
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ADD LOGIC FOR CHANGING USERNAME IN FIREBASE
                if (!newUser.getText().toString().equals(confUser.getText().toString())) {
                    Toast.makeText(ChangeUsernamePage.this, "Username Update Failed.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.getCurrentUser().verifyBeforeUpdateEmail(newUser.getText().toString());
                    Toast.makeText(ChangeUsernamePage.this, "Check your email.",
                            Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(ChangeUsernamePage.this, AccountInfoPage.class);
                startActivity(intent);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangeUsernamePage.this ,AccountInfoPage.class);
                startActivity(intent);
            }
        });

    }
}