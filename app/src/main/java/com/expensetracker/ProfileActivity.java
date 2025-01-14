package com.expensetracker;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileActivity extends AppCompatActivity {

    private EditText firstNameEditText, lastNameEditText, phoneEditText;
    private TextView emailTextView, changePasswordTextView, deleteAccountTextView;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private ListenerRegistration userListener;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        applyWindowInsets();
        initializeUIElements();
        initializeFirebase();

        changePasswordTextView.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class)));
        deleteAccountTextView.setOnClickListener(v -> promptDeleteAccount());

        findViewById(R.id.save_button).setOnClickListener(v -> saveUserInfo());
    }

    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeUIElements() {
        firstNameEditText = findViewById(R.id.first_name);
        lastNameEditText = findViewById(R.id.last_name);
        phoneEditText = findViewById(R.id.input_phone);
        emailTextView = findViewById(R.id.email_text_view);
        changePasswordTextView = findViewById(R.id.change_password);
        deleteAccountTextView = findViewById(R.id.delete_account);

        ImageView leftIcon = findViewById(R.id.arrow_back);
        leftIcon.setOnClickListener(view -> finish());
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db = FirebaseFirestore.getInstance();
            userRef = db.collection("users").document(userId);
            emailTextView.setText(currentUser.getEmail());
            loadUserInfo();
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserInfo() {
        userListener = userRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Toast.makeText(this, "Failed to load user info", Toast.LENGTH_SHORT).show();
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                User user = snapshot.toObject(User.class);
                if (user != null) {
                    firstNameEditText.setText(user.getName());
                    lastNameEditText.setText(user.getLastName());
                    phoneEditText.setText(user.getPhoneNumber());
                }
            }
        });
    }

    private void saveUserInfo() {
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String phoneNumber = phoneEditText.getText().toString();

        userRef.update("name", firstName,
                        "lastName", lastName,
                        "phoneNumber", phoneNumber)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show());
    }

    private void promptDeleteAccount() {
        if (mAuth.getCurrentUser() != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account? Deleting your account will:\n\n" +
                            "1. Permanently delete your data.\n" +
                            "2. Remove all your saved transactions.\n" +
                            "3. Log you out of this app.\n\n" +
                            "This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> deleteAccount())
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    private void deleteAccount() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Delete all subcollections
            deleteUserSubcollections(userId);

            // Delete the user document itself
            userRef.delete()
                    .continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw task.getException(); // If Firestore deletion fails, throw an exception.
                        }
                        // After deleting Firestore data, attempt to delete the user account.
                        return user.delete();
                    })
                    .addOnSuccessListener(aVoid -> {
                        // After successfully deleting user data and account, redirect user to the login screen
                        Toast.makeText(ProfileActivity.this, "User account deleted successfully.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ProfileActivity.this, "Failed to delete user account or data.", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void deleteUserSubcollections(String userId) {
        // Delete subcollections for expenses, income, budget, and categories
        deleteCollection(db.collection("users").document(userId).collection("expenses"));
        deleteCollection(db.collection("users").document(userId).collection("income"));
        deleteCollection(db.collection("users").document(userId).collection("budget"));
        deleteCollection(db.collection("users").document(userId).collection("categories"));
    }

    // Helper method to delete a collection
    private void deleteCollection(CollectionReference collectionRef) {
        collectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    documentSnapshot.getReference().delete();
                }
            } else {
                Toast.makeText(ProfileActivity.this, "Failed to fetch collection: " + task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userListener != null) {
            userListener.remove();
        }
    }
}
