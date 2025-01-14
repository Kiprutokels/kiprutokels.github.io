package com.expensetracker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailResetEditText;
    private Button resetPasswordButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize UI components
        emailResetEditText = findViewById(R.id.email_reset);
        resetPasswordButton = findViewById(R.id.btn_reset_password);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        // Set OnClickListener for the back button
        ImageButton backButton = findViewById(R.id.arrow_back);
        backButton.setOnClickListener(v -> finish());

        // Set OnClickListener for the reset password button
        resetPasswordButton.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String email = emailResetEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailResetEditText.setError("Email is required");
            emailResetEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Reset password link sent to your email", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset password email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
