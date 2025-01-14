package com.expensetracker;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class HelpActivity extends AppCompatActivity {

    private ImageView backIcon;
    private Button submitTicketButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // Initialize views
        backIcon = findViewById(R.id.back_button);
        submitTicketButton = findViewById(R.id.submit_ticket_btn);

        // click listener for back icon
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//  close the activity
            }
        });

        // click listener for submit ticket button
        submitTicketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to SubmitTicketActivity or open an email client
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@expensemanagerclassic.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Support Request");
                intent.putExtra(Intent.EXTRA_TEXT, "Describe your issue here...");
                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });
    }
}
