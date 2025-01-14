package com.expensetracker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddExpenseFragment extends Fragment {

    private TextInputEditText amountEditText, descriptionEditText;
    private TextView selectedCategoryTextView, textSelectedDatetime;
    private ImageView iconCalendar, iconCategory;
    private Calendar selectedCalendar = Calendar.getInstance();
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("EEE, d MMM yyyy, hh:mm a", Locale.getDefault());
    private FirebaseFirestore db;
    private String userId;

    public AddExpenseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_expense, container, false);

        // Initialize UI elements
        amountEditText = rootView.findViewById(R.id.amount_input);
        descriptionEditText = rootView.findViewById(R.id.description_input);
        selectedCategoryTextView = rootView.findViewById(R.id.selected_category_textview);
        textSelectedDatetime = rootView.findViewById(R.id.text_selected_datetime);
        iconCalendar = rootView.findViewById(R.id.icon_calendar);
        iconCategory = rootView.findViewById(R.id.icon_category);
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            // Handle the case where the user is not authenticated
            Toast.makeText(getContext(), "User not authenticated. Please log in.", Toast.LENGTH_SHORT).show();
        }

        // Set default date and time
        textSelectedDatetime.setText(dateTimeFormat.format(new Date()));

        // Category selection with BottomSheet
        iconCategory.setOnClickListener(v -> showCategoryBottomSheet());


        iconCalendar.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(getContext(),
                    (view, year, month, dayOfMonth) -> {
                        selectedCalendar.set(year, month, dayOfMonth);
                        showTimePickerDialog();
                    },
                    selectedCalendar.get(Calendar.YEAR),
                    selectedCalendar.get(Calendar.MONTH),
                    selectedCalendar.get(Calendar.DAY_OF_MONTH));
            datePicker.show();
        });

        // Save Expense
        rootView.findViewById(R.id.save_button).setOnClickListener(v -> saveExpense());

        return rootView;
    }

    // Show the BottomSheetCategoryDialog for selecting a category
    private void showCategoryBottomSheet() {
        BottomSheetCategoryDialog bottomSheet = new BottomSheetCategoryDialog();
        bottomSheet.setOnCategorySelectedListener(category -> {
            selectedCategoryTextView.setText(category.getName());

            bottomSheet.dismiss();
        });
        bottomSheet.show(getChildFragmentManager(), bottomSheet.getTag());
    }

    // Show TimePickerDialog
    private void showTimePickerDialog() {
        int hour = selectedCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = selectedCalendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute1) -> {
            selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedCalendar.set(Calendar.MINUTE, minute1);
            textSelectedDatetime.setText(dateTimeFormat.format(selectedCalendar.getTime()));
        }, hour, minute, false);

        timePickerDialog.show();
    }

    // Save Expense to Firestore
    private void saveExpense() {
        if (userId == null) {
            Toast.makeText(getContext(), "User ID is null. Unable to save expense.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountEditText.getText().toString());
            String description = descriptionEditText.getText().toString();
            String category = selectedCategoryTextView.getText().toString();
            Date date = selectedCalendar.getTime();

            // Generate unique ID for the expense
            String id = UUID.randomUUID().toString();

            // Create Expense object
            Expense expense = new Expense(id, amount, description, category, date, "expense");

            // Save to Firestore
            db.collection("users")
                    .document(userId)
                    .collection("expenses")
                    .document(id)
                    .set(expense)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Expense saved", Toast.LENGTH_SHORT).show();
                        getActivity().finish();  // Close the current activity
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error saving expense: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid amount entered. Please enter a valid number.", Toast.LENGTH_SHORT).show();
        }
    }
}
