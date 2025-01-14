package com.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat.Type;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
public class HomeActivity extends AppCompatActivity {

    private FloatingActionButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize the floating action button
        addButton = findViewById(R.id.floatingActionButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AddTransactionActivity.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            int bottomInset = insets.getInsets(Type.systemBars()).bottom;

            // Adjust padding for BottomNavigationView
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setPadding(0, 0, 0, bottomInset);

            // Adjust FloatingActionButton position
            addButton.setTranslationY(-bottomInset);

            return insets;
        });


        // Initialize BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Set default fragment (HomeFragment)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        // Set up listener for BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
                addButton.setVisibility(View.VISIBLE);
            } else if (itemId == R.id.nav_analysis) {
                selectedFragment = new AnalysisFragment();
                addButton.setVisibility(View.VISIBLE);
            } else if (itemId == R.id.nav_categories) {
                selectedFragment = new CategoriesFragment();
                addButton.setVisibility(View.GONE);
            } else if (itemId == R.id.nav_more) {
                selectedFragment = new MoreOptionsFragment();
                addButton.setVisibility(View.GONE);
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });
    }
}
