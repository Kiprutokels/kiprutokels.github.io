package com.expensetracker;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private LinearLayout accountSettingsLayout;
    private LinearLayout resetDataLayout;
    private LinearLayout helpSupportLayout;
    private Spinner themeSpinner;
    private Spinner currencySpinner;
    private Spinner languageSpinner;
    private String selectedLanguage = "English"; // Default language

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize views
        accountSettingsLayout = findViewById(R.id.account_settings_layout);
        resetDataLayout = findViewById(R.id.reset_data_layout);
        helpSupportLayout = findViewById(R.id.help_support_layout);
        themeSpinner = findViewById(R.id.theme_spinner);
        currencySpinner = findViewById(R.id.currency_spinner);
        languageSpinner = findViewById(R.id.language_spinner);

        ImageView leftIcon = findViewById(R.id.arrow_back);
        leftIcon.setOnClickListener(view -> finish());

        // Set up adapters for spinners
        setupSpinner(themeSpinner, R.array.theme_options);
        setupSpinner(currencySpinner, R.array.currency_options);
        setupLanguageSpinner();

        // Set click listeners for other actions
        accountSettingsLayout.setOnClickListener(v -> navigateToProfile());
        resetDataLayout.setOnClickListener(v -> showDeleteDataDialog());
        helpSupportLayout.setOnClickListener(v -> navigateToHelp());
    }

    private void setupSpinner(Spinner spinner, int arrayResId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                arrayResId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setupLanguageSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.language_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        int position = adapter.getPosition(selectedLanguage);
        languageSpinner.setSelection(position);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (!selectedItem.equals(selectedLanguage)) {
                    selectedLanguage = selectedItem;
                    if (selectedItem.equals("English")) {
                        setLocale("en");
                    } else if (selectedItem.equals("Swahili")) {
                        setLocale("sw");
                    }
                    recreate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    private void navigateToProfile() {
        Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    private void navigateToHelp() {
        Intent intent = new Intent(SettingsActivity.this, HelpActivity.class);
        startActivity(intent);
    }

    private void showDeleteDataDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Data")
                .setMessage("Are you sure you want to delete your data? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteUserData())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void deleteUserData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Delete expenses subcollection
            db.collection("users").document(userId).collection("expenses").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().delete();
                        }
                    });

            // Delete income subcollection
            db.collection("users").document(userId).collection("income").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().delete();
                        }
                    });

            // Delete budget subcollection
            db.collection("users").document(userId).collection("budget").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().delete();
                        }
                    });

            // Delete categories subcollection
            db.collection("users").document(userId).collection("categories").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().delete();
                        }
                    });

            Toast.makeText(this, "User data deleted successfully.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No user is currently signed in.", Toast.LENGTH_SHORT).show();
        }
    }
}
