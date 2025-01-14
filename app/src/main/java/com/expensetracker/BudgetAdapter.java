package com.expensetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {

    private List<Budget> budgetList;

    public BudgetAdapter(List<Budget> budgetList) {
        this.budgetList = budgetList;
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_budget, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        Budget budget = budgetList.get(position);
        holder.budgetName.setText(budget.getName());
        holder.budgetAmount.setText(String.format("Amount: %.2f", budget.getAmount()));
        holder.budgetPeriod.setText("Period: " + budget.getPeriod());
        holder.budgetCategory.setText("Category: " + budget.getCategory());
    }

    @Override
    public int getItemCount() {
        return budgetList.size();
    }

    static class BudgetViewHolder extends RecyclerView.ViewHolder {
        TextView budgetName, budgetAmount, budgetPeriod, budgetCategory;

        BudgetViewHolder(View itemView) {
            super(itemView);
            budgetName = itemView.findViewById(R.id.budget_name);
            budgetAmount = itemView.findViewById(R.id.budget_amount);
            budgetPeriod = itemView.findViewById(R.id.budget_period);
            budgetCategory = itemView.findViewById(R.id.budget_category);
        }
    }
}
