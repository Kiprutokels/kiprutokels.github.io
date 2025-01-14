package com.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.appcompat.widget.SearchView;

import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.text.NumberFormat;

public class HomeFragment extends Fragment {

    private FirebaseFirestore db;
    private TextView expenseSetResult;
    private TextView incomeSetResult;
    private TextView balanceValue;
    private FirebaseAuth mAuth;
    private Button set_budget;
    private RecyclerView recentTransactionsRecyclerView;
    private TransactionAdapter recentTransactionAdapter;
    private List<FinancialTransaction> recentTransactionList;
    private ProgressBar progressBar;
    private View noTransactionsView;

    private double totalExpenses = 0.0;
    private double totalIncome = 0.0;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        expenseSetResult = view.findViewById(R.id.expense_set_result);
        incomeSetResult = view.findViewById(R.id.income_set_result);
        balanceValue = view.findViewById(R.id.balance_value);
        progressBar = view.findViewById(R.id.progress_bar);
        noTransactionsView = view.findViewById(R.id.no_transactions_view);

        set_budget = view.findViewById(R.id.button_setup_budget);
        set_budget.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NewBudgetActivity.class);
            startActivity(intent);
        });

        // Recent transactions RecyclerView
        recentTransactionsRecyclerView = view.findViewById(R.id.recycler_view_recent_transactions);
        recentTransactionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recentTransactionList = new ArrayList<>();
        recentTransactionAdapter = new TransactionAdapter(recentTransactionList, this::onTransactionSelected);
        recentTransactionsRecyclerView.setAdapter(recentTransactionAdapter);

        setUpIcons(view);
        displayGreetingMessage(view);
        loadTotalExpensesAndIncome();
        loadRecentTransactions();
        setupViewAllListener(view);

        return view;
    }

    private void setUpIcons(View view) {
        ImageView userIcon = view.findViewById(R.id.user_icon);
        userIcon.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        });

        ImageView bellIcon = view.findViewById(R.id.bell_icon);
        bellIcon.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NotificationsActivity.class);
            startActivity(intent);
        });

        ImageView helpIcon = view.findViewById(R.id.help_icon);
        helpIcon.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), HelpActivity.class);
            startActivity(intent);
        });

        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TransactionsActivity.class);
            startActivity(intent);
        });
    }

    private void displayGreetingMessage(View view) {
        TextView userNameTextView = view.findViewById(R.id.user_name);
        TextView greetingMessageTextView = view.findViewById(R.id.greeting_message);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Fetch the user's name from Firestore
            db.collection("users").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String name = document.getString("name");
                                if (name != null && !name.isEmpty()) {
                                    userNameTextView.setText("Hi " + name + "!");
                                } else {
                                    userNameTextView.setText(getString(R.string.hi));
                                }
                            } else {
                                userNameTextView.setText(getString(R.string.hi));
                            }
                        } else {
                            Log.d("Firestore", "Fetch failed: ", task.getException());
                            userNameTextView.setText(getString(R.string.hi));
                        }
                    });

            // Determine greeting based on time of day
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            String greeting;

            if (hour >= 0 && hour < 12) {
                greeting = getString(R.string.good_morning);
            } else if (hour >= 12 && hour < 17) {
                greeting = getString(R.string.good_afternoon);
            } else {
                greeting = getString(R.string.good_evening);
            }

            greetingMessageTextView.setText(greeting);
        } else {
            userNameTextView.setText(getString(R.string.hi));
            greetingMessageTextView.setText(getString(R.string.welcome));
        }
    }

    private void setupViewAllListener(View view) {
        TextView viewAllTextView = view.findViewById(R.id.text_view_all);
        viewAllTextView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TransactionsActivity.class);
            startActivity(intent);
        });
    }

    private void loadTotalExpensesAndIncome() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Fetch total expenses
            db.collection("users").document(userId).collection("expenses")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            totalExpenses = 0.0;
                            for (DocumentSnapshot document : task.getResult()) {
                                Double amount = document.getDouble("amount");
                                if (amount != null) {
                                    totalExpenses += amount;
                                }
                            }

                            expenseSetResult.setText(String.format("%,.2f", totalExpenses));
                            loadIncomeAndUpdateBalance(userId);
                        } else {
                            Log.d("Firestore", "Error getting expenses: ", task.getException());
                        }
                    });
        }
    }

    private void loadIncomeAndUpdateBalance(String userId) {
        db.collection("users").document(userId).collection("income")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        totalIncome = 0.0;
                        for (DocumentSnapshot document : task.getResult()) {
                            Double amount = document.getDouble("amount");
                            if (amount != null) {
                                totalIncome += amount;
                            }
                        }

                        incomeSetResult.setText(String.format("%,.2f", totalIncome));

                        double balance = totalIncome - totalExpenses;
                        balanceValue.setText(String.format("%,.2f", balance));
                    } else {
                        Log.d("Firestore", "Error getting income: ", task.getException());
                    }
                });
    }

    private void loadRecentTransactions() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            recentTransactionList.clear();

            db.collection("users").document(userId).collection("expenses")
                    .orderBy("date", Query.Direction.DESCENDING)
                    .limit(3)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Expense expense = document.toObject(Expense.class);
                                expense.setId(document.getId());
                                recentTransactionList.add(new FinancialTransaction(expense.getId(), expense.getAmount(), expense.getCategory(), expense.getDate(), "Expense"));
                            }
                            // After loading expenses, load recent income
                            loadRecentIncomeTransactions(userId, 6 - recentTransactionList.size());
                        } else {
                            Log.d("Firestore", "Error getting expenses: ", task.getException());
                            progressBar.setVisibility(View.GONE);
                            updateNoTransactionsView();
                        }
                    });
        }
    }

    private void loadRecentIncomeTransactions(String userId, int limit) {
        db.collection("users").document(userId).collection("income")
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            Income income = document.toObject(Income.class);
                            income.setId(document.getId());
                            recentTransactionList.add(new FinancialTransaction(income.getId(), income.getAmount(), income.getCategory(), income.getDate(), "Income"));
                        }

                        // Sort by date after adding both expenses and income
                        Collections.sort(recentTransactionList, new Comparator<FinancialTransaction>() {
                            @Override
                            public int compare(FinancialTransaction t1, FinancialTransaction t2) {
                                return t2.getDate().compareTo(t1.getDate());
                            }
                        });

                        // Limit to 6 items only
                        if (recentTransactionList.size() > 6) {
                            recentTransactionList = recentTransactionList.subList(0, 6);
                        }

                        recentTransactionAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("Firestore", "Error getting income: ", task.getException());
                    }
                    progressBar.setVisibility(View.GONE);
                    updateNoTransactionsView();
                });
    }

    private void updateNoTransactionsView() {
        if (recentTransactionList.isEmpty()) {
            noTransactionsView.setVisibility(View.VISIBLE);
            recentTransactionsRecyclerView.setVisibility(View.GONE);
        } else {
            noTransactionsView.setVisibility(View.GONE);
            recentTransactionsRecyclerView.setVisibility(View.VISIBLE);
        }
    }



    private void onTransactionSelected(FinancialTransaction transaction) {
        Intent intent = new Intent(getActivity(), EditTransactionActivity.class);
        intent.putExtra("transaction_id", transaction.getId());
        intent.putExtra("transaction_type", transaction.getType());
        startActivity(intent);
    }
}