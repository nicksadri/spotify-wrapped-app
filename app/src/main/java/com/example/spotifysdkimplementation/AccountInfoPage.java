package com.example.spotifysdkimplementation;

import static android.content.ContentValues.TAG;

import static com.example.spotifysdkimplementation.MainActivity.currentUser;
import static com.example.spotifysdkimplementation.MainActivity.currentUserID;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.spotifysdkimplementation.databinding.AccountCreationPageBinding;
import com.example.spotifysdkimplementation.databinding.AccountInfoPageBinding;
import com.example.spotifysdkimplementation.databinding.LoginPageBinding;
import com.example.spotifysdkimplementation.databinding.LoginPageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

public class AccountInfoPage extends AppCompatActivity {
    private AccountInfoPageBinding binding;

    private Button changeUser, changePass, deleteAccount;
    private TextView logout, returnFromAccount, userName;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_info_page);

        changeUser = findViewById(R.id.change_username_button);
        changePass = findViewById(R.id.change_password_button);
        deleteAccount = findViewById(R.id.delete_account_button);
        logout = findViewById(R.id.logout);
        returnFromAccount = findViewById(R.id.returnToMainButton);
        userName = findViewById(R.id.username);
        userName.setText(currentUser.getDisplayName());

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser();
                Intent intent = new Intent(AccountInfoPage.this, DeleteAccountPage.class);
                startActivity(intent);
            }
        });

        changeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountInfoPage.this, ChangeUsernamePage.class);
                startActivity(intent);
            }
        });

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountInfoPage.this, ChangePasswordPage.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ADD LOGIC FOR LOGGING OUT OF ACCOUNT HERE
                Uri uri = Uri.parse("https://accounts.spotify.com/");
                startActivity(new Intent(Intent.ACTION_VIEW, uri));

            }
        });

        returnFromAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountInfoPage.this, WrappedPage.class);
                startActivity(intent);
            }
        });

    }
    private void deleteUser() {
        currentUser.delete();
        db.collection("users")
                .whereEqualTo("user_id", currentUserID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Get the reference to the document
                        DocumentReference userRef = documentSnapshot.getReference();
                        // Delete the user document
                        userRef.delete()
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "User document deleted successfully"))
                                .addOnFailureListener(e -> Log.w(TAG, "Error deleting user document", e));
                    }
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error searching for user by user ID", e));
    }

}