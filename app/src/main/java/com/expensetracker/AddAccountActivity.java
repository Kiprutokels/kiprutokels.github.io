package com.expensetracker;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddAccountActivity extends AppCompatActivity {

    private EditText accountNameEditText;
    private EditText initialBalanceEditText;
    private Spinner accountTypeSpinner;
    private Button saveAccountButton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);

        // Initialize UI components
        accountNameEditText = findViewById(R.id.account_name_edit_text);
        initialBalanceEditText = findViewById(R.id.initial_balance_edit_text);
        accountTypeSpinner = findViewById(R.id.account_type_spinner);
        saveAccountButton = findViewById(R.id.save_account_button);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Set OnClickListener for the save button
        saveAccountButton.setOnClickListener(v -> saveAccount());

        // Initialize the back button and set up a click listener
        findViewById(R.id.back_button).setOnClickListener(v -> onBackPressed());
    }

    private void saveAccount() {
        // Retrieve input values
        String accountName = accountNameEditText.getText().toString();
        String initialBalance = initialBalanceEditText.getText().toString();
        String accountType = accountTypeSpinner.getSelectedItem().toString();

        // Get the current user ID
        String userId = mAuth.getCurrentUser().getUid();

        // Create a new Account object
        Account newAccount = new Account(accountName, initialBalance, accountType);

        // Generate a unique ID for the account (optional)
        String accountId = db.collection("users").document(userId).collection("accounts").document().getId();

        // Save to Firestore
        db.collection("users")
                .document(userId)
                .collection("accounts")
                .document(accountId)  // Using the unique ID generated
                .set(newAccount)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddAccountActivity.this, "Account saved successfully", Toast.LENGTH_SHORT).show();
                    finish();  // Close the activity after saving
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddAccountActivity.this, "Error saving account", Toast.LENGTH_SHORT).show();
                });
    }

    public static class Account {
        private String accountName;
        private String initialBalance;
        private String accountType;

        public Account() {
            // Default constructor required for calls to DataSnapshot.getValue(Account.class)
        }

        public Account(String accountName, String initialBalance, String accountType) {
            this.accountName = accountName;
            this.initialBalance = initialBalance;
            this.accountType = accountType;
        }

        // Getters and setters
        public String getAccountName() {
            return accountName;
        }

        public void setAccountName(String accountName) {
            this.accountName = accountName;
        }

        public String getInitialBalance() {
            return initialBalance;
        }

        public void setInitialBalance(String initialBalance) {
            this.initialBalance = initialBalance;
        }

        public String getAccountType() {
            return accountType;
        }

        public void setAccountType(String accountType) {
            this.accountType = accountType;
        }
    }
}
