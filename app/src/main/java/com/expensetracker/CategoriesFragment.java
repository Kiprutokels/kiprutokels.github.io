package com.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CategoriesFragment extends Fragment {

    private FloatingActionButton fabAddCategory;
    private ViewPager2 viewPager;
    private CategoriesPagerAdapter pagerAdapter;
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        // Initialize the FloatingActionButton
        fabAddCategory = view.findViewById(R.id.fab_add_category);

        // Set OnClickListener for the FloatingActionButton
        fabAddCategory.setOnClickListener(v -> {
            // Intent to start AddCategoryActivity
            Intent intent = new Intent(getActivity(), AddCategoryActivity.class);
            startActivity(intent);
        });

        // Set up ViewPager and TabLayout
        viewPager = view.findViewById(R.id.view_pager);
        pagerAdapter = new CategoriesPagerAdapter(requireActivity());
        viewPager.setAdapter(pagerAdapter);

        tabLayout = view.findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(position == 0 ? "Income" : "Expense")).attach();

        return view;
    }
}
