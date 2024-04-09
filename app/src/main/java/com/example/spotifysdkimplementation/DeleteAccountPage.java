package com.example.spotifysdkimplementation;

import android.content.Intent;
import android.os.Bundle;
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

public class DeleteAccountPage extends AppCompatActivity {
    private DeleteAccountPageBinding binding;
    private Button deleteAccountConfirm;
    private TextView returnToAccount;

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

}