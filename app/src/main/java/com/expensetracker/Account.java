package com.expensetracker;

public class Account {
    private String accountName;
    private String initialBalance;
    private String accountType;

    public Account() {
        // Default constructor required for calls to DataSnapshot.getValue(Account.class)
    }

    public Account(String accountName, String initialBalance, String accountType) {
        this.accountName = accountName;
        this.initialBalance = initialBalance;
        this.accountType = accountType;
    }

    // Getters and setters
    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(String initialBalance) {
        this.initialBalance = initialBalance;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}
