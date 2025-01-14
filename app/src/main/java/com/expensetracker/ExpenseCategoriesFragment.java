package com.expensetracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ExpenseCategoriesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ExpenseCategoriesAdapter adapter;
    private List<ExpenseCategory> expenseCategories;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_categories, container, false); // Changed the layout file

        recyclerView = view.findViewById(R.id.recycler_view_expense_categories);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));  // Set the RecyclerView to use a GridLayoutManager with 2 columns

        // Initialize Firestore and Auth
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize the categories list
        expenseCategories = new ArrayList<>();
        expenseCategories.add(new ExpenseCategory("Bills and Utilities", R.drawable.ic_bills));
        expenseCategories.add(new ExpenseCategory("Education", R.drawable.ic_education));
        expenseCategories.add(new ExpenseCategory("Medical", R.drawable.ic_medical));
        expenseCategories.add(new ExpenseCategory("Rent", R.drawable.ic_rent));
        expenseCategories.add(new ExpenseCategory("Travelling", R.drawable.ic_travelling));
        expenseCategories.add(new ExpenseCategory("Shopping", R.drawable.ic_shopping));
        expenseCategories.add(new ExpenseCategory("Food and Dining", R.drawable.ic_food));
        expenseCategories.add(new ExpenseCategory("Investments", R.drawable.ic_investments));
        expenseCategories.add(new ExpenseCategory("Insurance", R.drawable.ic_insurance));
        expenseCategories.add(new ExpenseCategory("Other", R.drawable.ic_more));

        adapter = new ExpenseCategoriesAdapter(expenseCategories, category -> {
            // Handle category selection here if needed
        });
        recyclerView.setAdapter(adapter);

        // Load additional categories from Firestore
        loadCategoriesFromFirestore();

        return view;
    }

    private void loadCategoriesFromFirestore() {
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("categories")
                .whereEqualTo("type", "Expense")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            Long iconResIdLong = document.getLong("iconResId");
                            int iconResId = iconResIdLong != null ? iconResIdLong.intValue() : R.drawable.baseline_category_24; // Default icon if null
                            expenseCategories.add(new ExpenseCategory(name, iconResId));
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        // Handle errors here
                    }
                });
    }
}
