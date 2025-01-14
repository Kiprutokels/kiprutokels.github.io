package com.expensetracker;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BudgetsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BudgetAdapter budgetAdapter;
    private List<Budget> budgetList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budgets);

        ImageView leftIcon = findViewById(R.id.arrow_back);
        leftIcon.setOnClickListener(view -> finish());

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        budgetList = new ArrayList<>();
        budgetAdapter = new BudgetAdapter(budgetList);
        recyclerView.setAdapter(budgetAdapter);

        db = FirebaseFirestore.getInstance();

        // Load budgets from Firestore
        loadBudgets();
    }

    private void loadBudgets() {
        db.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("budget")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        budgetList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Budget budget = document.toObject(Budget.class);
                            budgetList.add(budget);
                        }
                        budgetAdapter.notifyDataSetChanged();
                    } else {
                        // Handle errors
                    }
                });
    }
}
