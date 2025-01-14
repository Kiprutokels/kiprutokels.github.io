package com.expensetracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<FinancialTransaction> transactionList;
    private Context context;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy, hh:mm a", Locale.getDefault());
    private final OnTransactionSelectedListener listener;

    public TransactionAdapter(List<FinancialTransaction> transactionList, OnTransactionSelectedListener listener) {
        this.transactionList = transactionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.transaction_item, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        FinancialTransaction transaction = transactionList.get(position);
        holder.textAmount.setText(String.format(Locale.getDefault(), "Ksh %,.2f", transaction.getAmount()));
        holder.textCategory.setText(transaction.getCategory());
        holder.textDate.setText(dateFormat.format(transaction.getDate()));

        if (transaction.getType().equals("Expense")) {
            holder.textAmount.setTextColor(context.getResources().getColor(R.color.red));
            holder.textCategory.setTextColor(context.getResources().getColor(R.color.red));
            holder.textDate.setTextColor(context.getResources().getColor(R.color.red));
        } else {
            holder.textAmount.setTextColor(context.getResources().getColor(R.color.green));
            holder.textCategory.setTextColor(context.getResources().getColor(R.color.green));
            holder.textDate.setTextColor(context.getResources().getColor(R.color.green));
        }

        holder.itemView.setOnClickListener(v -> listener.onTransactionSelected(transaction));
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public void updateList(List<FinancialTransaction> newList) {
        transactionList = newList;
        notifyDataSetChanged();
    }

    public interface OnTransactionSelectedListener {
        void onTransactionSelected(FinancialTransaction transaction);
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {

        public TextView textAmount, textCategory, textDate;
        public CardView cardView;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            textAmount = itemView.findViewById(R.id.text_amount);
            textCategory = itemView.findViewById(R.id.text_category);
            textDate = itemView.findViewById(R.id.text_date);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}
