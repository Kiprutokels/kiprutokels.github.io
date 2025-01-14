package com.expensetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IncomeCategoriesAdapter extends RecyclerView.Adapter<IncomeCategoriesAdapter.ViewHolder> {

    private List<IncomeCategory> incomeCategories;
    private IncomeCategoriesAdapter.OnCategorySelectedListener listener;

    public interface OnCategorySelectedListener {
        void onCategorySelected(IncomeCategory category);
    }

    public IncomeCategoriesAdapter(List<IncomeCategory> incomeCategories, OnCategorySelectedListener listener) {
        this.incomeCategories = incomeCategories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_income_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IncomeCategory category = incomeCategories.get(position);
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
        return incomeCategories.size();
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
