package com.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class MoreOptionsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more_options, container, false);


        view.findViewById(R.id.settings_option).setOnClickListener(v -> {
            // Navigate to Settings Activity
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.profile_option).setOnClickListener(v -> {
            // Navigate to Profile Activity
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.help_option).setOnClickListener(v -> {
            // Navigate to Help Activity
            Intent intent = new Intent(getActivity(), HelpActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.about_option).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AboutActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.logout_option).setOnClickListener(v -> {
            // Handle logout
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        });


        view.findViewById(R.id.budget_card).setOnClickListener(v -> {
            // Navigate to Budgets Activity
            Intent intent = new Intent(getActivity(), BudgetsActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.transactions_card).setOnClickListener(v -> {
            // Navigate to Transactions Activity
            Intent intent = new Intent(getActivity(), TransactionsActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.accounts_card).setOnClickListener(v -> {
            // Navigate to Accounts Activity
            Intent intent = new Intent(getActivity(), AccountsActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.reports_card).setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), ReportsActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
