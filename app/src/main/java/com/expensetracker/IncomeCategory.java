package com.expensetracker;

public class IncomeCategory {
    private String name;
    private int iconResId;

    public IncomeCategory(String name, int iconResId) {
        this.name = name;
        this.iconResId = iconResId;
    }

    public String getName() {
        return name;
    }

    public int getIconResId() {
        return iconResId;
    }
}
