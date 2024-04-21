package com.example.spotifysdkimplementation;

import static com.example.spotifysdkimplementation.MainActivity.currentUser;

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

import com.example.spotifysdkimplementation.databinding.ChangePasswordPageBinding;
import com.example.spotifysdkimplementation.databinding.LoginPageBinding;
import com.example.spotifysdkimplementation.databinding.LoginPageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangePasswordPage extends AppCompatActivity {
    private ChangePasswordPageBinding binding;

    private Button confirm, cancel;
    private EditText oldPass, newPass, confPass;

    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_page);

        confirm = findViewById(R.id.confirmPasswordButton);
        cancel = findViewById(R.id.cancelPasswordButton);
        oldPass = findViewById(R.id.old_password);
        newPass = findViewById(R.id.new_password);
        confPass = findViewById(R.id.confirm_password);
        mAuth = FirebaseAuth.getInstance();
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ADD LOGIC FOR CHANGING PASSWORD IN FIREBASE
                if (!newPass.getText().toString().equals(confPass.getText().toString())) {
                    Toast.makeText(ChangePasswordPage.this, "Password Update Failed.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.getCurrentUser().updatePassword(newPass.getText().toString());
                }
                Intent intent = new Intent(ChangePasswordPage.this, AccountInfoPage.class);
                startActivity(intent);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangePasswordPage.this, AccountInfoPage.class);
                startActivity(intent);
            }
        });
    }

    private void changePassword() {

    }

}