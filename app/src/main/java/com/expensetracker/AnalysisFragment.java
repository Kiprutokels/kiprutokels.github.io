package com.expensetracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AnalysisFragment extends Fragment {

    private FirebaseFirestore db;
    private CollectionReference expensesRef;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    public AnalysisFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firestore
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            db = FirebaseFirestore.getInstance();
            expensesRef = db.collection("users").document(userId).collection("expenses");
        } else {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_analysis, container, false);

        // Initialize TabLayout and ViewPager2
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        // Set up the ViewPager with the AnalysisPagerAdapter
        viewPager.setAdapter(new AnalysisPagerAdapter(requireActivity()));

        // Link the TabLayout and the ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Day");
                    break;
                case 1:
                    tab.setText("Week");
                    break;
                case 2:
                    tab.setText("Month");
                    break;
            }
        }).attach();

        return view;
    }
}
