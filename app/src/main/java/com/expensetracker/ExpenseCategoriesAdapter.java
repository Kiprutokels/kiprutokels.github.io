package com.expensetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ExpenseCategoriesAdapter extends RecyclerView.Adapter<ExpenseCategoriesAdapter.ViewHolder> {

    private List<ExpenseCategory> expenseCategories;
    private OnCategorySelectedListener listener;

    public interface OnCategorySelectedListener {
        void onCategorySelected(ExpenseCategory category);
    }

    public ExpenseCategoriesAdapter(List<ExpenseCategory> expenseCategories, OnCategorySelectedListener listener) {
        this.expenseCategories = expenseCategories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExpenseCategory category = expenseCategories.get(position);
        holder.categoryName.setText(category.getName());
        holder.categoryIcon.setImageResource(category.getIconResId());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategorySelected(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return expenseCategories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryName;
        public ImageView categoryIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.category_name);
            categoryIcon = itemView.findViewById(R.id.category_icon);
        }
    }
}
