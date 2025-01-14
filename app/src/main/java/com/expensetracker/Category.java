package com.expensetracker;

public class Category {
    private String name;
    private String type;
    private int iconResId;
    private String description;

    // Default constructor required for calls to DataSnapshot.getValue(Category.class)
    public Category() {
    }

    public Category(String name, String type, int iconResId, String description) {
        this.name = name;
        this.type = type;
        this.iconResId = iconResId;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
