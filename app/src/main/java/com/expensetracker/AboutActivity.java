package com.expensetracker;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    private TextView versionTextView;
    private TextView contactTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ImageView leftIcon = findViewById(R.id.arrow_back);
        leftIcon.setOnClickListener(view -> finish());
        versionTextView = findViewById(R.id.version_text_view);
        contactTextView = findViewById(R.id.contact_text_view);

        setAppVersion();

        // Set up email link
        setEmailLink();
    }

    private void setAppVersion() {
        try {
            // Get current app version
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            versionTextView.setText(getString(R.string.version, version));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            versionTextView.setText("Version not available");
        }
    }

    // Set up the email link functionality
    private void setEmailLink() {
        String email = "class.classicgroup@gmail.com";
        String subject = "Feedback for Expense Tracker";
        String body = "Please provide your feedback here:";

        // Create an email Intent
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);

        contactTextView.setText(email);
        contactTextView.setOnClickListener(v -> startActivity(Intent.createChooser(emailIntent, "Send email via")));
    }
}
