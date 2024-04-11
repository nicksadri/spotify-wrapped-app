package com.example.spotifysdkimplementation;

import static android.content.ContentValues.TAG;

import static com.example.spotifysdkimplementation.MainActivity.AUTH_CODE_REQUEST_CODE;
import static com.example.spotifysdkimplementation.MainActivity.CLIENT_ID;
import static com.example.spotifysdkimplementation.MainActivity.getRedirectUri;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.spotifysdkimplementation.databinding.AccountCreationPageBinding;
import com.example.spotifysdkimplementation.databinding.LoginPageBinding;
import com.example.spotifysdkimplementation.databinding.LoginPageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;


import java.util.HashMap;
import java.util.Map;

public class AccountCreationPage extends AppCompatActivity {
    private AccountCreationPageBinding binding;
    private Button confirm, cancel;

    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_creation_page);

        cancel = findViewById(R.id.cancel_creation);
        confirm = findViewById(R.id.confirm_creation);
        EditText inputEmail = findViewById(R.id.input_email);
        EditText inputPassword = findViewById(R.id.input_password);
        EditText createAccount = findViewById(R.id.confirm_password);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountCreationPage.this, MainActivity.class);
                startActivity(intent);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // INPUT METHOD FOR CREATING ACCOUNT IN FIREBASE
                signUpUser(inputEmail.getText().toString(), inputPassword.getText().toString());

            }
        });

        mAuth = FirebaseAuth.getInstance();

    }
    private void signUpUser(String email, String password) {
        // Create a new user with email and password
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign up success, update UI with the signed-in user's information
                        MainActivity.currentUser = mAuth.getCurrentUser();
                        MainActivity.currentUserID = mAuth.getCurrentUser().getUid();
                        // Proceed with further actions (e.g., saving user data to the database)
                        // creating a user entry
                        Map<String, Object> user = new HashMap<>();
                        assert MainActivity.currentUser != null;

                        user.put("email", email);
                        user.put("auth_code", null);
                        user.put("user_id", MainActivity.currentUserID);
                        user.put("api_token", null);
                        user.put("refresh_token", null);
                        user.put("history", null);

                        db.collection("users")
                                .add(user)
                                .addOnSuccessListener(
                                        documentReference -> {
                                            Log.d(TAG, "DocumentSnapshot added with ID: "
                                                    + documentReference.getId());
                                            getCode();
                                        })
                                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
                        Toast.makeText(AccountCreationPage.this, "Signed Up.",
                                Toast.LENGTH_SHORT).show();

                    } else {
                        // If sign up fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(AccountCreationPage.this, "Sign Up failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Get authentication request
     *
     * @param type the type of the request
     * @return the authentication request
     */
    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[] { "user-read-email", "user-top-read" }) // <--- Change the scope of your requested token here
                .setCampaign("your-campaign-token")
                .build();
    }

    public void getCode() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(AccountCreationPage.this, AUTH_CODE_REQUEST_CODE, request);
    }

    /**
     * When the app leaves this activity to momentarily get a token/code, this function
     * fetches the result of that external activity to get the response from Spotify
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        if (AUTH_CODE_REQUEST_CODE == requestCode) {
            Log.d("HELO", response.getCode());
            db.collection("users")
                    .whereEqualTo("user_id", MainActivity.currentUserID)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Get the reference to the document
                            DocumentReference userRef = documentSnapshot.getReference();

                            // Now update the auth_code field for this user document
                            userRef.update("auth_code", response.getCode())
                                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Auth code updated successfully"))
                                    .addOnFailureListener(e -> Log.w(TAG, "Error updating auth code", e));
                        }
                    })
                    .addOnFailureListener(e -> Log.w(TAG, "Error searching for user by user ID", e));
        }

        Intent intent = new Intent(AccountCreationPage.this, WrappedPage.class);
        startActivity(intent);
    }
}