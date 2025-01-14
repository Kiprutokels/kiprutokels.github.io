package com.expensetracker;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.expensetracker.databinding.ActivityReportsBinding;

import java.util.Calendar;
import java.util.Date;

public class ReportsActivity extends AppCompatActivity {
    private ActivityReportsBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Set up the back button
        ImageView backButton = binding.backButton;
        backButton.setOnClickListener(v -> finish());

        // Set up swipe refresh
        SwipeRefreshLayout swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (mAuth.getCurrentUser() != null) {
                loadMonthlyData(mAuth.getCurrentUser().getUid());
            }
            swipeRefreshLayout.setRefreshing(false);
        });

        // Load data if the user is authenticated
        if (mAuth.getCurrentUser() != null) {
            loadMonthlyData(mAuth.getCurrentUser().getUid());
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMonthlyData(String userId) {
        Date startDate = getMonthStartDate();
        Date endDate = getMonthEndDate();

        // Fetch income and expenses first, then budget
        fetchTransactions(db.collection("users").document(userId).collection("income"), startDate, endDate, "Income");
        fetchTransactions(db.collection("users").document(userId).collection("expenses"), startDate, endDate, "Expense");

        // Only fetch budget data once income and expenses are loaded
        fetchBudgetData(userId, startDate, endDate);
    }

    private Date getMonthStartDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Date getMonthEndDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    private void fetchTransactions(CollectionReference ref, Date startDate, Date endDate, String type) {
        ref.whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThan("date", endDate)
                .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    double totalAmount = 0;
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Check for null or invalid values before adding
                        Double amount = document.getDouble("amount");
                        if (amount != null) {
                            totalAmount += amount;
                        }
                    }
                    updateTransactionUI(type, totalAmount);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load " + type.toLowerCase(), Toast.LENGTH_SHORT).show());
    }

    private void fetchBudgetData(String userId, Date startDate, Date endDate) {
        db.collection("users")
                .document(userId)
                .collection("budget")
                .whereGreaterThanOrEqualTo("timestamp", startDate)
                .whereLessThanOrEqualTo("timestamp", endDate)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    double totalBudget = 0;
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        totalBudget += document.getDouble("amount");
                        Log.d("BudgetData", "Document: " + document.getData());
                    }
                    updateBudgetUI(totalBudget);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load budget", Toast.LENGTH_SHORT).show());
    }

    private void updateTransactionUI(String type, double totalAmount) {
        switch (type) {
            case "Income":
                binding.incomeTextView.setText(String.format("Income:\n%.2f", totalAmount));
                break;
            case "Expense":
                binding.expenseTextView.setText(String.format("Expense:\n%.2f", totalAmount));
                break;
        }
    }

    private void updateBudgetUI(double totalBudget) {
        // Default to zero if no data is available
        double totalIncome = 0;
        double totalExpense = 0;

        try {
            totalIncome = Double.parseDouble(binding.incomeTextView.getText().toString().replace("Income:\n", ""));
        } catch (NumberFormatException e) {
            // Handle the case where income value is not set yet
            totalIncome = 0;
        }

        try {
            totalExpense = Double.parseDouble(binding.expenseTextView.getText().toString().replace("Expense:\n", ""));
        } catch (NumberFormatException e) {
            // Handle the case where expense value is not set yet
            totalExpense = 0;
        }

        // Calculations
        double remainingBudget = totalBudget - totalExpense;
        double balance = totalIncome - totalExpense;

        // Update UI
        binding.budgetTextView.setText(String.format("Budget:\n%.2f", totalBudget));
        binding.remainingTextView.setText(String.format("Remaining:\n%.2f", remainingBudget));
        binding.balanceTextView.setText(String.format("Balance:\n%.2f", balance));
    }
}
