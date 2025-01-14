package com.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class AccountsActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private LinearLayout accountsListLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        accountsListLayout = findViewById(R.id.accounts_list_layout);

        // Initialize the back button and set up a click listener
        findViewById(R.id.back_button).setOnClickListener(v -> onBackPressed());

        // Initialize the Add Account button and set up a click listener
        findViewById(R.id.add_account_btn).setOnClickListener(v -> {
            // Navigate to AddAccountActivity
            Intent intent = new Intent(AccountsActivity.this, AddAccountActivity.class);
            startActivity(intent);
        });

        // Initialize the Add Transaction button and set up a click listener
        findViewById(R.id.add_transaction_btn).setOnClickListener(v -> {
            // Navigate to AddTransactionActivity
            Intent intent = new Intent(AccountsActivity.this, AddTransactionActivity.class);
            startActivity(intent);
        });

        // Load accounts from Firestore
//        loadAccounts();
    }

//    private void loadAccounts() {
//        String userId = mAuth.getCurrentUser().getUid();
//        db.collection("users").document(userId).collection("accounts")
//    }

}
