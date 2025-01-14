package com.expensetracker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddIncomeFragment extends Fragment implements PaymentMethodBottomSheetDialog.OnPaymentMethodSelectedListener {

    private TextView textSelectedDatetime;
    private EditText etAmount, etPaymentMethod, etCategory, etDescription;
    private Button btnSave;
    private FirebaseFirestore db;
    private String userId;
    private Calendar selectedCalendar = Calendar.getInstance();
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("EEE, d MMM yyyy, hh:mm a", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_income, container, false);

        textSelectedDatetime = view.findViewById(R.id.text_selected_datetime);
        etAmount = view.findViewById(R.id.etAmount);
        etPaymentMethod = view.findViewById(R.id.etPaymentMethod);
        etCategory = view.findViewById(R.id.etCategory);
        etDescription = view.findViewById(R.id.etDescription);
        btnSave = view.findViewById(R.id.btnSave);

        ImageView iconCalendar = view.findViewById(R.id.icon_calendar);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get current user ID

        // Show current date and time by default
        textSelectedDatetime.setText(dateTimeFormat.format(new Date()));

        iconCalendar.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(getContext(),
                    (view1, year, month, dayOfMonth) -> {
                        selectedCalendar.set(year, month, dayOfMonth);
                        showTimePickerDialog();
                    },
                    selectedCalendar.get(Calendar.YEAR),
                    selectedCalendar.get(Calendar.MONTH),
                    selectedCalendar.get(Calendar.DAY_OF_MONTH));
            datePicker.show();
        });

        etCategory.setOnClickListener(v -> showCategoryBottomSheet());
        etPaymentMethod.setOnClickListener(v -> showPaymentMethodBottomSheet());

        btnSave.setOnClickListener(v -> saveIncome());

        return view;
    }

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


    // Show the BottomSheetCategoryDialog for selecting a category
    private void showCategoryBottomSheet() {
        BottomSheetCategoryDialog bottomSheet = new BottomSheetCategoryDialog();
        bottomSheet.setOnCategorySelectedListener(category -> {
            etCategory.setText(category.getName());

            bottomSheet.dismiss();
        });
        bottomSheet.show(getChildFragmentManager(), bottomSheet.getTag());
    }

    private void showPaymentMethodBottomSheet() {
        PaymentMethodBottomSheetDialog paymentMethodDialog = new PaymentMethodBottomSheetDialog(Arrays.asList("Cash", "Credit Card", "Bank Transfer", "Mobile Money"), this);
        paymentMethodDialog.show(getFragmentManager(), paymentMethodDialog.getTag());
    }


    @Override
    public void onPaymentMethodSelected(String paymentMethod) {
        etPaymentMethod.setText(paymentMethod);
    }

    private void saveIncome() {
        // Get the values from the EditText fields
        String amountText = etAmount.getText().toString();
        String paymentMethod = etPaymentMethod.getText().toString();
        String category = etCategory.getText().toString();
        String description = etDescription.getText().toString();

        // Validate required fields: Amount, Payment Method, and Category
        if (amountText.isEmpty()) {
            Toast.makeText(getContext(), "Please enter the amount", Toast.LENGTH_SHORT).show();
            return;
        }
        if (paymentMethod.isEmpty()) {
            Toast.makeText(getContext(), "Please select a payment method", Toast.LENGTH_SHORT).show();
            return;
        }
        if (category.isEmpty()) {
            Toast.makeText(getContext(), "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        // If validation passes, parse the amount and proceed with saving
        double amount = Double.parseDouble(amountText);
        Date date = selectedCalendar.getTime();
        Timestamp timestamp = new Timestamp(date); // Convert date to Timestamp

        String type = "Income"; // You can set this based on your logic
        // Generate a unique ID for the document
        String id = UUID.randomUUID().toString();

        // Create an Income object
        Income income = new Income(id, amount, description, category, date, paymentMethod, type);

        // Save to Firestore
        db.collection("users")
                .document(userId)
                .collection("income")
                .document(id)
                .set(income)
                .addOnSuccessListener(aVoid -> {Toast.makeText(getContext(), "Income saved", Toast.LENGTH_SHORT).show();
                    getActivity().finish();})
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error saving income", Toast.LENGTH_SHORT).show());
    }
}
