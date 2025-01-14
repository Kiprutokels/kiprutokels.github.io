package com.expensetracker;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.expensetracker.databinding.FragmentDayBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DayFragment extends Fragment {

    private FragmentDayBinding binding;
    private FirebaseFirestore db;
    private CollectionReference expensesRef, incomeRef;

    private TransactionAdapter transactionAdapter;
    private List<FinancialTransaction> transactionList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firestore
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            db = FirebaseFirestore.getInstance();
            expensesRef = db.collection("users").document(userId).collection("expenses");
            incomeRef = db.collection("users").document(userId).collection("income");
        } else {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
        }

        // Initialize the transaction list and adapter
        transactionList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(transactionList, transaction -> {
            // Handle transaction selection
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDayBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Set up RecyclerView
        RecyclerView recyclerView = binding.transactionRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(transactionAdapter);

        // Set up CalendarView listener
        binding.calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            // Fetch and display analysis for the selected date
            fetchAndDisplayData(year, month, dayOfMonth);
        });

        return view;
    }

    private void fetchAndDisplayData(int year, int month, int dayOfMonth) {
        // Clear the previous data from the list
        transactionList.clear();
        transactionAdapter.notifyDataSetChanged();

        // Adjust Calendar instance for local time zone
        Calendar startOfDay = Calendar.getInstance();
        startOfDay.set(year, month, dayOfMonth, 0, 0, 0);
        startOfDay.set(Calendar.MILLISECOND, 0);

        Calendar endOfDay = Calendar.getInstance();
        endOfDay.set(year, month, dayOfMonth, 23, 59, 59);
        endOfDay.set(Calendar.MILLISECOND, 999);

        // Convert local time to UTC for Firestore queries
        Timestamp startTimestamp = new Timestamp(startOfDay.getTime());
        Timestamp endTimestamp = new Timestamp(endOfDay.getTime());

        // Fetch transactions for the selected day
        fetchTransactions(expensesRef, startTimestamp, endTimestamp, "Expense");
        fetchTransactions(incomeRef, startTimestamp, endTimestamp, "Income");
    }

    private void fetchTransactions(CollectionReference ref, Timestamp startTimestamp, Timestamp endTimestamp, String type) {
        ref.whereGreaterThanOrEqualTo("date", startTimestamp)
                .whereLessThan("date", endTimestamp)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            FinancialTransaction transaction = document.toObject(FinancialTransaction.class);
                            if (transaction != null) {
                                transaction.setType(type);
                                transactionList.add(transaction);
                                Log.d("DayFragment", "Fetched transaction: " + transaction.getType() + " - " + transaction.getAmount());
                            }
                        }
                    }
                    transactionAdapter.notifyDataSetChanged();
                    updateAnalysis(); // Ensure this is called only once after both income and expense fetching is complete
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to fetch transactions", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateAnalysis() {
        double totalIncome = 0.0;
        double totalExpense = 0.0;

        for (FinancialTransaction transaction : transactionList) {
            if ("Income".equals(transaction.getType())) {
                totalIncome += transaction.getAmount();
            } else {
                totalExpense += transaction.getAmount();
            }
        }

        // Update UI elements
        double balance = totalIncome - totalExpense;
        binding.incomeTextView.setText("Income: \n" + totalIncome);
        binding.expenseTextView.setText("Expense: \n" + totalExpense);
        binding.balanceTextView.setText("Balance:\n" + balance);

        // Toggle visibility based on data availability
        if (transactionList.isEmpty()) {
            displayNoTransactionsMessage();
        } else {
            binding.analysisSection.setVisibility(View.VISIBLE);
            binding.noTransactionsTextView.setVisibility(View.GONE);
        }
    }

    private void displayNoTransactionsMessage() {
        binding.analysisSection.setVisibility(View.GONE);
        binding.noTransactionsTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!transactionList.isEmpty()) {
            transactionAdapter.notifyDataSetChanged();
        }
    }
}
