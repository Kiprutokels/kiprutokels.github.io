package com.expensetracker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class NewBudgetActivity extends AppCompatActivity {

    private static final String TAG = "NewBudgetActivity";
    private EditText budgetName, budgetAmount;
    private TextView budgetPeriod;
    private Button saveBudgetButton;
    private Spinner categorySpinner;
    private ImageView leftIcon, iconSave, iconDelete;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_budget);  // Set the layout

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        budgetName = findViewById(R.id.budget_name);
        budgetAmount = findViewById(R.id.budget_amount);
        categorySpinner = findViewById(R.id.category_spinner);
        saveBudgetButton = findViewById(R.id.save_budget_button);
        budgetPeriod = findViewById(R.id.budget_period);
        leftIcon = findViewById(R.id.back_button);


        // Apply window insets (to adjust padding for system bars like status bar and navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Toolbar Button Actions
        leftIcon.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());  // Back button

        // Set OnClickListener for the save button
        saveBudgetButton.setOnClickListener(v -> saveBudget());

        // Set OnClickListener for the budget period TextView to show the month picker
        budgetPeriod.setOnClickListener(v -> showMonthPickerDialog());

        // Set default month to the current month
        Calendar calendar = Calendar.getInstance();
        String currentMonthYear = getMonthYearString(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
        budgetPeriod.setText(currentMonthYear);

        // Handle the back button using OnBackPressedCallback
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                finish();
            }
        });
    }

    private void showMonthPickerDialog() {
        try {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, selectedYear, selectedMonth, dayOfMonth) -> {
                        String selectedMonthYear = getMonthYearString(selectedMonth, selectedYear);
                        budgetPeriod.setText(selectedMonthYear);
                    }, year, month, calendar.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.getDatePicker().findViewById(
                    getResources().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
            datePickerDialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing DatePickerDialog", e);
        }
    }

    private String getMonthYearString(int month, int year) {
        String[] monthNames = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
        return monthNames[month] + " " + year;
    }

    private void saveBudget() {
        // Get the values from the UI elements
        String budgetName = this.budgetName.getText().toString().trim();
        String amountText = budgetAmount.getText().toString().trim();
        String periodText = budgetPeriod.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();

        if (budgetName.isEmpty() || amountText.isEmpty() || periodText.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse Amount
        float amount = Float.parseFloat(amountText);

        // Get the current user ID
        String userId = mAuth.getCurrentUser().getUid();

        // Create a new Budget object
        Budget newBudget = new Budget(budgetName, amount, category, periodText);

        // Generate a unique ID for the budget (optional)
        String budgetId = db.collection("users").document(userId).collection("budget").document().getId();

        // Save to Firestore
        db.collection("users")
                .document(userId)
                .collection("budget")
                .document(budgetId)  // Using the unique ID generated
                .set(newBudget)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(NewBudgetActivity.this, "Budget saved successfully", Toast.LENGTH_SHORT).show();
                    finish();  // Close the activity after saving
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(NewBudgetActivity.this, "Error saving budget", Toast.LENGTH_SHORT).show();
                });
    }
}
