package com.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class BottomSheetCategoryDialog extends BottomSheetDialogFragment {

    private RecyclerView recyclerView;
    private ExpenseCategoriesAdapter adapter;
    private List<ExpenseCategory> expenseCategories;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ExpenseCategoriesAdapter.OnCategorySelectedListener listener;

    public void setOnCategorySelectedListener(ExpenseCategoriesAdapter.OnCategorySelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_category_dialog, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_bottom_sheet);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));  // Set to 3 columns

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

        adapter = new ExpenseCategoriesAdapter(expenseCategories, listener);  // Pass the listener to the adapter
        recyclerView.setAdapter(adapter);

        loadCategoriesFromFirestore();

        view.findViewById(R.id.button_add_category).setOnClickListener(v -> {
            // Navigate to AddCategoryActivity
            startActivity(new Intent(getContext(), AddCategoryActivity.class));
        });

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
                        // errors
                    }
                });
    }
}
