package com.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity {

    private EditText Name, Email, Password, ConfirmPassword;
    private CheckBox Agree;
    private Button btnreg;
    private TextView mSignin;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        // Adjusting window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Back Button Action
        ImageButton lefticon = findViewById(R.id.arrow_back);
        lefticon.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), MainActivity.class)));

        progressBar = findViewById(R.id.progressBar);

        // Set up registration process
        registration();
    }

    private void registration() {
        // Find views by ID
        Name = findViewById(R.id.name);
        Email = findViewById(R.id.email_signup);
        Password = findViewById(R.id.password_signup);
        ConfirmPassword = findViewById(R.id.confirm_password_signup);
        btnreg = findViewById(R.id.btn_signup);
        mSignin = findViewById(R.id.login_here);
        Agree = findViewById(R.id.agree_data_processing);

        TextInputLayout nameLayout = findViewById(R.id.nameLayout);
        TextInputLayout emailLayout = findViewById(R.id.emailLayout);
        TextInputLayout passwordLayout = findViewById(R.id.passwordLayout);
        TextInputLayout confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);

        // Add text watchers for validation
        addTextWatchers(emailLayout, passwordLayout, confirmPasswordLayout);

        // Button click to register a new user
        btnreg.setOnClickListener(view -> {
            boolean isValid = true;

            // Find views by ID
            String my_name = Name.getText().toString().trim();
            String my_email = Email.getText().toString().trim();
            String Pass = Password.getText().toString().trim();
            String confirmPass = ConfirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(my_name)) {
                nameLayout.setError("Name is required");
                isValid = false;
            } else {
                nameLayout.setError(null); // Clear error
            }

            if (TextUtils.isEmpty(my_email)) {
                emailLayout.setError("Email is required");
                isValid = false;
            } else {
                emailLayout.setError(null); // Clear error
            }

            if (TextUtils.isEmpty(Pass)) {
                passwordLayout.setError("Password cannot be empty");
                isValid = false;
            } else if (Pass.length() < 6) {
                passwordLayout.setError("Password must be at least 6 characters");
                isValid = false;
            } else {
                passwordLayout.setError(null); // Clear error
            }

            if (TextUtils.isEmpty(confirmPass)) {
                confirmPasswordLayout.setError("Confirm password is required");
                isValid = false;
            } else if (!confirmPass.equals(Pass)) {
                confirmPasswordLayout.setError("Passwords do not match");
                isValid = false;
            } else {
                confirmPasswordLayout.setError(null); // Clear error
            }

            // Validate CheckBox
            TextView errorTextView = findViewById(R.id.errorTextView); // A TextView to display the error message
            if (!Agree.isChecked()) {
                errorTextView.setText("You must agree to processing of personal data.");
                errorTextView.setVisibility(View.VISIBLE); // Make sure error text is visible
                isValid = false;
            } else {
                errorTextView.setVisibility(View.GONE); // Clear error message
            }

            if (!isValid) {
                return; // Stop if validation fails
            }

            progressBar.setVisibility(View.VISIBLE); // Show progress bar

            // Register user with Firebase Authentication
            mAuth.createUserWithEmailAndPassword(my_email, Pass)
                    .addOnCompleteListener(this, task -> {
                        progressBar.setVisibility(View.GONE); // Hide progress bar

                        if (task.isSuccessful()) {
                            // Get the user ID
                            String userId = mAuth.getCurrentUser().getUid();

                            // Create a User object and store in Firestore
                            User user = new User(my_name, my_email);

                            db.collection("users").document(userId)
                                    .set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getApplicationContext(), "Registration complete", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                        finish(); // Close current activity
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getApplicationContext(), "Failed to save user data", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Redirect to SigninActivity if user already has an account
        mSignin.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SigninActivity.class)));
    }

    // TextWatcher for validation
    private void addTextWatchers(TextInputLayout emailLayout, TextInputLayout passwordLayout, TextInputLayout confirmPasswordLayout) {
        // Email validation
        Email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String email = editable.toString().trim();
                if (TextUtils.isEmpty(email)) {
                    emailLayout.setError("Email is required");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailLayout.setError("Invalid email address");
                } else {
                    emailLayout.setError(null); // Clear error
                }
            }
        });

        // Password validation
        Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String password = s.toString().trim();
                if (TextUtils.isEmpty(password)) {
                    passwordLayout.setError("Password cannot be empty");
                } else if (password.length() < 6) {
                    passwordLayout.setError("Password must be at least 6 characters long");
                } else {
                    passwordLayout.setError(null); // Clear error
                }
            }
        });

        // Confirm Password validation
        ConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String confirmPassword = s.toString().trim();
                String password = Password.getText().toString().trim();  // Get the password value

                if (TextUtils.isEmpty(confirmPassword)) {
                    confirmPasswordLayout.setError("Confirm password is required");
                } else if (!confirmPassword.equals(password)) {
                    confirmPasswordLayout.setError("Passwords do not match");
                } else {
                    confirmPasswordLayout.setError(null); // Clear error
                }
            }
        });
    }

    // User class to store user data
    public static class User {
        private String name;
        private String email;

        public User(String name, String email) {
            this.name = name;
            this.email = email;
        }

        // Default constructor for Firestore
        public User() {}

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
 }
}}
