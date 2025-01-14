package com.expensetracker;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class TransactionsActivity extends AppCompatActivity {

    private ExtendedFloatingActionButton addButton;
    private FirebaseFirestore db;
    private CollectionReference incomeRef, expensesRef;
    private RecyclerView recyclerView;
    private TransactionAdapter transactionAdapter;
    private List<FinancialTransaction> transactionList;
    private LinearLayout emptyStateLayout;
    private FirebaseAuth mAuth;
    private EditText searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        ImageView leftIcon = findViewById(R.id.lefticon);
        leftIcon.setOnClickListener(view -> finish());

        // Initialize the floating action button
        addButton = findViewById(R.id.fab_add_transaction);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TransactionsActivity.this, AddTransactionActivity.class);
                startActivity(intent);
            }
        });

        searchBar = findViewById(R.id.search_bar);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTransactions(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });

        ImageView filterIcon = findViewById(R.id.filter);
        filterIcon.setOnClickListener(v -> showBottomDialog());

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        db = FirebaseFirestore.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        incomeRef = db.collection("users").document(userId).collection("income");
        expensesRef = db.collection("users").document(userId).collection("expenses");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(transactionList, this::onTransactionSelected);
        recyclerView.setAdapter(transactionAdapter);

        emptyStateLayout = findViewById(R.id.empty_state_layout);

        loadTransactions();
    }

    private void loadTransactions() {
        transactionList.clear();

        // Load expenses
        expensesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    Expense expense = document.toObject(Expense.class);
                    expense.setId(document.getId());
                    transactionList.add(new FinancialTransaction(expense.getId(), expense.getAmount(), expense.getCategory(), expense.getDate(), "Expense"));
                }
                updateUI();
            } else {
                Toast.makeText(TransactionsActivity.this, "Error loading expenses: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Load income
        incomeRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    Income income = document.toObject(Income.class);
                    income.setId(document.getId());
                    transactionList.add(new FinancialTransaction(income.getId(), income.getAmount(), income.getCategory(), income.getDate(), "Income"));
                }
                updateUI();
            } else {
                Toast.makeText(TransactionsActivity.this, "Error loading income: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        if (transactionList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        transactionAdapter.notifyDataSetChanged();
    }

    private void onTransactionSelected(FinancialTransaction transaction) {
        Intent intent = new Intent(TransactionsActivity.this, EditTransactionActivity.class);
        intent.putExtra("transaction_id", transaction.getId());
        intent.putExtra("transaction_type", transaction.getType());
        startActivity(intent);
    }

    private void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_layout);

        LinearLayout expenseLayout = dialog.findViewById(R.id.layoutExpense);
        LinearLayout incomeLayout = dialog.findViewById(R.id.layoutIncome);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        expenseLayout.setOnClickListener(v -> {
            dialog.dismiss();
            filterByType("Expense");
        });

        incomeLayout.setOnClickListener(v -> {
            dialog.dismiss();
            filterByType("Income");
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void filterByType(String type) {
        List<FinancialTransaction> filteredList = new ArrayList<>();
        for (FinancialTransaction transaction : transactionList) {
            if (transaction.getType().equals(type)) {
                filteredList.add(transaction);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No " + type.toLowerCase() + " transactions found", Toast.LENGTH_SHORT).show();
        } else {
            transactionAdapter.updateList(filteredList);
        }
    }

    private void filterTransactions(String query) {
        List<FinancialTransaction> filteredList = new ArrayList<>();
        for (FinancialTransaction transaction : transactionList) {
            if (transaction.getCategory().toLowerCase().contains(query.toLowerCase()) ||
                    transaction.getId().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(transaction);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No transactions found", Toast.LENGTH_SHORT).show();
        } else {
            transactionAdapter.updateList(filteredList);
        }
    }
}
