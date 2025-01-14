package com.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private Button signUpbtn;
    private TextView Signintxt;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        // Check if the user is already signed in
        if (mAuth.getCurrentUser() != null) {
            // Navigate to the HomeActivity
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish(); // Close MainActivity
            return;
        }

        // Find views
        signUpbtn = findViewById(R.id.signup_btn);
        Signintxt = findViewById(R.id.signin);

        // Sign-up button click listener
        signUpbtn.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SignupActivity.class)));

        // Create and set a SpannableString for the "Already a member?" link
        String fullText = "Already a member? Login";
        SpannableString spannableString = new SpannableString(fullText);
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(MainActivity.this, R.color.green)),
                fullText.indexOf("Login"),
                fullText.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        Signintxt.setText(spannableString);

        // Sign-in text click listener
        Signintxt.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SigninActivity.class)));
    }
}
