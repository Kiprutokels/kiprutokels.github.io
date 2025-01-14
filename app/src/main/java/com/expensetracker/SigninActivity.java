package com.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class SigninActivity extends AppCompatActivity {

    private EditText Email, Password;
    private Button btnLogin;
    private TextView mSignup, mForgotPassword;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Back Button Action
        ImageButton lefticon = findViewById(R.id.arrow_back);
        lefticon.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), MainActivity.class)));

        progressBar = findViewById(R.id.progressBar);

        // Set up login process
        login();
    }

    private void login() {
        // Find views by ID
        Email = findViewById(R.id.email_login);
        Password = findViewById(R.id.password_login);
        btnLogin = findViewById(R.id.btn_login);
        mSignup = findViewById(R.id.signup_here);
        mForgotPassword = findViewById(R.id.forgot_password);

        // Button click to log in the user

        btnLogin.setOnClickListener(view -> {
            // Find views by ID
            TextInputLayout emailLayout = findViewById(R.id.emailLayout);
            TextInputLayout passwordLayout = findViewById(R.id.passwordLayout);

            String my_email = Email.getText().toString().trim();
            String Pass = Password.getText().toString().trim();

            // Validate inputs
            boolean isValid = true;

            if (TextUtils.isEmpty(my_email)) {
                emailLayout.setError("Email is required");
                isValid = false;
            } else {
                emailLayout.setError(null); // Clear error
            }

            if (TextUtils.isEmpty(Pass)) {
                passwordLayout.setError("Password is required");
                isValid = false;
            } else {
                passwordLayout.setError(null); // Clear error
            }

            if (!isValid) {
                return;
            }

            progressBar.setVisibility(View.VISIBLE); // Show progress bar

            // Sign in user with Firebase Authentication
            mAuth.signInWithEmailAndPassword(my_email, Pass)
                    .addOnCompleteListener(this, task -> {
                        progressBar.setVisibility(View.GONE); // Hide progress bar

                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SigninActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Redirect to SignupActivity if user doesn't have an account
        mSignup.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SignupActivity.class)));

        // Redirect to ForgotPasswordActivity if user forgot their password
        mForgotPassword.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class)));
    }
}
