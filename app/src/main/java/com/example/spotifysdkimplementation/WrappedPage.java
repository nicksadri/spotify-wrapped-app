package com.example.spotifysdkimplementation;

import static android.content.ContentValues.TAG;
import static com.example.spotifysdkimplementation.MainActivity.AUTH_CODE_REQUEST_CODE;
import static com.example.spotifysdkimplementation.MainActivity.AUTH_TOKEN_REQUEST_CODE;
import static com.example.spotifysdkimplementation.MainActivity.CLIENT_ID;
import static com.example.spotifysdkimplementation.MainActivity.getRedirectUri;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.spotifysdkimplementation.databinding.WelcomePageBinding;
import com.example.spotifysdkimplementation.databinding.WrappedPageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class WrappedPage extends AppCompatActivity {

    private WrappedPageBinding binding;
    private Button previous, account;
    private View wrapped;

    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrapped_page);

        previous = findViewById(R.id.previous_wrapped_button);
        wrapped = findViewById(R.id.wrapped_art);
        account = findViewById(R.id.accessAccountButton);

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

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WrappedPage.this, AccountInfoPage.class);
                startActivity(intent);
            }
        });
        setToken();
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

    public void setToken() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(WrappedPage.this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    /**
     * When the app leaves this activity to momentarily get a token/code, this function
     * fetches the result of that external activity to get the response from Spotify
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            Log.d("Wrapped page", response.getAccessToken());
            db.collection("users")
                    .whereEqualTo("user_id", MainActivity.currentUserID)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Get the reference to the document
                            DocumentReference userRef = documentSnapshot.getReference();

                            // Now update the auth_code field for this user document
                            userRef.update("api_token", response.getAccessToken())
                                    .addOnSuccessListener(aVoid -> Log.d(TAG, "api token updated successfully"))
                                    .addOnFailureListener(e -> Log.w(TAG, "Error updating api token", e));
                        }
                    })
                    .addOnFailureListener(e -> Log.w(TAG, "Error searching for user by user ID", e));
        }
    }
}