package com.expensetracker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditTransactionActivity extends AppCompatActivity {

    private EditText editTextAmount, editTextDescription, editTextCategory;
    private TextView textSelectedDate, textSelectedTime;
    private ImageView iconSave, iconDelete, arrowBack;
    private FirebaseFirestore db;
    private String transactionId, transactionType;
    private Calendar selectedCalendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);

        // Initialize views
        editTextAmount = findViewById(R.id.edit_text_amount);
        editTextDescription = findViewById(R.id.edit_text_description);
        editTextCategory = findViewById(R.id.edit_text_category);
        textSelectedDate = findViewById(R.id.text_selected_date);
        textSelectedTime = findViewById(R.id.text_selected_time);
        iconSave = findViewById(R.id.icon_save);
        iconDelete = findViewById(R.id.icon_delete);
        arrowBack = findViewById(R.id.back_arrow);
        arrowBack.setOnClickListener(view -> finish());

        db = FirebaseFirestore.getInstance();

        // Get transaction details from the Intent
        transactionId = getIntent().getStringExtra("transaction_id");
        transactionType = getIntent().getStringExtra("transaction_type");

        loadTransactionDetails();

        textSelectedDate.setOnClickListener(v -> showDatePickerDialog());
        textSelectedTime.setOnClickListener(v -> showTimePickerDialog());

        iconSave.setOnClickListener(v -> saveTransactionDetails());
        iconDelete.setOnClickListener(v -> confirmDeleteTransaction());
    }

    private void loadTransactionDetails() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference docRef = db.collection("users").document(userId)
                .collection(transactionType.equals("Expense") ? "expenses" : "income")
                .document(transactionId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (transactionType.equals("Expense")) {
                Expense expense = documentSnapshot.toObject(Expense.class);
                if (expense != null) {
                    populateFields(expense.getAmount(), expense.getDescription(), expense.getCategory(), expense.getDate());
                }
            } else {
                Income income = documentSnapshot.toObject(Income.class);
                if (income != null) {
                    populateFields(income.getAmount(), income.getDescription(), income.getCategory(), income.getDate());
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(EditTransactionActivity.this, "Error loading transaction: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void populateFields(Double amount, String description, String category, Date date) {
        editTextAmount.setText(String.valueOf(amount));
        editTextDescription.setText(description);
        editTextCategory.setText(category);
        selectedCalendar.setTime(date);
        textSelectedDate.setText(dateFormat.format(date));
        textSelectedTime.setText(timeFormat.format(date));
    }

    private void showDatePickerDialog() {
        int year = selectedCalendar.get(Calendar.YEAR);
        int month = selectedCalendar.get(Calendar.MONTH);
        int day = selectedCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            selectedCalendar.set(year1, month1, dayOfMonth);
            textSelectedDate.setText(dateFormat.format(selectedCalendar.getTime()));
        }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        int hour = selectedCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = selectedCalendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedCalendar.set(Calendar.MINUTE, minute1);
            textSelectedTime.setText(timeFormat.format(selectedCalendar.getTime()));
        }, hour, minute, false);

        timePickerDialog.show();
    }

    private void saveTransactionDetails() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference docRef = db.collection("users").document(userId)
                .collection(transactionType.equals("Expense") ? "expenses" : "income")
                .document(transactionId);

        double amount = Double.parseDouble(editTextAmount.getText().toString());
        String description = editTextDescription.getText().toString();
        String category = editTextCategory.getText().toString();
        Date date = selectedCalendar.getTime();

        if (transactionType.equals("Expense")) {
            Expense expense = new Expense(transactionId, amount, description, category, date, "expense");
            docRef.set(expense)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditTransactionActivity.this, "Transaction updated", Toast.LENGTH_SHORT).show();
                        finish(); // Finish the activity after updating
                    })
                    .addOnFailureListener(e -> Toast.makeText(EditTransactionActivity.this, "Error updating transaction: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Income income = new Income(transactionId, amount, description, category, date, "", "income");
            docRef.set(income)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditTransactionActivity.this, "Transaction updated", Toast.LENGTH_SHORT).show();
                        finish(); // Finish the activity after updating
                    })
                    .addOnFailureListener(e -> Toast.makeText(EditTransactionActivity.this, "Error updating transaction: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void confirmDeleteTransaction() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Transaction")
                .setMessage("Do you want to delete this transaction?")
                .setPositiveButton("Yes", (dialog, which) -> deleteTransaction())
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteTransaction() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference docRef = db.collection("users").document(userId)
                .collection(transactionType.equals("Expense") ? "expenses" : "income")
                .document(transactionId);

        docRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditTransactionActivity.this, "Transaction deleted", Toast.LENGTH_SHORT).show();
                    finish(); // Finish the activity after deletion
                })
                .addOnFailureListener(e -> Toast.makeText(EditTransactionActivity.this, "Error deleting transaction: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
