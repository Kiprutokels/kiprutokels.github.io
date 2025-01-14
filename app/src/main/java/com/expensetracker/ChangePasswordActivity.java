package com.expensetracker;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputLayout currentPasswordLayout, newPasswordLayout;
    private TextInputEditText currentPasswordEditText, newPasswordEditText;
    private Button submitButton;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Initialize UI elements
        currentPasswordLayout = findViewById(R.id.currentPasswordLayout);
        currentPasswordEditText = findViewById(R.id.current_password);
        newPasswordLayout = findViewById(R.id.newPasswordLayout);
        newPasswordEditText = findViewById(R.id.new_password);
        submitButton = findViewById(R.id.submit_button);
        ImageView backButton = findViewById(R.id.back_button);
        TextView title = findViewById(R.id.title);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Set up back button
        backButton.setOnClickListener(v -> finish());

        // Set up submit button
        submitButton.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String currentPassword = currentPasswordEditText.getText().toString();
        String newPassword = newPasswordEditText.getText().toString();

        if (TextUtils.isEmpty(currentPassword)) {
            currentPasswordLayout.setError("Enter your current password");
            return;
        } else {
            currentPasswordLayout.setError(null);
        }

        if (TextUtils.isEmpty(newPassword)) {
            newPasswordLayout.setError("Enter a new password");
            return;
        } else {
            newPasswordLayout.setError(null);
        }

        if (currentUser != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPassword);

            // Re-authenticate user
            currentUser.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Update password
                    currentUser.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Toast.makeText(ChangePasswordActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ChangePasswordActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    currentPasswordLayout.setError("Incorrect current password");
                    Toast.makeText(ChangePasswordActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
