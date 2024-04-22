package com.example.spotifysdkimplementation;

import static android.content.ContentValues.TAG;
import static com.example.spotifysdkimplementation.MainActivity.currentUser;
import static com.example.spotifysdkimplementation.MainActivity.currentUserID;

import android.content.Intent;
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
import com.example.spotifysdkimplementation.databinding.DeleteAccountPageBinding;
import com.example.spotifysdkimplementation.databinding.LoginPageBinding;
import com.example.spotifysdkimplementation.databinding.LoginPageBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class DeleteAccountPage extends AppCompatActivity {
    private DeleteAccountPageBinding binding;
    private Button deleteAccountConfirm;
    private TextView returnToAccount;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_account_page);

        deleteAccountConfirm = findViewById(R.id.deleteAccountButton);
        returnToAccount = findViewById(R.id.return_to_a);

        deleteAccountConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ADD LOGIC FOR DELETING ACCOUNT
                deleteUser();
                // After deleting account, takes user back to login page
                Intent intent = new Intent(DeleteAccountPage.this, MainActivity.class);
                startActivity(intent);
            }
        });

        returnToAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeleteAccountPage.this, AccountInfoPage.class);
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