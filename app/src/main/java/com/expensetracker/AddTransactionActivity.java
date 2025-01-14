package com.expensetracker;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AddTransactionActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        // Back button functionality
        ImageView backButton = findViewById(R.id.back_arrow);
        backButton.setOnClickListener(v -> finish());
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);

        adapter.addFragment(new AddExpenseFragment(), "Add Expense");
        adapter.addFragment(new AddIncomeFragment(), "Add Income");

        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(adapter.getPageTitle(position))).attach();
    }
}