package com.expensetracker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class CategoriesPagerAdapter extends FragmentStateAdapter {

    public CategoriesPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new IncomeCategoriesFragment();
            case 1:
                return new ExpenseCategoriesFragment();
            default:
                return new IncomeCategoriesFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;  // Number of pages (Income and Expense)
    }
}
