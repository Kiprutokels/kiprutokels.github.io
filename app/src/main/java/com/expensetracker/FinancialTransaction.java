package com.expensetracker;

import java.util.Date;

public class FinancialTransaction {
    private String id;
    private Double amount;
    private String category;
    private Date date;
    private String type;

    public FinancialTransaction() {
    }

    public FinancialTransaction(String id, Double amount, String category, Date date, String type) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.type = type;
    }

    // Getters and Setters
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

