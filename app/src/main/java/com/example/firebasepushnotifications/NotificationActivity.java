package com.example.firebasepushnotifications;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class NotificationActivity extends AppCompatActivity {
    private TextView mNotificationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        mNotificationView = (TextView) findViewById(R.id.notification_view);

        String dataMessage = getIntent().getStringExtra("notificationMessage");
        String dataFrom = getIntent().getStringExtra("senderId");

        mNotificationView.setText("FROM: " + dataFrom + " | MESSAGE: " + dataMessage);
    }
}
