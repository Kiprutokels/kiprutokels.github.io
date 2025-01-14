package com.expensetracker;

public class Budget {
    private String name;
    private float amount;
    private String category;
    private String period;

    // Default constructor required for calls to DataSnapshot.getValue(Budget.class)
    public Budget() {
    }

    public Budget(String name, float amount, String category, String period) {
        this.name = name;
        this.amount = amount;
        this.category = category;
        this.period = period;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
}
