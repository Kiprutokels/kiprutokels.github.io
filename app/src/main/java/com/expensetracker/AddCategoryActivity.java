package com.expensetracker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddCategoryActivity extends AppCompatActivity {

    private EditText categoryName, categoryDescription;
    private Spinner categoryTypeSpinner;
    private ImageView categoryIcon;
    private Button saveCategoryButton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private int selectedIconResId = R.drawable.baseline_category_24; // Default icon

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        categoryName = findViewById(R.id.category_name);
        categoryTypeSpinner = findViewById(R.id.category_type_spinner);
        categoryIcon = findViewById(R.id.category_icon);
        categoryDescription = findViewById(R.id.category_description);
        saveCategoryButton = findViewById(R.id.save_category_button);

        saveCategoryButton.setOnClickListener(v -> saveCategory());


        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        categoryIcon.setOnClickListener(v -> showIconPickerDialog());
    }

    private void showIconPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Icon");
        builder.setView(R.layout.dialog_icon_picker);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();

        GridLayout iconGrid = dialog.findViewById(R.id.icon_grid);
        if (iconGrid != null) {
            for (int i = 0; i < iconGrid.getChildCount(); i++) {
                ImageView iconView = (ImageView) iconGrid.getChildAt(i);
                iconView.setOnClickListener(v -> {
                    Object tagObject = v.getTag();
                    if (tagObject != null && tagObject instanceof String) {
                        String tagString = (String) tagObject;
                        selectedIconResId = getResources().getIdentifier(tagString, "drawable", getPackageName());
                        categoryIcon.setImageResource(selectedIconResId);
                    } else {
                        // Handle case where tag is null or not a String
                        Toast.makeText(AddCategoryActivity.this, "Error selecting icon", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                });
            }
        }
    }

    private void saveCategory() {
        String name = categoryName.getText().toString().trim();
        String description = categoryDescription.getText().toString().trim();
        String type = categoryTypeSpinner.getSelectedItem().toString();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        String categoryId = db.collection("users").document(userId).collection("categories").document().getId();
        Category newCategory = new Category(name, type, selectedIconResId, description);

        db.collection("users")
                .document(userId)
                .collection("categories")
                .document(categoryId)
                .set(newCategory)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddCategoryActivity.this, "Category saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddCategoryActivity.this, "Error saving category", Toast.LENGTH_SHORT).show();
                });
    }
}
