package com.expensetracker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AnalysisPagerAdapter extends FragmentStateAdapter {

    public AnalysisPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new DayFragment();
            case 1:
                return new WeekFragment();
            case 2:
                return new MonthFragment();

            default:
                return new DayFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
