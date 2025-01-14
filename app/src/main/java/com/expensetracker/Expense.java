package com.expensetracker;

import java.util.Date;

public class Expense {
    private String id;
    private Double amount;
    private String description;
    private String category;
    private Date date;
    private String type;

    public Expense() {
    }

    public Expense(String id, Double amount, String description, String category, Date date, String type) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.date = date;
        this.type = type;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

