package com.expensetracker;// PaymentMethodBottomSheetDialog.java
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class PaymentMethodBottomSheetDialog extends BottomSheetDialogFragment {

    private List<String> paymentMethods;
    private OnPaymentMethodSelectedListener listener;

    public PaymentMethodBottomSheetDialog(List<String> paymentMethods, OnPaymentMethodSelectedListener listener) {
        this.paymentMethods = paymentMethods;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_payment_method_bottom_sheet, container, false);

        // Populate the payment methods in the bottom sheet
        ViewGroup paymentMethodContainer = view.findViewById(R.id.paymentMethodContainer);
        for (String paymentMethod : paymentMethods) {
            TextView tvPaymentMethod = new TextView(getContext());
            tvPaymentMethod.setText(paymentMethod);
            tvPaymentMethod.setPadding(16, 16, 16, 16);
            tvPaymentMethod.setTextSize(16f);
            tvPaymentMethod.setOnClickListener(v -> {
                listener.onPaymentMethodSelected(paymentMethod);
                dismiss();
            });
            paymentMethodContainer.addView(tvPaymentMethod);
        }

        return view;
    }

    public interface OnPaymentMethodSelectedListener {
        void onPaymentMethodSelected(String paymentMethod);
    }
}

