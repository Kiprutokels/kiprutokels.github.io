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

public class IncomeCategoriesFragment extends Fragment {

    private RecyclerView recyclerView;
    private IncomeCategoriesAdapter adapter;
    private List<IncomeCategory> incomeCategories;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income_categories, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_income_categories);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));  // Set the RecyclerView to use a GridLayoutManager with 2 columns

        // Initialize Firestore and Auth
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize the categories list
        incomeCategories = new ArrayList<>();
        incomeCategories.add(new IncomeCategory("Salary", R.drawable.ic_salary));
        incomeCategories.add(new IncomeCategory("Business", R.drawable.ic_business));
        incomeCategories.add(new IncomeCategory("Investments", R.drawable.ic_investments));
        incomeCategories.add(new IncomeCategory("Freelancing", R.drawable.ic_freelancer));
        incomeCategories.add(new IncomeCategory("Rent", R.drawable.ic_rent));
        incomeCategories.add(new IncomeCategory("Pension", R.drawable.ic_pension));
        incomeCategories.add(new IncomeCategory("Savings", R.drawable.ic_savings));
        incomeCategories.add(new IncomeCategory("Other", R.drawable.ic_more));


        adapter = new IncomeCategoriesAdapter(incomeCategories, category -> {
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
                .whereEqualTo("type", "Income")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            Long iconResIdLong = document.getLong("iconResId");
                            int iconResId = iconResIdLong != null ? iconResIdLong.intValue() : R.drawable.baseline_category_24; // Default icon if null
                            incomeCategories.add(new IncomeCategory(name, iconResId));
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        // Handle errors here
                    }
                });
    }
}
