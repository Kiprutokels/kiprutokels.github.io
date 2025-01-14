package com.expensetracker;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.expensetracker.databinding.FragmentWeekBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeekFragment extends Fragment {

    private FragmentWeekBinding binding;
    private FirebaseFirestore db;
    private CollectionReference expensesRef, incomeRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWeekBinding.inflate(inflater, container, false);

        // Initialize Firestore
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            db = FirebaseFirestore.getInstance();
            expensesRef = db.collection("users").document(userId).collection("expenses");
            incomeRef = db.collection("users").document(userId).collection("income");
        }

        // Fetch data for the week
        fetchAndDisplayWeekData();

        return binding.getRoot();
    }

    private void fetchAndDisplayWeekData() {
        Map<String, Float> expenseMap = new HashMap<>();
        Map<String, Float> incomeMap = new HashMap<>();
        List<String> labels = getWeekLabels(); // Get labels for the week (e.g., "Sun 9", "Mon 10", etc.)

        // Initialize maps with zero values for all days
        for (String label : labels) {
            expenseMap.put(label, 0f);
            incomeMap.put(label, 0f);
        }

        // Set the start and end dates for the current week
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        Timestamp startOfWeek = new Timestamp(calendar.getTime());

        calendar.add(Calendar.DAY_OF_WEEK, 6);
        Timestamp endOfWeek = new Timestamp(calendar.getTime());

        // Fetch expenses and income within the week range
        fetchTransactions(expensesRef, startOfWeek, endOfWeek, expenseMap, labels, "Expenses");
        fetchTransactions(incomeRef, startOfWeek, endOfWeek, incomeMap, labels, "Income");
    }

    private void fetchTransactions(CollectionReference ref, Timestamp start, Timestamp end, Map<String, Float> dataMap, List<String> labels, String type) {
        ref.whereGreaterThanOrEqualTo("date", start)
                .whereLessThan("date", end)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Timestamp date = document.getTimestamp("date");
                        if (date != null) {
                            String label = getLabelForDate(date.toDate(), labels); // Convert date to week label
                            if (label != null) {
                                float amount = document.getDouble("amount").floatValue();
                                dataMap.put(label, dataMap.getOrDefault(label, 0f) + amount); // Aggregate amounts
                            }
                        }
                    }
                    setupBarChart(type.equals("Expenses") ? binding.expenseBarChart : binding.incomeBarChart, dataMap, labels, type);
                });
    }

    private void setupBarChart(BarChart barChart, Map<String, Float> dataMap, List<String> labels, String label) {
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < labels.size(); i++) {
            String day = labels.get(i);
            entries.add(new BarEntry(i, dataMap.getOrDefault(day, 0f)));
        }

        BarDataSet barDataSet = new BarDataSet(entries, label);
        barDataSet.setColor(label.equals("Expenses") ? Color.RED : Color.GREEN);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(12f);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f);

        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false);

        // Configure X-axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);

        barChart.invalidate(); // Refresh chart
    }

    private List<String> getWeekLabels() {
        List<String> labels = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        for (int i = 0; i < 7; i++) {
            String label = String.format("%ta %d", calendar.getTime(), calendar.get(Calendar.DAY_OF_MONTH));
            labels.add(label);
            calendar.add(Calendar.DAY_OF_WEEK, 1);
        }
        return labels;
    }

    private String getLabelForDate(java.util.Date date, List<String> labels) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String label = String.format("%ta %d", date, calendar.get(Calendar.DAY_OF_MONTH));
        return labels.contains(label) ? label : null;
    }
}
